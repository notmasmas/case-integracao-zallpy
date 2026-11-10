package com.branch_master.ecovolt360.faq.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FaqCategoryTest {

    @Test
    void shouldContainAllExpectedCategoriesInDeclaredOrder() {
        assertArrayEquals(
                new FaqCategory[]{
                        FaqCategory.SYSTEM_MONITORING,
                        FaqCategory.EQUIPMENT,
                        FaqCategory.INSTALLATION,
                        FaqCategory.MAINTENANCE,
                        FaqCategory.SCHEDULING,
                        FaqCategory.PAYMENT,
                        FaqCategory.PROJECT,
                        FaqCategory.ACCESS,
                        FaqCategory.OTHER
                },
                FaqCategory.values()
        );
    }

    @Test
    void shouldResolveEnumConstantByName() {
        assertEquals(FaqCategory.MAINTENANCE, FaqCategory.valueOf("MAINTENANCE"));
    }

    @Test
    void shouldThrowWhenResolvingUnknownConstant() {
        assertThrows(IllegalArgumentException.class, () -> FaqCategory.valueOf("NAO_EXISTE"));
    }
}
