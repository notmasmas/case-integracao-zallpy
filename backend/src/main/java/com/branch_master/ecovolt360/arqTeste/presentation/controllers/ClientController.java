/*
This controller receives HTTP requests related to the /client path
The data is received as a DTO, mapped to Client in order to inject it into the application service,
then the outcome is mapped to a DTO again for the server response.

Last updated by: @Helena
 */

package com.branch_master.ecovolt360.arqTeste.presentation.controllers;

import com.branch_master.ecovolt360.arqTeste.application.ClientApplication;
import com.branch_master.ecovolt360.arqTeste.domain.models.Client;
import com.branch_master.ecovolt360.arqTeste.presentation.DTOs.ClientDTO;
import com.branch_master.ecovolt360.arqTeste.presentation.DTOs.ClientMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

    ClientApplication clientApplication;

    public ClientController(ClientApplication clientApplication) {
        this.clientApplication = clientApplication;
    }

    @PostMapping("/client")
    public ClientDTO postClient(@RequestBody ClientDTO clientRequest) {

        Client client = clientApplication.processApplication(ClientMapper.toClient(clientRequest));

        return ClientMapper.toDTO(client);
    }
}
