package com.cidceradoy.task_management_system.validation;

import com.cidceradoy.task_management_system.exception.TitleAlreadyExistsException;
import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class TitleValidator implements ConstraintValidator<UniqueTitle, String> {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public boolean isValid(String title, ConstraintValidatorContext context) {
        Optional<Task> task = taskRepository.findByTitle(title);
        if (task.isPresent()) {
            throw new TitleAlreadyExistsException("Title already exists");
        }
        return true;
    }
}
