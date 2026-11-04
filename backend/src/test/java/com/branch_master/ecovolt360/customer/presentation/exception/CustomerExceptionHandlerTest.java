package com.branch_master.ecovolt360.customer.presentation.exception;

import com.branch_master.ecovolt360.customer.application.dto.CustomerBodyDTO;
import com.branch_master.ecovolt360.customer.application.exception.CpfAlreadyRegisteredException;
import com.branch_master.ecovolt360.customer.application.exception.CpfNotFoundException;
import com.branch_master.ecovolt360.customer.application.exception.EmailAlreadyInUseException;
import com.branch_master.ecovolt360.customer.application.service.CustomerService;
import com.branch_master.ecovolt360.customer.controller.CustomerController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerExceptionHandlerTest {

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

    @Test
    void shouldReturnNotFoundWhenCpfIsUnknown() throws Exception {
        when(customerService.processCustomer(any(CustomerBodyDTO.class)))
                .thenThrow(new CpfNotFoundException());

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CPF_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("CPF não cadastrado."));
    }

    @Test
    void shouldReturnConflictWhenCpfIsAlreadyRegistered() throws Exception {
        when(customerService.processCustomer(any(CustomerBodyDTO.class)))
                .thenThrow(new CpfAlreadyRegisteredException());

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CPF_ALREADY_REGISTERED"))
                .andExpect(jsonPath("$.message").value("Este CPF já possui cadastro."));
    }

    @Test
    void shouldReturnConflictWhenEmailIsAlreadyInUse() throws Exception {
        when(customerService.processCustomer(any(CustomerBodyDTO.class)))
                .thenThrow(new EmailAlreadyInUseException());

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_IN_USE"))
                .andExpect(jsonPath("$.message").value("Este e-mail já está em uso."));
    }

    @Test
    void shouldReturnValidationErrorForBlankPassword() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody().replace("Senha1234", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Verifique os campos informados."))
                .andExpect(jsonPath("$['fieldErrors']['user.password']").exists());
    }

    private static String validBody() {
        return """
                {
                  "user": {
                    "name": "Cliente de teste",
                    "email": "cliente@email.com",
                    "password": "Senha1234",
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
                """;
    }
}
