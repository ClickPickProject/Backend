package com.clickpick.service;

import com.clickpick.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JWTUtil jwtUtil;

    public ResponseEntity reissueToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies){

            if (cookie.getName().equals("refresh")){
                refresh = cookie.getValue();
            }
        }

        if (refresh == null){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰이 존재하지 않습니다.");
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우 처리
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write("로그인 시간이 만료되었습니다.");
            httpResponse.getWriter().flush();
        } catch (SignatureException e) {
            // 서명이 잘못된 경우 처리
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write("잘못된 토큰입니다.");
            httpResponse.getWriter().flush();
        } catch (MalformedJwtException e) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write("토큰이 올바르지 않습니다.");
            httpResponse.getWriter().flush();
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 토큰입니다.");
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("authorization", username, role, 600000L);

        response.setHeader("authorization", newAccess);

        return  ResponseEntity.status(HttpStatus.OK).body("토큰을 재발급 하였습니다.");


    }
}
