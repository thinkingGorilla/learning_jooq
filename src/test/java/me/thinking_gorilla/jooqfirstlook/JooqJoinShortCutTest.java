package me.thinking_gorilla.jooqfirstlook;

import me.thinking_gorilla.jooqfirstlook.config.*;
import me.thinking_gorilla.jooqfirstlook.film.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.jooq.*;
import org.springframework.context.annotation.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.context.annotation.ComponentScan.*;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@JooqTest(includeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = FilmRepository.class))
@Import(JooqConfig.class)
public class JooqJoinShortCutTest {

    @Autowired
    FilmRepository filmRepository;

    @Test
    @DisplayName("implicitPathJoin_테스트")
    void implicitPathJoin_테스트() {
        List<FilmWithActor> original = filmRepository.findFilmWithActors(PageRequest.of(1, 10));
        List<FilmWithActor> implicit = filmRepository.findFilmWithActorsImplicitPathJoin(PageRequest.of(1, 10));

        assertThat(original)
            .usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(implicit);
    }

    @Test
    @DisplayName("explicitPathJoin_테스트")
    void explicitPathJoin_테스트() {
        List<FilmWithActor> original = filmRepository.findFilmWithActors(PageRequest.of(1, 10));
        List<FilmWithActor> implicit = filmRepository.findFilmWithActorsExplicitPathJoin(PageRequest.of(1, 10));

        assertThat(original)
            .usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(implicit);
    }
}
