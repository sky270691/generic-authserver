package com.genericauthserver.controller;

import com.genericauthserver.dto.UserRegisterUpdateDto;
import com.genericauthserver.dto.UserResetPasswordDto;
import com.genericauthserver.service.user.UserDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserDataService userDataService;
    private final Logger logger;
    private final String appId;

    @Autowired
    public UserController(UserDataService userDataService,
                          @Value("${endpoint.header.appid}") String appId) {
        this.userDataService = userDataService;
        this.appId = appId;
        this.logger  = LoggerFactory.getLogger(this.getClass());
    }

    @PostMapping("/seller/login")
    public ResponseEntity<?> loginUserSeller(@RequestBody Map<String,String> userCredential, HttpServletRequest request){

        logger.info(request.getRequestURI()+" requested");

        Map<String,String> loginResponse = new HashMap<>();

        if(userCredential.containsKey("email") && userCredential.containsKey("password")){
            userDataService.login(userCredential.get("email"),userCredential.get("password"));
            loginResponse.put("status","success");
            loginResponse.put("message","check your email for the code");
            return ResponseEntity.ok().body(loginResponse);
        }else{
            loginResponse.put("status","error");
            return ResponseEntity.badRequest().body(loginResponse);
        }
    }

    @PostMapping("/customer/login")
    public ResponseEntity<?> loginUser(@RequestHeader String email){
        System.out.println("header email " + email);
        userDataService.loginByEmail(email);
        Map<String,String> returnBody = new LinkedHashMap<>();
        returnBody.put("status","success");
        returnBody.put("message","check your email for the code");
        return ResponseEntity.ok(returnBody);
    }

    @PostMapping("/customer/login/google")
    public ResponseEntity<?> loginUser(@RequestHeader(required = false) String email, @RequestHeader(value = "phone", required = false) String phoneNumber){

        String token = "";

        if(email != null && !email.equalsIgnoreCase("")){
            token = userDataService.loginByEmailGoogle(email);
        }

        if(phoneNumber != null && !phoneNumber.equalsIgnoreCase("")){
            token = userDataService.loginByPhoneGoogle(phoneNumber);
        }

        String url = "https://api.satutasmerah.com/api/v1/users?token_value="+token+"&Server_Data=true";
        if(email == null){
            url = "https://api.satutasmerah.com/api/v1/users?token_value="+token+"&Server_Data=true&credential="+phoneNumber;
        }else if(phoneNumber == null){
            url = "https://api.satutasmerah.com/api/v1/users?token_value="+token+"&Server_Data=true&credential="+email;
        }

        Map<String,String> returnBody = new LinkedHashMap<>();
        returnBody.put("status","success");
        returnBody.put("user_data",url);
        return ResponseEntity.ok(returnBody);
    }

    @PostMapping("/auth_code")
    public ResponseEntity<?> userLoginCode(@RequestBody Map<String,String> authCode, HttpServletRequest request){

        Map<String,String> tokenResponse = new LinkedHashMap<>();

        if(authCode.containsKey("code")){
            String token = userDataService.getJwtToken(authCode.get("code"));
            tokenResponse.put("status","success");
            tokenResponse.put("access_token",token);
            return ResponseEntity.ok(tokenResponse);
        }else{
            tokenResponse.put("status","error");
            tokenResponse.put("message","error getting token");
            return ResponseEntity.badRequest().body(tokenResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@RequestBody @Valid UserRegisterUpdateDto userRegisterUpdateDto){

        Map<String,Object> response = new LinkedHashMap<>();
        UserRegisterUpdateDto returnedUserData = userDataService.registerNewUser(userRegisterUpdateDto);
        response.put("status","success");
        response.put("registered_user",returnedUserData);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/google")
    public ResponseEntity<?> registerNewGoogleUserPhone(@RequestBody UserRegisterUpdateDto userRegisterUpdateDto){

        Map<String,Object> response = new LinkedHashMap<>();
        UserRegisterUpdateDto returnedUserData = userDataService.registerNewUser(userRegisterUpdateDto);
        response.put("status","success");
        response.put("registered_user",returnedUserData);

        return ResponseEntity.ok(response);

    }

    @PostMapping(value = "/reset_password/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> requestResetPasswordCode(@PathVariable @Email(regexp =
            "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{1,}$",
            message = "email harus menggunakan format yang benar") String email){

        Map<String,String> returnValue = new HashMap<>();
        userDataService.sendResetPasswordCodeToEmail(email);
        returnValue.put("status","success");
        return ResponseEntity.ok(returnValue);
    }

    @GetMapping("/reset_password/{code}")
    public ResponseEntity<?> validateResetCode(@PathVariable String code){
        Optional<UserRegisterUpdateDto> user = userDataService.validateResetPasswordCode(code);
        Map<String,String> returnBody = new HashMap<>();
        if(user.isPresent()){
            returnBody.put("status","success");
            return ResponseEntity.ok(returnBody);
        }else{
            returnBody.put("status","failed");
            return ResponseEntity.badRequest().body(returnBody);
        }
    }

    @PutMapping("/update_password")
    public ResponseEntity<?> updatePassword(@RequestBody UserResetPasswordDto emailPassword, @RequestHeader("X-Code") String code){
        logger.info("user with email: '"+emailPassword.getEmail()+"' tried to update password");
        Map<String, String> responseBody = new HashMap<>();
        boolean updatePasswordStatus = userDataService.updatePassword(emailPassword,code);
        System.out.println(emailPassword);
        logger.info("success reset password for user with email:'"+emailPassword.getEmail()+"'");
        if(updatePasswordStatus){
            responseBody.put("status","success");
            return ResponseEntity.ok(responseBody);
        }else {
            responseBody.put("status","error");
            return ResponseEntity.badRequest().body(responseBody);
        }
    }

    @PutMapping("/update_user")
    public ResponseEntity<?> updateUserData(@RequestBody UserRegisterUpdateDto dto, @RequestHeader("Server-Data") String serverData){
        Map<String,String> returnBody = new LinkedHashMap<>();
        if(serverData.equalsIgnoreCase("true")){

            System.out.println(dto);
            userDataService.updateUserDataGoogle(dto);
            returnBody.put("status","success");
            return ResponseEntity.ok(returnBody);
        }
        returnBody.put("status","bad request");
        return ResponseEntity.badRequest().body(returnBody);
    }

    @PutMapping("/edit_user_authority")
    public ResponseEntity<?> editUserAuthority(@RequestParam("email") String userEmail, @RequestBody Map<String, List<Integer>> authorityList){
        Map<String,Object> returnBody = new LinkedHashMap<>();

        if (authorityList.entrySet().size()<1 || !authorityList.containsKey("role_id_list")) {
            returnBody.put("status","bad request");
            return ResponseEntity.badRequest().build();
        }

        UserRegisterUpdateDto dto = userDataService.updateUserAuthority(userEmail,authorityList);
        returnBody.put("status","success");
        returnBody.put("updated_user_authority",dto);
        return ResponseEntity.ok(returnBody);
    }

    @PostMapping("/add_new_seller")
    public ResponseEntity<?> activateSellerRole(@RequestBody UserRegisterUpdateDto userRegisterUpdateDto){

        userDataService.addNewSellerByCms(userRegisterUpdateDto);
        Map<String,String> returnBody = new LinkedHashMap<>();
        returnBody.put("status","success");
        return ResponseEntity.ok(returnBody);
    }
}
