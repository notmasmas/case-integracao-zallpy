package com.branch_master.ecovolt360.customer.presentation.controller;

import com.branch_master.ecovolt360.customer.application.dto.CustomerBodyDTO;
import com.branch_master.ecovolt360.customer.application.dto.CustomerDetailsDTO;
import com.branch_master.ecovolt360.customer.application.service.CustomerService;
import com.branch_master.ecovolt360.customer.controller.CustomerController;
import com.branch_master.ecovolt360.customer.presentation.exception.CustomerExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CustomerController(customerService))
                .setControllerAdvice(new CustomerExceptionHandler())
                .build();
    }

    private static String bodyWithPassword(String password) {
        return """
                {
                  "user": {
                    "name": "Cliente de teste",
                    "email": "cliente@email.com",
                    "password": "%s",
                    "cpf": "98765432100",
                    "address": {
                      "cep": "90010-000",
                      "state": "RS",
                      "city": "Porto Alegre",
                      "neighborhood": "Centro Historico",
                      "street": "Rua dos Andradas",
                      "number": "100",
                      "complement": "Apto 9"
                    }
                  }
                }
                """.formatted(password);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1",
            "Abc1234",
            "12345678",
            "abcdefgh",
            "Abc12345Abc12345Abc1234567"
    })
    void shouldRejectPasswordOutsideBusinessRule(String password) throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyWithPassword(password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$['fieldErrors']['user.password']").exists());

        verifyNoInteractions(customerService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Senha1234",
            "Senha@123",
            "Ab1Ab1Ab1Ab1Ab1Ab1Ab1Ab1A"
    })
    void shouldAcceptPasswordWithinBusinessRule(String password) throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(customerService.processCustomer(any(CustomerBodyDTO.class)))
                .thenReturn(new CustomerDetailsDTO(id, userId));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyWithPassword(password)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        verify(customerService).processCustomer(any(CustomerBodyDTO.class));
    }

    @Test
    void shouldRejectBlankPassword() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyWithPassword("")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$['fieldErrors']['user.password']").exists());

        verifyNoInteractions(customerService);
    }
}
