package com.branch_master.ecovolt360.faq.presentation.controller;

import com.branch_master.ecovolt360.faq.application.dto.FaqDTO;
import com.branch_master.ecovolt360.faq.application.service.FaqService;
import com.branch_master.ecovolt360.faq.domain.entity.FaqCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FaqControllerTest {

    @Mock
    private FaqService faqService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FaqController(faqService))
                .build();
    }

    @Test
    void shouldReturnOkWithListOfFaqs() throws Exception {
        FaqDTO faq = new FaqDTO(UUID.randomUUID(), "Como funciona o monitoramento?", "Resposta", FaqCategory.SYSTEM_MONITORING);
        when(faqService.listFaqs()).thenReturn(List.of(faq));

        mockMvc.perform(get("/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(faq.id().toString()))
                .andExpect(jsonPath("$[0].title").value("Como funciona o monitoramento?"))
                .andExpect(jsonPath("$[0].answer").value("Resposta"))
                .andExpect(jsonPath("$[0].category").value("SYSTEM_MONITORING"));
    }

    @Test
    void shouldReturnOkWithEmptyListWhenNoFaqsExist() throws Exception {
        when(faqService.listFaqs()).thenReturn(List.of());

        mockMvc.perform(get("/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldDelegateToFaqService() throws Exception {
        when(faqService.listFaqs()).thenReturn(List.of());

        mockMvc.perform(get("/faqs"));

        verify(faqService).listFaqs();
    }
}
