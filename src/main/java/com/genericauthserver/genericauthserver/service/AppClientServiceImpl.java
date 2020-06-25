package com.genericauthserver.genericauthserver.service;

import com.genericauthserver.genericauthserver.entity.Client;
import com.genericauthserver.genericauthserver.repository.ClientRepository;
import com.genericauthserver.genericauthserver.security.config.SecurityClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;


public class AppClientServiceImpl implements ClientDetailsService{

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Client client = clientRepository.findByName(clientId);
        return new SecurityClient(client);
    }
}
