package me.thinking_gorilla.jooqfirstlook.config.converter;

import me.thinking_gorilla.jooqfirstlook.film.FilmPriceSummary.PriceCategory;
import org.jooq.impl.EnumConverter;

public class PriceCategoryConverter extends EnumConverter<String, PriceCategory> {

    public PriceCategoryConverter() {
        // PriceCategory::getCode는 (PriceCategory ) -> p.getCode()와 동일하다.
        // 즉 입력이 PriceCategory이고 출력은 String이다.
        // 따라서 Function<? super U, ? extends T> to를 만족한다.
        // e.g.
        // String::length → Function<String, Integer>
        // System.out::println → Consumer<String>
        // Math::random → Supplier<Double>
        // e.g.
        // Predicate<T> → 함수형 인터페이스
        // T -> boolean → 함수 디스크립터
        // ClassName::static_method → 메서드 참조
        super(String.class, PriceCategory.class, PriceCategory::getCode);
    }
}
