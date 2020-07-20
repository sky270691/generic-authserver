package com.genericauthserver.genericauthserver.service.user;

import com.genericauthserver.genericauthserver.entity.User;
import com.genericauthserver.genericauthserver.exception.UserException;
import com.genericauthserver.genericauthserver.repository.UserRepository;
import com.genericauthserver.genericauthserver.security.config.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDataServiceImpl implements UserDataService {

    private final UserRepository userRepository;

    @Autowired
    public UserDataServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(s).orElseThrow(()->new UserException("User with username: '"+s+"' not found"));
        return new SecurityUser(user);
    }


    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(()->new UserException("User with username: '"+username+"' not found"));
    }


}
