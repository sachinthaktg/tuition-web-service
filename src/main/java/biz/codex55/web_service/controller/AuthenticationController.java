package biz.codex55.web_service.controller;

import biz.codex55.web_service.config.TenantContext;
import biz.codex55.web_service.dto.AuthRequest;
import biz.codex55.web_service.dto.AuthResponse;
import biz.codex55.web_service.entity.Role;
import biz.codex55.web_service.repository.UserRepository;
import biz.codex55.web_service.security.JwtService;
import biz.codex55.web_service.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            HttpServletResponse response) {

        // 1. Set the Tenant Context BEFORE authentication
        // If no tenant is provided, fallback to the master database (Super Admin login)
        String activeTenant = (tenantId != null && !tenantId.isEmpty()) ? tenantId : "";

        log.info("🔥 TENANT: " + TenantContext.getCurrentTenant());


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

            String refreshToken = jwtService.generateRefreshToken(user);

            // 4. Attach token as an HTTP-Only Cookie for security
            Cookie cookie = new Cookie("jwt", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // Ensure HTTPS is used in production
            cookie.setPath("/");
            cookie.setMaxAge(15 * 60); // 15 minutes
            response.addCookie(cookie);

            // Optionally, you can also set the refresh token as a cookie
            Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(refreshCookie);

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

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refresh_token"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isValid(refreshToken, user)) {
            return ResponseEntity.status(401).build();
        }

        log.info("🔥 TENANT: " + TenantContext.getCurrentTenant());

        String newAccessToken = jwtService.generateToken(user, TenantContext.getCurrentTenant());

        Cookie cookie = new Cookie("jwt", newAccessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(15 * 60);

        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
