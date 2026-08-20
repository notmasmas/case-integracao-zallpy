/*
This class offers services for the Client domain.
It includes method to format name to uppercase

Last updated by: @Helena
 */

package com.branch_master.ecovolt360.arqTeste.domain.services;

import com.branch_master.ecovolt360.arqTeste.domain.models.Client;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    /*
    this is an example of a domain service method that formats the name to uppercase
    we could also have methods to validate values and reinforce business rules
     */
    public static Client processClient(Client client) {

        client.setName(client.getName().toUpperCase());

        return client;
    }
}
