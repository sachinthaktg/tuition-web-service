package biz.codex55.web_service.controller;

import biz.codex55.web_service.dto.TenantOnboardRequest;
import biz.codex55.web_service.dto.TenantStatusUpdateRequest;
import biz.codex55.web_service.entity.Tenant;
import biz.codex55.web_service.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin/tenants")
@RequiredArgsConstructor
public class SuperAdminController {

    private final TenantService tenantService;

    // Endpoint to get the list of all centers for the data table
    @GetMapping
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    // Endpoint called by the "Deploy Center" button in your modal
    @PostMapping("/onboard")
    public ResponseEntity<Tenant> onboardTenant(@RequestBody TenantOnboardRequest request) {
        Tenant newTenant = tenantService.onboardTenant(request);
        return ResponseEntity.ok(newTenant);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Tenant> updateTenantStatus(
            @PathVariable Long id,
            @RequestBody TenantStatusUpdateRequest request) {

        Tenant updatedTenant = tenantService.updateTenantStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedTenant);
    }
}
