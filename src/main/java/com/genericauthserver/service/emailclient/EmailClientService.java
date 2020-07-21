package com.genericauthserver.service.emailclient;

public interface EmailClientService {

    void sendSimpleEmail(String to, String subject, String message);

}
