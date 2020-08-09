package com.genericauthserver.repository;

import com.genericauthserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findUserByEmailOrPhoneNumber(String email,String phoneNumber);
}
