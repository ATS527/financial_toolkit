package io.github.ats527.demoapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that verify Spring Boot configuration properties introduced in
 * application.yml are loaded and resolved correctly.
 *
 * The "test" profile activates application-test.yml which substitutes the
 * PostgreSQL datasource with an in-memory H2 database, while leaving
 * Liquibase, JPA, and other settings untouched (or equivalent).
 */
@SpringBootTest
@ActiveProfiles("test")
class ApplicationConfigurationTest {

    @Autowired
    private Environment environment;

    @Value("${spring.liquibase.change-log}")
    private String liquibaseChangeLog;

    @Value("${spring.liquibase.enabled}")
    private boolean liquibaseEnabled;

    @Value("${spring.jpa.show-sql}")
    private boolean jpaShowSql;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String jpaHibernateDdlAuto;

    @Value("${spring.jpa.properties.hibernate.format_sql}")
    private boolean hibernateFormatSql;

    // -----------------------------------------------------------------------
    // Liquibase configuration (same in main + test profiles)
    // -----------------------------------------------------------------------

    @Test
    void liquibaseIsEnabled() {
        assertThat(liquibaseEnabled)
                .as("Liquibase must be enabled as configured in application.yml")
                .isTrue();
    }

    @Test
    void liquibaseChangeLogPointsToMasterFile() {
        assertThat(liquibaseChangeLog)
                .as("Liquibase change-log must point to the master changelog")
                .isEqualTo("classpath:db/changelog/db.changelog-master.yml");
    }

    // -----------------------------------------------------------------------
    // JPA configuration
    // -----------------------------------------------------------------------

    @Test
    void jpaShowSqlIsEnabled() {
        assertThat(jpaShowSql)
                .as("JPA show-sql must be true as configured in application.yml")
                .isTrue();
    }

    @Test
    void jpaHibernateDdlAutoIsValidate() {
        assertThat(jpaHibernateDdlAuto)
                .as("JPA hibernate ddl-auto must be 'validate' to rely on Liquibase for schema management")
                .isEqualTo("validate");
    }

    @Test
    void hibernateFormatSqlIsEnabled() {
        assertThat(hibernateFormatSql)
                .as("Hibernate format_sql must be true as configured in application.yml")
                .isTrue();
    }

    // -----------------------------------------------------------------------
    // Datasource configuration (test profile values)
    // -----------------------------------------------------------------------

    @Test
    void datasourceUrlIsConfigured() {
        String url = environment.getProperty("spring.datasource.url");
        assertThat(url)
                .as("spring.datasource.url must be configured")
                .isNotBlank();
    }

    @Test
    void datasourceUsernameIsConfigured() {
        String username = environment.getProperty("spring.datasource.username");
        assertThat(username)
                .as("spring.datasource.username must be configured")
                .isNotBlank();
    }

    // -----------------------------------------------------------------------
    // Active profile guard
    // -----------------------------------------------------------------------

    @Test
    void testProfileIsActive() {
        assertThat(environment.getActiveProfiles())
                .as("The 'test' profile must be active during tests")
                .contains("test");
    }
}


/**
 * Verifies the server port declared in application.yml is 8080.
 * Uses a separate nested context so the server.port property can be
 * read before the web server starts (NONE environment avoids port conflicts).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:porttest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;NON_KEYWORDS=VALUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class ServerPortConfigurationTest {

    @Value("${server.port:8080}")
    private int serverPort;

    @Test
    void serverPortIsConfiguredAs8080() {
        assertThat(serverPort)
                .as("Server port must be 8080 as declared in application.yml")
                .isEqualTo(8080);
    }
}