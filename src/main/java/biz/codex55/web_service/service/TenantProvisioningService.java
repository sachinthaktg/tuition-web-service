package biz.codex55.web_service.service;

import biz.codex55.web_service.entity.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantProvisioningService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder; // Inject BCrypt

    // Returns the generated plaintext password so you can email it to the admin
    public String createSchemaAndAdminForTenant(Tenant tenant) {
        String safeTenantId = tenant.getDomainPrefix().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String schemaName = "tenant_" + safeTenantId;

        log.info("Starting database provisioning for tenant: {}", schemaName);

        try {
            // 1. Create the Database
            jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS " + schemaName);

            // 2. Run Migrations (Flyway)
            initializeTenantTables(schemaName);

            // 3. Generate a secure temporary password (e.g., first 8 chars of a UUID)
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);
            String hashedPassword = passwordEncoder.encode(tempPassword);

            // 4. Insert the Primary Admin into the NEW tenant's database
            createInitialAdminUser(schemaName, tenant.getAdminEmail(), hashedPassword);

            log.info("Successfully provisioned database and admin for: {}", schemaName);

            return tempPassword;

        } catch (Exception e) {
            log.error("Failed to provision database for tenant: {}", schemaName, e);
            throw new RuntimeException("Database provisioning failed for tenant: " + tenant.getDomainPrefix(), e);
        }
    }

    private void initializeTenantTables(String schemaName) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .locations("classpath:db/migration/tenants")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
    }

    private void createInitialAdminUser(String schemaName, String email, String hashedPassword) {
        // We concatenate the schemaName (safe), but parameterize the user inputs (?) to prevent SQL Injection
        String insertSql = String.format(
                "INSERT INTO %s.users (username, password_hash, role, is_active) VALUES (?, ?, ?, ?)",
                schemaName
        );

        jdbcTemplate.update(
                insertSql,
                email,            // ? 1
                hashedPassword,   // ? 2
                "ADMIN",          // ? 3 (Role for the tenant's primary admin)
                true              // ? 4 (is_active)
        );
    }
}