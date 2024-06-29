package tn.zeros.smg.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import tn.zeros.smg.services.IServices.ITokenService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TokenService implements ITokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Override
    public String generateJwt(Authentication auth){
        Instant now = Instant.now();
        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        Instant expiry = now.plus(1, ChronoUnit.HOURS); // Set the expiration time to 1 hour from now
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(auth.getName())
                .claim("roles", scope)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public Jwt decodeJwt(String token){
        try {
            if (token.startsWith("{\"accessToken\":\"") && token.endsWith("\"}")) {
                token= token.substring(16, token.length() - 2);
            }
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            // Log the error and handle it appropriately
            throw new InvalidBearerTokenException("Invalid token", e);
        }
    }

    @Override
    public Boolean isTokenExpired(String token){
        //check if token exists
        if(token == null){
            return true;
        }
        //check if the token is expired without decoding it
        Jwt jwt=this.decodeJwt(token);
        return jwt.getExpiresAt().isBefore(Instant.now());
    }

}