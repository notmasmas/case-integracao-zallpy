package com.branch_master.ecovolt360.ticket.infrastructure.persistence;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByIdAndCustomerId(UUID id, UUID customerId);
    List<Ticket> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    @Modifying(clearAutomatically = true)
    @Query("""
            update Ticket t
               set t.evaluate = :evaluation,
                   t.evaluateComment = :comment,
                   t.isEvaluated = true,
                   t.updatedAt = :updatedAt
             where t.id = :id
               and t.customerId = :customerId
               and t.isEvaluated = false
               and t.status in (
                   com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus.RESOLVED,
                   com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus.CLOSED
               )
            """)
    int updateEvaluationAndComment(
            @Param("id") UUID id,
            @Param("customerId") UUID customerId,
            @Param("evaluation") Integer evaluation,
            @Param("comment") String comment,
            @Param("updatedAt") ZonedDateTime updatedAt
    );

    @Modifying(clearAutomatically = true)
    @Query("""
            update Ticket t
               set t.status = :status,
                   t.updatedAt = :updatedAt
             where t.id = :id
               and t.status <> com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus.CLOSED
            """)
    int updateStatus(
            @Param("id") UUID id,
            @Param("status") TicketStatus status,
            @Param("updatedAt") ZonedDateTime updatedAt
    );
}
