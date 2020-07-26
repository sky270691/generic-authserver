package com.genericauthserver.mapper;

import com.genericauthserver.dto.AuthorityDto;
import com.genericauthserver.entity.Authority;
import org.springframework.stereotype.Component;

@Component
public class AuthorityMapper {

    public AuthorityDto convertToAuthorityDto(Authority authority){
        AuthorityDto dto = new AuthorityDto();
        dto.setId(authority.getId());
        dto.setName(authority.getName());
        dto.setDescription(authority.getDescription());

        return dto;
    }

    public Authority convertToAuthorityEntity(AuthorityDto dto){
        Authority authority = new Authority();
        authority.setId(dto.getId());
        authority.setName(dto.getName());
        authority.setDescription(dto.getDescription());

        return authority;
    }

}
