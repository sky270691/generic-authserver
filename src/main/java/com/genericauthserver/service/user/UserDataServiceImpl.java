package com.genericauthserver.service.user;

import com.genericauthserver.dto.UserRegisterUpdateDto;
import com.genericauthserver.dto.UserResetPasswordDto;
import com.genericauthserver.entity.Authority;
import com.genericauthserver.entity.User;
import com.genericauthserver.exception.UserException;
import com.genericauthserver.mapper.UserMapper;
import com.genericauthserver.repository.UserRepository;
import com.genericauthserver.config.security.SecurityUser;
import com.genericauthserver.service.authcode.AuthCodeService;
import com.genericauthserver.service.authority.AuthorityService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDataServiceImpl implements UserDataService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final Logger logger;
    private final AuthCodeService authCodeService;
    private final UserMapper userMapper;
    private final String backendEndpointPrefix;
    private final String authserverEndpointPrefix;
    private static final Map<String,User> USER_TEMP_CODE_PAIR = new HashMap<>();

    @Autowired
    public UserDataServiceImpl(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               AuthorityService authorityService,
                               AuthCodeService authCodeService,
                               UserMapper userMapper,
                               @Value("${backend.live-endpoint.prefix}") String backendEndpointPrefix,
                               @Value("${auth-server.live-endpoint.prefix}") String authserverEndpointPrefix) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityService = authorityService;
        this.userMapper = userMapper;
        this.backendEndpointPrefix = backendEndpointPrefix;
        this.authserverEndpointPrefix = authserverEndpointPrefix;
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.authCodeService = authCodeService;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user;
        if(s.contains("@")){
            user = findUserByEmail(s);
        }else{
            user = findUserByPhoneNumber(s);
        }
        return new SecurityUser(user);
    }


    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new UserException("User with email: '"+email+"' not found"));
    }

    @Override
    public User findUserByPhoneNumber(String phoneNumnber){
        return userRepository.findByPhoneNumber(phoneNumnber).orElseThrow(()->new UserException("User with phone number: '"+phoneNumnber+"' not found"));
    }



    @Override
    public void login(String email, String password){
        String authHeaderPrefix = "Basic ";
        String credential = Base64.getEncoder().encodeToString((email+":"+password).getBytes());
        String fullAuthHeader = authHeaderPrefix+credential;
        System.out.println(fullAuthHeader);
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization",fullAuthHeader);
        String url = authserverEndpointPrefix+"oauth/authorize?scope=READ_WRITE&client_id=front-stm&response_type=code";

        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.exchange(url, HttpMethod.GET,entity,String.class,null,null);
        } catch (RestClientException e) {
            throw new UserException(e.getLocalizedMessage());
        }
    }

    @Override
    public String getJwtToken(String authCode) {
        RestTemplate restTemplate = new RestTemplate();

        String authHeaderPrefix = "Basic ";
        String clientCredential = Base64.getEncoder().encodeToString("front-stm:front123".getBytes());
        String fullAuthHeader = authHeaderPrefix+clientCredential;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization",fullAuthHeader);

        MultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("grant_type","authorization_code");
        multiValueMap.add("code",authCode);

        HttpEntity<MultiValueMap<String,String>> entity =new HttpEntity<>(multiValueMap,headers);
        String url = authserverEndpointPrefix+"oauth/token";

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(url,entity,String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new UserException("authorization code not valid");
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(response.getBody());
            String token = jsonNode.get("access_token").asText();
            return token;
        } catch (IOException e) {
            e.printStackTrace();
            throw new UserException("getting jwt token error");
        }

    }

    public String getJwtToken2(String emailOrPhone) {
        RestTemplate restTemplate = new RestTemplate();

        String authHeaderPrefix = "Basic ";
        String clientCredential = Base64.getEncoder().encodeToString("front-stm:front123".getBytes());
        String fullAuthHeader = authHeaderPrefix+clientCredential;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization",fullAuthHeader);

        MultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("grant_type","password");
        multiValueMap.add("username",emailOrPhone);
        multiValueMap.add("password",emailOrPhone);

        HttpEntity<MultiValueMap<String,String>> entity =new HttpEntity<>(multiValueMap,headers);
        String url = authserverEndpointPrefix+"oauth/token";

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(url,entity,String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(response.getBody());
            String token = jsonNode.get("access_token").asText();
            return token;
        } catch (IOException e) {
            e.printStackTrace();
            throw new UserException("getting jwt token error");
        }

    }


    @Override
    @Transactional
    public UserRegisterUpdateDto registerNewUser(UserRegisterUpdateDto dto) {
        User user = userMapper.convertUserRegisterUpdateDtoToUserEntity(dto,passwordEncoder);
        Optional<User> existingUserEmail = userRepository.findByEmail(dto.getEmail());
        Optional<User> existingUserPhone = userRepository.findByPhoneNumber(dto.getPhoneNumber());
        user.setAuthorityList(new ArrayList<>());

        Authority authority = authorityService.findAuthorityByName("ROLE_USER");
        user.getAuthorityList().add(authority);

        if(!existingUserEmail.isEmpty()){
            throw new UserException("User with email '"+dto.getEmail()+"' already exist");
        }else if(!existingUserPhone.isEmpty()){
            throw new UserException("User with phone number '"+dto.getPhoneNumber()+"' already exist");
        }


        UserRegisterUpdateDto registeredUser =  userMapper.convertToUserRegisterUpdateDto(userRepository.save(user));
        HttpEntity<UserRegisterUpdateDto> entity = new HttpEntity<>(registeredUser);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(authserverEndpointPrefix+"api/v1/users/register", HttpMethod.POST,entity,String.class);

        return registeredUser;
    }

    private String generateAlphaNumeric(int stringLength){
        String alphaLower = "abcdefghijklmnopqrstuvwxyz";
//        String alphaUpper = alphaLower.toUpperCase();
        String number = "0123456789";

        String combination = alphaLower + number;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            Random random = new SecureRandom();
            sb.append(combination.charAt(random.nextInt(combination.length())));
        }
        return sb.toString();
    }

    @Override
    public void sendResetPasswordCodeToEmail(String email) {
        User user = findUserByEmail(email);
        if (USER_TEMP_CODE_PAIR.containsValue(user)) {
            for (Map.Entry<String, User> stringUserEntry : USER_TEMP_CODE_PAIR.entrySet()) {
                if(stringUserEntry.getValue().equals(user)){
                    USER_TEMP_CODE_PAIR.remove(stringUserEntry.getKey());
                }
            }
        }
        String uniqueCode = generateAlphaNumeric(6);
        StringBuilder emailMessage = new StringBuilder();
        emailMessage.append("Halo "+user.getFirstName()+" "+user.getLastName()+", silahkan input kode ini di aplikasi Satu Tas Merah\n");
        USER_TEMP_CODE_PAIR.put(uniqueCode,user);
        emailMessage.append(uniqueCode);
        emailMessage.append("\n\n\n");
        emailMessage.append("--- SATU TAS MERAH ---");
        authCodeService.sendResetPasswordCodeToEmail(uniqueCode,email);
    }

    @Override
    public Optional<UserRegisterUpdateDto> validateResetPasswordCode(String code) {

        for (Map.Entry<String, User> stringUserEntry : USER_TEMP_CODE_PAIR.entrySet()) {
            if(code.equalsIgnoreCase(stringUserEntry.getKey())){
                User user = USER_TEMP_CODE_PAIR.get(code);
                return Optional.of(userMapper.convertToUserRegisterUpdateDto(user));
            }
        }
        return Optional.empty();
    }

    @Override
    public void updateUserData(UserRegisterUpdateDto userRegisterUpdateDto){
        User user = userMapper.convertUserRegisterUpdateDtoToUserEntity(userRegisterUpdateDto,passwordEncoder);
        userRepository.save(user);
    }

    @Override
    public void updateUserDataGoogle(UserRegisterUpdateDto userRegisterUpdateDto){
        User user = null;
//        if(userRegisterUpdateDto.getEmail() != null){
//            user = userRepository.findByEmail(userRegisterUpdateDto.getEmail())
//                    .orElseThrow(()-> new UserException("user with email: '"+userRegisterUpdateDto.getEmail()+"' not found"));
//        }else if(userRegisterUpdateDto.getPhoneNumber() != null){
//            user = userRepository.findByPhoneNumber(userRegisterUpdateDto.getPhoneNumber())
//                    .orElseThrow(()-> new UserException("user with phone number: '"+userRegisterUpdateDto.getPhoneNumber()+"' not found"));
//        }

        if(userRegisterUpdateDto.getEmail() != null && userRegisterUpdateDto.getPhoneNumber() != null){
            Optional<User> userOptionalByEmail = userRepository.findByEmail(userRegisterUpdateDto.getEmail());
            Optional<User> userOptionalByPhone = userRepository.findByPhoneNumber(userRegisterUpdateDto.getPhoneNumber());
            if(userOptionalByEmail.isPresent()){
                user = userOptionalByEmail.get();
            }else if(userOptionalByPhone.isPresent()){
                user = userOptionalByPhone.get();
            }else {
                throw new UserException("user not found");
            }
        }else if(userRegisterUpdateDto.getEmail() != null){
            user = userRepository.findByEmail(userRegisterUpdateDto.getEmail())
                    .orElseThrow(()-> new UserException("user with email: '"+userRegisterUpdateDto.getEmail()+"' not found"));
        }else if(userRegisterUpdateDto.getPhoneNumber() != null){
            user = userRepository.findByPhoneNumber(userRegisterUpdateDto.getPhoneNumber())
                    .orElseThrow(()-> new UserException("user with phone number: '"+userRegisterUpdateDto.getPhoneNumber()+"' not found"));
        }

        user.setFirstName(userRegisterUpdateDto.getFirstName());
        if(userRegisterUpdateDto.getLastName() != null && !userRegisterUpdateDto.getLastName().equalsIgnoreCase("")){
            user.setLastName(userRegisterUpdateDto.getLastName());
        }
        user.setSex(userRegisterUpdateDto.getSex());
        user.setDateOfBirth(userRegisterUpdateDto.getDateOfBirth());

        if(userRegisterUpdateDto.getPhoneNumber() !=null && !userRegisterUpdateDto.getPhoneNumber().equalsIgnoreCase("")){
            user.setPhoneNumber(userRegisterUpdateDto.getPhoneNumber());
        }

        if(userRegisterUpdateDto.getEmail() !=null && !userRegisterUpdateDto.getEmail().equalsIgnoreCase("")){
            user.setEmail(userRegisterUpdateDto.getEmail());
        }
        if(userRegisterUpdateDto.getFcmDataList() != null && !userRegisterUpdateDto.getFcmDataList().isEmpty()){
            for (int i = 0; i < userRegisterUpdateDto.getFcmDataList().size(); i++) {
                if(i == userRegisterUpdateDto.getFcmDataList().size()-1){
                    user.setFcmData(userRegisterUpdateDto.getFcmDataList().get(i));
                }
                user.setFcmData(userRegisterUpdateDto.getFcmDataList().get(i)+";");
            }
        }

        userRepository.save(user);

//        UserRegisterUpdateDto registeredUser =  userMapper.convertToUserRegisterUpdateDto(savedUser);
//        HttpEntity<UserRegisterUpdateDto> entity = new HttpEntity<>(registeredUser);
//        RestTemplate restTemplate = new RestTemplate();
//        try {
//            restTemplate.exchange(resourceServerBackendRegistrationUrl, HttpMethod.POST,entity,String.class);
//        } catch (RestClientException e) {
//            e.printStackTrace();
//        }
//

    }

    @Override
    @Transactional
    public void addNewSellerByCms(UserRegisterUpdateDto dto) {
        User user = null;

        try {
            user = findUserByEmail(dto.getEmail());
        } catch (UserException exception) {
            User seller = userMapper.convertUserRegisterUpdateDtoToUserEntity(dto,passwordEncoder);
            String userEmail = userRepository.save(seller).getEmail();
            activateSellerAccount(userEmail);
        }

        if(user != null){
            throw new UserException("user with email: "+dto.getEmail()+" already exist");
        }
    }

    @Override
    @Transactional
    public void activateSellerAccount(String email) {
        User user = findUserByEmail(email);
        Authority sellerAuthority = authorityService.findAll().stream()
                .filter(authority -> authority.getName().contains("SELLER"))
                .findAny()
                .orElseThrow(()->new UserException("Authority failed to input"));
        if (user.getAuthorityList() == null) {
            user.setAuthorityList(new ArrayList<>());
        }
        if(user.getAuthorityList().stream().noneMatch(auth->auth.getName().contains("SELLER"))){
            user.getAuthorityList().add(sellerAuthority);
        }else{
            throw new UserException("User already a seller");
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean updatePassword(UserResetPasswordDto userResetPasswordDto, String codeHeader) {
        if(codeHeader!=null && USER_TEMP_CODE_PAIR.containsKey(codeHeader)){
            User user;
            try {
                user = findUserByEmail(userResetPasswordDto.getEmail());
            } catch (NullPointerException e) {
                throw new UserException("User is not exist");
            }
            UserRegisterUpdateDto userRegisterDto = userMapper.convertToUserRegisterUpdateDto(user);
            userRegisterDto.setPassword(userResetPasswordDto.getPassword());
            updateUserData(userRegisterDto);
        }else{
            throw new UserException("Error Occured");
        }
        return true;
    }

    @Override
    @Transactional
    public UserRegisterUpdateDto updateUserAuthority(String userEmail, Map<String, List<Integer>> authorityList) {

        User user = findUserByEmail(userEmail);
        List<Authority> findAddedAuthority = authorityList.values()
                .stream()
                .flatMap(Collection::stream)
                .map(authorityService::findAuthorityById)
                .collect(Collectors.toList());
        user.setAuthorityList(findAddedAuthority);
        User savedUser = userRepository.save(user);

        if(savedUser.getAuthorityList().stream().anyMatch(x->x.getId()==4)){

            HttpHeaders headers = new HttpHeaders();
            headers.add("Server_Data","true");
            HttpEntity<?> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            String url = backendEndpointPrefix+"api/v1/users/activate_seller/"+savedUser.getEmail();
            try {
                restTemplate.exchange(url,HttpMethod.PUT,entity,String.class);
            } catch (RestClientException e) {
                e.printStackTrace();
            }
        }

        UserRegisterUpdateDto dto = userMapper.convertToUserRegisterUpdateDto(savedUser);
        return dto;
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(()->new UserException("user with id:'"+id+"' not found"));
    }

    @Override
    public void loginByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(email));
            newUser.setAuthorityList(new ArrayList<>());
            newUser.getAuthorityList().add(authorityService.findAuthorityById(2));
            userRepository.saveAndFlush(newUser);
        }else{
            user.get().setPassword(passwordEncoder.encode(user.get().getEmail()));
            userRepository.saveAndFlush(user.get());
        }
        login(email,email);
    }

    @Override
    public String loginByPhoneGoogle(String phoneNumber){


        phoneNumber = phoneNumber.trim();
        if(phoneNumber.startsWith("0")){
            phoneNumber = phoneNumber.replaceAll("^0","+62");
        }else if(phoneNumber.startsWith("62")){
            phoneNumber = phoneNumber.replaceAll("^62","+62");
        }else if(phoneNumber.startsWith("8")){
            phoneNumber = "+62"+phoneNumber;
        }

        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if(user.isEmpty()){
            User newUser = new User();
            newUser.setPhoneNumber(phoneNumber);
            newUser.setPassword(passwordEncoder.encode(phoneNumber));
            newUser.setAuthorityList(new ArrayList<>());
            newUser.getAuthorityList().add(authorityService.findAuthorityById(2));
            userRepository.saveAndFlush(newUser);
        }else{
            user.get().setPassword(passwordEncoder.encode(user.get().getPhoneNumber()));
            userRepository.saveAndFlush(user.get());
        }
        return getJwtToken2(phoneNumber);
    }

    @Override
    public String loginByEmailGoogle(String email){

        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(email));
            newUser.setAuthorityList(new ArrayList<>());
            newUser.getAuthorityList().add(authorityService.findAuthorityById(2));
            userRepository.saveAndFlush(newUser);
        }else{
            user.get().setPassword(passwordEncoder.encode(user.get().getEmail()));
            userRepository.saveAndFlush(user.get());
        }
        return getJwtToken2(email);
    }


    //delete the temp code on 23:59 everyday
    @Scheduled(cron = "0 59 23 * * ?")
    protected void deleteTempCode(){
        if(!USER_TEMP_CODE_PAIR.entrySet().isEmpty()){
            USER_TEMP_CODE_PAIR.clear();
        }
    }

}
