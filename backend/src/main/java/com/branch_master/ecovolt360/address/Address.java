package com.branch_master.ecovolt360.address;

import com.branch_master.ecovolt360.address.dto.AddressBodyDTO;
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

    public Address(AddressBodyDTO addressDTO) {
        this.cep = addressDTO.cep();
        this.state = addressDTO.state();
        this.city = addressDTO.city();
        this.neighborhood = addressDTO.neighborhood();
        this.street = addressDTO.street();
        this.number = addressDTO.number();
        this.complement = addressDTO.complement();
    }
}
