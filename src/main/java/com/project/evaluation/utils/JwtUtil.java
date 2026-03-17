package com.project.evaluation.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    // 私钥
    private static final String SECRET = "evaluation1234567890evaluation1234567890";

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 生成token
     * @param subject
     * @param expire
     * @return
     */
    public static String createToken(String subject, long expire) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setId(UUID.randomUUID().toString())
                .setIssuer("evaluation")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .setSubject(subject)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 解析token
     * @param token
     * @return
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
