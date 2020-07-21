package com.genericauthserver.controller;

import com.genericauthserver.dto.UserRegisterUpdateDto;
import com.genericauthserver.service.user.UserDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserDataService userDataService;
    private final Logger logger;

    @Autowired
    public UserController(UserDataService userDataService) {
        this.userDataService = userDataService;
        this.logger  = LoggerFactory.getLogger(this.getClass());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String,String> userCredential, HttpServletRequest request){

        logger.info(request.getRequestURI()+" requested");

        Map<String,String> loginResponse = new HashMap<>();

        if(userCredential.containsKey("email") && userCredential.containsKey("password")){
            userDataService.login(userCredential.get("email"),userCredential.get("password"));
            loginResponse.put("status","success");
            loginResponse.put("message","check your email for the code");
            return ResponseEntity.ok().body(loginResponse);
        }else{
            loginResponse.put("status","error");
            return ResponseEntity.badRequest().body(loginResponse);
        }
    }

    @PostMapping("/auth_code")
    public ResponseEntity<?> userLoginCode(@RequestBody Map<String,String> authCode, HttpServletRequest request){

        Map<String,String> tokenResponse = new LinkedHashMap<>();

        if(authCode.containsKey("code")){
            String token = userDataService.getJwtToken(authCode.get("code"));
            tokenResponse.put("status","success");
            tokenResponse.put("access_token",token);
            return ResponseEntity.ok(tokenResponse);
        }else{
            tokenResponse.put("status","error");
            tokenResponse.put("message","error getting token");
            return ResponseEntity.badRequest().body(tokenResponse);
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@RequestBody @Valid UserRegisterUpdateDto userRegisterUpdateDto){

        Map<String,Object> response = new LinkedHashMap<>();
        UserRegisterUpdateDto returnedUserData = userDataService.registerNewUser(userRegisterUpdateDto);
        response.put("status","success");
        response.put("registered-user",returnedUserData);

        return ResponseEntity.ok(response);
    }

}
