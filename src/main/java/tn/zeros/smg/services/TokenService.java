package tn.zeros.smg.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Service;
import tn.zeros.smg.services.IServices.ITokenService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TokenService implements ITokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Override
    public String generateJwt(Authentication auth) {
        Instant now = Instant.now();
        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        Instant expiry = now.plus(12, ChronoUnit.HOURS);
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
    public Jwt decodeJwt(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            // Log the error and handle it appropriately
            log.error("Error decoding JWT", e);
            throw new InvalidBearerTokenException("Invalid token", e);
        }
    }

    @Override
    public Boolean isTokenExpired(String token) {
        // Check if token exists
        if (token == null) {
            return true;
        }
        // Check if the token is expired
        Jwt jwt = this.decodeJwt(token);
        Instant expiresAt = jwt.getExpiresAt();
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

    @Override
    public String generateRefreshToken(Authentication auth) {
        Instant now = Instant.now();
        Instant expiry = now.plus(7, ChronoUnit.DAYS);
        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(auth.getName())
                .claim("roles", scope)
                .claim("type", "refresh")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public Map<String, String> generateTokenPair(Authentication auth) {
        String accessToken = generateJwt(auth);
        String refreshToken = generateRefreshToken(auth);
        log.info("access token is: " + accessToken);
        log.info("refresh token is: " + refreshToken);
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

}