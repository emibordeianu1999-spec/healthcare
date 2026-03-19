package com.auth_service.auth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private boolean emailVerified;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;
}
