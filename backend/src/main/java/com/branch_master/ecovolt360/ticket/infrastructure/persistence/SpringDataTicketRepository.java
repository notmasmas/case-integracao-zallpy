package com.branch_master.ecovolt360.ticket.infrastructure.persistence;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
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
    List<Ticket> findByCustomerId(UUID customerId);

    @Modifying(clearAutomatically = true)
    @Query("""
            update Ticket t
               set t.evaluate = :evaluation,
                   t.evaluateComment = :comment,
                   t.updatedAt = :updatedAt
             where t.id = :id
               and t.customerId = :customerId
            """)
    int updateEvaluationAndComment(
            @Param("id") UUID id,
            @Param("customerId") UUID customerId,
            @Param("evaluation") Integer evaluation,
            @Param("comment") String comment,
            @Param("updatedAt") ZonedDateTime updatedAt
    );
}
