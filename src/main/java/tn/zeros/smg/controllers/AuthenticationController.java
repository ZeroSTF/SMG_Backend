package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.controllers.DTO.LoginDTO;
import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.controllers.DTO.LogoutResponseDTO;
import tn.zeros.smg.controllers.DTO.RegistrationDTO;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.exceptions.InvalidCredentialsException;
import tn.zeros.smg.services.IServices.IAuthService;
import tn.zeros.smg.services.IServices.ITokenService;
import tn.zeros.smg.services.IServices.IUserService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
@CrossOrigin(origins = "https://smgf.ghariani.com.tn", allowCredentials = "true")  // Enable CORS for this controller
public class AuthenticationController {

    private final IUserService userService;
    private final IAuthService authService;
    private final ITokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDTO body) {
        try {
            User user = createUserFromRegistrationDTO(body);
            User registeredUser = authService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO body) {
        try {
            LoginResponseDTO response = authService.login(body.getCode(), body.getPassword());
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            log.warn("Invalid login attempt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid code or password");
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout() {
        try {
            authService.logout();
            return ResponseEntity.ok(new LogoutResponseDTO(true, "Logout successful!"));
        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new LogoutResponseDTO(false, "Logout failed"));
        }
    }

    @GetMapping("/check-token")
    public ResponseEntity<Boolean> checkToken(@RequestHeader("AccessToken") String token) {
        try {
            boolean isTokenExpired = tokenService.isTokenExpired(token);
            return ResponseEntity.ok(!isTokenExpired);
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("RefreshToken") String refreshToken) {
        try {
            Jwt decodedRefreshToken = tokenService.decodeJwt(refreshToken);
            validateRefreshToken(decodedRefreshToken);
            String username = decodedRefreshToken.getSubject();
            UserDetails userDetails = userService.loadUserByCode(username);
            Map<String, String> tokens = tokenService.generateTokenPair(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
            return ResponseEntity.ok(tokens);
        } catch (InvalidBearerTokenException e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("An error occurred during token refresh");
        }
    }

    // Helper method to validate refresh tokens
    private void validateRefreshToken(Jwt refreshToken) {
        if (!"refresh".equals(refreshToken.getClaim("type"))) {
            throw new InvalidBearerTokenException("Invalid token type");
        }
    }

    // Helper method to create a User entity from RegistrationDTO
    private User createUserFromRegistrationDTO(RegistrationDTO body) {
        return new User(
                body.getEmail(), body.getPassword(), body.getNom(),
                body.getAdresse(), body.getCodetva(), body.getTel1(),
                body.getTel2(), body.getFax(), body.getIdfiscal()
        );
    }
}
