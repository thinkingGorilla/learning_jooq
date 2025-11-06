package me.thinking_gorilla.jooqfirstlook.film;

import lombok.Getter;

import java.math.BigDecimal;

public record FilmPriceSummary(
    Long filmId,
    String title,
    BigDecimal rentalRate,
    PriceCategory priceCategory,
    Long totalInventory
) {

    @Getter
    public enum PriceCategory {
        CHEAP("Cheap"),
        MODERATE("Moderate"),
        EXPENSIVE("Expensive");

        private final String code;

        PriceCategory(String code) {
            this.code = code;
        }

        public static PriceCategory findByCode(String code) {
            for (PriceCategory value : PriceCategory.values()) {
                if (value.code.equalsIgnoreCase(code)) {
                    return value;
                }
            }
            return null;
        }
    }
}