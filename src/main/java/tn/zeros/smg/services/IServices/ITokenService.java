package tn.zeros.smg.services.IServices;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public interface ITokenService {
    String generateJwt(Authentication auth);

    Jwt decodeJwt(String token);
    Boolean isTokenExpired(String token);
}
