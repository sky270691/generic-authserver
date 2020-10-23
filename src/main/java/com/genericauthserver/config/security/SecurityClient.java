package com.genericauthserver.config.security;

import com.genericauthserver.entity.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SecurityClient implements ClientDetails {

    private final Client client;
    private String url;

    public SecurityClient(Client client) {
        this.client = client;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(@Value("${auth-server.live-endpoint.prefix}") String url) {
        this.url = url;
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
        return false;
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
        return Set.of("READ_WRITE");
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
        return Set.of(getUrl()+"api/v1/authcode");
    }


    @Override
    public Integer getAccessTokenValiditySeconds() {
        return 1296000;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return 1296000;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return scope.equalsIgnoreCase("READ_WRITE");
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        Map<String,Object> moreInformation = new LinkedHashMap<>();
        moreInformation.put("test","myTest information");
        return moreInformation;
    }
}
