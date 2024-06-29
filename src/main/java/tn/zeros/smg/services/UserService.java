package tn.zeros.smg.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.entities.Role;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.entities.enums.UStatus;
import tn.zeros.smg.exceptions.InvalidCredentialsException;
import tn.zeros.smg.repositories.RoleRepository;
import tn.zeros.smg.repositories.UserRepository;
import tn.zeros.smg.services.IServices.IEmailService;
import tn.zeros.smg.services.IServices.ITokenService;
import tn.zeros.smg.services.IServices.IUserService;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    //private final ConfirmationRepository confirmationRepository;

    private final IEmailService emailService;
    private final ITokenService tokenService;

    @Lazy
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    //Authentication
    @Override
    public User registerUser(User user) {
        //////////////////// Check if the email is unique/////////////////////
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return null;
        }
        //////////////////////////////////////////////////////////////////////
        String encodedPassword = encoder.encode(user.getPassword());
        Role userRole = roleRepository.findById(2L).get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);
        user.setPassword(encodedPassword);
        user.setStatus(UStatus.Pending);
        user.setRole(authorities);
        userRepository.save(user);
        /////////////////MAILING//////////////////////////
        //Confirmation confirmation = new Confirmation(user);
        //confirmationRepository.save(confirmation);
        //emailService.sendHtmlEmail(user.getNom(),user.getEmail(),confirmation.getToken());
        /////////////////////////////////////////////////
        return user;
    }

    @Override
    public LoginResponseDTO login(String email, String password) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Wrong email or password"));

        // Check if the password matches
        if (!encoder.matches(password, user.getPassword()) || !user.getStatus().equals(UStatus.Active)) {
            throw new InvalidCredentialsException("Wrong email or password");
        }

        // Generate JWT token
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        String token = tokenService.generateJwt(auth);

        String role = "user";
        Set<Role> roles = user.getRole();
        if (!roles.isEmpty()) {
            Iterator<Role> iterator = roles.iterator();
            Role firstRole = iterator.next();
            Long firstRoleId = firstRole.getId();
            if (firstRoleId == 1) {
                role = "admin";
            }
        }

        // Create and return the login response
        return new LoginResponseDTO(user.getCode(), user.getNom(), user.getEmail(), role, token);
    }

    //login with token
    @Override
    public LoginResponseDTO login(String token) {
        if (token.startsWith("{\"accessToken\":\"") && token.endsWith("\"}")) {
            token= token.substring(16, token.length() - 2);
        }
        //login with token
        Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
        String email = tokenService.decodeJwt(token).getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid token"));
        if(!user.getStatus().equals(UStatus.Active))
            return null;
        SecurityContextHolder.getContext().setAuthentication(auth);

        String role = "user";
        Set<Role> roles = user.getRole();
        if (!roles.isEmpty()) {
            Iterator<Role> iterator = roles.iterator();
            Role firstRole = iterator.next();
            Long firstRoleId = firstRole.getId();
            if (firstRoleId == 1) {
                role = "admin";
            }
        }
        return new LoginResponseDTO(user.getCode(), user.getNom(), user.getEmail(), role, token);
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public Boolean verifyToken(String token) {
        /*Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByEmail(confirmation.getUser().getEmail()).get();
        user.setStatus(UStatus.Active);
        confirmationRepository.delete(confirmation);
        userRepository.save(user);*/
        return Boolean.TRUE;
    }

    @Override
    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User retrieveUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public User retrieveUserByCode(String code) {
        return userRepository.findByCode(code).orElseThrow(() -> new EntityNotFoundException("User not found with code: " + code));
    }

    @Override
    @Transactional
    public User addUser(User c) {
        //////////////////// Check if the email is unique/////////////////////
        if (userRepository.findByEmail(c.getEmail()).isPresent()) {
            return null;
        }
        //////////////////////////////////////////////////////////////////////
        c.setPassword(encoder.encode(c.getPassword()));
        Role userRole = roleRepository.findById(c.getRole().stream().findFirst().get().getId()).get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);
        c.setRole(authorities);
        return userRepository.save(c);
    }

    @Override
    @Transactional
    public void removeUser(Long id) throws IOException {
        User user = userRepository.findById(id).orElse(null);
        if(user != null){
            /*Confirmation c = confirmationRepository.findConfirmationByUser(user);
            if(c != null){
                confirmationRepository.delete(c);
            }*/
            userRepository.deleteById(id);
        }
    }

    @Override
    public User modifyUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            return userRepository.save(user);
        } else {
            throw new EntityNotFoundException("User not found with id: " + user.getId());
        }
    }

    @Override
    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }
}
