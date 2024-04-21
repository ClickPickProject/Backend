package com.clickpick.jwt;

import lombok.Data;

@Data
public class JWTUserDto {
    private String userId;
    private String password;
    private String nickname;
    private String role;

    public JWTUserDto(String userId, String nickname ,String password, String role) {
        this.userId = userId;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    //세션용
    public JWTUserDto(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }
}
