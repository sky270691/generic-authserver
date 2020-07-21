package com.genericauthserver.service.user;

import com.genericauthserver.dto.UserRegisterUpdateDto;
import com.genericauthserver.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserDataService extends UserDetailsService{

    User getUserByEmail(String username);
    void login(String username, String password);
    String getJwtToken(String authCode);
    UserRegisterUpdateDto registerNewUser(UserRegisterUpdateDto dto);
}
