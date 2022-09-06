package com.imizsoft.backendsecurity.security.jwt;

import com.imizsoft.backendsecurity.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${com.imizsoft.backensecurity.jwtkey}")
    private String key;
    @Value("${com.imizsoft.backensecurity.jwtexpiration}")
    private int jwtExpirationMs;

    @SuppressWarnings("deprecation")
	public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        
        /*SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String base64Key = Encoders.BASE64.encode(secretKey.getEncoded());*/
         //or HS384 or HS512
        /*try {
        	SecretKey secretKey = KeyGenerator.getInstance(key).generateKey();
            base64Key = Encoders.BASE64.encode(secretKey.getEncoded());
        }
        catch (NoSuchAlgorithmException e) {
        	logger.error(e.getMessage());
        }*/
        return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();
    }

    @SuppressWarnings("deprecation")
	public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }

    @SuppressWarnings("deprecation")
	public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
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
