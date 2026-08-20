/*
Mapper class for the Client domain and ClientDTO record.
It contains methods for both ways of formatting: DTO -> domain & domain -> DTO

Last updated by: @Helena
 */

package com.branch_master.ecovolt360.arqTeste.presentation.DTOs;

import com.branch_master.ecovolt360.arqTeste.domain.models.Client;

public class ClientMapper {

    public static Client toClient(ClientDTO clientDTO) {
        return new Client(clientDTO.name());
    }

    public static ClientDTO toDTO(Client client) {
        return new ClientDTO(client.getName());
    }
}
