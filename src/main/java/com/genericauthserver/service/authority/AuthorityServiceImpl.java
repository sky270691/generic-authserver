package com.genericauthserver.service.authority;

import com.genericauthserver.dto.AuthorityDto;
import com.genericauthserver.entity.Authority;
import com.genericauthserver.mapper.AuthorityMapper;
import com.genericauthserver.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final AuthorityMapper authorityMapper;

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository authorityRepository,
                                @Lazy AuthorityMapper authorityMapper) {
        this.authorityRepository = authorityRepository;
        this.authorityMapper = authorityMapper;
    }

    @Override
    public int addNewAuthority(AuthorityDto dto) {
        dto.setName("ROLE_"+dto.getName().toUpperCase());
        return authorityRepository.save(authorityMapper.convertToAuthorityEntity(dto)).getId();
    }

    @Override
    public Authority findAuthorityById(int id) {
        return authorityRepository.findAuthorityById(id).orElse(null);
    }

    @Override
    public Authority findAuthorityByName(String name) {
        return authorityRepository.findByNameContains(name).orElse(null);
    }

    @Override
    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }
}
