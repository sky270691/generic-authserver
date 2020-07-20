package com.genericauthserver.genericauthserver.security.config;

import com.genericauthserver.genericauthserver.entity.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        final Map<String,Object> additionalInfo = new HashMap<>();

        additionalInfo.put("role",user.getUser().getAuthorityList());
        additionalInfo.put("first_name",user.getUser().getFirstName());
        additionalInfo.put("last_name",user.getUser().getLastName());
        additionalInfo.put("phone_number",user.getUser().getPhoneNumber());
        additionalInfo.put("email",user.getUser().getEmail());
        additionalInfo.put("city",user.getUser().getCity());
        additionalInfo.put("province",user.getUser().getProvince());
        additionalInfo.put("address",user.getUser().getAddress());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
