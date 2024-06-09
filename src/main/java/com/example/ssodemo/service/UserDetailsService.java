package com.example.ssodemo.service;

import com.example.ssodemo.service.JanParichayProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final JanParichayProperties janParichayProperties;

    public UserDetailsService(ObjectMapper objectMapper, JanParichayProperties janParichayProperties) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
        this.janParichayProperties = janParichayProperties;
    }

    public Map<String, Object> getUserDetails(String accessToken) {
        String userDetailsUri = janParichayProperties.getUserinfoUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userDetailsUri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            logger.debug("User details response: {}", response.getBody());

            return objectMapper.readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            logger.error("Error fetching user details", e);
            throw new RuntimeException("Error fetching user details", e);
        }
    }
}
