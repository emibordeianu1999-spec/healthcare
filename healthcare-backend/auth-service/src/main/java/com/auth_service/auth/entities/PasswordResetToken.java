package com.auth_service.auth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Setter
@Getter
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    public PasswordResetToken() {}

    public PasswordResetToken(User user) {
        this.user = user;
        this.expiryDate = calculateExpiryDate();
        this.token = UUID.randomUUID().toString();
    }

    private Date calculateExpiryDate() {
        final long ONE_MINUTE_IN_MILLIS = 60000;
        long t = new Date().getTime();
        return new Date(t + (PasswordResetToken.EXPIRATION * ONE_MINUTE_IN_MILLIS));
    }
}
