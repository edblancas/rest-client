package com.edblancas.restclient.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BearerToken {
    private String access_token;
    public BearerToken() {
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public String toString() {
        return "BearerToken{" +
                "access_token='" + access_token + '\'' +
                '}';
    }
}
