package org.example.emailservice_nov2025evening.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.emailservice_nov2025evening.dtos.EmailDto;
import org.example.emailservice_nov2025evening.emailsender.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.mail.Session;
import java.util.Properties;

@Component
public class KafkaEmailConsumer {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EmailSender emailSender;

    @KafkaListener(topics = "password_reset", groupId = "emailService")
    public void sendEmail(String message) {

        try {
            // parse JSON message into email dto
            EmailDto emailDto = objectMapper.readValue(message, EmailDto.class);

            // setup SMTP properties and send email
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            // create session with authentication
            Session session = Session.getDefaultInstance(properties);

            // send email using the email sender
            emailSender.send(session, emailDto.getTo(), emailDto.getSubject(), emailDto.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }

      }
}
