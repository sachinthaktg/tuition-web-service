package biz.codex55.web_service.service;

import biz.codex55.web_service.entity.Tenant;
import biz.codex55.web_service.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantMigrationService {

    private final TenantRepository tenantRepository;
    private final DataSource dataSource;

    public void migrateAllTenants() {

        List<Tenant> tenants = tenantRepository.findAll();

        for (Tenant tenant : tenants) {
            String dbName = "tenant_" + tenant.getDomainPrefix();

            runMigrationForTenant(dbName);
        }
    }

    private void runMigrationForTenant(String dbName) {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(dbName)
                    .locations("classpath:db/migration/tenants")
                    .load();

            flyway.migrate();

            log.info("✅ Migrated: " + dbName);

        } catch (Exception e) {
            log.error("❌ Migration failed for: " + dbName);
            e.printStackTrace();
        }
    }
}
