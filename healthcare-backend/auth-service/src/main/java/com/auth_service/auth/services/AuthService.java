package com.auth_service.auth.services;

import com.auth_service.auth.dtos.*;
import com.auth_service.auth.entities.BlacklistedToken;
import com.auth_service.auth.entities.PasswordResetToken;
import com.auth_service.auth.entities.User;
import com.auth_service.auth.entities.VerificationToken;
import com.auth_service.auth.repositories.BlacklistedTokenRepository;
import com.auth_service.auth.repositories.PasswordResetTokenRepository;
import com.auth_service.auth.repositories.UserRepository;
import com.auth_service.auth.repositories.VerificationTokenRepository;
import com.auth_service.auth.utils.JwtUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtTokenUtil, UserService userService,
                       UserRepository userRepository, PasswordEncoder passwordEncoder,
                       PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService, BlacklistedTokenRepository blacklistedTokenRepository, VerificationTokenRepository verificationTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public AuthenticationResponse createAuthenticationToken(AuthenticationRequest authenticationRequest) throws Exception {
        String authenticationRequestUsername = authenticationRequest.getUsername();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequestUsername, authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequestUsername);
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return new AuthenticationResponse(jwt);
    }

    public User register(RegisterRequest registerRequest) {
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setEmail(registerRequest.getEmail());
        newUser.setEnabled(false);
        newUser.setEmailVerified(false);

        Role assignedRole;
        if (registerRequest.getRole() == Role.ROLE_DOCTOR) {
            assignedRole = Role.ROLE_DOCTOR;
        } else {
            assignedRole = Role.ROLE_PATIENT; // Default to PATIENT
        }

        newUser.setRoles(Collections.singleton(assignedRole.name()));

        userRepository.save(newUser);

        VerificationToken verificationToken = new VerificationToken(newUser);
        verificationTokenRepository.save(verificationToken);

        // TODO: This is a temporary implementation. Move email sending to a dedicated notification-service.
        emailService.sendEmail(newUser.getEmail(), "Email Verification",
                "To verify your email, click the link below:\n" +
                        "http://localhost:4200/verify-email?token=" + verificationToken.getToken());

        return newUser;
    }

    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Invalid or expired token");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    public void changePassword(ChangePasswordRequest request) {
        UserDetails userDetails = (UserDetails) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert userDetails != null;
        userService.changePassword(request, userDetails);
    }

    public void logout(String token) {
        Instant expiryDate = jwtTokenUtil.extractExpiration(token).toInstant();
        BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiryDate);
        blacklistedTokenRepository.save(blacklistedToken);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        PasswordResetToken token = new PasswordResetToken(user.orElse(null));
        passwordResetTokenRepository.save(token);

        // TODO: This is a temporary implementation. Move email sending to a dedicated notification-service.
        emailService.sendEmail(user.get().getEmail(), "Password Reset Request",
                "To reset your password, click the link below:\n" +
                        "http://localhost:4200/reset-password?token=" + token.getToken());
    }

    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken());
        if (token == null || token.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Invalid or expired token");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(token);
    }

    public void forgotUsername(ForgotUsernameRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // TODO: This is a temporary implementation. Move email sending to a dedicated notification-service.
        emailService.sendEmail(user.get().getEmail(), "Forgot Username",
                "Your username is: " + user.get().getUsername());
    }

}
