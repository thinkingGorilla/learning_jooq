package me.thinking_gorilla.jooqfirstlook.film;

import java.math.BigDecimal;

public record FilmPriceSummary(
    Long filmId,
    String title,
    BigDecimal rentalRate,
    BigDecimal priceCategory,
    Long totalInventory
) {}