package com.genericauthserver.service.authcode;

import com.genericauthserver.entity.User;
import com.genericauthserver.service.emailclient.EmailClientService;
import com.genericauthserver.service.user.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthCodeServiceImpl implements AuthCodeService {

    private final UserDataService userDataService;
    private final EmailClientService emailClientService;

    @Autowired
    public AuthCodeServiceImpl(UserDataService userDataService,
                               EmailClientService emailClientService) {
        this.userDataService = userDataService;
        this.emailClientService = emailClientService;
    }

    @Override
    public void sendAuthCodeToEmail(String code) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDataService.getUserByUsername(username);
        String email = user.getEmail();
        String subject = "Kode login Satu Tas Merah";
        String message = "Kode otentikasi anda adalah:\n\n"+code;

        emailClientService.sendSimpleEmail(email,subject,message);
    }
}
