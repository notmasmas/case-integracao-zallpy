package com.branch_master.ecovolt360.user;

import com.branch_master.ecovolt360.user.dto.UserBodyDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String phone;
    private String cpf;

    @Column(name = "address_id")
    private UUID addressId;

    private String email;
    private String password;
    private String role;

    public User(UserBodyDTO userDTO, String passwordHash, UUID addressId) {
        this.name = userDTO.name();
        this.email = userDTO.email();
        this.password = passwordHash;
        this.addressId = addressId;
        this.role = "CUSTOMER";
    }
}
