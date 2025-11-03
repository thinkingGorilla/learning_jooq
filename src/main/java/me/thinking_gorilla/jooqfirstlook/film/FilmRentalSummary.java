package me.thinking_gorilla.jooqfirstlook.film;

import java.math.BigDecimal;

public record FilmRentalSummary(
    Long filmId,
    String title,
    BigDecimal averageRentalDuration
) {}