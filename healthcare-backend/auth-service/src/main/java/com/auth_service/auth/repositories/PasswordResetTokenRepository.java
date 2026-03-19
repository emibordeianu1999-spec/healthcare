package com.auth_service.auth.repositories;

import com.auth_service.auth.entities.PasswordResetToken;
import com.auth_service.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);
}
