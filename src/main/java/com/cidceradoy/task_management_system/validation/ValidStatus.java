package com.cidceradoy.task_management_system.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StatusValidator.class)
public @interface ValidStatus {
    String message() default "Status must be one of values: [DONE, IN_PROGRESS, PENDING]";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
