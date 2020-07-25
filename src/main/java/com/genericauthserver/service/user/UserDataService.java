package com.genericauthserver.service.user;

import com.genericauthserver.dto.UserRegisterUpdateDto;
import com.genericauthserver.dto.UserResetPasswordDto;
import com.genericauthserver.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserDataService extends UserDetailsService{

    User findUserByEmail(String username);
    void login(String username, String password);
    String getJwtToken(String authCode);
    UserRegisterUpdateDto registerNewUser(UserRegisterUpdateDto dto);
    void sendResetPasswordCodeToEmail(String email);
    Optional<UserRegisterUpdateDto> validateResetPasswordCode(String code);
    void updateUserData(UserRegisterUpdateDto userRegisterUpdateDto);
    boolean updatePassword(UserResetPasswordDto userResetPasswordDto, String code);
}
