package biz.codex55.web_service.config;

import biz.codex55.web_service.service.TenantMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupMigrationRunner implements CommandLineRunner {

    private final TenantMigrationService tenantMigrationService;

    @Override
    public void run(String... args) {
        tenantMigrationService.migrateAllTenants();
    }
}
