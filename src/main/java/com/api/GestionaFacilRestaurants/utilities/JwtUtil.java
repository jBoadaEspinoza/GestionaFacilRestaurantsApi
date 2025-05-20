package com.api.GestionaFacilRestaurants.utilities;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.api.GestionaFacilRestaurants.models.Authorization;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    @Value("${spring.profiles.active}")
    private String environment;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    private static final long TOKEN_EXTENSION_TIME = 15 * 60 * 1000;


    public String extendTokenExpiration(String token) {
        Claims claims = extractAllClaims(token);
        Date issuedAt = claims.getIssuedAt(); // Fecha de emisión original
        Date originalExpiration = claims.getExpiration(); // Fecha de expiración original
        Date extendedExpiration = new Date(originalExpiration.getTime() + TOKEN_EXTENSION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt) // Mantener la fecha de emisión original
                .setExpiration(extendedExpiration) // Nueva fecha de expiración extendida
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractUserName(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public Long extractUid(String token){
        Claims claims=extractAllClaims(token);
        return claims.get("uid",Long.class);
    }

    public Long extractBusinessRuc(String token){
        Claims claims = extractAllClaims(token);
        return claims.get("bruc",Long.class);
    }

    public String extractEnvironment(String token){
        Claims claims=extractAllClaims(token);
        return claims.get("env",String.class);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails, jwtExpiration);
    }

    public String generateToken(UserDetails userDetails,Map<String, Object> extraClaims){
        return createToken(extraClaims,userDetails,jwtExpiration);
    }

    private String createToken(Map<String, Object> claims,UserDetails userDetails, long expiration) {
        Authorization user = (Authorization) userDetails;
        claims.put("uid", user.getId());
        claims.put("bruc",user.getBusinessRuc());
        claims.put("env", environment);
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(),SignatureAlgorithm.HS256)
            .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
