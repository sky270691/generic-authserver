package com.genericauthserver.genericauthserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthCodeController {

    @GetMapping("/mycode")
    public String getCode(@RequestParam String code){
        System.out.println(code);
        return "your code is: "+code;
    }

}
