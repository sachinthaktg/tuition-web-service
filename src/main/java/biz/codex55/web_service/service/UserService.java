package biz.codex55.web_service.service;

import biz.codex55.web_service.dto.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
}
