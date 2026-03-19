package com.auth_service.auth.services;

import com.auth_service.auth.dtos.ChangePasswordRequest;
import com.auth_service.auth.dtos.Role;
import com.auth_service.auth.entities.User;
import com.auth_service.auth.repositories.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails
                .User(user.getUsername(), user.getPassword(), authorities);
    }

    public void changePassword(ChangePasswordRequest request, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void updateUserRole(String username, Role role, boolean grant) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<String> roles = new HashSet<>(user.getRoles());

        if (grant) {
            roles.add(role.name());
        } else {
            roles.remove(role.name());
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

}
