/*
This application is related to the Client workflow.
It takes a repository interface as its sole attribute and call it to save the client on the DB
After the domain's service processes it.

Last updated by: @Helena
 */

package com.branch_master.ecovolt360.arqTeste.application;

import com.branch_master.ecovolt360.arqTeste.application.repositories.IClientRepository;
import com.branch_master.ecovolt360.arqTeste.domain.models.Client;
import com.branch_master.ecovolt360.arqTeste.domain.services.ClientService;
import org.springframework.stereotype.Service;

@Service
public class ClientApplication {

    IClientRepository clienteRepository;

    public ClientApplication(IClientRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Client processApplication(Client clientRequest) {

        Client client = ClientService.processClient(clientRequest);

        return clienteRepository.save(client);

    }
}
