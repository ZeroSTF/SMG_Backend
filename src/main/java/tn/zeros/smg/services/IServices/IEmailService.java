package tn.zeros.smg.services.IServices;

public interface IEmailService {
    void sendSimpleMailMessage(String name, String to, String token);
    void sendHtmlEmail(String name, String to, String token);
}