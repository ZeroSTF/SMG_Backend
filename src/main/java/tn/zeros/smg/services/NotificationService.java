package tn.zeros.smg.services;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.zeros.smg.entities.Notification;
import tn.zeros.smg.entities.User;
import tn.zeros.smg.repositories.NotificationRepository;
import tn.zeros.smg.services.IServices.INotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> retrieveAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification retrieveNotification(Long id) {
        if (notificationRepository.findById(id).isPresent()){
            Notification notification = notificationRepository.findById(id).get();
            notification.setRead(true);
            notificationRepository.save(notification);
            return notification;
        }else {
            throw new EntityNotFoundException("Notification not found with id: " + id);
        }
    }

    @Override
    public Notification addNotification(Notification notification) {
        notification.setRead(false);
        notification.setTime(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @Override
    public void removeNotification(Long id) {notificationRepository.deleteById(id);}

    @Override
    public Notification modifyNotification(Notification notification) {
        Optional<Notification> existingUser = notificationRepository.findById(notification.getId());
        if (existingUser.isPresent()) {
            return notificationRepository.save(notification);
        } else {
            throw new EntityNotFoundException("Notification not found with id: " + notification.getId());
        }
    }

    @Override
    public List<Notification> getUnread(User user){
        return notificationRepository.findNotificationsByUserAndIsRead(user,false);
    }

    @Override
    public List<Notification> getByUser(User user){return notificationRepository.findNotificationsByUser(user);}
}
