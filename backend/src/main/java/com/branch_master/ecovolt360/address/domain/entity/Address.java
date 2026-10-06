package com.branch_master.ecovolt360.address.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="addresses")
@Getter
@Setter
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String cep;
    private String state;
    private String city;
    private String neighborhood;
    private String street;
    private String number;
    private String complement;

    public Address(String cep, String state, String city, String neighborhood,
                   String street, String number, String complement) {
        this.cep = cep;
        this.state = state;
        this.city = city;
        this.neighborhood = neighborhood;
        this.street = street;
        this.number = number;
        this.complement = complement;
    }
}
