package com.example.ens.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.ens.model.User;
import com.example.ens.repository.UserRepository;
import com.example.ens.security.JwtService;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Пользователь уже существует";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider("LOCAL");
        user.setRole("USER");
        userRepository.save(user);
        return "Регистрация успешна!";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent() &&
            passwordEncoder.matches(user.getPassword(), existing.get().getPassword())) {

            String token = jwtService.generateToken(user.getEmail(), Map.of("role", existing.get().getRole()));
            return "Bearer " + token;
        }
        return "Неверный логин или пароль";
    }

    @GetMapping("/google/success")
    public String googleSuccess() {
        return "Google OAuth успешен!";
    }
}
