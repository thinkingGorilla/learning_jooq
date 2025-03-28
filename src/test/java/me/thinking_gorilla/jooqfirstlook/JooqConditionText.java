package me.thinking_gorilla.jooqfirstlook;

import me.thinking_gorilla.jooqfirstlook.actor.ActorFilmography;
import me.thinking_gorilla.jooqfirstlook.actor.ActorFilmographySearchCondition;
import me.thinking_gorilla.jooqfirstlook.actor.ActorRepository;
import org.jooq.generated.tables.pojos.Actor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

// @formatter:off
@JooqTest(
    includeFilters = @ComponentScan.Filter(
    type = ASSIGNABLE_TYPE,
        classes = {ActorRepository.class}
    )
)
// @formatter:on
public class JooqConditionText {

    @Autowired
    private ActorRepository actorRepository;

    @Test
    @DisplayName("and 조건 검색 - firstName과 lastName이 일치하는 배우 조회")
    void and_case_1() {
        // given
        String firstName = "ED";
        String lastName = "CHASE";

        // when
        List<Actor> actors = actorRepository.findByFirstNameAndLastName(firstName, lastName);

        // then
        assertThat(actors).hasSize(1);
    }

    @Test
    @DisplayName("or 조건 검색 - firstName 또는 lastName이 일치하는 배우 조회")
    void or_case_1() {
        // given
        String firstName = "ED";
        String lastName = "CHASE";

        // when
        List<Actor> actors = actorRepository.findByFirstNameOrLastName(firstName, lastName);

        // then
        assertThat(actors).hasSizeGreaterThan(1);
    }

    @Test
    @DisplayName("in 절 - 동적 조건 검색")
    void in_case_1() {
        // given
        List<Long> ids = List.of(1L, 2L, 3L);

        // when
        List<Actor> actors = actorRepository.findByActorIdIn(ids);

        // then
        assertThat(actors).hasSizeGreaterThan(1);
    }

    @Test
    @DisplayName("in 절 - 동적 조건 검색 - empty list인 경우 제외")
    void in_case_2() {
        // given
        List<Long> ids = List.of(1L, 2L, 3L);

        // when
        // List<Actor> actors = actorRepository.findByActorIdIn(null);
        List<Actor> actors = actorRepository.findByActorIdIn(List.of());

        // then
        assertThat(actors).hasSizeGreaterThan(1);
    }

    @Test
    @DisplayName("다중 조건 검색 - 배우 이름으로 조회")
    void multi_condition_case_1() {
        // given
        var searchCondition = ActorFilmographySearchCondition
                .builder()
                .actorName("LOLLOBRIGIDA")
                .build();

        // when
        List<ActorFilmography> filmographies = actorRepository.findActorFilmography(searchCondition);

        // then
        assertThat(filmographies).hasSize(1);
    }

    @Test
    @DisplayName("다중 조건 검색 - 배우 이름과 영화 제목으로 조회")
    void multi_condition_case_2() {
        // given
        var searchCondition = ActorFilmographySearchCondition
                .builder()
                .actorName("LOLLOBRIGIDA")
                .filmTitle("COMMANDMENTS EXPRESS")
                .build();

        // when
        List<ActorFilmography> filmographies = actorRepository.findActorFilmography(searchCondition);

        // then
        assertThat(filmographies).hasSize(1);
        assertThat(filmographies.get(0).films()).hasSize(1);
    }
}
