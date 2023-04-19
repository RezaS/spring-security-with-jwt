package com.example.spring_security.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    private final Logger log = LoggerFactory.getLogger(EmailService.class);
    private JavaMailSender javaMailSender;

    public void sendEmail(String recipient, String subject, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(text, true);
        log.debug("Sent e-mail to User '{}'", recipient);
        javaMailSender.send(message);
    }
}
