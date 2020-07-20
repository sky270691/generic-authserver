package com.genericauthserver.genericauthserver.controller;

import com.genericauthserver.genericauthserver.service.user.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController {

    private final UserDataService userDataService;

    @Autowired
    public LoginController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @PostMapping
    public ResponseEntity<?> loginUser(@RequestBody Map<String,String> usernamePass){

        if(usernamePass.containsKey("username") && usernamePass.containsKey("password")){
            userDataService.login(usernamePass.get("username"),usernamePass.get("password"));
        }
        return ResponseEntity.ok().build();
    }

}
