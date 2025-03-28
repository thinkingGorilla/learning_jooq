package me.thinking_gorilla.jooqfirstlook.actor;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JActor;
import org.jooq.generated.tables.daos.ActorDao;
import org.jooq.generated.tables.pojos.Actor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
