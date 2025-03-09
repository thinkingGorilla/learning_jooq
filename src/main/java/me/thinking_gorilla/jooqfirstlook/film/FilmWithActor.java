package me.thinking_gorilla.jooqfirstlook.film;

import lombok.RequiredArgsConstructor;
import org.jooq.generated.tables.pojos.Actor;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.generated.tables.pojos.FilmActor;

@RequiredArgsConstructor
public final class FilmWithActor {

    private final Film film;
    private final FilmActor filmActor;
    private final Actor actor;

    public String getTitle() {
        return film.getTitle();
    }

    public String getActorFullName() {
        return actor.getFirstName() + " " + actor.getLastName();
    }

    public Long getFilmId() {
        return film.getFilmId();
    }
}
