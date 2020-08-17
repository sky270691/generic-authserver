package com.genericauthserver.controller;

import com.genericauthserver.dto.AuthorityDto;
import com.genericauthserver.mapper.AuthorityMapper;
import com.genericauthserver.service.authority.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/authorities")
public class AuthorityController {


    private final AuthorityService authorityService;
    private final AuthorityMapper authorityMapper;

    @Autowired
    public AuthorityController(AuthorityService authorityService,
                               AuthorityMapper authorityMapper) {
        this.authorityService = authorityService;
        this.authorityMapper = authorityMapper;
    }

    @GetMapping
    public ResponseEntity<?> getAllAuthority(){

        List<AuthorityDto> authorityDtoList = authorityService.findAll().stream()
                .map(authorityMapper::convertToAuthorityDto)
                .collect(Collectors.toList());

        Map<String,Object> returnBody = new LinkedHashMap<>();
        returnBody.put("status","success");
        returnBody.put("authority_list",authorityDtoList);
        return ResponseEntity.ok(returnBody);
    }

    @PostMapping
    public ResponseEntity<?> addNewAuthority(@RequestBody AuthorityDto dto){

        int id = authorityService.addNewAuthority(dto);
        Map<String,Object> returnBody = new LinkedHashMap<>();
        returnBody.put("status","success");
        returnBody.put("authority_id",id);

        return ResponseEntity.ok(returnBody);

    }
}
