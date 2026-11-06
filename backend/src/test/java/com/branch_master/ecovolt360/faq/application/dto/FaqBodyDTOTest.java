package com.branch_master.ecovolt360.faq.application.dto;

import com.branch_master.ecovolt360.faq.domain.entity.FaqCategory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FaqBodyDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void shouldHaveNoViolationsWhenFullyPopulated() {
        FaqBodyDTO body = new FaqBodyDTO(FaqCategory.SYSTEM_MONITORING, "Titulo", "Resposta");

        Set<ConstraintViolation<FaqBodyDTO>> violations = validator.validate(body);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldViolateNotNullWhenCategoryIsNull() {
        FaqBodyDTO body = new FaqBodyDTO(null, "Titulo", "Resposta");

        Set<ConstraintViolation<FaqBodyDTO>> violations = validator.validate(body);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("category")));
    }

    @Test
    void shouldViolateNotBlankWhenTitleAndAnswerAreBlank() {
        FaqBodyDTO body = new FaqBodyDTO(FaqCategory.MAINTENANCE, "   ", "   ");

        Set<ConstraintViolation<FaqBodyDTO>> violations = validator.validate(body);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("answer")));
    }
}
