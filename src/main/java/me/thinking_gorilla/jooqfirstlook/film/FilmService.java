package me.thinking_gorilla.jooqfirstlook.film;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmRepository repository;

    public Page<FilmWithActor> getFilmWithActors(Pageable pageable) {
        return new PageImpl<>(repository.findFilmWithActors(pageable));
    }
}
