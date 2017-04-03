package jserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ColorNames {
	private static Map<String, Integer> colors = new HashMap<String, Integer>();

	static {
		colors.put("BLACK", 0x000000);
		colors.put("NAVY", 0x000080);
		colors.put("DARKBLUE", 0x00008B);
		colors.put("MEDIUMBLUE", 0x0000CD);
		colors.put("BLUE", 0x0000FF);
		colors.put("DARKGREEN", 0x006400);
		colors.put("GREEN", 0x008000);
		colors.put("TEAL", 0x008080);
		colors.put("DARKCYAN", 0x008B8B);
		colors.put("DEEPSKYBLUE", 0x00BFFF);
		colors.put("DARKTURQUOISE", 0x00CED1);
		colors.put("MEDIUMSPRINGGREEN", 0x00FA9A);
		colors.put("LIME", 0x00FF00);
		colors.put("SPRINGGREEN", 0x00FF7F);
		colors.put("AQUA", 0x00FFFF);
		colors.put("CYAN", 0x00FFFF);
		colors.put("MIDNIGHTBLUE", 0x191970);
		colors.put("DODGERBLUE", 0x1E90FF);
		colors.put("LIGHTSEAGREEN", 0x20B2AA);
		colors.put("FORESTGREEN", 0x228B22);
		colors.put("SEAGREEN", 0x2E8B57);
		colors.put("DARKSLATEGRAY", 0x2F4F4F);
		colors.put("LIMEGREEN", 0x32CD32);
		colors.put("MEDIUMSEAGREEN", 0x3CB371);
		colors.put("TURQUOISE", 0x40E0D0);
		colors.put("ROYALBLUE", 0x4169E1);
		colors.put("STEELBLUE", 0x4682B4);
		colors.put("DARKSLATEBLUE", 0x483D8B);
		colors.put("MEDIUMTURQUOISE", 0x48D1CC);
		colors.put("INDIGO", 0x4B0082);
		colors.put("DARKOLIVEGREEN", 0x556B2F);
		colors.put("CADETBLUE", 0x5F9EA0);
		colors.put("CORNFLOWERBLUE", 0x6495ED);
		colors.put("REBECCAPURPLE", 0x663399);
		colors.put("MEDIUMAQUAMARINE", 0x66CDAA);
		colors.put("DIMGRAY", 0x696969);
		colors.put("SLATEBLUE", 0x6A5ACD);
		colors.put("OLIVEDRAB", 0x6B8E23);
		colors.put("SLATEGRAY", 0x708090);
		colors.put("LIGHTSLATEGRAY", 0x778899);
		colors.put("MEDIUMSLATEBLUE", 0x7B68EE);
		colors.put("LAWNGREEN", 0x7CFC00);
		colors.put("CHARTREUSE", 0x7FFF00);
		colors.put("AQUAMARINE", 0x7FFFD4);
		colors.put("MAROON", 0x800000);
		colors.put("PURPLE", 0x800080);
		colors.put("OLIVE", 0x808000);
		colors.put("GRAY", 0x808080);
		colors.put("SKYBLUE", 0x87CEEB);
		colors.put("LIGHTSKYBLUE", 0x87CEFA);
		colors.put("BLUEVIOLET", 0x8A2BE2);
		colors.put("DARKRED", 0x8B0000);
		colors.put("DARKMAGENTA", 0x8B008B);
		colors.put("SADDLEBROWN", 0x8B4513);
		colors.put("DARKSEAGREEN", 0x8FBC8F);
		colors.put("LIGHTGREEN", 0x90EE90);
		colors.put("MEDIUMPURPLE", 0x9370DB);
		colors.put("DARKVIOLET", 0x9400D3);
		colors.put("PALEGREEN", 0x98FB98);
		colors.put("DARKORCHID", 0x9932CC);
		colors.put("YELLOWGREEN", 0x9ACD32);
		colors.put("SIENNA", 0xA0522D);
		colors.put("BROWN", 0xA52A2A);
		colors.put("DARKGRAY", 0xA9A9A9);
		colors.put("LIGHTBLUE", 0xADD8E6);
		colors.put("GREENYELLOW", 0xADFF2F);
		colors.put("PALETURQUOISE", 0xAFEEEE);
		colors.put("LIGHTSTEELBLUE", 0xB0C4DE);
		colors.put("POWDERBLUE", 0xB0E0E6);
		colors.put("FIREBRICK", 0xB22222);
		colors.put("DARKGOLDENROD", 0xB8860B);
		colors.put("MEDIUMORCHID", 0xBA55D3);
		colors.put("ROSYBROWN", 0xBC8F8F);
		colors.put("DARKKHAKI", 0xBDB76B);
		colors.put("SILVER", 0xC0C0C0);
		colors.put("MEDIUMVIOLETRED", 0xC71585);
		colors.put("INDIANRED", 0xCD5C5C);
		colors.put("PERU", 0xCD853F);
		colors.put("CHOCOLATE", 0xD2691E);
		colors.put("TAN", 0xD2B48C);
		colors.put("LIGHTGRAY", 0xD3D3D3);
		colors.put("THISTLE", 0xD8BFD8);
		colors.put("ORCHID", 0xDA70D6);
		colors.put("GOLDENROD", 0xDAA520);
		colors.put("PALEVIOLETRED", 0xDB7093);
		colors.put("CRIMSON", 0xDC143C);
		colors.put("GAINSBORO", 0xDCDCDC);
		colors.put("PLUM", 0xDDA0DD);
		colors.put("BURLYWOOD", 0xDEB887);
		colors.put("LIGHTCYAN", 0xE0FFFF);
		colors.put("LAVENDER", 0xE6E6FA);
		colors.put("DARKSALMON", 0xE9967A);
		colors.put("VIOLET", 0xEE82EE);
		colors.put("PALEGOLDENROD", 0xEEE8AA);
		colors.put("LIGHTCORAL", 0xF08080);
		colors.put("KHAKI", 0xF0E68C);
		colors.put("ALICEBLUE", 0xF0F8FF);
		colors.put("HONEYDEW", 0xF0FFF0);
		colors.put("AZURE", 0xF0FFFF);
		colors.put("SANDYBROWN", 0xF4A460);
		colors.put("WHEAT", 0xF5DEB3);
		colors.put("BEIGE", 0xF5F5DC);
		colors.put("WHITESMOKE", 0xF5F5F5);
		colors.put("MINTCREAM", 0xF5FFFA);
		colors.put("GHOSTWHITE", 0xF8F8FF);
		colors.put("SALMON", 0xFA8072);
		colors.put("ANTIQUEWHITE", 0xFAEBD7);
		colors.put("LINEN", 0xFAF0E6);
		colors.put("LIGHTGOLDENRODYELLOW", 0xFAFAD2);
		colors.put("OLDLACE", 0xFDF5E6);
		colors.put("RED", 0xFF0000);
		colors.put("FUCHSIA", 0xFF00FF);
		colors.put("MAGENTA", 0xFF00FF);
		colors.put("DEEPPINK", 0xFF1493);
		colors.put("ORANGERED", 0xFF4500);
		colors.put("TOMATO", 0xFF6347);
		colors.put("HOTPINK", 0xFF69B4);
		colors.put("CORAL", 0xFF7F50);
		colors.put("DARKORANGE", 0xFF8C00);
		colors.put("LIGHTSALMON", 0xFFA07A);
		colors.put("ORANGE", 0xFFA500);
		colors.put("LIGHTPINK", 0xFFB6C1);
		colors.put("PINK", 0xFFC0CB);
		colors.put("GOLD", 0xFFD700);
		colors.put("PEACHPUFF", 0xFFDAB9);
		colors.put("NAVAJOWHITE", 0xFFDEAD);
		colors.put("MOCCASIN", 0xFFE4B5);
		colors.put("BISQUE", 0xFFE4C4);
		colors.put("MISTYROSE", 0xFFE4E1);
		colors.put("BLANCHEDALMOND", 0xFFEBCD);
		colors.put("PAPAYAWHIP", 0xFFEFD5);
		colors.put("LAVENDERBLUSH", 0xFFF0F5);
		colors.put("SEASHELL", 0xFFF5EE);
		colors.put("CORNSILK", 0xFFF8DC);
		colors.put("LEMONCHIFFON", 0xFFFACD);
		colors.put("FLORALWHITE", 0xFFFAF0);
		colors.put("SNOW", 0xFFFAFA);
		colors.put("YELLOW", 0xFFFF00);
		colors.put("LIGHTYELLOW", 0xFFFFE0);
		colors.put("IVORY", 0xFFFFF0);
		colors.put("WHITE", 0xFFFFFF);
	}

	public static Map<String, Integer> getColors() {
		return colors;
	}

	public static void setColors(Map<String, Integer> colors) {
		ColorNames.colors = colors;
	}

	public static String getName(int color) {
		for (Entry<String, Integer> entry : colors.entrySet()) {
			if (entry.getValue().equals(color)) {
				return entry.getKey();
			}
		}
		return null;
	}

}
