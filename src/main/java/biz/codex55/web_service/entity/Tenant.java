package biz.codex55.web_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String centerName;

    @Column(nullable = false, unique = true)
    private String domainPrefix; // e.g., "apex" for apex.spacer.lk

    @Column(nullable = false)
    private String adminName;

    @Column(nullable = false, unique = true)
    private String adminEmail;

    @Column(nullable = false)
    private String adminPhone;

    // Billing details configured during onboarding
    @Column(nullable = false)
    private Double setupFee;

    @Column(nullable = false)
    private Double revenueShare;

    @Column(nullable = false)
    private String status; // e.g., "ONBOARDING", "ACTIVE"

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
