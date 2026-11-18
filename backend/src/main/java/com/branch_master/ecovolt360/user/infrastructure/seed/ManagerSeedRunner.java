package com.branch_master.ecovolt360.user.infrastructure.seed;

import com.branch_master.ecovolt360.user.application.service.ManagerSeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ManagerSeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ManagerSeedRunner.class);

    private final ManagerSeedService managerSeedService;
    private final String name;
    private final String email;
    private final String password;
    private final String cpf;

    public ManagerSeedRunner(
            ManagerSeedService managerSeedService,
            @Value("${app.manager.name:}") String name,
            @Value("${app.manager.email:}") String email,
            @Value("${app.manager.password:}") String password,
            @Value("${app.manager.cpf:}") String cpf
    ) {
        this.managerSeedService = managerSeedService;
        this.name = name;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean created = managerSeedService.createIfMissing(name, email, password, cpf);
        if (created) {
            log.info("Gerente pré-cadastrado criado: {}", email);
        } else {
            log.info("Cadastro do gerente ignorado (variáveis MANAGER_* vazias ou e-mail já cadastrado).");
        }
    }
}
