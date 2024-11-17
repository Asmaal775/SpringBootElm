package org.example.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${jwtSecret}")
  private String jwtSecret;

  @Value("${jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateToken(String username) {
    long expirationTime = 24 * 60 * 60 * 1000;  // 24 hours in milliseconds
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // Token expiration time
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact();
  }



  private Claims extractAllClaims(String token) {
    return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
  }

private Key key() {
  return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
}
public String getUserNameFromJwtToken(String token) {
  return Jwts.parserBuilder().setSigningKey(key()).build()
          .parseClaimsJws(token).getBody().getSubject();
}
// JWT tokens are signed with a secret key or private key
public boolean validateJwtToken(String authToken) {
  try {
    Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
    return true;
  } catch (MalformedJwtException e) {
    logger.error("Invalid JWT token: {}", e.getMessage());
  } catch (ExpiredJwtException e) {
    logger.error("JWT token is expired: {}", e.getMessage());
  } catch (UnsupportedJwtException e) {
    logger.error("JWT token is unsupported: {}", e.getMessage());
  } catch (IllegalArgumentException e) {
    logger.error("JWT claims string is empty: {}", e.getMessage());
  }

  return false;
}
}