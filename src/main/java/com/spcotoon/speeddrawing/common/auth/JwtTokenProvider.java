package com.spcotoon.speeddrawing.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String secretKey;

    private final long ACCESS_EXPIRE = 30 * 60 * 1000L;
    private Key SECRET_KEY;

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey) {
        this.secretKey = secretKey;
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createAccessToken(Long id, String email, String nickname ,String role) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(id));
        claims.put("email", email);
        claims.put("nickname", nickname);
        claims.put("role", role);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_EXPIRE))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Key getSigningKey() {
        return SECRET_KEY;
    }

    public String getNicknameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get("nickname");
    }

    public JwtTokenUserInfo getUserInfoFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        String memberIdStr = claims.get("sub", String.class);
        Long memberId = Long.valueOf(memberIdStr);
        String email = claims.get("email", String.class);
        String nickname = claims.get("nickname", String.class);

        return JwtTokenUserInfo.builder()
                .memberId(memberId)
                .email(email)
                .nickname(nickname)
                .build();
    }
}
