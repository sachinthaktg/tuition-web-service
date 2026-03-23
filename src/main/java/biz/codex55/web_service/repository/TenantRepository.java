package biz.codex55.web_service.repository;

import biz.codex55.web_service.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByDomainPrefix(String domainPrefix);

    Optional<Tenant> findByAdminEmail(String adminEmail);
}
