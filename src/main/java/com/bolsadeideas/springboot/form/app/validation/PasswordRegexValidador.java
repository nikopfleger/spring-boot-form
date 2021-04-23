package com.bolsadeideas.springboot.form.app.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordRegexValidador implements ConstraintValidator<PasswordRegex, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
			return true;
		}
		return false;
	}
}
