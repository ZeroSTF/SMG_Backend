package tn.zeros.smg.services;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.entities.Role;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.entities.enums.UStatus;
import tn.zeros.smg.exceptions.InvalidCredentialsException;
import tn.zeros.smg.repositories.RoleRepository;
import tn.zeros.smg.repositories.UserRepository;
import tn.zeros.smg.services.IServices.IAuthService;
import tn.zeros.smg.services.IServices.ITokenService;
import tn.zeros.smg.services.IServices.IUserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final IUserService userService;
    private final ITokenService tokenService;

    @Lazy
    private final AuthenticationManager authenticationManager;

    @Override
    public User registerUser(User user) {
        try {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            String encodedPassword = encoder.encode(user.getPassword());
            Role userRole = roleRepository.findById(2L)
                    .orElseThrow(() -> new EntityNotFoundException("Role not found"));
            Set<Role> authorities = new HashSet<>();
            authorities.add(userRole);
            user.setPassword(encodedPassword);
            user.setStatus(UStatus.Unconfirmed);
            user.setRole(authorities);
            User savedUser = userRepository.save(user);
            userService.ensureUserHasPanier(savedUser);
            userService.confirmNewEmail(savedUser);
            return savedUser;
        } catch (Exception e) {
            log.error("Error registering user: ", e);
            throw new RuntimeException("Failed to register user", e);
        }
    }

    @Override
    public LoginResponseDTO login(String code, String password) {
        try {
            User user = userRepository.findByCode(code)
                    .orElseThrow(() -> new InvalidCredentialsException("Wrong code or password"));

            if (!encoder.matches(password, user.getPassword()) || !user.getStatus().equals(UStatus.Active)) {
                throw new InvalidCredentialsException("Wrong code or password");
            }

            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(code, password));
            String accessToken = tokenService.generateJwt(auth);
            String refreshToken = tokenService.generateRefreshToken(auth);

            String role = "user";
            Set<Role> roles = user.getRole();
            if (!roles.isEmpty()) {
                Iterator<Role> iterator = roles.iterator();
                Role firstRole = iterator.next();
                Long firstRoleId = firstRole.getId();
                if (firstRoleId == 1) {
                    role = "admin";
                }
                if (firstRoleId == 3) {
                    role = "old";
                }
            }

            return new LoginResponseDTO(user.getCode(), user.getNom(), user.getEmail(), role, accessToken,
                    refreshToken);
        } catch (Exception e) {
            log.error("Error during login: ", e);
            throw new RuntimeException("Login failed", e);
        }
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

}
