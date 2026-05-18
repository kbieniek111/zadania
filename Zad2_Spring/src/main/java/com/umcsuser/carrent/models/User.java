package com.umcsuser.carrent.models;
import lombok.*;
import jakarta.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "passwordHash")

@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(name = "password", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User copy() {
        return User.builder()
                .id(id)
                .login(login)
                .passwordHash(passwordHash)
                .role(role)
                .build();
    }
}