package jserver;

import java.util.List;
import java.util.Random;

import plotter.LineStyle;
import plotter.Plotter;

/**
 * This class contains some helper methods for working with dice 
 * 
 * @author Euler
 *
 */
public class Dice {
	private static final double d = 0.5 ;
	private static double pipFactor = 0.2;
	private static double[][] pos = { { 0., 0. }, { -d , d, d , -d  },
			{ -d , d , 0., 0., d , -d  },
			{ -d , -d , d , -d , d , d , -d , d  },
			{ -d , -d , d , -d , d , d , -d , d , 0., 0. },
			{ -d , -d , d , -d , d , d , -d , d , d , 0., -d , 0. }

	};
	private static  Random random = new Random() ;
	private static SymbolType[] types = {SymbolType.DICE_1, SymbolType.DICE_2, SymbolType.DICE_3, 
		SymbolType.DICE_4, SymbolType.DICE_5, SymbolType.DICE_6};

	public static void draw(String key, List<String> secondaryKeys, int value,
			Plotter plotter, Position center, double size) {
		
		double pipSize = pipFactor * size;


		String key2 = key;
		for (int n = 0; n < value; n++) {
			if (n > 0) {
				key2 = plotter.nextVector();
				secondaryKeys.add(key2);
				plotter.setDataLineStyle(key2, LineStyle.FILL);
				plotter.setDataColor(key2, plotter.getDataObject(key)
						.getColor());
			}
			double px = center.x + pos[value - 1][2 * n] * size;
			double py = center.y + pos[value - 1][2 * n + 1] * size;
			for (double t = 0; t < 2 * Math.PI; t += 0.2) {
				double x = px + pipSize * Math.cos(t);
				double y = py + pipSize * Math.sin(t);
				plotter.add(key2, x, y);
			}

		}
	}

	public static boolean isDiceType(SymbolType type) {
		return getValue(type) > 0;
	}

	public static int getValue(SymbolType type) {
		switch (type) {
		case DICE_1:
			return 1;
		case DICE_2:
			return 2;
		case DICE_3:
			return 3;
		case DICE_4:
			return 4;
		case DICE_5:
			return 5;
		case DICE_6:
			return 6;
		default:
			return 0;
		}
	}

	public static SymbolType getRandom() {
		return types [random.nextInt(types.length)];
	}


}
