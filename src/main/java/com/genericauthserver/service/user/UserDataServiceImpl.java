package com.genericauthserver.service.user;

import com.genericauthserver.dto.UserRegisterUpdateDto;
import com.genericauthserver.entity.User;
import com.genericauthserver.exception.UserException;
import com.genericauthserver.mapper.UserMapper;
import com.genericauthserver.repository.UserRepository;
import com.genericauthserver.config.security.SecurityUser;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserDataServiceImpl implements UserDataService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDataServiceImpl(UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = getUserByEmail(s);
        return new SecurityUser(user);
    }


    @Override
    public User getUserByEmail(String username) {
        return userRepository.findByEmail(username).orElseThrow(()->new UserException("User with email: '"+username+"' not found"));
    }

    @Override
    public void login(String email, String password){
        String authHeaderPrefix = "Basic ";
        String credential = Base64.getEncoder().encodeToString((email+":"+password).getBytes());
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
        String url = "http://localhost:8080/oauth/token";

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

    @Override
    @Transactional
    public UserRegisterUpdateDto registerNewUser(UserRegisterUpdateDto dto) {
        UserMapper userMapper = new UserMapper();
        User user = userMapper.convertUserRegisterUpdateDtoToUserEntity(dto,passwordEncoder);
        Optional<User> existingUserEmail = userRepository.findByEmail(dto.getEmail());
        Optional<User> existingUserPhone = userRepository.findByPhoneNumber(dto.getPhoneNumber());

        if(!existingUserEmail.isEmpty()){
            throw new UserException("User with email '"+dto.getEmail()+"' already exist");
        }else if(!existingUserPhone.isEmpty()){
            throw new UserException("User with phone number '"+dto.getPhoneNumber()+"' already exist");
        }


        String url = "http://backend:8080/api/v1/register";
        UserRegisterUpdateDto registeredUser =  userMapper.convertToUserRegisterUpdateDto(userRepository.save(user));
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(url,registeredUser,String.class);

        return registeredUser;
    }


}
