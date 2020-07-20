package com.genericauthserver.genericauthserver.service.client;

import com.genericauthserver.genericauthserver.entity.Client;
import com.genericauthserver.genericauthserver.repository.ClientRepository;
import com.genericauthserver.genericauthserver.security.config.SecurityClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;


public class AppClientServiceImpl implements ClientDetailsService{

    @Autowired
    private ClientRepository clientRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Client client = clientRepository.findByName(clientId);
        logger.info("client: "+clientId+", is getting their data");
        return new SecurityClient(client);
    }
}
