package biz.codex55.web_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException, IOException {

        String tenant = request.getHeader("X-Tenant-ID");

        log.debug("--- TenantFilter: tenant {}", tenant);

        if (tenant != null) {
            TenantContext.setCurrentTenant(tenant);
        } else {
            log.warn("No tenant specified in request. Defaulting to 'spacer_core'.");
            TenantContext.setCurrentTenant("spacer_core");
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
