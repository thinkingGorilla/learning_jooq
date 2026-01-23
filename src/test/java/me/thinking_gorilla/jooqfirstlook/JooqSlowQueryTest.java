package me.thinking_gorilla.jooqfirstlook;

import me.thinking_gorilla.jooqfirstlook.config.*;
import org.jooq.*;
import org.jooq.impl.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.jooq.*;
import org.springframework.boot.test.system.*;
import org.springframework.context.annotation.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.impl.DSL.dual;

@ExtendWith(OutputCaptureExtension.class)
@JooqTest
@Import(JooqConfig.class)
public class JooqSlowQueryTest {

    @Autowired
    DSLContext dslContext;

    @Test
    @DisplayName("SLOW 쿼리 탐지테스트")
    void 슬로우쿼리_탐지_테스트(CapturedOutput output) {
        // given & when
        dslContext.select(DSL.field("SLEEP(4)"))
            .from(dual())
            .execute();

        // then
        assertThat(output.getOut()).contains("경고: jOOQ로 실행된 쿼리 중 3초 이상 실행된 쿼리가 있습니다.");
    }
}
