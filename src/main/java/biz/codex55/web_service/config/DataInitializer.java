package biz.codex55.web_service.config;

import biz.codex55.web_service.entity.Role;
import biz.codex55.web_service.entity.User;
import biz.codex55.web_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {

            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("1234")); // 🔐 encoded
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);

                log.info("✅ Admin user created: admin / 1234");
            }

            if (userRepository.findByUsername("superadmin").isEmpty()) {

                User teacher = new User();
                teacher.setUsername("superadmin");
                teacher.setPassword(passwordEncoder.encode("1234"));
                teacher.setRole(Role.SUPER_ADMIN);

                userRepository.save(teacher);

                log.info("✅ Super Admin user created: superadmin / 1234");
            }
        };
    }
}
