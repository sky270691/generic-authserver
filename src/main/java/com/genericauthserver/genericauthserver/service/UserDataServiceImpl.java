package com.genericauthserver.genericauthserver.service;

import com.genericauthserver.genericauthserver.entity.User;
import com.genericauthserver.genericauthserver.repository.UserRepository;
import com.genericauthserver.genericauthserver.security.config.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDataServiceImpl implements UserDataService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(s);
        return new SecurityUser(user);
    }
}
