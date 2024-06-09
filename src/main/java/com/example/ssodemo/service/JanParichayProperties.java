package com.example.ssodemo.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "janparichay")
public class JanParichayProperties {
    private String userinfoUri;

    public String getUserinfoUri() {
        return userinfoUri;
    }

    public void setUserinfoUri(String userinfoUri) {
        this.userinfoUri = userinfoUri;
    }
}
