package jserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

/**
 * The enummeration of all signal types. Each type has a description, a tool tip text and a short form. 
 * The Board uses this information to fill its menu. 
 * Any new type is therefore included automatically in the menu. 
 * The short forms are used to specify the signal type in receiveMessage() and other methods. 
 * Therefore they have to be unique.  
 * 
 * 
 * @author Euler
 *
 */
public enum SymbolType {
	CIRCLE, SQUARE, DIAMOND, STAR, PLUS, VLINE, HLINE, UP, DOWN, 
	TRIANGLE_LD, TRIANGLE_RD, TRIANGLE_LU, TRIANGLE_RU, DICE_1,  DICE_2, DICE_3, DICE_4,  DICE_5, DICE_6, 
	RANDOM, NONE;

	static Properties shortForm = new Properties();
	static Map<SymbolType, String> texts = new HashMap<SymbolType, String>();
	static Map<SymbolType, String> tooltips = new HashMap<SymbolType, String>();
	static Random random = new Random();

	static {
		texts.put(CIRCLE, "Kreise");
		texts.put(SQUARE, "Quadrate");
		texts.put(DIAMOND, "Diamanten");
		texts.put(STAR, "Sterne");
		texts.put(PLUS, "Plus");
		texts.put(VLINE, "senkrechte Linien");
		texts.put(HLINE, "horizontale Linien");
		texts.put(UP, "schräge Linien /");
		texts.put(DOWN, "schräge Linien \\");
		texts.put(TRIANGLE_LD, "Dreiecke, links unten");
		texts.put(TRIANGLE_RD, "Dreiecke, rechts unten");
		texts.put(TRIANGLE_LU, "Dreiecke, links oben");
		texts.put(TRIANGLE_RU, "Dreiecke, rechts oben");
		texts.put(DICE_1, "Würfel 1");
		texts.put(DICE_2, "Würfel 2");
		texts.put(DICE_3, "Würfel 3");
		texts.put(DICE_4, "Würfel 4");
		texts.put(DICE_5, "Würfel 5");
		texts.put(DICE_6, "Würfel 6");
		texts.put(RANDOM, "Gemischt");
		texts.put(NONE, "Nichts");

		for (SymbolType key : texts.keySet()) {
			tooltips.put(key, "Umschalten auf " + texts.get(key));
		}

		tooltips.put(RANDOM, "Umschalten auf \"bunte Mischung\"");

		shortForm.setProperty("s", texts.get(SQUARE));
		shortForm.setProperty("c", texts.get(CIRCLE));
		shortForm.setProperty("d", texts.get(DIAMOND));
		shortForm.setProperty("*", texts.get(STAR));
		shortForm.setProperty("+", texts.get(PLUS));
		shortForm.setProperty("|", texts.get(VLINE));
		shortForm.setProperty("-", texts.get(HLINE));
		shortForm.setProperty("/", texts.get(UP));
		shortForm.setProperty("\\", texts.get(DOWN));
		shortForm.setProperty("tld", texts.get(TRIANGLE_LD));
		shortForm.setProperty("trd", texts.get(TRIANGLE_RD));
		shortForm.setProperty("tlu", texts.get(TRIANGLE_LU));
		shortForm.setProperty("tru", texts.get(TRIANGLE_RU));
		shortForm.setProperty("d1", texts.get(DICE_1));
		shortForm.setProperty("d2", texts.get(DICE_2));
		shortForm.setProperty("d3", texts.get(DICE_3));
		shortForm.setProperty("d4", texts.get(DICE_4));
		shortForm.setProperty("d5", texts.get(DICE_5));
		shortForm.setProperty("d6", texts.get(DICE_6));
		shortForm.setProperty("r", texts.get(RANDOM));
		shortForm.setProperty("none", texts.get(NONE));
	}

	/**
	 * This method returns a random type (excluding none). 
	 * 
	 * @return the choosen type
	 */
	public static SymbolType getRandom() {
		// -2 to ignore RANDOM and NONE type
		return values()[random.nextInt( values().length -2 )];
	}

	public static boolean hasType(String cmd) {
		return texts.containsValue(cmd);
	}

	public static SymbolType getTypeFromText(String text) {
		for (Entry<SymbolType, String> entry : texts.entrySet()) {
			if (entry.getValue().equals(text)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static SymbolType getTypeFromShortName(String shortName ) {
		String longName = shortForm.getProperty( shortName  );
		if( longName != null ) {
			return getTypeFromText( longName );
		}
		return null;
	}

}
