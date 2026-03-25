package biz.codex55.web_service.service;

import biz.codex55.web_service.entity.Role;
import biz.codex55.web_service.entity.User;
import biz.codex55.web_service.repository.UserRepository;
import biz.codex55.web_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public void register(String username, String password, Role role) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setRole(role);

        userRepo.save(user);
    }
}
