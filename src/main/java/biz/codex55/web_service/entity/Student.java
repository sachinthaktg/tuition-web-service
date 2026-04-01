package biz.codex55.web_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String firstName;
    private String middleName;
    private String lastName;

    private String photoUrl;

    private String address;
    private String phone;

    private String parentName;
    private String parentPhone;

    private String qrCode;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;
}
