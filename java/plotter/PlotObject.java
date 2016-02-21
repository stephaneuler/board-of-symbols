package plotter;

import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

public class PlotObject {
	Color color = Color.BLUE;
	//LineStyle lineStyle = LineStyle.UNDEFINED;
	List<LineStyle> lineStyles = new ArrayList<LineStyle>();
	Stroke stroke = null;

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the lineStyle
	 */
	public LineStyle getLineStyle() {
		if (lineStyles.isEmpty()) {
			return LineStyle.UNDEFINED;
		} else {
			return lineStyles.get(0);
		}
	}

	public boolean hasLineStyle(LineStyle style) {
		return lineStyles.contains(style);
	}

	/**
	 * @param lineStyle
	 *            the lineStyle to set
	 */
	public void setLineStyle(LineStyle lineStyle) {
		//this.lineStyle = lineStyle;
		lineStyles.clear();
		lineStyles.add(lineStyle);
	}

	/**
	 * @param lineStyle
	 *            the lineStyle to add
	 */
	public void addLineStyle(LineStyle lineStyle) {
		lineStyles.add(lineStyle);
	}

	/**
	 * @return the stroke
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * @param stroke
	 *            the stroke to set
	 */
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}
}
