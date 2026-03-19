package com.cidceradoy.task_management_system.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaskUpdateFormValidator.class)
public @interface ContainsAtLeastOneNonNullField {
    String message() default "Request body should have at least one non-null field";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

