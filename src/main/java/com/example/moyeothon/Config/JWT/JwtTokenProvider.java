package com.example.moyeothon.Config.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key key;

    // 활성화된 토큰을 저장하는 맵
    private Map<String, String> activeTokens = new ConcurrentHashMap<>();
    // 무효화된 토큰을 저장하는 집합
    private Set<String> invalidTokens = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 64) {
            throw new IllegalArgumentException("경고 : 비밀 키의 길이는 64자 이상으로 설정할 것");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 새로운 JWT 토큰을 생성
    public String generateToken(String uid) {
        invalidateToken(uid);
        Map<String, Object> claims = new HashMap<>();
        String token = doGenerateToken(claims, uid);
        activeTokens.put(uid, token);
        return token;
    }

    // JWT 토큰을 생성
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // JWT 토큰을 생성
    public String getUidFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    // JWT 토큰에서 모든 클레임을 추출
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("토큰의 유효기간이 지나 만료되었습니다. 다시 로그인 해주세요");
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("토큰이 유효하지 않습니다");
            throw e;
        }
    }

    // JWT 토큰의 유효성과 만료 여부를 체크
    public Boolean validateToken(String token, String uid) {
        try {
            logger.info("토큰 유효성 및 만료여부를 체크합니다");
            final String userUid = getUidFromToken(token);
            if (invalidTokens.contains(token)) {
                logger.error("토큰이 무효화되었습니다.");
                return false;
            }
            return (userUid.equals(uid) && !isTokenExpired(token) && token.equals(activeTokens.get(uid)));
        } catch (ExpiredJwtException e) {
            logger.error("토큰의 유효기간이 지나 만료되었습니다. 다시 로그인 해주세요");
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("토큰이 유효하지 않습니다");
            return false;
        }
    }

    // JWT 토큰이 만료되었는지 확인
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // JWT 토큰에서 만료 기간을 추출
    private Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;
    }

    // 주어진 사용자 ID의 기존 JWT 토큰을 무효화
    public void invalidateToken(String uid) {
        String token = activeTokens.remove(uid);
        if (token != null) {
            invalidTokens.add(token);
        }
    }
}


