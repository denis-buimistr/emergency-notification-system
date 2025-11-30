package com.example.ens.security;

import com.example.ens.model.User;
import com.example.ens.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    // Получение email текущего пользователя
    public static String getEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return auth.getName(); // email
    }

    // Получение объекта User по email
    public static User getUser(UserRepository userRepository) {
        String email = getEmail();
        if (email == null) return null;

        return userRepository.findByEmail(email).orElse(null);
    }
}
