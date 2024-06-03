package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.controllers.DTO.LoginDTO;
import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.controllers.DTO.LogoutResponseDTO;
import tn.zeros.smg.controllers.DTO.RegistrationDTO;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.services.IServices.IUserService;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthenticationController {
    private final IUserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDTO body){
        User user = new User(body.getEmail(), body.getPassword(), body.getNom());
        User registeredUser = userService.registerUser(user);
        if (registeredUser != null) {
            return ResponseEntity.ok(registeredUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already in use");
        }
    }

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody LoginDTO body){
        return userService.login(body.getEmail(), body.getPassword());
    }
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout() {
        try {
            userService.logout(); // Perform logout logic
            return ResponseEntity.ok(new LogoutResponseDTO(true, "Logout successful!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LogoutResponseDTO(false, e.getMessage()));
        }
    }
}