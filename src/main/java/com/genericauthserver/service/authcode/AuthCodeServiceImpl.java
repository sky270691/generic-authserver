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
        String message = "Kode otentikasi anda adalah:\n\n\n"+code+"\n\n\n- Satu Tas Merah -";

        System.out.println("reach here " + code);
        emailClientService.sendSimpleEmail(email,subject,message);
    }

    @Override
    public void sendResetPasswordCodeToEmail(String code, String email) {
        String subject = "Kode Reset Password Satu Tas Merah";
        String message = "Silahkan input kode berikut di aplikasi Satu Tas Merah: \n\n\n"+code+"\n\n\n- Satu Tas Merah -";

        emailClientService.sendSimpleEmail(email,subject,message);
    }
}
