package tn.zeros.smg.services.IServices;

import org.springframework.security.core.Authentication;
public interface ITokenService {
    String generateJwt(Authentication auth);
}
