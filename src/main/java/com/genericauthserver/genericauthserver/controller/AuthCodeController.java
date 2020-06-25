package com.genericauthserver.genericauthserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthCodeController {

    @GetMapping("/mycode")
    public String getCode(@RequestParam String code){
        System.out.println(code);
        return "redirect:http://www.google.com";
    }

}
