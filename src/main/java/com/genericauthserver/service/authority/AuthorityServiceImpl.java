package com.genericauthserver.service.authority;

import com.genericauthserver.entity.Authority;
import com.genericauthserver.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public Authority findAuthorityById(int id) {
        return authorityRepository.findAuthorityById(id).orElse(null);
    }

    @Override
    public Authority findAuthorityByName(String name) {
        return authorityRepository.findAuthorityByName(name).orElse(null);
    }

    @Override
    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }
}
