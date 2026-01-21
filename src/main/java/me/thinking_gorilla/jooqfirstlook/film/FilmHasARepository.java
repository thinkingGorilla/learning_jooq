package me.thinking_gorilla.jooqfirstlook.film;

import org.jooq.*;
import org.jooq.generated.tables.*;
import org.jooq.generated.tables.daos.*;
import org.jooq.generated.tables.pojos.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

import static org.jooq.impl.DSL.*;

// 컴포지트 패턴을 통해 DAO 사용
@Repository
public class FilmHasARepository {

    private final DSLContext dslContext;
    private final JFilm FILM = JFilm.FILM;
    private final FilmDao dao;

    public FilmHasARepository(Configuration configuration, DSLContext dslContext) {
        this.dao = new FilmDao(configuration);
        this.dslContext = dslContext;
    }

    public Film findById(Long id) {
        return dao.fetchOneByJFilmId(id);
    }

    public List<Film> findByRangeBetween(Integer from, Integer to) {
        return dao.fetchRangeOfJLength(from, to);
    }

    public SimpleFilmInfo findSimpleFilmInfoById(Long id) {
        return dslContext
            .select(FILM.FILM_ID, FILM.TITLE, FILM.DESCRIPTION)
            .from(FILM)
            .where(FILM.FILM_ID.eq(id))
            .fetchOneInto(SimpleFilmInfo.class);
    }

    public List<FilmWithActor> findFilmWithActors(Pageable pageable) {
        JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;
        JActor ACTOR = JActor.ACTOR;

        return dslContext
            .select(
                row(FILM.fields()),
                row(FILM_ACTOR.fields()),
                row(ACTOR.fields())
            )
            .from(FILM_ACTOR)
            .join(FILM)
            .on(FILM_ACTOR.FILM_ID.eq(FILM.FILM_ID))
            .join(ACTOR)
            .on(FILM_ACTOR.ACTOR_ID.eq(ACTOR.ACTOR_ID))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchInto(FilmWithActor.class);
    }
}
