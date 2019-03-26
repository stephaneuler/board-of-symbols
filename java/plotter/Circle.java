package plotter;

import java.awt.Color;

public class Circle {
	double x;
	double y;
	double size;
	Color color;

	public Circle(double x, double y, double size, Color color) {
		super();
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "Circle [x=" + x + ", y=" + y + ", size=" + size + "]";
	}
}
