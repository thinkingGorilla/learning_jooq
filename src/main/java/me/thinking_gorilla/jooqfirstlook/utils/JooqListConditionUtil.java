package me.thinking_gorilla.jooqfirstlook.utils;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Objects;

public class JooqListConditionUtil {

    public static <T> Condition inIfNotEmpty(Field<T> field, List<T> ids) {
        return Objects.isNull(ids) || ids.isEmpty() ? DSL.noCondition() : field.in(ids);
    }

    public static Condition containsIfNotBlank(Field<String> field, String value) {
        return Objects.isNull(value) || value.isBlank() ? DSL.noCondition() : field.like("%" + value + "%");
    }
}
