package tn.zeros.smg.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.controllers.DTO.CurrentDTO;
import tn.zeros.smg.controllers.DTO.LoginResponseDTO;
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
            String currentEmail = authentication.getName();
            currentUser = userService.loadUserByEmail(currentEmail);
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
            String currentEmail = authentication.getName();
            currentUser = userService.loadUserByEmail(currentEmail);
            ////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(currentUser);
    }

}
