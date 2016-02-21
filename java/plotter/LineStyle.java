package plotter;
/**
 * Plot styles
 * 
 * @author Euler
 * 
 */
public enum LineStyle {
	/**
	 * Solid line
	 */
	LINE,
	/**
	 * symbol (currently only circle)
	 */
	SYMBOL,
	/**
	 * small dot
	 */
	DOT,
	/**
	 * verticle line from x=0 or baseline to point
	 */
	IMPULS,
	/**
	 * verticle line from y=0 or baseline to point
	 */
	YIMPULS,
	/**
	 * verticle box from x=0 or baseline to point
	 */
	HISTOGRAM,
	/**
	 * verticle box from x=0 or baseline to point
	 */
	YHISTOGRAM,
	/**
	 * Combination of LINE and SYMBOL
	 */
	BOTH, 
	/**
	 * Draw a line and then fill the area
	 */
	FILL,
	/**
	 * symbol (currently only circle), filled in current color
	 */
	FILLED_SYMBOL, 
	/**
	 * write the y-value at the position of each data point
	 */
	VALUE, 
	/**
	 * write the x,y-values at the position of each data point
	 */
	COORD, 
	/**
	 * draw a star using the first point as center
	 */
	STAR, 
	/**
	 * do not show the points 
	 */
	HIDDEN,
	/**
	 * just undefined
	 */
	UNDEFINED;

	public static LineStyle getLineStyleByName(String string) {
		for (LineStyle ls : LineStyle.values()) {
			//System.out.println(ls.toString());
			if (string.equals(ls.toString().toLowerCase())) {
				return ls;
			}
		}

		return null;
	}
}
