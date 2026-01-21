package me.thinking_gorilla.jooqfirstlook.film;

import java.math.*;

public record FilmRentalSummary(
    Long filmId,
    String title,
    BigDecimal averageRentalDuration
) {
}
