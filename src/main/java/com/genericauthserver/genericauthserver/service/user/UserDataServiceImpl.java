package com.genericauthserver.genericauthserver.service.user;

import com.genericauthserver.genericauthserver.entity.User;
import com.genericauthserver.genericauthserver.exception.UserException;
import com.genericauthserver.genericauthserver.repository.UserRepository;
import com.genericauthserver.genericauthserver.security.config.SecurityUser;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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

    @Override
    public void login(String username, String password){
        String authHeaderPrefix = "Basic ";
        String credential = Base64.getEncoder().encodeToString((username+":"+password).getBytes());
        String fullAuthHeader = authHeaderPrefix+credential;
        System.out.println(fullAuthHeader);
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization",fullAuthHeader);
        String url = "http://localhost:8080/oauth/authorize?scope=READ_WRITE&client_id=front-stm&response_type=code";

        HttpEntity entity = new HttpEntity(headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(url, HttpMethod.GET,entity,String.class,null,null);
    }

    @Override
    public String getJwtToken(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        String authHeaderPrefix = "Basic ";
        String clientCredential = Base64.getEncoder().encodeToString("front-stm:front123".getBytes());
        String fullAuthHeader = authHeaderPrefix+clientCredential;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization",fullAuthHeader);

        MultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("grant_type","authorization_code");
        multiValueMap.add("code",authCode);

        HttpEntity<MultiValueMap<String,String>> entity =new HttpEntity<>(multiValueMap,headers);
        String url = "http://localhost:8080/oauth/token?grant_type=authorization_code&code=rfu9Ca";

        ResponseEntity<String> response = restTemplate.postForEntity(url,entity,String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(response.getBody());
            String token = jsonNode.get("access_token").asText();
            return token;
        } catch (IOException e) {
            e.printStackTrace();
            throw new UserException("getting jwt token error");
        }

    }


}
