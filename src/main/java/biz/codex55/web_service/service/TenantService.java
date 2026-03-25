package biz.codex55.web_service.service;

import biz.codex55.web_service.dto.TenantOnboardRequest;
import biz.codex55.web_service.entity.Tenant;
import biz.codex55.web_service.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantProvisioningService tenantProvisioningService;

    @Transactional
    public Tenant onboardTenant(TenantOnboardRequest request) {

        if (tenantRepository.findByDomainPrefix(request.getDomainPrefix()).isPresent()) {
            throw new RuntimeException("Domain prefix is already taken.");
        }
        if (tenantRepository.findByAdminEmail(request.getAdminEmail()).isPresent()) {
            throw new RuntimeException("Admin email is already registered.");
        }

        Tenant tenant = Tenant.builder()
                .centerName(request.getCenterName())
                .domainPrefix(request.getDomainPrefix())
                .adminName(request.getAdminName())
                .adminEmail(request.getAdminEmail())
                .adminPhone(request.getAdminPhone())
                .setupFee(request.getSetupFee())
                .revenueShare(request.getRevenueShare())
                .status("ACTIVE")
                .build();

        // Save the tenant in the master spacer_core database
        tenant = tenantRepository.save(tenant);

        // Provision the database AND create the admin user
        String temporaryPassword = tenantProvisioningService.createSchemaAndAdminForTenant(tenant);

        // TODO: Dispatch Email
        log.info("ATTENTION: Dispatching email to {}. Temporary Password is: {}",
                tenant.getAdminEmail(), temporaryPassword);

        // if (request.isGenerateInvoice()) {
        //     emailService.sendOnboardingEmail(tenant.getAdminEmail(), temporaryPassword, request.getSetupFee());
        // }

        return tenant;
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    private void generateAndSendInvoice(Tenant tenant) {
        System.out.println("Generating invoice of Rs. " + tenant.getSetupFee() +
                " for " + tenant.getCenterName());
        // Add your PDF generation and email logic here
    }

    @Transactional
    public Tenant updateTenantStatus(Long id, String newStatus) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + id));

        // Ensure only valid statuses are saved
        if (!newStatus.equals("ACTIVE") && !newStatus.equals("INACTIVE")) {
            throw new IllegalArgumentException("Invalid status provided");
        }

        tenant.setStatus(newStatus);

        // If Deactivated, you might want to add logic here to invalidate all
        // active JWT tokens for this tenant so they are instantly logged out.

        return tenantRepository.save(tenant);
    }
}
