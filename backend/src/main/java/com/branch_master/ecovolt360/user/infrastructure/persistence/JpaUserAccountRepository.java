package com.branch_master.ecovolt360.user.infrastructure.persistence;

import com.branch_master.ecovolt360.user.domain.entity.User;
import com.branch_master.ecovolt360.user.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserAccountRepository implements UserRepository {

    private final SpringDataUserAccountRepository repository;

    public JpaUserAccountRepository(SpringDataUserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return repository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }
}
