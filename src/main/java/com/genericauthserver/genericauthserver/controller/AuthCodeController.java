package com.genericauthserver.genericauthserver.controller;

import com.genericauthserver.genericauthserver.service.authcode.AuthCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthCodeController {

    private final Logger logger;
    private final AuthCodeService authCodeService;

    public AuthCodeController(AuthCodeService authCodeService) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.authCodeService = authCodeService;
    }


    @GetMapping("/authcode")
    public String getCode(@RequestParam String code){
        authCodeService.sendAuthCodeToEmail(code);
        return "success";
    }

}
