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
import org.springframework.stereotype.Service;

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
        Address address = addressRepository.save(new Address(userDTO.address()));
        User user = userRepository.save(new User(
                userDTO,
                passwordHasher.hash(userDTO.password()),
                address.getId()
        ));
        Customer newCustomer = customerRepository.save(new Customer(user.getId()));
        return new CustomerDetailsDTO(newCustomer);
    }
}
