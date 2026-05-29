package com.macrotrack.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidAgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    private final Clock clock;

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(ValidAge constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
        this.maxAge = constraintAnnotation.max();
    }


    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true;
        }
        int age = Period.between(birthDate, LocalDate.now(clock)).getYears();
        return age >= minAge && age <= maxAge;
    }
}
