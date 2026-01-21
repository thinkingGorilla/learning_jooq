package me.thinking_gorilla.jooqfirstlook.film;

import org.jooq.*;
import org.jooq.generated.tables.*;
import org.jooq.generated.tables.daos.*;
import org.jooq.generated.tables.pojos.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

import static org.jooq.impl.DSL.*;

// DAO 상속을 통해서 사용
// 상속을 사용하기 때문에 자동생성되었지만 사용하지 않는 모든 메서드가 외부에 노출된다.
@Repository
public class FilmIsARepository extends FilmDao {

    private final DSLContext dslContext;
    private final JFilm FILM = JFilm.FILM;

    public FilmIsARepository(Configuration configuration, DSLContext dslContext) {
        super(configuration);
        this.dslContext = dslContext;
    }

    public Film findById(Long id) {
        return super.findById(id);
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
