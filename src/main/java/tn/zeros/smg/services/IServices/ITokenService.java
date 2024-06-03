package tn.zeros.smg.services.IServices;

import org.springframework.security.core.Authentication;
public interface ITokenService {
    public String generateJwt(Authentication auth);
}
