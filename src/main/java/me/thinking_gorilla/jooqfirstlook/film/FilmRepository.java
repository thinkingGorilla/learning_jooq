package me.thinking_gorilla.jooqfirstlook.film;

import lombok.RequiredArgsConstructor;
import me.thinking_gorilla.jooqfirstlook.config.converter.PriceCategoryConverter;
import me.thinking_gorilla.jooqfirstlook.film.FilmPriceSummary.PriceCategory;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.generated.tables.*;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.impl.EnumConverter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static org.jooq.generated.tables.JInventory.*;
import static org.jooq.generated.tables.JRental.*;
import static org.jooq.impl.DSL.*;

@RequiredArgsConstructor
@Repository
public class FilmRepository {

    private final DSLContext dslContext;
    private final JFilm FILM = JFilm.FILM;

    public Film findById(Long id) {
        return dslContext
                .select(FILM.fields())
                .from(FILM)
                .where(FILM.FILM_ID.eq(id))
                .fetchOneInto(Film.class);
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

    public List<FilmPriceSummary> findFilmPriceSummaryByFilmTitle(String filmTitle) {
        return dslContext
            .select(
                FILM.FILM_ID,
                FILM.TITLE,
                FILM.RENTAL_RATE,
                case_()
                    .when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(1.0)), "Cheap")
                    .when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(3.0)), "Moderate")
                .else_("Expensive")
                .as("price_category")
                // .convert(new PriceCategoryConverter()),
                // .convert(new EnumConverter<>(String.class, PriceCategory.class)),
                // .convert(PriceCategory.class, PriceCategory::findByCode, PriceCategory::getCode),
                // if conversions only happen from the database type
                .convertFrom(PriceCategory.class, PriceCategory::findByCode),
                // if when you only need to convert from the user type (the U type) to the database type
                // .convertTo(PriceCategory.class, PriceCategory::getCode)
                selectCount()
                    .from(INVENTORY)
                    .where(INVENTORY.FILM_ID.eq(FILM.FILM_ID))
                    .asField("total_inventory")

            )
            .from(FILM)
            .where(FILM.TITLE.like("%" + filmTitle + "%"))
            .fetchInto(FilmPriceSummary.class);
    }

    public List<FilmRentalSummary> findFilmRentalSummaryByFilmTitle(String filmTitle) {
        var inlineViewSubquery = select(
                INVENTORY.FILM_ID,
                avg(localDateTimeDiff(DatePart.DAY, RENTAL.RENTAL_DATE, RENTAL.RETURN_DATE)).as("average_rental_duration")
            )
            .from(RENTAL)
            .join(INVENTORY)
            .on(RENTAL.INVENTORY_ID.eq(INVENTORY.INVENTORY_ID))
            .where(RENTAL.RENTAL_DATE.isNotNull())
            .groupBy(INVENTORY.FILM_ID)
            .asTable("rental_duration_info");

        return dslContext
            .select(
                FILM.FILM_ID,
                FILM.TITLE,
                inlineViewSubquery.field("average_rental_duration")
            )
            .from(FILM)
            .join(inlineViewSubquery)
            .on(FILM.FILM_ID.eq(inlineViewSubquery.field(INVENTORY.FILM_ID)))
            .where(FILM.TITLE.like("%" + filmTitle + "%"))
            // .orderBy(inlineViewSubquery.field("average_rental_duration").desc())
            .orderBy(field(name("average_rental_duration")).desc())
            .fetchInto(FilmRentalSummary.class);
    }

    public List<Film> findRentedFilmByTitle(String filmTitle) {
        return dslContext
            .selectFrom(FILM)
            .whereExists(
                selectOne()
                    .from(INVENTORY)
                    .join(RENTAL)
                    .on(RENTAL.INVENTORY_ID.eq(INVENTORY.INVENTORY_ID))
                    .where(INVENTORY.FILM_ID.eq(FILM.FILM_ID))
            )
            .and(FILM.TITLE.like("%" + filmTitle + "%"))
            .fetchInto(Film.class);
    }
}
