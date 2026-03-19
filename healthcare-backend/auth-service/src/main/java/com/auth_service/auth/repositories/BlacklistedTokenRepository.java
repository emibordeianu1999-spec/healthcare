package com.auth_service.auth.repositories;

import com.auth_service.auth.entities.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findByToken(String token);
    void deleteByExpiryDateBefore(Instant now);
}
