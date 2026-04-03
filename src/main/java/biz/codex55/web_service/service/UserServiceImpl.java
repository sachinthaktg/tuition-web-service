package biz.codex55.web_service.service;

import biz.codex55.web_service.dto.CreateUserRequest;
import biz.codex55.web_service.dto.UserResponse;
import biz.codex55.web_service.entity.Grade;
import biz.codex55.web_service.entity.Student;
import biz.codex55.web_service.entity.Teacher;
import biz.codex55.web_service.entity.User;
import biz.codex55.web_service.repository.GradeRepository;
import biz.codex55.web_service.repository.StudentRepository;
import biz.codex55.web_service.repository.TeacherRepository;
import biz.codex55.web_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final GradeRepository gradeRepository;
    private final PasswordEncoder passwordEncoder;

    private final FileStorageService fileStorageService;
    private final QRCodeService qrCodeService;

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

    @Transactional
    @Override
    public String createUser(CreateUserRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        String savedFileName = null;

        // Create the content for the QR Code (e.g., login credentials)
        String qrContent = null;

        // 1. If a profile picture was uploaded, save it to the local directory
        if (request.getProfilePic() != null && !request.getProfilePic().isEmpty()) {
            savedFileName = fileStorageService.storeFile(request.getProfilePic());
        }

        // 🔥 1. Create User
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .photoUrl(savedFileName)
                .role(request.getRole())
                .isActive(true)
                .build();

        userRepository.save(user);

        // 🔥 2. Create role-specific entity
        switch (request.getRole()) {

            case STUDENT -> {
                Grade grade = gradeRepository.findById(request.getGradeId())
                        .orElseThrow(() -> new RuntimeException("Grade not found"));

                Student student = Student.builder()
                        .user(user)
                        .address(request.getAddress())
                        .phone(request.getPhone())
                        .parentName(request.getParentName())
                        .parentPhone(request.getParentPhone())
                        .grade(grade)
                        .qrCode(generateQrCode(user)) // 🔥 optional
                        .build();

                studentRepository.save(student);
                qrContent = String.format("Name: %s\nGrade: %s\nID: %s", user.getFullName(), student.getGrade(), student.getQrCode());
            }

            case TEACHER -> {
                Teacher teacher = Teacher.builder()
                        .user(user)
                        .phone(request.getPhone())
                        .salaryPercentage(request.getSalaryPercentage())
                        .build();

                teacherRepository.save(teacher);
            }

            case ADMIN -> {
                // no extra table needed
            }
        }

        // Generate and return the Base64 QR code image
        if (qrContent != null) {
            return qrCodeService.generateQRCodeBase64(qrContent, 250, 250);
        } else {
            return null; // or some default QR code
        }
    }

    // 🔥 simple QR generator
    private String generateQrCode(User user) {
        return "QR-" + user.getId() + "-" + System.currentTimeMillis();
    }
}
