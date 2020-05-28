package com.vivaio_felice.vivaio_hibernate.customValidators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = DataValidaPatente.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPatente {

	String message() default "Inserisci una data valida.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
