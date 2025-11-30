package me.thinking_gorilla.jooqfirstlook;

import me.thinking_gorilla.jooqfirstlook.film.FilmPriceSummary;
import me.thinking_gorilla.jooqfirstlook.film.FilmRentalSummary;
import me.thinking_gorilla.jooqfirstlook.film.FilmRepository;
import org.jooq.generated.tables.pojos.Film;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.context.annotation.ComponentScan.Filter;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@JooqTest(includeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = FilmRepository.class))
public class JooqSubqueryTest {

    @Autowired
    FilmRepository repository;

    @Test
    @DisplayName(
        """
        영화별 대여료가
        1.0 이하면 Cheap,
        3.0 이하면 Moderate,
        그 이상이면 Expensive로 분류하고,
        각 영화의 총 재고 수를 조회한다.
        """
    )
    void test_when_scalar_subquery_is_used() {
        String filmTitle = "EGG";
        List<FilmPriceSummary> summaryList = repository.findFilmPriceSummaryByFilmTitle(filmTitle);
        assertEquals(3, summaryList.size());
    }

    @Test
    @DisplayName("평균 대여 기간이 가장 긴 영화부터 정렬해서 조회한다.")
    void test_when_inline_view_is_used() {
        String filmTitle = "EGG";
        List<FilmRentalSummary> summaryList = repository.findFilmRentalSummaryByFilmTitle(filmTitle);
        assertEquals(3, summaryList.size());
        BigDecimal largest = summaryList.get(0).averageRentalDuration();
        BigDecimal smallest = summaryList.get(2).averageRentalDuration();
        assertTrue(largest.compareTo(smallest) > 0);
    }

    @Test
    @DisplayName("대여한 기록이 있는 영화가 있는 영화만 조회")
    void test_when_nested_subquery_is_used() {
        String filmTitle = "EGG";
        List<Film> filmList= repository.findRentedFilmByTitle(filmTitle);
        assertEquals(3, filmList.size());
    }
}
