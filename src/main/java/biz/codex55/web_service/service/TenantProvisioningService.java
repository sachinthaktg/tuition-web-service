package biz.codex55.web_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantProvisioningService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource; // Inject your main DataSource

    public void createSchemaForTenant(String domainPrefix) {
        String safeTenantId = domainPrefix.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String schemaName = "tenant_" + safeTenantId;

        log.info("Starting database provisioning for tenant: {}", schemaName);

        try {
            // 1. Create the Database/Schema
            jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS " + schemaName);
            log.info("Successfully created database schema: {}", schemaName);

            // 2. Use Flyway to initialize the tables
            initializeTenantTables(schemaName);

        } catch (Exception e) {
            log.error("Failed to provision database for tenant: {}", schemaName, e);
            throw new RuntimeException("Database provisioning failed for tenant: " + domainPrefix, e);
        }
    }

    private void initializeTenantTables(String schemaName) {
        log.info("Running Flyway migrations for schema: {}", schemaName);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName) // Tell Flyway to execute specifically inside this new database
                .locations("classpath:db/migration/tenants") // Point it to your tenant SQL folder
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();

        log.info("Tables successfully initialized via Flyway for {}", schemaName);
    }
}
