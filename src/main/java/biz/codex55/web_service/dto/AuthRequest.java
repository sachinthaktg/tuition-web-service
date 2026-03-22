package biz.codex55.web_service.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
