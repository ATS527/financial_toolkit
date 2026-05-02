package io.github.ats527.demoapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests verifying that Liquibase migrations run correctly and
 * produce the expected database schema for the accounts table introduced in
 * changeset 001-create-accounts-table.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LiquibaseMigrationIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // -----------------------------------------------------------------------
    // Table existence
    // -----------------------------------------------------------------------

    @Test
    void accountsTableExistsAfterMigration() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, "ACCOUNTS", new String[]{"TABLE"})) {
                assertThat(rs.next()).as("accounts table must exist after Liquibase migration").isTrue();
            }
        }
    }

    @Test
    void liquibaseTrackingTableExists() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            // Liquibase creates DATABASECHANGELOG to track applied changesets
            try (ResultSet rs = meta.getTables(null, null, "DATABASECHANGELOG", new String[]{"TABLE"})) {
                assertThat(rs.next()).as("DATABASECHANGELOG table must exist").isTrue();
            }
        }
    }

    @Test
    void liquibaseLockTableExists() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, "DATABASECHANGELOGLOCK", new String[]{"TABLE"})) {
                assertThat(rs.next()).as("DATABASECHANGELOGLOCK table must exist").isTrue();
            }
        }
    }

    // -----------------------------------------------------------------------
    // Column existence
    // -----------------------------------------------------------------------

    @Test
    void accountsTableHasAllRequiredColumns() throws Exception {
        List<String> columns = getColumnNames("ACCOUNTS");
        assertThat(columns).as("accounts table must contain all expected columns")
                .contains("ID", "ACCOUNT_NUMBER", "NAME", "TYPE", "STATUS", "CREATED_AT", "UPDATED_AT");
    }

    @Test
    void accountsTableHasExactlySevenColumns() throws Exception {
        List<String> columns = getColumnNames("ACCOUNTS");
        assertThat(columns).as("accounts table must have exactly 7 columns").hasSize(7);
    }

    // -----------------------------------------------------------------------
    // Column data type and nullability
    // -----------------------------------------------------------------------

    @Test
    void idColumnIsNotNullable() throws Exception {
        assertColumnNotNullable("ACCOUNTS", "ID");
    }

    @Test
    void accountNumberColumnIsNotNullable() throws Exception {
        assertColumnNotNullable("ACCOUNTS", "ACCOUNT_NUMBER");
    }

    @Test
    void nameColumnIsNotNullable() throws Exception {
        assertColumnNotNullable("ACCOUNTS", "NAME");
    }

    @Test
    void typeColumnIsNotNullable() throws Exception {
        assertColumnNotNullable("ACCOUNTS", "TYPE");
    }

    @Test
    void statusColumnIsNotNullable() throws Exception {
        assertColumnNotNullable("ACCOUNTS", "STATUS");
    }

    @Test
    void createdAtColumnIsNotNullable() throws Exception {
        assertColumnNotNullable("ACCOUNTS", "CREATED_AT");
    }

    @Test
    void updatedAtColumnIsNotNullable() throws Exception {
        assertColumnNotNullable("ACCOUNTS", "UPDATED_AT");
    }

    // -----------------------------------------------------------------------
    // Primary key
    // -----------------------------------------------------------------------

    @Test
    void idColumnIsPrimaryKey() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getPrimaryKeys(null, null, "ACCOUNTS")) {
                assertThat(rs.next()).as("accounts table must have a primary key").isTrue();
                assertThat(rs.getString("COLUMN_NAME"))
                        .as("Primary key must be on the id column")
                        .isEqualTo("ID");
            }
        }
    }

    // -----------------------------------------------------------------------
    // Constraint enforcement via SQL
    // -----------------------------------------------------------------------

    @Test
    void insertValidAccountSucceeds() {
        int rows = jdbcTemplate.update(
                "INSERT INTO accounts (account_number, name, type, status) VALUES (?, ?, ?, ?)",
                "ACC-001", "Test Account", "CHECKING", "ACTIVE");
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void insertDuplicateAccountNumberViolatesUniqueConstraint() {
        jdbcTemplate.update(
                "INSERT INTO accounts (account_number, name, type, status) VALUES (?, ?, ?, ?)",
                "ACC-DUP", "First Account", "SAVINGS", "ACTIVE");

        assertThatThrownBy(() ->
                jdbcTemplate.update(
                        "INSERT INTO accounts (account_number, name, type, status) VALUES (?, ?, ?, ?)",
                        "ACC-DUP", "Second Account", "SAVINGS", "ACTIVE"))
                .as("Inserting a duplicate account_number must throw a constraint violation")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void insertWithNullAccountNumberViolatesNotNullConstraint() {
        assertThatThrownBy(() ->
                jdbcTemplate.update(
                        "INSERT INTO accounts (account_number, name, type, status) VALUES (?, ?, ?, ?)",
                        null, "Test Account", "CHECKING", "ACTIVE"))
                .as("Inserting a null account_number must throw a constraint violation")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void insertWithNullNameViolatesNotNullConstraint() {
        assertThatThrownBy(() ->
                jdbcTemplate.update(
                        "INSERT INTO accounts (account_number, name, type, status) VALUES (?, ?, ?, ?)",
                        "ACC-002", null, "CHECKING", "ACTIVE"))
                .as("Inserting a null name must throw a constraint violation")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void insertWithNullTypeViolatesNotNullConstraint() {
        assertThatThrownBy(() ->
                jdbcTemplate.update(
                        "INSERT INTO accounts (account_number, name, type, status) VALUES (?, ?, ?, ?)",
                        "ACC-003", "Test Account", null, "ACTIVE"))
                .as("Inserting a null type must throw a constraint violation")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void insertWithNullStatusViolatesNotNullConstraint() {
        assertThatThrownBy(() ->
                jdbcTemplate.update(
                        "INSERT INTO accounts (account_number, name, type, status) VALUES (?, ?, ?, ?)",
                        "ACC-004", "Test Account", "CHECKING", null))
                .as("Inserting a null status must throw a constraint violation")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void createdAtAndUpdatedAtHaveDefaultValues() {
        jdbcTemplate.update(
                "INSERT INTO accounts (account_number, name, type, status) VALUES (?, ?, ?, ?)",
                "ACC-DEFAULTS", "Default Test", "CHECKING", "ACTIVE");

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM accounts WHERE account_number = 'ACC-DEFAULTS' "
                        + "AND created_at IS NOT NULL AND updated_at IS NOT NULL",
                Long.class);
        assertThat(count).as("created_at and updated_at must have non-null defaults").isEqualTo(1L);
    }

    // -----------------------------------------------------------------------
    // Changeset tracking
    // -----------------------------------------------------------------------

    @Test
    void liquibaseChangelogRecordsAccountsChangeset() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM DATABASECHANGELOG WHERE ID = ?",
                Long.class,
                "001-create-accounts-table");
        assertThat(count).as("DATABASECHANGELOG must record the 001-create-accounts-table changeset")
                .isEqualTo(1L);
    }

    @Test
    void liquibaseChangelogRecordsCorrectAuthor() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM DATABASECHANGELOG WHERE ID = ? AND AUTHOR = ?",
                Long.class,
                "001-create-accounts-table", "ats527");
        assertThat(count).as("DATABASECHANGELOG must record author 'ats527' for the accounts changeset")
                .isEqualTo(1L);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private List<String> getColumnNames(String tableName) throws Exception {
        List<String> columns = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME"));
                }
            }
        }
        return columns;
    }

    private void assertColumnNotNullable(String tableName, String columnName) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
                assertThat(rs.next())
                        .as("Column " + columnName + " must exist in table " + tableName).isTrue();
                int nullable = rs.getInt("NULLABLE");
                assertThat(nullable)
                        .as("Column " + columnName + " must be NOT NULL (NULLABLE=0)")
                        .isEqualTo(DatabaseMetaData.columnNoNulls);
            }
        }
    }
}