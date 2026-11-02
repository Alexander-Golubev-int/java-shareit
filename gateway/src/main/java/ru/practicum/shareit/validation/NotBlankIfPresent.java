package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankIfPresentValidator.class)
@Target(ElementType.FIELD)
public @interface NotBlankIfPresent {
    String message() default "Поле не должно быть пустым";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
