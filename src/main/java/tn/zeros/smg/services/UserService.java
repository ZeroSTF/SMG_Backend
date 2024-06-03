package tn.zeros.smg.services;

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
import tn.zeros.smg.entities.Confirmation;
import tn.zeros.smg.entities.Role;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.entities.enums.UStatus;
import tn.zeros.smg.repositories.ConfirmationRepository;
import tn.zeros.smg.repositories.RoleRepository;
import tn.zeros.smg.repositories.UserRepository;
import tn.zeros.smg.services.IServices.IEmailService;
import tn.zeros.smg.services.IServices.ITokenService;
import tn.zeros.smg.services.IServices.IUserService;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationRepository confirmationRepository;

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
        Confirmation confirmation = new Confirmation(user);
        userRepository.save(user);
        confirmationRepository.save(confirmation);
        /////////////////MAILING//////////////////////////
        emailService.sendHtmlEmail(user.getNom(),user.getEmail(),confirmation.getToken());
        /////////////////////////////////////////////////
        return user;
    }

    @Override
    public LoginResponseDTO login(String email, String password) {
        try{
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            String token = tokenService.generateJwt(auth);
            User user = userRepository.findByEmail(email).get();
            return new LoginResponseDTO(user.getNom(), token);
        } catch (AuthenticationException e){
            //e.printStackTrace();
            return new LoginResponseDTO("No email to return", "Invalid email/password supplied");
        }
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByEmail(confirmation.getUser().getEmail()).get();
        user.setStatus(UStatus.Active);
        confirmationRepository.delete(confirmation);
        userRepository.save(user);
        return Boolean.TRUE;
    }
}
