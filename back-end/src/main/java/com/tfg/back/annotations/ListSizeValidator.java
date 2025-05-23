package com.tfg.back.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class ListSizeValidator implements ConstraintValidator<ListSize, Collection<?>> {
    private int size;

    @Override
    public void initialize(ListSize constraintAnnotation) {
        this.size = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Collection<?> list, ConstraintValidatorContext context) {
        return list != null && list.size() == size;
    }
}
