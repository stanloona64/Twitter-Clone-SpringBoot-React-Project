package com.example.twitterclone.controller;

import com.example.twitterclone.dto.AuthResponse;
import com.example.twitterclone.entity.User;
import com.example.twitterclone.exception.ApiException;
import com.example.twitterclone.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3200")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ApiException("Bu kullanıcı adı zaten alınmış!", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(savedUser.getId(), savedUser.getUsername(), "Kayıt başarılı!"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new ApiException("Geçersiz kullanıcı adı veya şifre!", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            throw new ApiException("Geçersiz kullanıcı adı veya şifre!", HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(new AuthResponse(existingUser.getId(), existingUser.getUsername(), "Giriş başarılı!"));
    }
}
