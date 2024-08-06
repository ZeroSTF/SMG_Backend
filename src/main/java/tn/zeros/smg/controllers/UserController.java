package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.zeros.smg.controllers.DTO.CurrentDTO;
import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
import tn.zeros.smg.entities.Panier;
import tn.zeros.smg.entities.Role;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.services.IServices.IUserService;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Slf4j
public class UserController {
    private final IUserService userService;
    @GetMapping("/getAll")
    public List<User> getUsers() {
        return userService.retrieveAllUsers();
    }

    @GetMapping("/get/{user-id}")
    public User retrieveUser(@PathVariable("user-id") Long userId) {
        return userService.retrieveUser(userId);
    }

    @PostMapping("/add")
    public User addUser(@RequestBody User c) {
        return userService.addUser(c);
    }

    @DeleteMapping("/delete/{user-id}")
    public void removeUser(@PathVariable("user-id") Long userId) throws IOException {
        userService.removeUser(userId);
    }

    @PutMapping("/update")
    public User modifyUser(@RequestBody User c) {
        return userService.modifyUser(c);
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrent() {
        User currentUser;
        try {
            ////////////retrieving current user/////////////////////////////////
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentCode = authentication.getName();
            log.info("currentCode: " + currentCode);
            currentUser = userService.loadUserByCode(currentCode);
            ////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        String role = "user";
        Set<Role> roles = currentUser.getRole();
        if (!roles.isEmpty()) {
            Iterator<Role> iterator = roles.iterator();
            Role firstRole = iterator.next();
            Long firstRoleId = firstRole.getId();
            if (firstRoleId == 1) {
                role = "admin";
            }
        }
        //return the current user in the form of an object which has the user's id, name, email, avatar and status
        return ResponseEntity.ok().body(new CurrentDTO(currentUser.getCode(), currentUser.getNom(), currentUser.getEmail(), role, "", ""));
    }

    @GetMapping("/currentDetails")
    public ResponseEntity<?> getCurrentDetails() {
        User currentUser;
        try {
            ////////////retrieving current user/////////////////////////////////
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentCode = authentication.getName();
            currentUser = userService.loadUserByCode(currentCode);
            ////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(currentUser);
    }

    @GetMapping("/search")
    public ResponseEntity<?> chercherUser(@RequestParam(value = "query", required = false) String query) {
        List<User> users = userService.chercherUser(query);
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String token) {
        Boolean isSuccess = userService.verifyToken(token);
        if (isSuccess) {
            return ResponseEntity.ok("Email Confirmé avec succés.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }

    @PostMapping("/upload/{user-id}")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file, @PathVariable("user-id") Long userId) {
        // Check if file is empty
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }
        try {
            User user=userService.retrieveUser(userId);
            String fileName = userService.savePhoto(file);
            user.setPhotomat(fileName);
            userService.modifyUser(user);
            return ResponseEntity.ok().body("Profile picture uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/panier")
    public ResponseEntity<Panier> getUserPanier(@PathVariable Long userId) {
        Panier panier = userService.getUserPanier(userId);
        return ResponseEntity.ok(panier);
    }

    @GetMapping("/current/panier-id")
    public ResponseEntity<Long> getCurrentUserPanierId() {
        User currentUser;
        currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser.getPanier().getId());
    }

    @GetMapping("/solde")
    public ResponseEntity<String> soldeSum() {
        log.info("soldeSum");
        log.info(userService.soldeSum());
        return ResponseEntity.ok(userService.soldeSum());
    }
}
