package com.genericauthserver.service.authority;

import com.genericauthserver.entity.Authority;

public interface AuthorityService {

    Authority findAuthorityById(int id);
    Authority findAuthorityByName(String name);

}
