package biz.codex55.web_service.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String SECRET = "c3VwZXItc2VjcmV0LWtleS1mb3ItdGp3dC1hdXRoLXN5c3RlbS0xMjM0NTY3ODkw"; // 🔥 SAME as JwtService

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String tenant = null;

        // 🔥 1. Try extract tenant from JWT (cookie)
        String token = extractJwtFromCookies(request);

        if (token != null) {
            try {
                tenant = Jwts.parser()
                        .verifyWith(getKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .get("tenant", String.class);
                log.debug("Tenant from JWT: {}", tenant);
            } catch (Exception e) {
                log.warn("Invalid JWT, falling back to header");
            }
        }

        // 🔥 2. Fallback to header (before login)
        if (tenant == null || tenant.isEmpty()) {
            tenant = request.getHeader("X-Tenant-ID");
            log.debug("Tenant from header: {}", tenant);
        }

        // 🔥 3. Final fallback
        if (tenant == null || tenant.isEmpty()) {
            tenant = "spacer_core";
            log.warn("No tenant found. Defaulting to '{}'", tenant);
        }

        // 🔥 Set tenant
        TenantContext.setCurrentTenant(tenant);

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🔐 Extract JWT from cookies
    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}