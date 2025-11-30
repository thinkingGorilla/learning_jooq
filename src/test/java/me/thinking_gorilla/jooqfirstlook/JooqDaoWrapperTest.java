package me.thinking_gorilla.jooqfirstlook;

import me.thinking_gorilla.jooqfirstlook.film.FilmHasARepository;
import me.thinking_gorilla.jooqfirstlook.film.FilmIsARepository;
import org.jooq.generated.tables.pojos.Film;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.context.annotation.ComponentScan.*;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@JooqTest(includeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = {FilmIsARepository.class, FilmHasARepository.class}))
public class JooqDaoWrapperTest {

    @Autowired
    FilmIsARepository isARepository;

    @Autowired
    FilmHasARepository hasARepository;

    @Test
    @DisplayName("DAO 상속 구현체 테스트")
    void test_when_using_dao_by_inheritance() {
        Film byId = isARepository.findById(10L);
        assertThat(byId).isNotNull();
    }

    @Test
    @DisplayName("DAO 상속 구현체로 범위 조건을 적용한 데이터 가져오기")
    void test_range_data_by_inheritance() {
        List<Film> films = isARepository.fetchRangeOfJLanguageId(100L, 180L);
        assertThat(films)
                .allSatisfy(film -> assertThat(film.getLength()).isBetween(100, 180));
    }

    @Test
    @DisplayName("DAO 컴포지션 구현체 테스트 1")
    void test_when_using_dao_by_composition() {
        Film byId = hasARepository.findById(10L);
        assertThat(byId).isNotNull();
    }

    @Test
    @DisplayName("DAO 컴포지션 구현체로 범위 조건을 적용한 데이터 가져오기")
    void test_range_data_by_composition() {
        List<Film> films = hasARepository.findByRangeBetween(100, 180);
        assertThat(films)
                .allSatisfy(film -> assertThat(film.getLength()).isBetween(100, 180));
    }
}
