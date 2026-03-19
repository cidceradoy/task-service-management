package com.cidceradoy.task_management_system.validation;

import com.cidceradoy.task_management_system.model.Task;
import com.cidceradoy.task_management_system.repository.TaskRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TitleValidatorTest {

    @InjectMocks
    private TitleValidator titleValidator;

    @Mock
    private TaskRepository taskRepository;

    @Test
    public void isValid_titleDoesNotExist_returnTrue() {
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);

        when(taskRepository.findByTitle("title")).thenReturn(Optional.empty());

        boolean result = titleValidator.isValid("title", ctx);

        assertThat(result).isTrue();
    }

    @Test
    public void isValid_titleExists_returnFalse() {
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);

        when(taskRepository.findByTitle("title")).thenReturn(Optional.of(mock(Task.class)));

        boolean result = titleValidator.isValid("title", ctx);

        assertThat(result).isFalse();
    }
}
