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
        User user = (User) authentication.getPrincipal();
        final Map<String,Object> additionalInfo = new HashMap<>();

        additionalInfo.put("role",user.getAuthorityList());
        additionalInfo.put("first_name",user.getFirstName());
        additionalInfo.put("last_name",user.getLastName());
        additionalInfo.put("phone_number",user.getPhoneNumber());
        additionalInfo.put("email",user.getEmail());
        additionalInfo.put("city",user.getCity());
        additionalInfo.put("province",user.getProvince());
        additionalInfo.put("address",user.getAddress());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
