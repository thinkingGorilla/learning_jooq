package me.thinking_gorilla.jooqfirstlook;

import me.thinking_gorilla.jooqfirstlook.actor.ActorRepository;
import me.thinking_gorilla.jooqfirstlook.actor.ActorUpdateRequest;
import me.thinking_gorilla.jooqfirstlook.config.JooqConfig;
import org.jooq.generated.tables.pojos.Actor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.context.annotation.ComponentScan.Filter;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@JooqTest(includeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = {JooqConfig.class, ActorRepository.class}))
public class JooqUpdateTest {

    @Autowired
    ActorRepository actorRepository;

    @Test
    @Transactional
    @DisplayName("pojo를 사용한 update")
    void update_with_pojo() {
        // given
        Actor newActor = new Actor();
        newActor.setFirstName("Tom");
        newActor.setLastName("Cruise");

        Actor actor = actorRepository.saveWithReturning(newActor);

        // when
        actor.setFirstName("Suri");
        actorRepository.update(actor);

        // then
        Actor updatedActor = actorRepository.filmByActorId(actor.getActorId());
        assertEquals("Suri", updatedActor.getFirstName());
    }

    @Test
    @Transactional
    @DisplayName("일부 필드만 update - DTO 활용")
    void update_partial_field_with_pojo() {
        // given
        Actor newActor = new Actor();
        newActor.setFirstName("Tom");
        newActor.setLastName("Cruise");

        Long newActorId = actorRepository.saveWithReturningPkOnly(newActor);
        var request = ActorUpdateRequest.builder()
                .firstName("Suri")
                .build();

        // when
        int result = actorRepository.updateWithDto(newActorId, request);

        // then
        assertEquals(1, result);
    }

    @Test
    @Transactional
    @DisplayName("일부 필드만 update - DTO 활용")
    void update_partial_field_with_record() {
        // given
        Actor newActor = new Actor();
        newActor.setFirstName("Tom");
        newActor.setLastName("Cruise");

        Long newActorId = actorRepository.saveWithReturningPkOnly(newActor);
        var request = ActorUpdateRequest.builder()
                .firstName("Suri")
                .build();

        // when
        int result = actorRepository.updateWithRecord(newActorId, request);

        // then
        assertEquals(1, result);
    }

    @Test
    @Transactional
    @DisplayName("delete 예제")
    void delete() {
        // given
        Actor newActor = new Actor();
        newActor.setFirstName("Tom");
        newActor.setLastName("Cruise");

        Long newActorId = actorRepository.saveWithReturningPkOnly(newActor);
        var request = ActorUpdateRequest.builder()
                .firstName("Suri")
                .build();

        // when
        int result = actorRepository.delete(newActorId);

        // then
        assertEquals(1, result);
    }

    @Test
    @Transactional
    @DisplayName("delete 예제 - with active record")
    void delete_with_active_record() {
        // given
        Actor newActor = new Actor();
        newActor.setFirstName("Tom");
        newActor.setLastName("Cruise");

        Long newActorId = actorRepository.saveWithReturningPkOnly(newActor);

        // when
        int result = actorRepository.deleteWithRecord(newActorId);

        // then
        assertEquals(1, result);
    }
}
