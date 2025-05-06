package com.domulink.util;

import com.domulink.exception.InvalidTokenException;
import com.domulink.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
@Slf4j
@Component
public class JwtTokenUtil {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration-time}")
    private long expirationTime;
    @Value("${application.security.jwt.refresh-token.expiration-time}")
    private long refreshExpiration;



    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public String extractUuid(String token) {
        return extractClaim(token, claim -> claim.get("uuid", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, CustomUserDetails customUserDetails) {

        try{

            final String email = extractEmail(token);
            return (email.equals(customUserDetails.getUsername()) && !isTokenExpired(token));

        } catch (SignatureException e) {
            log.info("Invalid JWT signature",e);
            throw new InvalidTokenException("Invalid token signature");
        } catch (JwtException e) {
            log.error("Invalid token", e);
            throw new InvalidTokenException("Invalid token");
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



    public String generateToken(CustomUserDetails customUserDetails) {

        return Jwts
                .builder()
                .subject(customUserDetails.getUsername())
                .claim("role", customUserDetails.getAuthorities())
                .claim("uuid", customUserDetails.getUuid())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(CustomUserDetails customUserDetails) {
        return Jwts
                .builder()
                .subject(customUserDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }






    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
