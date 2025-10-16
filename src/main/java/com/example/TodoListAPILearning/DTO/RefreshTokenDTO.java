package com.example.TodoListAPILearning.DTO;

public class RefreshTokenDTO {

    private String refreshToken;

    public RefreshTokenDTO() {
    }

    public RefreshTokenDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
