package biz.codex55.web_service.dto;

import biz.codex55.web_service.entity.Role;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateUserRequest {

    private String username;   // email
    private String password;

    private String fullName;

    private Role role;

    // optional (based on role)
    private String phone;
    private String address;

    private String parentName;
    private String parentPhone;

    private Long gradeId; // for student

    private Double salaryPercentage; // for teacher


    private MultipartFile profilePic;
}