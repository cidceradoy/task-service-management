package com.cidceradoy.task_management_system.validation;

import com.cidceradoy.task_management_system.dto.TaskUpdateForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class TaskUpdateFormValidator implements ConstraintValidator<ContainsAtLeastOneNonNullField, TaskUpdateForm> {

    @Override
    public boolean isValid(TaskUpdateForm form, ConstraintValidatorContext context) {
        return Objects.nonNull(form.getTitle()) || Objects.nonNull(form.getDescription())
                || Objects.nonNull(form.getStatus()) || Objects.nonNull(form.getDueDate());
    }
}
