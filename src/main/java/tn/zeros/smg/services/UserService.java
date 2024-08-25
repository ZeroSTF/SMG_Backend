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
import tn.zeros.smg.controllers.DTO.UserDTO;
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
        try {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            String encodedPassword = encoder.encode(user.getPassword());
            Role userRole = roleRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("Role not found"));
            Set<Role> authorities = new HashSet<>();
            authorities.add(userRole);
            user.setPassword(encodedPassword);
            user.setStatus(UStatus.Unconfirmed);
            user.setRole(authorities);
            User savedUser = userRepository.save(user);
            ensureUserHasPanier(savedUser);
            confirmNewEmail(savedUser);
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

            return new LoginResponseDTO(user.getCode(), user.getNom(), user.getEmail(), role, token);
        } catch (Exception e) {
            log.error("Error during login: ", e);
            throw new RuntimeException("Login failed", e);
        }
    }

    @Override
    public LoginResponseDTO login(String token) {
        try {
            if (token.startsWith("{\"accessToken\":\"") && token.endsWith("\"}")) {
                token = token.substring(16, token.length() - 2);
            }
            Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
            String code = tokenService.decodeJwt(token).getSubject();
            User user = userRepository.findByCode(code)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid token"));
            if (!user.getStatus().equals(UStatus.Active))
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
        } catch (Exception e) {
            log.error("Error during token login: ", e);
            throw new RuntimeException("Token login failed", e);
        }
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional
    public Boolean verifyToken(String token) {
        try {
            Confirmation confirmation = confirmationRepository.findByToken(token);
            if (confirmation == null) {
                throw new EntityNotFoundException("Confirmation not found for token: " + token);
            }
            User user = userRepository.findById(confirmation.getUser().getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            user.setStatus(UStatus.Pending);
            confirmationRepository.delete(confirmation);
            log.info("notifying all admins");
            List<User> admins = userRepository.findAdminUsers();
            admins.forEach(admin -> {
                Notification N = Notification.builder().title("Nouveau utilisateur en attente").description("Un nouveau utilisateur est en attente de confirmation").useRouter(true).link("/dashboards/clients/" + user.getId()).user(admin).build();
                notificationService.addNotification(N);
            });
            userRepository.save(user);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Error verifying token: ", e);
            throw new RuntimeException("Token verification failed", e);
        }
    }

    @Override
    public List<UserDTO> retrieveAllUsers() {
        try {
            return userRepository.findAllProjectedBy(Sort.by(Sort.Direction.ASC, "nom"));
        } catch (Exception e) {
            log.error("Error retrieving all users: ", e);
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }

    @Override
    public User retrieveUser(Long id) {
        try {
            return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        } catch (Exception e) {
            log.error("Error retrieving user: ", e);
            throw new RuntimeException("Failed to retrieve user", e);
        }
    }

    @Override
    @Transactional
    public User addUser(User c) {
        try {
            if (userRepository.findByEmail(c.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            c.setPassword(encoder.encode(c.getPassword()));
            Role userRole = roleRepository.findById(c.getRole().stream().findFirst().orElseThrow(() -> new EntityNotFoundException("Role not found")).getId())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found"));
            Set<Role> authorities = new HashSet<>();
            authorities.add(userRole);
            c.setRole(authorities);
            return userRepository.save(c);
        } catch (Exception e) {
            log.error("Error adding user: ", e);
            throw new RuntimeException("Failed to add user", e);
        }
    }

    @Override
    @Transactional
    public void removeUser(Long id) throws IOException {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
            this.deletePhoto(user.getPhotomat());
            Confirmation c = confirmationRepository.findConfirmationByUser(user);
            if (c != null) {
                confirmationRepository.delete(c);
            }
            userRepository.deleteById(id);
        } catch (IOException e) {
            log.error("Error removing user: ", e);
            throw e;
        } catch (Exception e) {
            log.error("Error removing user: ", e);
            throw new RuntimeException("Failed to remove user", e);
        }
    }

    @Override
    public User modifyUser(User user) {
        try {
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));
            if (existingUser.getRole().stream().findFirst().get().getId() == 3) {
                Role userRole = roleRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("Role not found"));
                Set<Role> authorities = new HashSet<>();
                authorities.add(userRole);
                user.setRole(authorities);
            }
            if (!existingUser.getEmail().equals(user.getEmail())) {
                user.setStatus(UStatus.Unconfirmed);
                confirmNewEmail(user);
            }
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Error modifying user: ", e);
            throw new RuntimeException("Failed to modify user", e);
        }
    }

    @Override
    public User loadUserByCode(String code) {
        try {
            return userRepository.findByCode(code).orElseThrow(() -> new EntityNotFoundException("User not found with code: " + code));
        } catch (Exception e) {
            log.error("Error loading user by code: ", e);
            throw new RuntimeException("Failed to load user by code", e);
        }
    }

    @Override
    public void confirmNewEmail(User user) {
        try {
            Confirmation confirmation = new Confirmation(user);
            confirmationRepository.save(confirmation);
            emailService.sendHtmlEmail(user.getNom(), user.getEmail(), confirmation.getToken());
        } catch (Exception e) {
            log.error("Error confirming new email: ", e);
            throw new RuntimeException("Failed to confirm new email", e);
        }
    }

    @Override
    public List<User> chercherUser(String nom) {
        try {
            if (nom == null || nom.isEmpty()) {
                return userRepository.findAll(Sort.by(Sort.Direction.ASC, "nom"));
            }
            return userRepository.findByNomContainingIgnoreCaseOrderByNom(nom);
        } catch (Exception e) {
            log.error("Error searching for users: ", e);
            throw new RuntimeException("Failed to search for users", e);
        }
    }

    @Override
    public String savePhoto(MultipartFile file) throws IOException {
        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            createUploadDirectoryIfNotExist();
            String filePath = UPLOAD_DIR + fileName;
            Path destPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), destPath);
            return fileName;
        } catch (IOException e) {
            log.error("Error saving photo: ", e);
            throw e;
        }
    }

    @Override
    public void deletePhoto(String fileName) throws IOException {
        try {
            String filePath = UPLOAD_DIR + fileName;
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("Profile picture deleted successfully: " + fileName);
            } else {
                System.out.println("Profile picture not found: " + fileName);
            }
        } catch (IOException e) {
            log.error("Error deleting photo: ", e);
            throw e;
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
        try {
            if (user.getPanier() == null) {
                Panier panier = new Panier();
                panier.setUser(user);
                user.setPanier(panier);
                userRepository.save(user);
            }
        } catch (Exception e) {
            log.error("Error ensuring user has panier: ", e);
            throw new RuntimeException("Failed to ensure user has panier", e);
        }
    }

    @Transactional
    public void ensureAllUsersHavePaniers() {
        try {
            List<User> users = userRepository.findAll();
            for (User user : users) {
                ensureUserHasPanier(user);
            }
        } catch (Exception e) {
            log.error("Error ensuring all users have paniers: ", e);
            throw new RuntimeException("Failed to ensure all users have paniers", e);
        }
    }

    @Override
    public Panier getUserPanier(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            return user.getPanier();
        } catch (Exception e) {
            log.error("Error getting user panier: ", e);
            throw new RuntimeException("Failed to get user panier", e);
        }
    }

    @Override
    public String soldeSum() {
        try {
            List<String> soldes = userRepository.findSoldes();
            soldes.removeIf(String::isEmpty);
            double sum = soldes.stream().mapToDouble(s -> Double.parseDouble(s.replaceAll("€", "").replaceAll("-", "").replaceAll(",", ".").replaceAll(" ", ""))).sum();
            return String.format("%.2f", sum);
        } catch (Exception e) {
            log.error("Error calculating solde sum: ", e);
            throw new RuntimeException("Failed to calculate solde sum", e);
        }
    }

    @Override
    public User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentCode = authentication.getName();
            return userRepository.findByCode(currentCode).orElseThrow(() -> new EntityNotFoundException("User not found with code: " + currentCode));
        } catch (Exception e) {
            log.error("Error getting current user: ", e);
            throw new RuntimeException("Failed to get current user", e);

        }
    }
}