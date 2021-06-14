package com.example.riderremake.Services;

public class TokenModel {
    private String Token;

    public TokenModel(String token) {
        Token = token;
    }

    public TokenModel() {
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
