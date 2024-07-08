package tn.zeros.smg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.zeros.smg.entities.Notification;
import tn.zeros.smg.entities.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user = ?1 AND n.isRead = ?2")
    List<Notification> findNotificationsByUserAndIsRead(User user, boolean isRead);
    List<Notification> findNotificationsByUser(User user);
}
