package com.cidceradoy.task_management_system.validation;

import com.cidceradoy.task_management_system.dto.TaskUpdateForm;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TaskUpdateFormValidatorTest {

    @InjectMocks
    private TaskUpdateFormValidator taskUpdateFormValidator;

    @Test
    public void isValid_containsAtleastOneNonNullField_returnTrue() {
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);
        TaskUpdateForm form = new TaskUpdateForm("t-1", null, null, null);

        boolean result = taskUpdateFormValidator.isValid(form, ctx);

        assertThat(result).isTrue();
    }

    @Test
    public void isValid_allFieldsAreNull_returnFalse() {
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);
        TaskUpdateForm form = new TaskUpdateForm(null, null, null, null);

        boolean result = taskUpdateFormValidator.isValid(form, ctx);

        assertThat(result).isFalse();
    }
}
