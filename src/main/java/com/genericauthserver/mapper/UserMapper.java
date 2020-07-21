package com.genericauthserver.mapper;

import com.genericauthserver.dto.UserRegisterUpdateDto;
import com.genericauthserver.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

public class UserMapper {

    public User convertUserRegisterUpdateDtoToUserEntity(UserRegisterUpdateDto dto, PasswordEncoder encoder){
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setId(dto.getId());
        return user;
    }

    public UserRegisterUpdateDto convertToUserRegisterUpdateDto(User user){
        UserRegisterUpdateDto dto = new UserRegisterUpdateDto();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setId(user.getId());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }

}
