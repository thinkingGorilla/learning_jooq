package me.thinking_gorilla;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.*;

import java.io.*;
import java.util.*;

@Getter
@Setter
@Embeddable
public class FilmCategoryId implements Serializable {

    private static final long serialVersionUID = -3650065536069802683L;

    @Column(name = "film_id")
    private Long filmId;

    @Column(name = "category_id")
    private Long categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FilmCategoryId entity = (FilmCategoryId) o;
        return Objects.equals(this.filmId, entity.filmId) &&
            Objects.equals(this.categoryId, entity.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filmId, categoryId);
    }
}
