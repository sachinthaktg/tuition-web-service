package biz.codex55.web_service.config;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiTenantJpaConfig {

    @Bean
    public org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer multiTenantHibernateCustomizer(
            CurrentTenantIdentifierResolver<String> tenantResolver,
            MultiTenantConnectionProvider<String> tenantConnectionProvider) {

        return hibernateProperties -> {
            // Programmatically inject the Spring-managed beans into Hibernate
            hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
            hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, tenantConnectionProvider);
        };
    }
}
