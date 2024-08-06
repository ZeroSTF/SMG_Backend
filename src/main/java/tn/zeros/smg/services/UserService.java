package tn.zeros.smg.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.entities.*;
import tn.zeros.smg.entities.enums.UStatus;
import tn.zeros.smg.exceptions.InvalidCredentialsException;
import tn.zeros.smg.repositories.ConfirmationRepository;
import tn.zeros.smg.repositories.PanierRepository;
import tn.zeros.smg.repositories.RoleRepository;
import tn.zeros.smg.repositories.UserRepository;
import tn.zeros.smg.services.IServices.IEmailService;
import tn.zeros.smg.services.IServices.INotificationService;
import tn.zeros.smg.services.IServices.ITokenService;
import tn.zeros.smg.services.IServices.IUserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationRepository confirmationRepository;
    private final PanierRepository panierRepository;

    private final IEmailService emailService;
    private final ITokenService tokenService;
    private final INotificationService notificationService;

    @Lazy
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    public static final String UPLOAD_DIR = "uploads/matricules/";

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
        user.setStatus(UStatus.Unconfirmed);
        user.setRole(authorities);
        userRepository.save(user);
        ensureUserHasPanier(user);
        confirmNewEmail(user);
        return user;
    }

    @Override
    public LoginResponseDTO login(String code, String password) {
        // Find the user by code
        User user = userRepository.findByCode(code)
                .orElseThrow(() -> new InvalidCredentialsException("Wrong code or password"));

        // Check if the password matches
        if (!encoder.matches(password, user.getPassword()) || !user.getStatus().equals(UStatus.Active)) {
            throw new InvalidCredentialsException("Wrong code or password");
        }

        // Generate JWT token
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(code, password));
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
            if (firstRoleId == 3) {
                role = "old";
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
        String code = tokenService.decodeJwt(token).getSubject();
        User user = userRepository.findByCode(code)
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
    @Transactional
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findById(confirmation.getUser().getId()).get();
        user.setStatus(UStatus.Pending);
        confirmationRepository.delete(confirmation);
        //notify all admins that a new user is pending
        log.info("notifying all admins");
        List<User> admins = userRepository.findAdminUsers();
        admins.forEach(admin -> {
            Notification N= Notification.builder().title("Nouveau utilisateur en attente").description("Un nouveau utilisateur est en attente de confirmation").useRouter(true).link("/dashboards/clients/"+user.getId()).user(admin).build();
            notificationService.addNotification(N);
        });
        userRepository.save(user);
        return Boolean.TRUE;
    }

    @Override
    public List<User> retrieveAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "nom"));
    }

    @Override
    public User retrieveUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
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
            this.deletePhoto(user.getPhotomat());
            Confirmation c = confirmationRepository.findConfirmationByUser(user);
            if(c != null){
                confirmationRepository.delete(c);
            }
            userRepository.deleteById(id);
        }
    }

    @Override
    public User modifyUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            //if the user is old, change him to user
            if (existingUser.get().getRole().stream().findFirst().get().getId() == 3) {
                Role userRole = roleRepository.findById(2L).get();
                Set<Role> authorities = new HashSet<>();
                authorities.add(userRole);
                user.setRole(authorities);
            }
            //if email is changed, confirm the new email
            if (!existingUser.get().getEmail().equals(user.getEmail())) {
                user.setStatus(UStatus.Unconfirmed);
                confirmNewEmail(user);
            }
            return userRepository.save(user);
        } else {
            throw new EntityNotFoundException("User not found with id: " + user.getId());
        }
    }

    @Override
    public User loadUserByCode(String code) {
        return userRepository.findByCode(code).orElseThrow(() -> new EntityNotFoundException("User not found with code: " + code));
    }

    @Override
    public void confirmNewEmail(User user) {
        /////////////////MAILING//////////////////////////
        Confirmation confirmation = new Confirmation(user);
        confirmationRepository.save(confirmation);
        emailService.sendHtmlEmail(user.getNom(),user.getEmail(),confirmation.getToken());
        /////////////////////////////////////////////////
    }

    @Override
    public List<User> chercherUser(String nom) {
        if(nom == null || nom.isEmpty()){
            return userRepository.findAll(Sort.by(Sort.Direction.ASC, "nom"));
        }
        return userRepository.findByNomContainingIgnoreCaseOrderByNom(nom);
    }

    @Override
    public String savePhoto(MultipartFile file) throws IOException {
        // Create a unique file name to prevent conflicts
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        // Create the directory if it doesn't exist
        createUploadDirectoryIfNotExist();
        // Get the file path to save the image
        String filePath = UPLOAD_DIR + fileName;
        // Save the file to the specified location
        Path destPath = Paths.get(filePath);
        Files.copy(file.getInputStream(), destPath);

        return fileName;
    }
    @Override
    public void deletePhoto(String fileName) throws IOException {
        // Construct the file path
        String filePath = UPLOAD_DIR + fileName;

        // Create a Path object for the file
        Path path = Paths.get(filePath);

        // Check if the file exists
        if (Files.exists(path)) {
            // Delete the file
            Files.delete(path);
            System.out.println("Profile picture deleted successfully: " + fileName);
        } else {
            // File doesn't exist
            System.out.println("Profile picture not found: " + fileName);
        }
    }

    // Helper method to generate a unique file name
    private String generateUniqueFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return uuid + extension;
    }

    // Helper method to create upload directory if it doesn't exist
    private void createUploadDirectoryIfNotExist() {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Transactional
    public void ensureUserHasPanier(User user) {
        if (user.getPanier() == null) {
            Panier panier = new Panier();
            panier.setUser(user);
            user.setPanier(panier);
            userRepository.save(user);
        }
    }

    @Transactional
    public void ensureAllUsersHavePaniers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            ensureUserHasPanier(user);
        }
    }

    @Override
    public Panier getUserPanier(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return user.getPanier();
    }

    @Override
    public String soldeSum() {
        List<String> soldes = userRepository.findSoldes();
        //ignore empty strings
        soldes.removeIf(String::isEmpty);
        double sum = soldes.stream().mapToDouble(s -> Double.parseDouble(s.replaceAll("€", "").replaceAll("-", "").replaceAll(",", ".").replaceAll(" ", ""))).sum();
        return String.format("%.2f", sum);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentCode = authentication.getName();
        return userRepository.findByCode(currentCode).orElseThrow(() -> new EntityNotFoundException("User not found with code: " + currentCode));
    }
}

