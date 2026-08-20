/*
Interface for the Client repository.
It's not necessary to implement this class, as JPA implements it during compilation

Last updated by: @Helena
 */

package com.branch_master.ecovolt360.arqTeste.application.repositories;

import com.branch_master.ecovolt360.arqTeste.domain.models.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IClientRepository extends CrudRepository<Client, Long> {

    List<Client> findByName(String name);
    Client findById(long id);

}
