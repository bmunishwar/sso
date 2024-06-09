package com.example.ssodemo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Value("${spring.security.oauth2.client.provider.janparichay.user-info-uri}")
    private String userInfoUri;

    @Value("${spring.security.oauth2.client.provider.janparichay.token-uri}")
    private String tokenUri;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    public String getUserDetails(OAuth2AuthorizedClient authorizedClient) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    public Map<String, Object> exchangeToken(String clientId, String clientSecret, String codeVerifier,
                                             String grantType, String redirectUri, String code) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("code_verifier", codeVerifier);
        requestBody.put("grant_type", grantType);
        requestBody.put("redirect_uri", redirectUri);
        requestBody.put("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN); // Set content type to JSON
        
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
		System.out.println("heloooooooooooooooooooooooooooooooooooooooooooooooooo");

        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestBody); // Convert Map to JSON
            HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

            logger.debug("Sending token request to {}: {}", tokenUri, requestBodyJson);
            ResponseEntity<String> response = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, String.class);
            logger.debug("Received response from token endpoint: {}", response);

            return objectMapper.readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            logger.error("Error during token exchange: {}", e.getMessage(), e);
            throw new RuntimeException("Error during token exchange", e);
        }
    }
}
