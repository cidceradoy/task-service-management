package com.cidceradoy.task_management_system.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class StatusValidatorTest {

    @InjectMocks
    private StatusValidator statusValidator;

    @Test
    public void isValid_validStatus_returnTrue() {
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);

        boolean result = statusValidator.isValid("PENDING", ctx);

        assertThat(result).isTrue();
    }

    @Test
    public void isValid_invalidStatus_returnFalse() {
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);

        boolean result = statusValidator.isValid("notvalidstatus", ctx);

        assertThat(result).isFalse();
    }
}
