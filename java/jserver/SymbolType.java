package jserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

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
	CIRCLE, SQUARE, DIAMOND, STAR, PLUS, VLINE, HLINE, BAR, BLOCK, UP, DOWN, 
	TRIANGLE_LD, TRIANGLE_RD, TRIANGLE_LU, TRIANGLE_RU, DICE_1,  DICE_2, DICE_3, DICE_4,  DICE_5, DICE_6, 
	RANDOM, NONE;

	static Properties shortForm = new Properties();
	static Map<SymbolType, String> texts = new HashMap<SymbolType, String>();
	static Map<SymbolType, String> tooltips = new HashMap<SymbolType, String>();
	static SymbolType[] values = values();
	static Random random = new Random();

	static {
		texts.put(CIRCLE, "Kreise");
		texts.put(SQUARE, "Quadrate");
		texts.put(DIAMOND, "Diamanten");
		texts.put(STAR, "Sterne");
		texts.put(PLUS, "Plus");
		texts.put(VLINE, "senkrechte Linien");
		texts.put(HLINE, "horizontale Linien");
		texts.put(BAR, "Säulen");
		texts.put(BLOCK, "Block");
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

		updateShortForms();
	}

	/**
	 * This method returns a random type (excluding RANDOM and NONE). 
	 * 
	 * @return the choosen type
	 */
	public static SymbolType getRandom() {
		// -2 to ignore RANDOM and NONE type
		return values[random.nextInt( values.length -2 )];
	}

	private static void updateShortForms() {
		shortForm.setProperty("s", texts.get(SQUARE));
		shortForm.setProperty("c", texts.get(CIRCLE));
		shortForm.setProperty("d", texts.get(DIAMOND));
		shortForm.setProperty("*", texts.get(STAR));
		shortForm.setProperty("+", texts.get(PLUS));
		shortForm.setProperty("|", texts.get(VLINE));
		shortForm.setProperty("-", texts.get(HLINE));
		shortForm.setProperty("b", texts.get(BAR));
		shortForm.setProperty("B", texts.get(BLOCK));
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
		//System.out.println( shortName + " -> " + longName );
		if( longName != null ) {
			return getTypeFromText( longName );
		}
		return null;
	}

	/**
	 * Set the symbol texts using a resource bundle and overwriting the defaults in German. 
	 * 
	 * @param messages the resource bundle containing the texts
	 */
	public static void setSymbolTexts(ResourceBundle messages) {
		texts.put(CIRCLE, messages.getString("circle"));
		texts.put(SQUARE, messages.getString("square"));
		texts.put(DIAMOND, messages.getString("diamond"));
		texts.put(STAR, messages.getString("asterisk"));
		texts.put(PLUS,  messages.getString("plus"));
		texts.put(VLINE, messages.getString("verticalLine"));
		texts.put(HLINE, messages.getString("horizontalLine"));
		texts.put(BAR, messages.getString("bar"));
		texts.put(BLOCK, messages.getString("block"));
		texts.put(UP, messages.getString("slantedLine") + " /");
		texts.put(DOWN, messages.getString("slantedLine") + " \\");
		texts.put(TRIANGLE_LD, Utils.concat( messages, "triangle", ",", "left", "down") );
		texts.put(TRIANGLE_RD, Utils.concat( messages, "triangle", ",", "right", "down") );
		texts.put(TRIANGLE_LU, Utils.concat( messages, "triangle", ",", "left", "up") );
		texts.put(TRIANGLE_RU, Utils.concat( messages, "triangle", ",", "right", "up") );
		
		texts.put(DICE_1, messages.getString("dice") + " 1");
		texts.put(DICE_2, messages.getString("dice") + " 2");
		texts.put(DICE_3, messages.getString("dice") + " 3");
		texts.put(DICE_4, messages.getString("dice") + " 4");
		texts.put(DICE_5, messages.getString("dice") + " 5");
		texts.put(DICE_6, messages.getString("dice") + " 6");
		
		texts.put(RANDOM, messages.getString("mixture"));
		texts.put(NONE, messages.getString("none"));

		for (SymbolType key : texts.keySet()) {
			tooltips.put(key,  messages.getString("switchTo") + " " +texts.get(key));
		}
		tooltips.put(RANDOM, messages.getString("switchTo") + " " +  messages.getString("tooltip.mixture"));

		updateShortForms();
		// TODO Auto-generated method stub
		
	}

}
