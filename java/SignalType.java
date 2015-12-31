package jserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

public enum SignalType {
	CIRCLE, SQUARE, DIAMOND, STAR, PLUS, VLINE, HLINE, UP, DOWN, TRIANGLE_LD, TRIANGLE_RD, TRIANGLE_LU, TRIANGLE_RU, RANDOM, NONE;

	static Properties shortForm = new Properties();
	static Map<SignalType, String> texts = new HashMap<SignalType, String>();
	static Map<SignalType, String> tooltips = new HashMap<SignalType, String>();
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
		texts.put(RANDOM, "Gemischt");
		texts.put(NONE, "Nichts");

		for (SignalType key : texts.keySet()) {
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
		shortForm.setProperty("r", texts.get(RANDOM));
		shortForm.setProperty("none", texts.get(NONE));
	}

	public static SignalType getRandom() {
		// -2 to ignore RANDOM and NONE type
		return values()[random.nextInt( values().length -2 )];
	}

	public static SignalType getTypeFromText(String text) {
		for (Entry<SignalType, String> entry : texts.entrySet()) {
			if (entry.getValue().equals(text)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static boolean hasType(String cmd) {
		return texts.containsValue(cmd);
	}

}
