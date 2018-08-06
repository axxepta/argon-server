package de.axxepta.tools;

import org.apache.commons.validator.routines.UrlValidator;

import de.axxepta.tools.interfaces.IValidationUrl;

public class ValidateURL {

	public static IValidationUrl validateURL = (urlValue) -> {
		UrlValidator urlValidator = UrlValidator.getInstance();
		return urlValidator.isValid(urlValue);
	};
	
}
