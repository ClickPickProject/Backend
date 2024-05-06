package com.clickpick.controller;



import com.clickpick.jwt.JWTUtil;
import com.clickpick.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    @PostMapping("/api/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseEntity responseEntity = jwtService.reissueToken(request,response);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }


}
