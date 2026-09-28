package com.branch_master.ecovolt360.auth.domain.entity;
import java.util.UUID;
import jakarta.persistence.*;


@Entity
@Table(name = "users")
public class  User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    private UUID addressId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public Role getRole() {
        return role;
    }

    protected  User() {

    }
}