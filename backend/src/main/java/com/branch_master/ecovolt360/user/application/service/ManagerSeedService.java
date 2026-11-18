package com.branch_master.ecovolt360.user.application.service;

import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.user.application.port.PasswordHasher;
import com.branch_master.ecovolt360.user.domain.entity.User;
import com.branch_master.ecovolt360.user.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

public class ManagerSeedService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public ManagerSeedService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    /**
     * Cria o gerente se ainda não existir usuário com o mesmo e-mail.
     *
     * @return true se criou o gerente, false se pulou (dado faltando ou e-mail já cadastrado)
     */
    @Transactional
    public boolean createIfMissing(String name, String email, String password, String cpf) {
        if (isBlank(name) || isBlank(email) || isBlank(password) || isBlank(cpf)) {
            return false;
        }

        // Não altera quem já existe: repetir o cadastro não duplica nem sobrescreve a senha.
        if (userRepository.existsByEmail(email)) {
            return false;
        }

        User manager = new User();
        manager.setName(name);
        manager.setEmail(email);
        manager.setCpf(cpf);
        manager.setPassword(passwordHasher.hash(password));
        manager.setRole(Role.GERENTE.name());
        userRepository.save(manager);
        return true;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
