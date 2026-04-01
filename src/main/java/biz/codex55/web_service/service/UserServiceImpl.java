package biz.codex55.web_service.service;

import biz.codex55.web_service.dto.UserResponse;
import biz.codex55.web_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .name(user.getUsername())
                        .email(user.getUsername()) // adjust if separate email field
                        .role(user.getRole().name())
                        .status(user.isEnabled() ? "Active" : "Inactive")
                        .createdAt("2026-01-01") // map properly
                        .build())
                .toList();
    }
}
