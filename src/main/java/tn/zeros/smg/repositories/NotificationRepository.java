package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.zeros.smg.entities.Notification;
import tn.zeros.smg.entities.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndSeen(User user, boolean seen);
    List<Notification> findNotificationsByUser(User user);
}
