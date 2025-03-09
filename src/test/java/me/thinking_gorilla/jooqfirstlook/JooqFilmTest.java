package me.thinking_gorilla.jooqfirstlook;

import me.thinking_gorilla.jooqfirstlook.film.FilmRepository;
import me.thinking_gorilla.jooqfirstlook.film.FilmService;
import me.thinking_gorilla.jooqfirstlook.film.FilmWithActor;
import me.thinking_gorilla.jooqfirstlook.film.SimpleFilmInfo;
import org.jooq.generated.tables.pojos.Film;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.context.annotation.ComponentScan.Filter;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@JooqTest(
        includeFilters = @Filter(
                type = ASSIGNABLE_TYPE,
                classes = {FilmRepository.class, FilmService.class}
        )
)
public class JooqFilmTest {

    @Autowired
    FilmRepository repository;

    @Autowired
    FilmService service;

    @Test
    @DisplayName("1) 영화정보 조회")
    void get_film_test() {
        Film byId = repository.findById(1L);
        assertThat(byId).isNotNull();
    }

    @Test
    @DisplayName("2) 영화정보 간략 조회")
    void get_simple_film_info_test() {
        SimpleFilmInfo byId = repository.findSimpleFilmInfoById(1L);
        assertThat(byId).hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("3) 영화와 영화에 출력한 배우 정보를 페이징하여 조회")
    void get_film_with_actors_test() {
        Page<FilmWithActor> paged = service.getFilmWithActors(PageRequest.of(1, 10));
        assertThat(paged).hasSize(10);
    }
}
