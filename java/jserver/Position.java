package jserver;

import plotter.LineStyle;
import plotter.Plotter;

public class Position {
	public double x;
	public double y;

	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}

	void zeichnen(Plotter plotter) {
		plotter.nextVector();
		plotter.add(x, y);
		plotter.setDataLineStyle(LineStyle.SYMBOL);
		plotter.setSymbolSize(5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public void bewegeUm(double dx, double dy) {
		x += dx;
		y += dy;

	}

}
