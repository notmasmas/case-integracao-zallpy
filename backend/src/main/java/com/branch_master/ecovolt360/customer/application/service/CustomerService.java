package com.branch_master.ecovolt360.customer.application.service;

import com.branch_master.ecovolt360.address.application.dto.AddressBodyDTO;
import com.branch_master.ecovolt360.address.domain.entity.Address;
import com.branch_master.ecovolt360.address.domain.repository.AddressRepository;
import com.branch_master.ecovolt360.customer.application.dto.CustomerBodyDTO;
import com.branch_master.ecovolt360.customer.application.dto.CustomerDetailsDTO;
import com.branch_master.ecovolt360.customer.application.exception.CpfAlreadyRegisteredException;
import com.branch_master.ecovolt360.customer.application.exception.CpfNotFoundException;
import com.branch_master.ecovolt360.customer.application.exception.EmailAlreadyInUseException;
import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.user.application.dto.UserBodyDTO;
import com.branch_master.ecovolt360.user.application.port.PasswordHasher;
import com.branch_master.ecovolt360.user.domain.entity.User;
import com.branch_master.ecovolt360.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;

public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public CustomerService(
            CustomerRepository customerRepository,
            AddressRepository addressRepository,
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public CustomerDetailsDTO processCustomer(CustomerBodyDTO customerDTO) {
        UserBodyDTO userDTO = customerDTO.user();

        User user = userRepository.findByCpf(userDTO.cpf())
                .filter(found -> "CUSTOMER".equals(found.getRole()))
                .orElseThrow(CpfNotFoundException::new);

        if (customerRepository.existsByUserId(user.getId())) {
            throw new CpfAlreadyRegisteredException();
        }

        if (userRepository.existsByEmailAndIdNot(userDTO.email(), user.getId())) {
            throw new EmailAlreadyInUseException();
        }

        AddressBodyDTO addressDTO = userDTO.address();
        Address address = addressRepository.save(new Address(
                addressDTO.cep(),
                addressDTO.state(),
                addressDTO.city(),
                addressDTO.neighborhood(),
                addressDTO.street(),
                addressDTO.number(),
                addressDTO.complement()
        ));

        user.setName(userDTO.name());
        user.setEmail(userDTO.email());
        user.setPassword(passwordHasher.hash(userDTO.password()));
        user.setAddressId(address.getId());
        userRepository.save(user);

        Customer newCustomer = customerRepository.save(new Customer(user.getId()));
        return new CustomerDetailsDTO(newCustomer);
    }
}
