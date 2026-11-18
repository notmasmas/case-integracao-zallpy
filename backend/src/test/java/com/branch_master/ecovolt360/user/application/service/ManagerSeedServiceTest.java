package com.branch_master.ecovolt360.user.application.service;

import com.branch_master.ecovolt360.user.application.port.PasswordHasher;
import com.branch_master.ecovolt360.user.domain.entity.User;
import com.branch_master.ecovolt360.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagerSeedServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private ManagerSeedService managerSeedService;

    @BeforeEach
    void setUp() {
        managerSeedService = new ManagerSeedService(userRepository, passwordHasher);
    }

    @Test
    void createsManagerWithGerenteRoleAndHashedPassword() {
        when(userRepository.existsByEmail("gerente@ecovolt.com")).thenReturn(false);
        when(passwordHasher.hash("senha-teste")).thenReturn("hash-gerado");

        boolean created = managerSeedService.createIfMissing(
                "Gerente Teste", "gerente@ecovolt.com", "senha-teste", "33333333301");

        assertTrue(created);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("GERENTE", saved.getRole());
        assertEquals("gerente@ecovolt.com", saved.getEmail());
        assertEquals("hash-gerado", saved.getPassword());
    }

    @Test
    void doesNotCreateManagerAgainWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("gerente@ecovolt.com")).thenReturn(true);

        boolean created = managerSeedService.createIfMissing(
                "Gerente Teste", "gerente@ecovolt.com", "senha-teste", "33333333301");

        assertFalse(created);
        verify(userRepository, never()).save(any());
    }

    @Test
    void doesNothingWhenSomeDataIsMissing() {
        boolean created = managerSeedService.createIfMissing("Gerente Teste", "gerente@ecovolt.com", "", "33333333301");

        assertFalse(created);
        verify(userRepository, never()).save(any());
    }
}
