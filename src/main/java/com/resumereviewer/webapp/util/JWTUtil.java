package com.resumereviewer.webapp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Component
public class JWTUtil {

    @Value("${app.secret.key}")
    private String SECRET_KEY;
    private static SecretKey getSigningKey(String secret) {
        byte[] kBytes = Base64.getEncoder().encode(secret.getBytes());
        return new SecretKeySpec(kBytes, 0, kBytes.length, "HmacSHA256");
    }

    public String generateToken(String subject) {

        return Jwts.builder()
                .issuedAt(new Date())
                .subject(subject)
                .issuer("URBAN_KICKS")
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)))
                .signWith(getSigningKey(SECRET_KEY))
                .compact();

    }


    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(Base64.getEncoder().encode(SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public boolean isValidToken(String token) {
        return getClaims(token).getExpiration().after(new Date(System.currentTimeMillis()));
    }

    // code to check if token is valid as per username
    public boolean isValidToken(String token, String username) {
        String tokenUserName=getSubject(token);
        return (username.equals(tokenUserName) && !isTokenExpired(token));
    }

    // code to check if token is expired
    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date(System.currentTimeMillis()));
    }

    //code to get expiration date
    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    //code to get expiration date
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }


}
