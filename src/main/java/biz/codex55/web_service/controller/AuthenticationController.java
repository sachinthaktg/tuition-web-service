package biz.codex55.web_service.controller;

import biz.codex55.web_service.dto.AuthRequest;
import biz.codex55.web_service.dto.AuthResponse;
import biz.codex55.web_service.entity.Role;
import biz.codex55.web_service.security.JwtService;
import biz.codex55.web_service.config.TenantContext;
import biz.codex55.web_service.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            HttpServletResponse response) {

        // 1. Set the Tenant Context BEFORE authentication
        // If no tenant is provided, fallback to the master database (Super Admin login)
        String activeTenant = (tenantId != null && !tenantId.isEmpty()) ? tenantId : "spacer_core";

        try {
            // 2. Authenticate against the specific tenant's database
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 3. Retrieve user details and generate token
            UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());

            // Note: Update your JwtService.generateToken to accept the tenant string
            String jwtToken = jwtService.generateToken(user, activeTenant);

            // 4. Attach token as an HTTP-Only Cookie for security
            Cookie cookie = new Cookie("jwt", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // Ensure HTTPS is used in production
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 1 day expiration
            response.addCookie(cookie);

            return ResponseEntity.ok(new AuthResponse("Login successful"));

        } finally {
            // ALWAYS clear the context to prevent thread-leakage
            TenantContext.clear();
        }
    }

    @PostMapping("/register")
    public void register(@RequestBody AuthRequest req) {
        authService.register(req.getUsername(), req.getPassword(), Role.ADMIN);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // delete

        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }
}
