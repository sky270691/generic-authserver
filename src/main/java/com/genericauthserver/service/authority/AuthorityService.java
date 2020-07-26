package com.genericauthserver.service.authority;

import com.genericauthserver.entity.Authority;

import java.util.List;

public interface AuthorityService {

    Authority findAuthorityById(int id);
    Authority findAuthorityByName(String name);
    List<Authority> findAll();
}
