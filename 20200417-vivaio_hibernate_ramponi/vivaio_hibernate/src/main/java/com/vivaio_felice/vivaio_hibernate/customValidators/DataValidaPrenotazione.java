package com.vivaio_felice.vivaio_hibernate.customValidators;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DataValidaPrenotazione implements ConstraintValidator<DataPrenotazione, Date> {

	@Override
	public boolean isValid(Date data, ConstraintValidatorContext context) {

		return data != null
				&& data.compareTo(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())) >= 0;

	}

}
