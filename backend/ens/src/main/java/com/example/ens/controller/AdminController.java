package com.example.ens.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String adminPage() {
        // просто перенаправляем на статический HTML
        return "redirect:/admin.html";
    }
}
