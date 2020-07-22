package com.genericauthserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericAuthServerApplicationTests {

    @Test
    void checkUser(){
        String test = "test";
        assertEquals("test",test);
    }

}
