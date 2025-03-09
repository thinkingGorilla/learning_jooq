package me.thinking_gorilla.jooqfirstlook.config;

import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

    @Bean
    public DefaultConfigurationCustomizer jooqConfigurationCustomizer() {
        return config -> config.settings()
                // SELECT * FROM my_schema.users; (renderSchema: true)
                // SELECT * FROM users; (renderSchema: false)
                //
                // renderSchema를 false로 설정하는 이유
                //
                // 1. 멀티 테넌트 환경에서 특정 스키마에 종속되지 않도록 하기 위해
                //  - my_schema.users 대신 users만 사용하면, 다른 스키마에서 실행할 수 있음.
                //
                // 2. 테스트 환경과 운영 환경에서 스키마가 다를 경우 유연하게 대응하기 위해
                //  - 예: 로컬에서는 test_schema.users, 운영에서는 prod_schema.users를 사용하는 경우.
                //
                // 3. JOOQ가 생성하는 SQL을 더 짧고 간결하게 만들기 위해
                //  - SQL이 스키마 없이 실행 가능하다면, renderSchema: false로 설정하여 불필요한 길이를 줄일 수 있음.
                .withRenderSchema(false);
    }
}
