package me.thinking_gorilla.jooqfirstlook.actor;

import org.jooq.*;
import org.jooq.generated.tables.*;
import org.jooq.generated.tables.daos.*;
import org.jooq.generated.tables.pojos.*;
import org.jooq.generated.tables.records.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import java.util.*;

import static java.util.Objects.*;
import static me.thinking_gorilla.jooqfirstlook.utils.JooqListConditionUtil.*;
import static org.jooq.impl.DSL.*;

@Repository
public class ActorRepository {

    private final DSLContext dslContext;
    private final ActorDao actorDao;
    private final JActor ACTOR = JActor.ACTOR;

    public ActorRepository(DSLContext dslContext, Configuration configuration) {
        this.actorDao = new ActorDao(configuration);
        this.dslContext = dslContext;
    }

    public List<Actor> findByFirstNameAndLastName(String firstName, String lastName) {
        return dslContext.selectFrom(ACTOR)
            .where(
                // ACTOR.FIRST_NAME.eq(firstName)
                // .and(ACTOR.LAST_NAME.eq(lastName))
                ACTOR.FIRST_NAME.eq(firstName),
                ACTOR.LAST_NAME.eq(lastName)
            ).fetchInto(Actor.class);
    }

    public List<Actor> findByFirstNameOrLastName(String firstName, String lastName) {
        return dslContext.selectFrom(ACTOR)
            .where(ACTOR.FIRST_NAME.eq(firstName).or(ACTOR.LAST_NAME.eq(lastName)))
            .fetchInto(Actor.class);
    }

    public List<Actor> findByActorIdIn(List<Long> ids) {
        return dslContext.selectFrom(ACTOR)
            // DSL.noCondition()으로 `where false` 구문을 없앨 수 있다.
            .where(inIfNotEmpty(ACTOR.ACTOR_ID, ids))
            .fetchInto(Actor.class);
    }

    public List<ActorFilmography> findActorFilmography(ActorFilmographySearchCondition searchCondition) {

        final JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;
        final JFilm FILM = JFilm.FILM;

        return dslContext.select(
                row(ACTOR.fields()).as("actor"),
                row(FILM.fields()).as("film")
            ).from(FILM_ACTOR)
            .join(FILM)
            .on(FILM_ACTOR.FILM_ID.eq(FILM.FILM_ID))
            .join(ACTOR)
            .on(FILM_ACTOR.ACTOR_ID.eq(ACTOR.ACTOR_ID))
            // 캐스캐이딩된 조건이 나오니 주의
            // .fetchInto(ActorFilmography.class)
            .where(
                containsIfNotBlank(ACTOR.FIRST_NAME.concat(" ").concat(ACTOR.LAST_NAME), searchCondition.getActorName()),
                containsIfNotBlank(FILM.TITLE, searchCondition.getFilmTitle())
            )
            .fetchGroups(
                // 애플리케이션 수준에서 그룹핑
                // 나열하는 순서가 중요하다.
                // `actor → film`은 배우별로 영화 목록이지만(= Map<Actor, List<Film>>),
                // `film → actor`으로 나열하면 영화별로 등장한 배우 목록이 된다(= Map<Film, List<Actor>>).
                record -> record.get("actor", Actor.class),
                record -> record.get("film", Film.class)
            )
            .entrySet()
            .stream()
            .map(entry -> new ActorFilmography(entry.getKey(), entry.getValue()))
            .toList();
    }

    public Actor saveByDao(Actor actor) {
        // 이때 PK가 actor 객체에 추가됨
        // https://github.com/jOOQ/jOOQ/issues/2536
        // jOOQ의 DAO를 통해 insert()할 때, DB가 생성한 IDENTITY(예: AUTO_INCREMENT) 값이 POJO 객체에 자동 반영되지 않는다.
        // @Lukas Eder - 필요성은 이해하지만, 자동으로 POJO를 수정하는 것은 예상치 못한 부작용(side-effect) 이 될 수 있음.
        // @walec51 - dao.insertAndFetch(pojo) 형태의 메서드를 생성하면 좋겠다는 의견.
        // 이 메서드는 ID뿐 아니라 trigger로 생성된 다른 값들도 가져올 수 있음.
        // @chuchiperriman(반론) - insertAndFetch는 내부적으로 두 번의 쿼리를 실행하므로 성능상 다름.
        // 자신이 원한 건 단순히 insert() 호출 후 POJO의 ID만 반영되는 것.
        // 대부분의 SQL dialect에서는 getGeneratedKeys() 또는 RETURNING 구문을 통해 추가 round-trip 없이 ID를 가져올 수 있음.
        // INSERT INTO users (name) VALUES ('Alice') RETURNING id, created_at;
        actorDao.insert(actor);
        return actor;
    }

