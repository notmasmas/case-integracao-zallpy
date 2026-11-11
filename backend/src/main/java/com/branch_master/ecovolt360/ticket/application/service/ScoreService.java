package com.branch_master.ecovolt360.ticket.application.service;

import com.branch_master.ecovolt360.support.domain.entity.Support;
import com.branch_master.ecovolt360.support.domain.repository.SupportRepository;
import com.branch_master.ecovolt360.ticket.application.dto.MetricsDTO;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ScoreService {

    private TicketRepository ticketRepository;
    private SupportRepository supportRepository;

    public MetricsDTO calculateMetrics(UUID userId) {

        Optional<Support> support = supportRepository.findByUserId(userId);

        if (support.isPresent()) {
            return ticketRepository.findMetricsBySupportId(support.get().getId());
        } else {
            throw new IllegalArgumentException("Este suporte não foi encontrado");
        }
    }
}
