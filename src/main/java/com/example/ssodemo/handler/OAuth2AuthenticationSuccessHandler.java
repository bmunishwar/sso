package com.example.ssodemo.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        HttpSession session = request.getSession();

        try {
            logger.info("Authentication successful. Principal class: {}", authentication.getPrincipal().getClass().getName());

            Map<String, Object> attributes;

            if (authentication.getPrincipal() instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
                attributes = oidcUser.getAttributes();
                logger.debug("OIDC User details: {}", attributes);
            } else if (authentication.getPrincipal() instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                attributes = oAuth2User.getAttributes();
                logger.debug("OAuth2 User details: {}", attributes);
            } else {
                logger.warn("Unknown principal type: {}", authentication.getPrincipal().getClass().getName());
                attributes = Map.of();
            }

            setSessionAttributes(session, attributes);

            logger.info("Session ID: {}, Attributes set: firstName={}, lastName={}, mobileNo={}, emailId={}, profilePic={}, sessionId={}, gender={}, parichayId={}, userId={}",
                        session.getId(),
                        session.getAttribute("firstName"),
                        session.getAttribute("lastName"),
                        session.getAttribute("mobileNo"),
                        session.getAttribute("emailId"),
                        session.getAttribute("profilePic"),
                        session.getAttribute("sessionId"),
                        session.getAttribute("gender"),
                        session.getAttribute("parichayId"),
                        session.getAttribute("userId"));

            response.sendRedirect("/home");

        } catch (Exception e) {
            logger.error("Error setting session attributes: {}", e.getMessage(), e);
            response.sendRedirect("/error");
        }
    }

    private void setSessionAttributes(HttpSession session, Map<String, Object> attributes) {
        String firstName = (String) attributes.get("FirstName");
        String lastName = (String) attributes.get("LastName");
        String mobileNo = (String) attributes.get("MobileNo");
        String emailId = (String) attributes.get("EmailId");
        String profilePic = (String) attributes.get("ProfilePic");
        String sessionId = (String) attributes.get("sessionId");
        String gender = (String) attributes.get("Gender");
        String parichayId = (String) attributes.get("ParichayId");
        String userId = (String) attributes.get("userId");

        logger.debug("Extracted session attributes: firstName={}, lastName={}, mobileNo={}, emailId={}, profilePic={}, sessionId={}, gender={}, parichayId={}, userId={}",
                     firstName, lastName, mobileNo, emailId, profilePic, sessionId, gender, parichayId, userId);

        session.setAttribute("firstName", firstName);
        session.setAttribute("lastName", lastName);
        session.setAttribute("mobileNo", mobileNo);
        session.setAttribute("emailId", emailId);
        session.setAttribute("profilePic", profilePic);
        session.setAttribute("sessionId", sessionId);
        session.setAttribute("gender", gender);
        session.setAttribute("parichayId", parichayId);
        session.setAttribute("userId", userId);
    }
}
