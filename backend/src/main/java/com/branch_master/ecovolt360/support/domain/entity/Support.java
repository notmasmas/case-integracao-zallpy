package com.branch_master.ecovolt360.support.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="support")
@Getter
@Setter
@NoArgsConstructor
public class Support {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    public Support(UUID userId) {
        this.userId = userId;
    }
}
