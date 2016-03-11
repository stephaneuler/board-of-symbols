package jserver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodExtractor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		getMethods("jserver.XSend");

	}

	/**
	 * This method looks up all methods of the class with the given name. 
	 * Setters and getters and the special method send are ignored. 
	 * For all other methods a simplified text description is stored in a list. 
	 * Finally the list is converted into an array of strings. 
	 * This array is sorted and returned. 
	 * 
	 * @param className
	 * @return an array with methods
	 */
	static String[] getMethods(String className ) {
		List<String> methods = new ArrayList<String>();
		Class<?> c;
		try {
			c = Class.forName( className );
			Method[] allMethods = c.getDeclaredMethods();
			for (Method m : allMethods) {
				String info = "";
				String name = m.getName();
				// ignore setter, getter and send()
				if( name.startsWith("get") || name.startsWith("set" )  || name.equals("send")) {
					continue;
				}
				info +=  name +"( ";

				Class<?>[] pType = m.getParameterTypes();
				for (int i = 0; i < pType.length; i++) {
					if( pType[i].toString().equals("class java.lang.String")) {
						info += "String";
					} else {
						info +=  pType[i] ;
					}
					if( i< pType.length -1 ) {
						info += ", ";
					}
				}
				info += " )";
				methods.add( info );

			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] methodsArray = methods.toArray( new String[1] );
		Arrays.sort(methodsArray );
		return methodsArray;
	}
}
