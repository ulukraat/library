    package com.library.config;

    import org.springframework.test.context.DynamicPropertyRegistry;
    import org.springframework.test.context.DynamicPropertySource;
    import org.testcontainers.containers.PostgreSQLContainer;
    import org.testcontainers.junit.jupiter.Container;
    import org.testcontainers.junit.jupiter.Testcontainers;

    @Testcontainers
    public abstract class DatabaseConfigTest {

        @Container
        protected static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");

        @DynamicPropertySource
        static void configure(DynamicPropertyRegistry registry) {
            registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES::getUsername);
            registry.add("spring.datasource.password", POSTGRES::getPassword);
        }
    }

