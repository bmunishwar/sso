package com.example.ssodemo.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) exception;
            logger.error("OAuth2 Authentication failed: {}", oauth2Exception.getError().toString(), oauth2Exception);
            logger.debug("Exception details:", oauth2Exception);

            String errorMessage = "OAuth2 authentication failed: " + oauth2Exception.getError().getDescription();
            request.getSession().setAttribute("oauth2Error", errorMessage);
        } else {
            logger.error("Authentication failed: {}", exception.getMessage(), exception);
            request.getSession().setAttribute("oauth2Error", "Authentication failed: " + exception.getMessage());
        }

        logger.info("Session ID: {} - Authentication failure", request.getSession().getId());

        getRedirectStrategy().sendRedirect(request, response, "/login?error=true");
    }
}
