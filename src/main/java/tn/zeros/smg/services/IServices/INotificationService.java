package tn.zeros.smg.services.IServices;

import tn.zeros.smg.entities.Notification;
import tn.zeros.smg.entities.User;

import java.util.List;

public interface INotificationService {
    List<Notification> retrieveAllNotifications();
    Notification retrieveNotification(Long id);
    Notification addNotification(Notification notification);
    void removeNotification(Long id);
    Notification modifyNotification(Notification notification);
    List<Notification> getUnread(User user);
    List<Notification> getByUser(User user);
}
