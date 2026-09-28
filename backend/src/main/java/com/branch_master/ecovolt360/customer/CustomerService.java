package com.branch_master.ecovolt360.customer;

import com.branch_master.ecovolt360.address.Address;
import com.branch_master.ecovolt360.address.AddressRepository;
import com.branch_master.ecovolt360.customer.dto.CustomerBodyDTO;
import com.branch_master.ecovolt360.customer.dto.CustomerDetailsDTO;
import com.branch_master.ecovolt360.user.Pbkdf2PasswordHasher;
import com.branch_master.ecovolt360.user.User;
import com.branch_master.ecovolt360.user.UserRepository;
import com.branch_master.ecovolt360.user.dto.UserBodyDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Pbkdf2PasswordHasher passwordHasher;

    @Transactional
    public CustomerDetailsDTO processCustomer(@Valid CustomerBodyDTO customerDTO) {
        UserBodyDTO userDTO = customerDTO.user();

        User user = userRepository.findByCpf(userDTO.cpf())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "CPF não cadastrado."));

        if (customerRepository.existsByUserId(user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este CPF já possui cadastro.");
        }

        if (userRepository.existsByEmailAndIdNot(userDTO.email(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso.");
        }

        Address address = addressRepository.save(new Address(userDTO.address()));

        user.setName(userDTO.name());
        user.setEmail(userDTO.email());
        user.setPassword(passwordHasher.hash(userDTO.password()));
        user.setAddressId(address.getId());
        userRepository.save(user);

        Customer newCustomer = customerRepository.save(new Customer(user.getId()));
        return new CustomerDetailsDTO(newCustomer);
    }
}
