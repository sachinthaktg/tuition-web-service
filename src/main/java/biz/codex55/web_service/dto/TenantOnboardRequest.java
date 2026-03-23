package biz.codex55.web_service.dto;

import lombok.Data;

@Data
public class TenantOnboardRequest {
    private String centerName;
    private String domainPrefix;
    private String adminName;
    private String adminEmail;
    private String adminPhone;
    private boolean generateInvoice;
    private Double setupFee;
    private Double revenueShare;
}
