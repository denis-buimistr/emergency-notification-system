package com.example.ens.controller;

import com.example.ens.model.User;
import com.example.ens.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // получить всех пользователей
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // получить пользователя по id
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    // обновить пользователя (только админ)
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updated) {
        return userRepository.findById(id)
                .map(u -> {
                    u.setFirstName(updated.getFirstName());
                    u.setLastName(updated.getLastName());
                    u.setEmail(updated.getEmail());
                    u.setRole(updated.getRole());
                    return userRepository.save(u);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // удалить пользователя
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
