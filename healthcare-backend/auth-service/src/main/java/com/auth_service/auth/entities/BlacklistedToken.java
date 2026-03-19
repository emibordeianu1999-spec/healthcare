package com.auth_service.auth.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Setter
@Getter
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    public BlacklistedToken() {}

    public BlacklistedToken(String token, Instant expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
