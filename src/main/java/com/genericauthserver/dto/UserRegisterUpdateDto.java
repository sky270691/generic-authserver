package com.genericauthserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class UserRegisterUpdateDto {

    private long id;

    @NotBlank(message = "kata sandi tidak boleh kosong")
    @Size(min = 6, message = "kata sandi harus mengandung minimal 6 karakter ")
    @Pattern(regexp = "(^(?=\\S*([a-z]|[A-Z])+\\S*[0-9]+\\S*)|^(?=\\S*[0-9]+\\S*([a-z]|[A-Z])+\\S*)).{6,}",message = "kata sandi harus mengandung gabungan angka dan huruf")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty("first_name")
    @NotBlank
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @Pattern(regexp = "(MALE)|(FEMALE)")
    private String sex;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("phone_number")
    @Pattern(regexp = "^08[\\d+]{10,15}")
    private String phoneNumber;

    @Pattern(regexp = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{1,}$", message = "email format should be valid")
    private String email;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
