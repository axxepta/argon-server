package de.axxepta.tools;

import org.apache.log4j.Logger;

import de.axxepta.tools.interfaces.IValidationString;

public class ValidationString implements IValidationString{
	
	private static final Logger LOG = Logger.getLogger(ValidationString.class);
	
	private static final int LONG_LENGTH = (int) Math.pow(2, 31) - 1000;
	
	public static boolean validationString(String string, String name) {
		return new ValidationString().validate(string, name);
	}
	
	public boolean validate(String string, String name) {
		if (string == null) {
			LOG.error("String null value for " + name);
			return false;
		}
		else if(string.length() == 0) {
			LOG.error("String empty for " + name);
			return false;
		}
		else if(string.length() > LONG_LENGTH) {
			LOG.error("Very long lenght for " + name);
			return false;
		}
		return true;
	}
}
