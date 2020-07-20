package com.genericauthserver.genericauthserver.service.emailclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailClientServiceImpl implements EmailClientService {

    private final JavaMailSender mailSender;
    private final Logger logger;

    @Autowired
    public EmailClientServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }


    @Override
    public void sendSimpleEmail(String to, String subject, String message) {
        logger.info("Sending email to: "+to);
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}
