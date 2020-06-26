package com.genericauthserver.genericauthserver.security.config;

import com.genericauthserver.genericauthserver.entity.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.*;

public class SecurityClient implements ClientDetails {

    private final Client client;

    public SecurityClient(Client client) {
        this.client = client;
    }

    @Override
    public String getClientId() {
        return client.getName();
    }

    @Override
    public Set<String> getResourceIds() {
        return null;
    }

    @Override
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    public String getClientSecret() {
        return this.client.getSecret();
    }

    @Override
    public boolean isScoped() {
        return true;
    }

    @Override
    public Set<String> getScope() {
        return Set.of("writes","read");
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return List.of(()->"writes");
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return Set.of("authorization_code","password");
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return Set.of("http://127.0.0.1:8080/mycode");
    }


    @Override
    public Integer getAccessTokenValiditySeconds() {
        return 50000;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return 50000;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return scope.equalsIgnoreCase("read");
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        Map<String,Object> moreInformation = new LinkedHashMap<>();
        moreInformation.put("test","myTest information");
        return moreInformation;
    }
}
