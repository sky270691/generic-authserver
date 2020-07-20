package com.genericauthserver.genericauthserver;

import com.genericauthserver.genericauthserver.service.user.UserDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenericAuthServerApplicationTests {


    @Autowired
    private UserDataService uds;

    @Test
    void contextLoads() {
    }

    @Test
    void checkUser(){
        UserDetails user = uds.loadUserByUsername("komo");
        assertEquals("admin",user.getAuthorities().stream().map(x->x.getAuthority()).findFirst().orElse("none"));
    }

}
