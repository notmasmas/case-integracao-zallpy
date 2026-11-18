package com.branch_master.ecovolt360.project.domain.repository;

import com.branch_master.ecovolt360.project.domain.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório único do módulo de projetos.
 * O endereço é carregado junto (JOIN) pelo EntityGraph, sem precisar de outro repositório.
 */
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @EntityGraph(attributePaths = "address")
    List<Project> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
}
