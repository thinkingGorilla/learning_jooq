package me.thinking_gorilla.jooqfirstlook.actor;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JActor;
import org.jooq.generated.tables.JFilm;
import org.jooq.generated.tables.JFilmActor;
import org.jooq.generated.tables.daos.ActorDao;
import org.jooq.generated.tables.pojos.Actor;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.thinking_gorilla.jooqfirstlook.utils.JooqListConditionUtil.containsIfNotBlank;
import static me.thinking_gorilla.jooqfirstlook.utils.JooqListConditionUtil.inIfNotEmpty;

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
                        DSL.row(ACTOR.fields()).as("actor"),
                        DSL.row(FILM.fields()).as("film")
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
}
