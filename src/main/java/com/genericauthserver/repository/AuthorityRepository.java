package com.genericauthserver.repository;

import com.genericauthserver.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority,Integer> {

    Optional<Authority> findAuthorityById(int id);
    Optional<Authority> findAuthorityByName(String name);
}
