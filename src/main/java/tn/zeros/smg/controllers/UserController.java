package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.zeros.smg.controllers.DTO.CurrentDTO;
import tn.zeros.smg.controllers.DTO.UserDTO;
import tn.zeros.smg.entities.Panier;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.services.IServices.IUserService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import static tn.zeros.smg.services.UserService.UPLOAD_DIR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final IUserService userService;

    @GetMapping("/getAll")
    @Cacheable("users")
    public List<UserDTO> getUsers() {
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
        User currentUser=userService.getCurrentUser();
        String role = userService.getRoleString(currentUser);
        return ResponseEntity.ok().body(new CurrentDTO(currentUser.getCode(), currentUser.getNom(), currentUser.getEmail(), role, "", ""));
    }

    @GetMapping("/currentDetails")
    public ResponseEntity<?> getCurrentDetails() {
        return ResponseEntity.ok().body(userService.getCurrentUser());
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
        return ResponseEntity.ok(userService.soldeSum());
    }

    @GetMapping("/getMat/{fileName}")
    public ResponseEntity<byte[]> getMat(@PathVariable String fileName) throws IOException {
        String filePath = UPLOAD_DIR + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }
        byte[] imageData = Files.readAllBytes(file.toPath());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }
}
