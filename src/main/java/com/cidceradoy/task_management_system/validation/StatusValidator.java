package com.cidceradoy.task_management_system.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class StatusValidator implements ConstraintValidator<ValidStatus, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return List.of("DONE", "IN_PROGRESS", "PENDING").contains(value);
    }
}
