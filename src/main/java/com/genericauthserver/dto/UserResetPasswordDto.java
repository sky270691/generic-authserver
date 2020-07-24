package com.genericauthserver.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserResetPasswordDto {

    private String email;

    @NotBlank(message = "password shouldn't be blank")
    @Size(min = 6, message = "password should be at least 6 character")
    @Pattern(regexp = "^(?=.*[a-zA-Z\\d].*)[a-zA-Z\\d!@#$%&*-]{6,}$",message = "password should contain number and alphabet")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserResetPasswordDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
