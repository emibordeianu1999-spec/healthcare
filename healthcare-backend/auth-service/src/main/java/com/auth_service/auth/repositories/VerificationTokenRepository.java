package com.auth_service.auth.repositories;

import com.auth_service.auth.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    VerificationToken findByToken(String token);
}
