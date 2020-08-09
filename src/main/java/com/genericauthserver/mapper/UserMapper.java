package com.genericauthserver.mapper;

import com.genericauthserver.dto.UserRegisterUpdateDto;
import com.genericauthserver.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final AuthorityMapper authorityMapper;

    @Autowired
    public UserMapper(AuthorityMapper authorityMapper) {
        this.authorityMapper = authorityMapper;
    }

    public User convertUserRegisterUpdateDtoToUserEntity(UserRegisterUpdateDto dto, PasswordEncoder encoder){
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setEmail(dto.getEmail());
        user.setSex(dto.getSex());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setId(dto.getId());
        return user;
    }

    public User convertUserRegisterUpdateDtoToUserEntityWithoutPass(UserRegisterUpdateDto dto){
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setEmail(dto.getEmail());
        user.setSex(dto.getSex());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setId(dto.getId());
        return user;
    }

    public UserRegisterUpdateDto convertToUserRegisterUpdateDto(User user){
        UserRegisterUpdateDto dto = new UserRegisterUpdateDto();

        dto.setEmail(user.getEmail());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setFirstName(user.getFirstName());
        dto.setId(user.getId());
        dto.setSex(user.getSex());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAuthorityList(user.getAuthorityList().stream()
                .map(authorityMapper::convertToAuthorityDto)
                .collect(Collectors.toList()));
        return dto;
    }

}
