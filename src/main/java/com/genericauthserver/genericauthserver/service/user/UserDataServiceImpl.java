package com.genericauthserver.genericauthserver.service.user;

import com.genericauthserver.genericauthserver.entity.User;
import com.genericauthserver.genericauthserver.exception.UserException;
import com.genericauthserver.genericauthserver.repository.UserRepository;
import com.genericauthserver.genericauthserver.security.config.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.Base64;

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

    public void login(String username, String password){
        String authHeaderPrefix = "Basic ";
        String credential = Base64.getEncoder().encodeToString((username+":"+password).getBytes());
        String fullAuthHeader = authHeaderPrefix+credential;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization",fullAuthHeader);

        MultiValueMap<String,String> body =new LinkedMultiValueMap<>();
        body.add("scope","READ_WRITE");
        body.add("client_id","front-stm");
        body.add("response_type","code");

        HttpEntity<MultiValueMap<String,String>> entity =new HttpEntity<>(body,headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForEntity("http://localhost:8080/oauth/authorize",null,entity);
    }


}
