package com.genericauthserver.service.authority;

import com.genericauthserver.dto.AuthorityDto;
import com.genericauthserver.entity.Authority;

import java.util.List;

public interface AuthorityService {

    int addNewAuthority(AuthorityDto dto);
    Authority findAuthorityById(int id);
    Authority findAuthorityByName(String name);
    List<Authority> findAll();
}
