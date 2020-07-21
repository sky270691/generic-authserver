package com.genericauthserver.service.authcode;

import com.genericauthserver.entity.User;
import com.genericauthserver.service.emailclient.EmailClientService;
import com.genericauthserver.service.user.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthCodeServiceImpl implements AuthCodeService {

    private final EmailClientService emailClientService;

    @Autowired
    public AuthCodeServiceImpl(EmailClientService emailClientService) {
        this.emailClientService = emailClientService;
    }

    @Override
    public void sendAuthCodeToEmail(String code) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String subject = "Kode login Satu Tas Merah";
        String message = "Kode otentikasi anda adalah:\n\n"+code;

        emailClientService.sendSimpleEmail(email,subject,message);
    }
}
