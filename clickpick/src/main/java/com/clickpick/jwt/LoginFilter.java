package com.clickpick.jwt;

import com.clickpick.dto.user.LoginReq;
import com.clickpick.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/login";
    private static final String HTTP_METHOD = "POST";
    private static final String CONTENT_TYPE = "application/json";
    private final AuthenticationManager authenticationManager;

    private final JWTUtil jwtUtil;

    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD); // 기본인 /login 에서 /api/login 으로 url 변경
    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil){
        
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.setRequiresAuthenticationRequestMatcher(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LoginReq loginReq = objectMapper.readValue(request.getInputStream(), LoginReq.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginReq.getId(),loginReq.getPassword(),null);
            return  authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        


    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        String nickname = customUserDetails.getNickname();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // String id = jwtUtil.getUsername(token); 아이디 값 추출

        String token = jwtUtil.createJwt(username, role, 3600000L); // 3600000ms => 60분

        response.addHeader("Authorization", "Bearer " + token);
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("nickname", nickname );
        jsonResponse.addProperty("message", "로그인 되었습니다.");
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("잘못된 아이디 또는 비밀번호 입니다.");
        response.getWriter().flush();

    }




}
