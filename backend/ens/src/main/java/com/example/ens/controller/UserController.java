package com.example.ens.controller;

import com.example.ens.model.User;
import com.example.ens.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.ens.security.AuthUtil;
import com.example.ens.security.AuthUtil;


import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String email = AuthUtil.getEmail();
        if (email == null) return ResponseEntity.status(401).body("Unauthorized");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody User dto) {
        String email = AuthUtil.getEmail();
        if (email == null) return ResponseEntity.status(401).body("Unauthorized");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBirthDate(dto.getBirthDate());
        user.setBloodType(dto.getBloodType());
        user.setWeight(dto.getWeight());
        user.setHeight(dto.getHeight());
        user.setAllergies(dto.getAllergies());
        user.setDiseases(dto.getDiseases());
        user.setEmergencyContact(dto.getEmergencyContact());
        user.setPhone(dto.getPhone());
        user.setNotifNewAlerts(dto.getNotifNewAlerts());
        user.setNotifDangerZone(dto.getNotifDangerZone());
        user.setShareLocation(dto.getShareLocation());


        userRepository.save(user);

        return ResponseEntity.ok("Профиль обновлён");
    }
}
