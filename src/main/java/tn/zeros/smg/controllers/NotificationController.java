package tn.zeros.smg.controllers;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.services.IServices.INotificationService;
import tn.zeros.smg.services.IServices.IUserService;
import tn.zeros.smg.entities.Notification;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Slf4j
public class NotificationController {
    private final INotificationService notificationService;
    private final IUserService userService;

    @GetMapping("/getAll")
    public List<Notification> getNotifications() {
        return notificationService.retrieveAllNotifications();
    }

    @GetMapping("/get/{notification-id}")
    public Notification retrieveNotification(@PathVariable("notification-id") Long notificationId) {
        return notificationService.retrieveNotification(notificationId);
    }

    @PostMapping("/add")
    public Notification addNotification(@RequestBody Notification c) {
        return notificationService.addNotification(c);
    }

    @DeleteMapping("/delete/{notification-id}")
    public void removeNotification(@PathVariable("notification-id") Long notificationId) {
        notificationService.removeNotification(notificationId);
    }

    @PutMapping("/update")
    public Notification modifyNotification(@RequestBody Notification c) {
        return notificationService.modifyNotification(c);
    }

    @GetMapping("/getUnread")
    public ResponseEntity<?> getUnread() {
        User currentUser;
        currentUser = userService.getCurrentUser();
        return ResponseEntity.ok().body(notificationService.getUnread(currentUser));
    }

    @GetMapping("/getAllByUser")
    public ResponseEntity<?> getAllByUser() {
        User currentUser;
        currentUser = userService.getCurrentUser();
        return ResponseEntity.ok().body(notificationService.getByUser(currentUser));
    }

    @GetMapping("/markAllAsRead")
    @Transactional
    public ResponseEntity<?> markAllAsRead() {
        User currentUser;
        currentUser = userService.getCurrentUser();
        List<Notification> notifications = notificationService.getUnread(currentUser);
        for(Notification n : notifications) {
            n.setRead(true);
            notificationService.modifyNotification(n);
        }
        return ResponseEntity.ok().body(true);
    }

}
