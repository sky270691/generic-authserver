package com.genericauthserver.genericauthserver.service.authcode;

public interface AuthCodeService {

    void sendAuthCodeToEmail(String code);

}
