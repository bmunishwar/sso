package com.example.ssodemo.controller;

import com.example.ssodemo.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Controller
public class HomeController {

  
    @GetMapping("/")
    public String index() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // Retrieve session attributes
        String firstName = (String) session.getAttribute("firstName");
        String lastName = (String) session.getAttribute("lastName");
        String mobileNo = (String) session.getAttribute("mobileNo");

        // Add session attributes to the model
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("mobileNo", mobileNo);

        return "home"; // Return the home page view
    }
    
    
    @GetMapping("/test")
    public String test(Model model) {
       

        return "test"; // Return the home page view
    }
}
