package biz.codex55.web_service.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String currentTenant = TenantContext.getCurrentTenant();

        // If no tenant is found in the context (e.g., during Super Admin login or public routes),
        // fallback to your default database schema (e.g., "spacer_core" or "public")
        return currentTenant != null ? currentTenant : "spacer_core";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
