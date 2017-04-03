package jserver;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleWrapper {
	ResourceBundle bundle;

	public ResourceBundleWrapper(ResourceBundle bundle) {
		super();
		this.bundle = bundle;
	}

	public String getString(String string) {
		try {
			return bundle.getString(string);
		} catch( MissingResourceException ex ) {
			return " * " + string + " * ";
		}
	}
	
	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

}
