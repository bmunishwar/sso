package com.example.ssodemo.security;

import org.springframework.security.web.session.InvalidSessionStrategy;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomInvalidSessionStrategy implements InvalidSessionStrategy {

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Redirect to a custom session expired page
        response.sendRedirect(request.getContextPath() + "/session-expired");
    }
}
