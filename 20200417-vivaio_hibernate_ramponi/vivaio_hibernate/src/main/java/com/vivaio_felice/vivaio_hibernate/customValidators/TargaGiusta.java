package com.vivaio_felice.vivaio_hibernate.customValidators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TargaGiusta implements ConstraintValidator<Targa, String> {

	@Override
	public boolean isValid(String s, ConstraintValidatorContext cxt) {

		return s.matches("[a-zA-Z].2[0-9].3[a-zA-z].2");

	}

}
