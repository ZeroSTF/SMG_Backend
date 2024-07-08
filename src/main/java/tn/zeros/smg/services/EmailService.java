package tn.zeros.smg.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tn.zeros.smg.services.IServices.IEmailService;

import java.util.Map;

import static tn.zeros.smg.utils.EmailUtility.getEmailMessage;
import static tn.zeros.smg.utils.EmailUtility.getVerificationUrl;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "Verification SMG";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String EMAIL_TEMPLATE = "emailTemplate";
    public static final String TEXT_HTML_ENCONDING = "text/html";

    private final TemplateEngine templateEngine;

    @Value("${spring.mail.properties.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender emailSender;
    @Override
    @Async
    public void sendSimpleMailMessage(String name, String to, String token) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(getEmailMessage(name, host, token));
            emailSender.send(message);
        } catch(Exception exception){
            throw new RuntimeException(exception);
        }
    }

    @Override
    @Async
    public void sendHtmlEmail(String name, String to, String token) {
        try {
            Context context = new Context();
            /*context.setVariable("name", name);
            context.setVariable("url", getVerificationUrl(host, token));*/
            context.setVariables(Map.of("name", name, "url", getVerificationUrl(host, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text, true);
           /* //Add attachments
            FileSystemResource fort = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/fort.jpg"));
            FileSystemResource dog = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/dog.jpg"));
            FileSystemResource homework = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/images/homework.docx"));
            helper.addAttachment(fort.getFilename(), fort);
            helper.addAttachment(dog.getFilename(), dog);
            helper.addAttachment(homework.getFilename(), homework);*/
            emailSender.send(message);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }
}