package com.vivaio_felice.vivaio_hibernate.customValidators;

import java.time.LocalDate;

import java.time.ZoneId;
import java.sql.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DataValidaPatente implements ConstraintValidator<DataPatente, Date> {

	@Override
	public boolean isValid(Date data, ConstraintValidatorContext context) {

		return data != null && data
				.compareTo(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())) <= 0;
	}

}
