package io.github.ats527.demoapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Liquibase changelog YAML structure.
 * Validates that changelog files are well-formed and contain the expected
 * database schema definitions introduced in this PR.
 */
class LiquibaseChangelogStructureTest {

    private Yaml yaml;

    @BeforeEach
    void setUp() {
        yaml = new Yaml();
    }

    // -----------------------------------------------------------------------
    // Master changelog tests
    // -----------------------------------------------------------------------

    @Test
    void masterChangelogIsParseable() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("db/changelog/db.changelog-master.yml")) {
            assertThat(is).as("db.changelog-master.yml must exist on the classpath").isNotNull();
            Map<?, ?> doc = yaml.load(is);
            assertThat(doc).isNotNull();
        } catch (Exception e) {
            throw new AssertionError("Failed to parse db.changelog-master.yml", e);
        }
    }

    @Test
    void masterChangelogIncludesAccountsChangelogFile() {
        Map<?, ?> doc = loadYaml("db/changelog/db.changelog-master.yml");
        List<?> changeLog = (List<?>) doc.get("databaseChangeLog");
        assertThat(changeLog).as("databaseChangeLog must be a non-empty list").isNotEmpty();

        Map<?, ?> include = (Map<?, ?>) changeLog.get(0);
        assertThat(include).containsKey("include");
        Map<?, ?> includeDetails = (Map<?, ?>) include.get("include");
        assertThat(includeDetails.get("file"))
                .as("Master changelog must include the accounts table changelog")
                .isEqualTo("db/changelog/001-create-accounts-table.yml");
    }

    @Test
    void masterChangelogContainsExactlyOneInclude() {
        Map<?, ?> doc = loadYaml("db/changelog/db.changelog-master.yml");
        List<?> changeLog = (List<?>) doc.get("databaseChangeLog");
        assertThat(changeLog).hasSize(1);
    }

    // -----------------------------------------------------------------------
    // 001-create-accounts-table.yml – top-level structure
    // -----------------------------------------------------------------------

    @Test
    void accountsChangelogIsParseable() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("db/changelog/001-create-accounts-table.yml")) {
            assertThat(is).as("001-create-accounts-table.yml must exist on the classpath").isNotNull();
            Map<?, ?> doc = yaml.load(is);
            assertThat(doc).isNotNull();
        } catch (Exception e) {
            throw new AssertionError("Failed to parse 001-create-accounts-table.yml", e);
        }
    }

    @Test
    void accountsChangelogHasExpectedChangeSetId() {
        Map<?, ?> changeSet = getAccountsChangeSet();
        assertThat(changeSet.get("id"))
                .as("changeSet id must be '001-create-accounts-table'")
                .isEqualTo("001-create-accounts-table");
    }

    @Test
    void accountsChangelogHasExpectedAuthor() {
        Map<?, ?> changeSet = getAccountsChangeSet();
        assertThat(changeSet.get("author"))
                .as("changeSet author must be 'ats527'")
                .isEqualTo("ats527");
    }

    // -----------------------------------------------------------------------
    // createTable change
    // -----------------------------------------------------------------------

    @Test
    void accountsChangelogCreatesAccountsTable() {
        Map<?, ?> createTable = getCreateTable();
        assertThat(createTable.get("tableName"))
                .as("Table name must be 'accounts'")
                .isEqualTo("accounts");
    }

    @Test
    void accountsTableHasSevenColumns() {
        List<?> columns = getColumns();
        assertThat(columns).as("accounts table must have exactly 7 columns").hasSize(7);
    }

    @Test
    void accountsTableHasIdColumn() {
        Map<?, ?> col = getColumnByName("id");
        assertThat(col.get("type"))
                .as("id column must be BIGSERIAL")
                .isEqualTo("BIGSERIAL");
        Map<?, ?> constraints = (Map<?, ?>) col.get("constraints");
        assertThat(constraints.get("primaryKey")).isEqualTo(true);
        assertThat(constraints.get("nullable")).isEqualTo(false);
    }

    @Test
    void accountsTableHasAccountNumberColumn() {
        Map<?, ?> col = getColumnByName("account_number");
        assertThat(col.get("type"))
                .as("account_number must be VARCHAR(50)")
                .isEqualTo("VARCHAR(50)");
        Map<?, ?> constraints = (Map<?, ?>) col.get("constraints");
        assertThat(constraints.get("nullable")).isEqualTo(false);
        assertThat(constraints.get("unique")).isEqualTo(true);
    }

    @Test
    void accountsTableHasNameColumn() {
        Map<?, ?> col = getColumnByName("name");
        assertThat(col.get("type"))
                .as("name must be VARCHAR(100)")
                .isEqualTo("VARCHAR(100)");
        Map<?, ?> constraints = (Map<?, ?>) col.get("constraints");
        assertThat(constraints.get("nullable")).isEqualTo(false);
    }

    @Test
    void accountsTableHasTypeColumn() {
        Map<?, ?> col = getColumnByName("type");
        assertThat(col.get("type"))
                .as("type must be VARCHAR(50)")
                .isEqualTo("VARCHAR(50)");
        Map<?, ?> constraints = (Map<?, ?>) col.get("constraints");
        assertThat(constraints.get("nullable")).isEqualTo(false);
    }

    @Test
    void accountsTableHasStatusColumn() {
        Map<?, ?> col = getColumnByName("status");
        assertThat(col.get("type"))
                .as("status must be VARCHAR(50)")
                .isEqualTo("VARCHAR(50)");
        Map<?, ?> constraints = (Map<?, ?>) col.get("constraints");
        assertThat(constraints.get("nullable")).isEqualTo(false);
    }

    @Test
    void accountsTableHasCreatedAtColumn() {
        Map<?, ?> col = getColumnByName("created_at");
        assertThat(col.get("type"))
                .as("created_at must be TIMESTAMP WITH TIME ZONE")
                .isEqualTo("TIMESTAMP WITH TIME ZONE");
        assertThat(col.get("defaultValueComputed"))
                .as("created_at must default to CURRENT_TIMESTAMP")
                .isEqualTo("CURRENT_TIMESTAMP");
        Map<?, ?> constraints = (Map<?, ?>) col.get("constraints");
        assertThat(constraints.get("nullable")).isEqualTo(false);
    }

    @Test
    void accountsTableHasUpdatedAtColumn() {
        Map<?, ?> col = getColumnByName("updated_at");
        assertThat(col.get("type"))
                .as("updated_at must be TIMESTAMP WITH TIME ZONE")
                .isEqualTo("TIMESTAMP WITH TIME ZONE");
        assertThat(col.get("defaultValueComputed"))
                .as("updated_at must default to CURRENT_TIMESTAMP")
                .isEqualTo("CURRENT_TIMESTAMP");
        Map<?, ?> constraints = (Map<?, ?>) col.get("constraints");
        assertThat(constraints.get("nullable")).isEqualTo(false);
    }

    // -----------------------------------------------------------------------
    // Index
    // -----------------------------------------------------------------------

    @Test
    void accountsChangelogCreatesIndexOnAccountNumber() {
        Map<?, ?> changeSet = getAccountsChangeSet();
        List<?> changes = (List<?>) changeSet.get("changes");
        Map<?, ?> createIndex = null;
        for (Object change : changes) {
            Map<?, ?> changeMap = (Map<?, ?>) change;
            if (changeMap.containsKey("createIndex")) {
                createIndex = (Map<?, ?>) changeMap.get("createIndex");
                break;
            }
        }
        assertThat(createIndex).as("changeSet must contain a createIndex change").isNotNull();
        assertThat(createIndex.get("tableName")).isEqualTo("accounts");
        assertThat(createIndex.get("indexName")).isEqualTo("idx_accounts_account_number");
    }

    @Test
    void accountsIndexIsOnAccountNumberColumn() {
        Map<?, ?> changeSet = getAccountsChangeSet();
        List<?> changes = (List<?>) changeSet.get("changes");
        Map<?, ?> createIndex = null;
        for (Object change : changes) {
            Map<?, ?> changeMap = (Map<?, ?>) change;
            if (changeMap.containsKey("createIndex")) {
                createIndex = (Map<?, ?>) changeMap.get("createIndex");
                break;
            }
        }
        assertThat(createIndex).isNotNull();
        List<?> indexColumns = (List<?>) createIndex.get("columns");
        assertThat(indexColumns).hasSize(1);
        Map<?, ?> indexCol = (Map<?, ?>) ((Map<?, ?>) indexColumns.get(0)).get("column");
        assertThat(indexCol.get("name")).isEqualTo("account_number");
    }

    // -----------------------------------------------------------------------
    // Negative / edge cases
    // -----------------------------------------------------------------------

    @Test
    void accountsChangelogHasExactlyTwoChanges() {
        // createTable + createIndex
        Map<?, ?> changeSet = getAccountsChangeSet();
        List<?> changes = (List<?>) changeSet.get("changes");
        assertThat(changes).as("changeSet must have exactly 2 changes (createTable + createIndex)").hasSize(2);
    }

    @Test
    void noColumnHasNameNullOrEmpty() {
        List<?> columns = getColumns();
        for (Object colWrapper : columns) {
            Map<?, ?> colDef = (Map<?, ?>) ((Map<?, ?>) colWrapper).get("column");
            String name = (String) colDef.get("name");
            assertThat(name).as("Column name must not be null or empty").isNotBlank();
        }
    }

    @Test
    void noColumnHasTypeNullOrEmpty() {
        List<?> columns = getColumns();
        for (Object colWrapper : columns) {
            Map<?, ?> colDef = (Map<?, ?>) ((Map<?, ?>) colWrapper).get("column");
            String type = (String) colDef.get("type");
            assertThat(type).as("Column type must not be null or empty for column: " + colDef.get("name"))
                    .isNotBlank();
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Map<?, ?> loadYaml(String resourcePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assertThat(is).as("Resource not found: " + resourcePath).isNotNull();
            return yaml.load(is);
        } catch (Exception e) {
            throw new AssertionError("Failed to load YAML from " + resourcePath, e);
        }
    }

    private Map<?, ?> getAccountsChangeSet() {
        Map<?, ?> doc = loadYaml("db/changelog/001-create-accounts-table.yml");
        List<?> changeLog = (List<?>) doc.get("databaseChangeLog");
        assertThat(changeLog).isNotEmpty();
        Map<?, ?> entry = (Map<?, ?>) changeLog.get(0);
        return (Map<?, ?>) entry.get("changeSet");
    }

    private Map<?, ?> getCreateTable() {
        Map<?, ?> changeSet = getAccountsChangeSet();
        List<?> changes = (List<?>) changeSet.get("changes");
        for (Object change : changes) {
            Map<?, ?> changeMap = (Map<?, ?>) change;
            if (changeMap.containsKey("createTable")) {
                return (Map<?, ?>) changeMap.get("createTable");
            }
        }
        throw new AssertionError("No createTable change found in changeSet");
    }

    private List<?> getColumns() {
        return (List<?>) getCreateTable().get("columns");
    }

    private Map<?, ?> getColumnByName(String name) {
        List<?> columns = getColumns();
        for (Object colWrapper : columns) {
            Map<?, ?> colDef = (Map<?, ?>) ((Map<?, ?>) colWrapper).get("column");
            if (name.equals(colDef.get("name"))) {
                return colDef;
            }
        }
        throw new AssertionError("Column '" + name + "' not found in accounts table definition");
    }
}