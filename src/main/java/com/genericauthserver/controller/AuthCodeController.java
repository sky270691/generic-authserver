package com.genericauthserver.controller;

import com.genericauthserver.service.authcode.AuthCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/authcode")
public class AuthCodeController {

    private final Logger logger;
    private final AuthCodeService authCodeService;

    public AuthCodeController(AuthCodeService authCodeService) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.authCodeService = authCodeService;
    }


    @GetMapping
    public ResponseEntity<?> getCode(@RequestParam String code){
        System.out.println(code);
//        authCodeService.sendAuthCodeToEmail(code);
        authCodeService.sendResetPasswordCodeToEmail("bakekok","langi.risky@gmail.com");

        Map<String,String> returnValue = new HashMap<>();
        returnValue.put("status","success");

        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

}
