package com.genericauthserver.genericauthserver.service.user;

import com.genericauthserver.genericauthserver.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserDataService extends UserDetailsService{

    User getUserByUsername(String username);
    void login(String username, String password);
}
