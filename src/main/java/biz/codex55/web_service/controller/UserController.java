package biz.codex55.web_service.controller;

import biz.codex55.web_service.dto.CreateUserRequest;
import biz.codex55.web_service.dto.UserResponse;
import biz.codex55.web_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUser(@ModelAttribute CreateUserRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok("User created successfully");
    }
}
