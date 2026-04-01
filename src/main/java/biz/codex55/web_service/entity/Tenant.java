package biz.codex55.web_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@ToString
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Tenant tenant = (Tenant) o;
        return getId() != null && Objects.equals(getId(), tenant.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
