package com.branch_master.ecovolt360.faq.infrastructure.persistence;

import com.branch_master.ecovolt360.faq.application.exception.FaqUnavailableException;
import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import com.branch_master.ecovolt360.faq.domain.repository.FaqRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionException;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaFaqRepository implements FaqRepository {

    private final SpringDataFaqRepository repository;

    public JpaFaqRepository(SpringDataFaqRepository repository) {this.repository = repository;}

    @Override
    public List<Faq> findAll(){
        try {
            return repository.findAllByOrderByTitleAsc();
        } catch (DataAccessException | TransactionException exception) {
            throw new FaqUnavailableException(exception);
        }
    }
}
