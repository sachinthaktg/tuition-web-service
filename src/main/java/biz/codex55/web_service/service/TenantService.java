package biz.codex55.web_service.service;

import biz.codex55.web_service.dto.TenantOnboardRequest;
import biz.codex55.web_service.entity.Tenant;
import biz.codex55.web_service.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    // Inject your EmailService or InvoiceService here later

    @Transactional
    public Tenant onboardTenant(TenantOnboardRequest request) {

        // 1. Verify domain and email uniqueness
        if (tenantRepository.findByDomainPrefix(request.getDomainPrefix()).isPresent()) {
            throw new RuntimeException("Domain prefix is already taken.");
        }
        if (tenantRepository.findByAdminEmail(request.getAdminEmail()).isPresent()) {
            throw new RuntimeException("Admin email is already registered.");
        }

        // 2. Build and save the Tenant
        Tenant tenant = Tenant.builder()
                .centerName(request.getCenterName())
                .domainPrefix(request.getDomainPrefix())
                .adminName(request.getAdminName())
                .adminEmail(request.getAdminEmail())
                .adminPhone(request.getAdminPhone())
                .setupFee(request.getSetupFee())
                .revenueShare(request.getRevenueShare())
                .status("ONBOARDING")
                .build();

        tenant = tenantRepository.save(tenant);

        // 3. Handle Initial Setup Invoice
        if (request.isGenerateInvoice()) {
            generateAndSendInvoice(tenant);
        }

        // 4. (Future Step) Trigger Database Schema generation for the new tenant
        // tenantProvisioningService.createSchemaForTenant(tenant.getDomainPrefix());

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
