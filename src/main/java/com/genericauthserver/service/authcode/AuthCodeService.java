package com.genericauthserver.service.authcode;

public interface AuthCodeService {

    void sendAuthCodeToEmail(String code);
    void sendResetPasswordCodeToEmail(String code, String email);
}
