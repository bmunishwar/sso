package com.example.ssodemo.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final RestTemplate restTemplate;

    public CustomOAuth2UserService() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String userInfoEndpointUri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();

        if (!userInfoEndpointUri.isEmpty()) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization",accessToken); 
                headers.add("Content-Type", "application/json");

                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);

                Map<String, Object> userAttributes = response.getBody();

                // Log the user attributes (for debugging)
                System.out.println("Received User Attributes: " + userAttributes);

                // Extract authorities from scopes or assign default authority
                Set<SimpleGrantedAuthority> authorities = userRequest.getClientRegistration().getScopes().stream()
                        .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                        .collect(Collectors.toSet());

                if (authorities.isEmpty()) {
                    authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
                }

                return new DefaultOAuth2User(
                    authorities,
                    userAttributes,
                    "MobileNo" // Use the attribute you choose as the unique identifier
                );

            } catch (HttpStatusCodeException ex) {
                OAuth2Error oauth2Error = new OAuth2Error("invalid_user_info_response", "An error occurred while accessing the user info endpoint: " + ex.getResponseBodyAsString(), null);
                throw new OAuth2AuthenticationException(oauth2Error, ex);
            } catch (Exception ex) {
                OAuth2Error oauth2Error = new OAuth2Error("invalid_user_info_response", "An error occurred while accessing the user info endpoint", null);
                throw new OAuth2AuthenticationException(oauth2Error, ex);
            }
        }

        return super.loadUser(userRequest);
    }
}
