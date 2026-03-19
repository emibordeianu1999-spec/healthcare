package com.auth_service.auth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "verification_tokens")
@Setter
@Getter
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    public VerificationToken() {
        super();
    }

    public VerificationToken(final User user) {
        super();

        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}