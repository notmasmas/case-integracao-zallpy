package com.branch_master.ecovolt360.customer.application.service;

import com.branch_master.ecovolt360.address.application.dto.AddressBodyDTO;
import com.branch_master.ecovolt360.address.domain.entity.Address;
import com.branch_master.ecovolt360.address.domain.repository.AddressRepository;
import com.branch_master.ecovolt360.customer.application.dto.CustomerBodyDTO;
import com.branch_master.ecovolt360.customer.application.dto.CustomerDetailsDTO;
import com.branch_master.ecovolt360.customer.application.exception.EmailAlreadyInUseException;
import com.branch_master.ecovolt360.customer.application.exception.FiscalIdAlreadyRegisteredException;
import com.branch_master.ecovolt360.customer.application.exception.FiscalIdNotFoundException;
import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.user.application.dto.UserBodyDTO;
import com.branch_master.ecovolt360.user.application.port.PasswordHasher;
import com.branch_master.ecovolt360.user.domain.entity.User;
import com.branch_master.ecovolt360.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(
                customerRepository,
                addressRepository,
                userRepository,
                passwordHasher
        );
    }

    @Test
    void shouldRegisterCustomerWhenCpfExistsAndEmailIsFree() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID addressId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID customerId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        User user = existingCustomerUser(userId, "98765432100");
        when(userRepository.findByFiscalId("98765432100")).thenReturn(Optional.of(user));
        when(customerRepository.existsByUserId(userId)).thenReturn(false);
        when(userRepository.existsByEmailAndIdNot("cliente@email.com", userId)).thenReturn(false);
        when(passwordHasher.hash("Senha1234")).thenReturn("hashed-password");
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setId(addressId);
            return address;
        });
        when(userRepository.save(user)).thenReturn(user);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(customerId);
            return customer;
        });

        CustomerDetailsDTO result = customerService.processCustomer(customerBody("98765432100"));

        assertEquals(customerId, result.id());
        assertEquals(userId, result.userId());
        assertEquals("Cliente de teste", user.getName());
        assertEquals("cliente@email.com", user.getEmail());
        assertEquals("hashed-password", user.getPassword());
        assertEquals(addressId, user.getAddressId());
        verify(passwordHasher).hash("Senha1234");
    }

    @Test
    void shouldRegisterCustomerWhenCnpjExistsAndEmailIsFree() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID addressId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID customerId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        User user = existingCustomerUser(userId, "12345678998765");
        when(userRepository.findByFiscalId("12345678998765")).thenReturn(Optional.of(user));
        when(customerRepository.existsByUserId(userId)).thenReturn(false);
        when(userRepository.existsByEmailAndIdNot("cliente@email.com", userId)).thenReturn(false);
        when(passwordHasher.hash("Senha1234")).thenReturn("hashed-password");
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setId(addressId);
            return address;
        });
        when(userRepository.save(user)).thenReturn(user);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(customerId);
            return customer;
        });

        CustomerDetailsDTO result = customerService.processCustomer(customerBody("12345678998765"));

        assertEquals(customerId, result.id());
        assertEquals(userId, result.userId());
        assertEquals("Cliente de teste", user.getName());
        assertEquals("cliente@email.com", user.getEmail());
        assertEquals("hashed-password", user.getPassword());
        assertEquals(addressId, user.getAddressId());
        verify(passwordHasher).hash("Senha1234");
    }

    @Test
    void shouldRejectUnknownCpf() {
        when(userRepository.findByFiscalId("98765432100")).thenReturn(Optional.empty());

        assertThrows(FiscalIdNotFoundException.class,
                () -> customerService.processCustomer(customerBody("98765432100")));

        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldRejectUnknownCnpj() {
        when(userRepository.findByFiscalId("12345678998765")).thenReturn(Optional.empty());

        assertThrows(FiscalIdNotFoundException.class,
                () -> customerService.processCustomer(customerBody("12345678998765")));

        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldRejectCpfThatDoesNotBelongToCustomerRole() {
        User user = existingCustomerUser(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "98765432100"
        );
        user.setRole("SUPPORT");
        when(userRepository.findByFiscalId("98765432100")).thenReturn(Optional.of(user));

        assertThrows(FiscalIdNotFoundException.class,
                () -> customerService.processCustomer(customerBody("98765432100")));
    }

    @Test
    void shouldRejectCnpjThatDoesNotBelongToCustomerRole() {
        User user = existingCustomerUser(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "12345678998765"
        );
        user.setRole("SUPPORT");
        when(userRepository.findByFiscalId("12345678998765")).thenReturn(Optional.of(user));

        assertThrows(FiscalIdNotFoundException.class,
                () -> customerService.processCustomer(customerBody("12345678998765")));
    }

    @Test
    void shouldRejectCpfAlreadyRegisteredAsCustomer() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(userRepository.findByFiscalId("98765432100"))
                .thenReturn(Optional.of(existingCustomerUser(userId, "98765432100")));
        when(customerRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(FiscalIdAlreadyRegisteredException.class,
                () -> customerService.processCustomer(customerBody("98765432100")));
    }

    @Test
    void shouldRejectCnpjAlreadyRegisteredAsCustomer() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(userRepository.findByFiscalId("12345678998765"))
                .thenReturn(Optional.of(existingCustomerUser(userId, "12345678998765")));
        when(customerRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(FiscalIdAlreadyRegisteredException.class,
                () -> customerService.processCustomer(customerBody("12345678998765")));
    }

    @Test
    void shouldRejectEmailAlreadyInUseByAnotherUser() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(userRepository.findByFiscalId("98765432100"))
                .thenReturn(Optional.of(existingCustomerUser(userId, "98765432100")));
        when(customerRepository.existsByUserId(userId)).thenReturn(false);
        when(userRepository.existsByEmailAndIdNot("cliente@email.com", userId)).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class,
                () -> customerService.processCustomer(customerBody("98765432100")));
    }

    private static User existingCustomerUser(UUID userId, String fiscalId) {
        User user = new User();
        user.setId(userId);
        user.setRole("CUSTOMER");
        user.setFiscalId(fiscalId);
        return user;
    }

    private static CustomerBodyDTO customerBody(String fiscalId) {
        return new CustomerBodyDTO(new UserBodyDTO(
                "Cliente de teste",
                "cliente@email.com",
                "Senha1234",
                fiscalId,
                new AddressBodyDTO(
                        "90010-000",
                        "RS",
                        "Porto Alegre",
                        "Centro Historico",
                        "Rua dos Andradas",
                        "100",
                        "Apto 9"
                )
        ));
    }
}
