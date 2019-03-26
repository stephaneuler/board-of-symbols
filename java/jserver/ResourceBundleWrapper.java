package jserver;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class wraps a resource bundle. Asking for a non-existing key does not throw an exception. 
 * Instead getString returns the key marked with two asterisks.  
 * 
 * @author Euler
 *
 */
public class ResourceBundleWrapper {
	ResourceBundle bundle;

	public ResourceBundleWrapper(ResourceBundle bundle) {
		super();
		this.bundle = bundle;
	}

	public String getString(String key) {
		try {
			return bundle.getString(key);
		} catch( MissingResourceException ex ) {
			return " * " + key + " * ";
		}
	}
	
	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public Locale getLocale() {
		return bundle.getLocale();
	}

}
