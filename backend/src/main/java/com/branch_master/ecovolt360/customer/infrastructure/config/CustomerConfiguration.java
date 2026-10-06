package com.branch_master.ecovolt360.customer.infrastructure.config;

import com.branch_master.ecovolt360.address.domain.repository.AddressRepository;
import com.branch_master.ecovolt360.customer.application.service.CustomerService;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.user.application.port.PasswordHasher;
import com.branch_master.ecovolt360.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerConfiguration {

    @Bean
    public CustomerService customerService(
            CustomerRepository customerRepository,
            AddressRepository addressRepository,
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        return new CustomerService(
                customerRepository,
                addressRepository,
                userRepository,
                passwordHasher
        );
    }
}
