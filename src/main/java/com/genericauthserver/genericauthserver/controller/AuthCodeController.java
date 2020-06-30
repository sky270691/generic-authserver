package com.genericauthserver.genericauthserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthCodeController {

    @Autowired
    private JavaMailSender javaMailSender;

    @GetMapping("/mycode")
    public String getCode(@RequestParam String code){
        System.out.println(code);
        String to= "langi.risky@gmail.com";
        String subject="Verification Code";
        String text = "this is your verification code: "+code;
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);

        return "your code is: "+code;
    }

}
