package com.genericauthserver.genericauthserver.repository;

import com.genericauthserver.genericauthserver.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client,Long> {

    Client findById(long id);
    Client findByName(String name);

}
