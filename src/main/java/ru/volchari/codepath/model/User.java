package ru.volchari.codepath.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    private String name;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;

    @Enumerated(EnumType.STRING)
    private Provider provider;
}
