package com.branch_master.ecovolt360.project.domain.entity;

import com.branch_master.ecovolt360.address.domain.entity.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor
public class Project {

    @Id
    private UUID id;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "address_id")
    private UUID addressId;

    // Somente leitura: o endereço vem junto na mesma consulta (JOIN), sem repositório extra.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address address;

    private String name;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Column(name = "installation_date")
    private ZonedDateTime installationDate;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}
