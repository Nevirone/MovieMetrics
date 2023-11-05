package com.example.moviemetrics.api.util;

import com.example.moviemetrics.api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTProvider {
    final static String secret = "my_secret_key_that_is_long_enough";
    final static private int tokenExpirationTimeMs = 2 * 60 * 60 * 100;


    public static String generateToken(User user) {
        final Date issuedAt = new Date(System.currentTimeMillis());
        final Date expireAt = new Date(System.currentTimeMillis() + tokenExpirationTimeMs);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .header()
                    .type("JWT")
                    .and()
                .issuer("MovieMetrics")
                .subject(user.getEmail())
                .claims(extraClaims)
                .issuedAt(issuedAt)
                .expiration(expireAt)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public static Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        Claims payload = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        System.out.println(payload.getSubject());
        System.out.println(payload.get("id"));
        return payload;
    }



}
