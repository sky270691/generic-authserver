package com.genericauthserver.genericauthserver.controller;

import com.genericauthserver.genericauthserver.service.user.UserDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class LoginController {

    private final UserDataService userDataService;
    private final Logger logger;

    @Autowired
    public LoginController(UserDataService userDataService) {
        this.userDataService = userDataService;
        this.logger  = LoggerFactory.getLogger(this.getClass());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String,String> userCredential, HttpServletRequest request){

        logger.info(request.getRequestURI()+" requested");

        Map<String,String> loginResponse = new HashMap<>();

        if(userCredential.containsKey("username") && userCredential.containsKey("password")){
            userDataService.login(userCredential.get("username"),userCredential.get("password"));
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

}
