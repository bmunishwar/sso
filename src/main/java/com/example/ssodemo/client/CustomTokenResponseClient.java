package com.example.ssodemo.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private static final Logger logger = LoggerFactory.getLogger(CustomTokenResponseClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CustomTokenResponseClient(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        String clientId = authorizationGrantRequest.getClientRegistration().getClientId();
        String clientSecret = authorizationGrantRequest.getClientRegistration().getClientSecret();
        String codeVerifier = (String) authorizationGrantRequest.getAuthorizationExchange()
                .getAuthorizationRequest().getAdditionalParameters().get("code_verifier");
        String authorizationCode = authorizationGrantRequest.getAuthorizationExchange()
                .getAuthorizationResponse().getCode();
        String redirectUri = authorizationGrantRequest.getAuthorizationExchange()
                .getAuthorizationRequest().getRedirectUri();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("code_verifier", codeVerifier);
        requestBody.put("grant_type", "authorization_code");
        requestBody.put("redirect_uri", redirectUri);
        requestBody.put("code", authorizationCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

            logger.debug("Requesting OAuth2 token with the following details: client_id={}, grant_type={}, redirect_uri={}, code_verifier={}, code={}",
                    clientId, "authorization_code", redirectUri, codeVerifier, authorizationCode);

            ResponseEntity<String> response = restTemplate.exchange(
                    authorizationGrantRequest.getClientRegistration().getProviderDetails().getTokenUri(),
                    HttpMethod.POST,
                    entity,
                    String.class);

            logger.debug("Received OAuth2 token response: {}", response.getBody());

            Map<String, Object> tokenResponse = objectMapper.readValue(response.getBody(), Map.class);

            return OAuth2AccessTokenResponse.withToken((String) tokenResponse.get("access_token"))
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .expiresIn(((Number) tokenResponse.get("expires_in")).longValue())
                    .refreshToken((String) tokenResponse.get("refresh_token"))
                    .scopes(authorizationGrantRequest.getClientRegistration().getScopes())
                    .build();

        } catch (HttpClientErrorException e) {
            logger.error("Error response from token endpoint: status={}, response={}", e.getStatusCode(), e.getResponseBodyAsString());
            OAuth2Error oauth2Error = new OAuth2Error("invalid_token_response", "HTTP error while fetching token: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), null);
            throw new OAuth2AuthorizationException(oauth2Error, e);
        } catch (Exception e) {
            logger.error("Exception occurred while fetching token", e);
            OAuth2Error oauth2Error = new OAuth2Error("invalid_token_response", "Error while fetching token: " + e.getMessage(), null);
            throw new OAuth2AuthorizationException(oauth2Error, e);
        }
    }
}
