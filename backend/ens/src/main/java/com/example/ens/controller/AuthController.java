package com.example.ens.controller;

import com.example.ens.model.User;
import com.example.ens.repository.UserRepository;
import com.example.ens.security.JwtService;
import com.example.ens.service.LoginLogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import com.example.ens.repository.ResetTokenRepository;
import com.example.ens.model.ResetToken;
import java.util.UUID;
import java.time.LocalDateTime;

import com.example.ens.service.EmailService;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginLogService loginLogService;
    private final ResetTokenRepository resetTokenRepository;
    private final EmailService emailService;



    public AuthController(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      LoginLogService loginLogService,
                      ResetTokenRepository resetTokenRepository,
                      EmailService emailService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.loginLogService = loginLogService;
    this.resetTokenRepository = resetTokenRepository;
    this.emailService = emailService;
}



    @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody Map<String, String> body) {

    String email = body.get("email");
    String password = body.get("password");
    String name = body.get("name");

    if (email == null || password == null || name == null)
        return ResponseEntity.badRequest().body("Некорректные данные");

    if (userRepository.findByEmail(email).isPresent()) {
        return ResponseEntity.badRequest().body("Пользователь уже существует");
    }

    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));

    // Имя кладём в firstName
    user.setFirstName(name);
    user.setLastName("");

    user.setProvider("LOCAL");
    user.setRole("USER");

    userRepository.save(user);

    loginLogService.write(email, "REGISTER");

    return ResponseEntity.ok("Регистрация успешна!");
}


    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

    String email = body.get("email");
    String password = body.get("password");

    Optional<User> existing = userRepository.findByEmail(email);

    if (existing.isEmpty())
        return ResponseEntity.status(401).body("Неверный логин или пароль");

    User user = existing.get();

    if (user.getPassword() == null)
        return ResponseEntity.status(401).body("Этот аккаунт создан через Google. Войдите через Google.");

    if (!passwordEncoder.matches(password, user.getPassword()))
        return ResponseEntity.status(401).body("Неверный логин или пароль");

    String token = jwtService.generateToken(
            user.getEmail(),
            Map.of("role", user.getRole())
    );

    loginLogService.write(user.getEmail(), "LOCAL");

    return ResponseEntity.ok(Map.of(
            "token", token,
            "role", user.getRole()
    ));
}



    @GetMapping("/google/success")
public void googleSuccess(
        HttpServletResponse response,
        @AuthenticationPrincipal OAuth2User oauthUser
) throws IOException {

    String email = oauthUser.getAttribute("email");
    String firstName = oauthUser.getAttribute("given_name");
    String lastName = oauthUser.getAttribute("family_name");

    User user = userRepository.findByEmail(email)
            .orElseGet(() -> {
                User u = new User();
                u.setEmail(email);
                u.setFirstName(firstName);
                u.setLastName(lastName);
                u.setProvider("GOOGLE");
                u.setRole("USER");
                return userRepository.save(u);
            });

    String token = jwtService.generateToken(
            user.getEmail(),
            Map.of("role", user.getRole())
    );

    loginLogService.write(user.getEmail(), "GOOGLE");

    response.sendRedirect("/map.html?token=" + token);
}

@PostMapping("/forgot")
public ResponseEntity<?> forgot(@RequestBody Map<String, String> body) {

    String email = body.get("email");
    Optional<User> user = userRepository.findByEmail(email);

    // Даже если нет пользователя — НЕ выдаем ошибку (безопасность)
    if (user.isEmpty()) {
        return ResponseEntity.ok("Если email существует — мы отправили код.");
    }

    // генерируем токен
    String token = UUID.randomUUID().toString();

    ResetToken rt = new ResetToken();
    rt.setEmail(email);
    rt.setToken(token);
    rt.setExpiresAt(LocalDateTime.now().plusMinutes(15));

    resetTokenRepository.save(rt);

    // отправка письма
    emailService.send(
            email,
            "Восстановление пароля ENS",
            "Ваш код восстановления: " + token
    );

    return ResponseEntity.ok("Письмо отправлено");
}

@PostMapping("/reset")
public ResponseEntity<?> reset(@RequestBody Map<String, String> body) {

    String token = body.get("token");
    String password = body.get("password");

    ResetToken rt = resetTokenRepository.findByToken(token).orElse(null);

    if (rt == null || rt.getExpiresAt().isBefore(LocalDateTime.now())) {
        return ResponseEntity.badRequest().body("Код недействителен");
    }

    User user = userRepository.findByEmail(rt.getEmail()).orElseThrow();

    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);

    resetTokenRepository.delete(rt);

    return ResponseEntity.ok("Пароль успешно изменён");
}





}
