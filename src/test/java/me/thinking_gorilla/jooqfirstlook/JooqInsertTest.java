package me.thinking_gorilla.jooqfirstlook;

import me.thinking_gorilla.jooqfirstlook.actor.ActorRepository;
import org.jooq.generated.tables.pojos.Actor;
import org.jooq.generated.tables.records.ActorRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.context.annotation.ComponentScan.Filter;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@JooqTest(includeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = ActorRepository.class))
public class JooqInsertTest {

    @Autowired
    ActorRepository actorRepository;

    @Test
    @DisplayName("자동생성된 DAO를 통한 insert")
    @Transactional
    void insert_dao() {
        Actor actor = new Actor();
        actor.setFirstName("Taehwan");
        actor.setLastName("KIM");
        actor.setLastUpdate(LocalDateTime.now());

        actorRepository.saveByDao(actor);

        assertNotNull(actor.getActorId());
    }

    @Test
    @DisplayName("ActiveRecord를 통한 insert")
    @Transactional
    void insert_by_record() {
        Actor actor = new Actor();
        actor.setFirstName("Taehwan");
        actor.setLastName("KIM");
        // actor.setLastUpdate(LocalDateTime.now());

        ActorRecord actorRecord = actorRepository.saveByRecord(actor);

        assertNotNull(actorRecord.getActorId());
        assertNull(actor.getActorId());
    }

    @Test
    @DisplayName("SQL 실행 후 PK만 반환")
    @Transactional
    void insert_with_returning_pk() {
        Actor actor = new Actor();
        actor.setFirstName("Taehwan");
        actor.setLastName("KIM");
        // actor.setLastUpdate(LocalDateTime.now());

        Long savedId = actorRepository.saveWithReturningPkOnly(actor);

        assertNotNull(savedId);
    }

    @Test
    @DisplayName("SQL 실행 후 해당 ROW 반환")
    @Transactional
    void insert_with_returining() {
        Actor actor = new Actor();
        actor.setFirstName("Taehwan");
        actor.setLastName("KIM");
        // actor.setLastUpdate(LocalDateTime.now());

        Actor saved = actorRepository.saveWithReturning(actor);

        assertNotNull(saved);
    }

    @Test
    @DisplayName("Bulk insert 예제")
    @Transactional
    void bulk_insert() {
        Actor actor_1 = new Actor();
        actor_1.setFirstName("Taehwan");
        actor_1.setLastName("KIM");

        Actor actor_2 = new Actor();
        actor_2.setFirstName("World");
        actor_2.setLastName("Hello");

        List<Actor> result = actorRepository.bulkInsertWithRows(List.of(actor_1, actor_2));

        assertEquals(2, result.size());
    }
}
