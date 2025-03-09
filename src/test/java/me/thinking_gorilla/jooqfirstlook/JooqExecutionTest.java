package me.thinking_gorilla.jooqfirstlook;

import org.jooq.DSLContext;
import org.jooq.generated.tables.JActor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;

// https://docs.spring.io/spring-boot/reference/data/sql.html#data.sql.jooq
@JooqTest
class JooqExecutionTest {

    @Autowired
    DSLContext dslContext;

    @Test
    void test() {
        dslContext
                .selectFrom(JActor.ACTOR)
                .limit(10)
                .fetch();
    }
}