    public ActorRecord saveByRecord(Actor actor) {
        ActorRecord record = dslContext.newRecord(ACTOR, actor);
        record.insert();
        return record;
    }

    public Long saveWithReturningPkOnly(Actor actor) {
        // RETURNING 구문이 해당 dialect에서 지원되지 않을 경우 SELECT를 추가로 실행한다.
        // PostgresQL, MariaDB 등은 RETURNING 구문을 지원한다
        return dslContext.insertInto(
                ACTOR,
                ACTOR.FIRST_NAME,
                ACTOR.LAST_NAME
            )
            .values(
                actor.getFirstName(),
                actor.getLastName()
            )
            .returningResult(ACTOR.ACTOR_ID)
            .fetchOneInto(Long.class);
    }

    public Actor saveWithReturning(Actor actor) {
        return dslContext.insertInto(
                ACTOR,
                ACTOR.FIRST_NAME,
                ACTOR.LAST_NAME
            )
            .values(
                actor.getFirstName(),
                actor.getLastName()
            )
            .returning(ACTOR.fields())
            .fetchOneInto(Actor.class);
    }

    public List<Actor> bulkInsertWithRows(List<Actor> actors) {
        return dslContext.insertInto(
                ACTOR,
                ACTOR.FIRST_NAME,
                ACTOR.LAST_NAME
            )
            .valuesOfRows(
                actors
                    .stream()
                    .map(actor -> row(
                        actor.getFirstName(),
                        actor.getLastName()
                    ))
                    .toList()
            )
            .returning(ACTOR.fields())
            .fetchInto(Actor.class);
    }

    public void update(Actor actor) {
        actorDao.update(actor);
    }

    public Actor filmByActorId(Long actorId) {
        return actorDao.findById(actorId);
    }

    public int updateWithDto(Long newActorId, ActorUpdateRequest request) {
        Field<String> firstName = StringUtils.hasText(request.getFirstName())
            ? val(request.getFirstName())
            : noField(ACTOR.FIRST_NAME);
        Field<String> lastName = StringUtils.hasText(request.getLastName())
            ? val(request.getLastName())
            : noField(ACTOR.LAST_NAME);

        return dslContext.update(ACTOR)
            .set(ACTOR.FIRST_NAME, firstName)
            .set(ACTOR.LAST_NAME, lastName)
            .where(ACTOR.ACTOR_ID.eq(newActorId))
            .execute();
    }

    public int updateWithRecord(Long newActorId, ActorUpdateRequest request) {
        ActorRecord fetched = dslContext.fetchOne(ACTOR, ACTOR.ACTOR_ID.eq(newActorId));

        if (isNull(fetched)) {
            return 0;
        }

        if (StringUtils.hasText(request.getFirstName())) {
            fetched.setFirstName(request.getFirstName());
        }
        if (StringUtils.hasText(request.getLastName())) {
            fetched.setLastName(request.getLastName());
        }

        return dslContext.update(ACTOR)
            .set(fetched)
            // .where(ACTOR.ACTOR_ID.eq(newActorId))
            .execute();
        // return fetched.store();
    }

    public int delete(Long newActorId) {
        // actorDao.deleteById(newActorId);
        return dslContext.deleteFrom(ACTOR)
            .where(ACTOR.ACTOR_ID.eq(newActorId))
            .execute();
    }

    public int deleteWithRecord(Long newActorId) {
        ActorRecord fetched = dslContext.fetchOne(ACTOR, ACTOR.ACTOR_ID.eq(newActorId));

        if (isNull(fetched)) {
            return 0;
        }

        return fetched.delete();
    }

    public ActorRecord findRecordByActorId(Long actorId) {
        return dslContext.fetchOne(ACTOR, ACTOR.ACTOR_ID.eq(actorId));
    }
}
