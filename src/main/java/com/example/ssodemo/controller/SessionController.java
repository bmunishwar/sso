package com.example.ssodemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SessionController {

    @GetMapping("/session-expired")
    public String sessionExpired() {
        return "session-expired";  // The name of the Thymeleaf template without the ".html" extension
    }
}
