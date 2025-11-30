package me.thinking_gorilla.jooqfirstlook;

import me.thinking_gorilla.jooqfirstlook.actor.ActorRepository;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.ActorRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import static org.jooq.generated.tables.JActor.ACTOR;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@JooqTest(includeFilters = @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = ActorRepository.class))
public class JooqActiveRecordTest {

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    DSLContext dslContext;

    @Test
    @DisplayName("액티브 레코드 select 예제")
    @Transactional
    void active_record_with_select() {
        // given
        Long actorId = 1L;

        // when
        ActorRecord found = actorRepository.findRecordByActorId(actorId);

        // then
        assertNotNull(found);
        assertEquals(1, found.getActorId());
    }

    @Test
    @DisplayName("액티브 레코드 refresh 예제")
    @Transactional
    void active_record_with_refresh() {
        // given
        Long actorId = 1L;
        ActorRecord found = actorRepository.findRecordByActorId(actorId);

        // when & then
        found.setFirstName(null);
        assertEquals(null, found.getFirstName());

        // when & then
        found.refresh();
        // 특정 컬럼만 select 할 수도 있다.
        // found.refresh(ACTOR.FIRST_NAME);
        assertNotNull(found.getFirstName());
    }

    @Test
    @DisplayName("액티브 레코드 store 예제")
    @Transactional
    void active_record_with_store() {
        // given
        ActorRecord actorRecord = dslContext.newRecord(ACTOR);
        actorRecord.setFirstName("Taehwan");
        actorRecord.setLastName("KIM");

        // when
        actorRecord.store(); // manage changed, fetched field so that insert or update can be performed
        // actorRecord.insert(); // insert only
        actorRecord.refresh();

        // then
        assertNotNull(actorRecord.getActorId());
        assertNotNull(actorRecord.getLastUpdate());
    }

    @Test
    @DisplayName("액티브 레코드 update 예제")
    @Transactional
    void active_record_with_update() {
        // given
        Long actorId = 1L;
        String newName = "Taehwan";
        ActorRecord found = actorRepository.findRecordByActorId(actorId);

        // when
        found.setFirstName(newName);
        found.update(); // update only

        assertEquals(newName, found.getFirstName());
    }

    @Test
    @DisplayName("액티브 레코드 delete 예제")
    @Transactional
    void active_record_with_delete() {
        // given
        ActorRecord actorRecord = dslContext.newRecord(ACTOR);
        actorRecord.setFirstName("Taehwan");
        actorRecord.setLastName("KIM");
        actorRecord.store();

        // when & then
        assertEquals(1, actorRecord.delete());
    }
}
