package me.thinking_gorilla.jooqfirstlook.film;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmRepository repository;

    public Page<FilmWithActor> getFilmWithActors(Pageable pageable) {
        return new PageImpl<>(repository.findFilmWithActors(pageable));
    }
}
