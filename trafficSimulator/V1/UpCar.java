package traffic;

import jserver.XSendAdapter;

// Ein Auto, das von Unten nach Oben faehrt

// Version 
// 1.0 Maerz 2022 SE: erste Version

public class UpCar {
	private double x;
	private double y = 0;
	private double speed = 1;
	private int color = XSendAdapter.CADETBLUE;

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getX() {
		return (int) Math.round(x);
	}

	public void setX(double x) {
		this.x = x;
	}

	public int getY() {
		return (int) Math.round(y);
	}

	public void setY(double y) {
		this.y = y;
	}

	public void move(Map map) {
		y = (y + speed) % map.getHeight();
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setPos(double x, double y) {
		setX(x);
		setY(y);
	}

	public void setLane(int n, int middle) {
		x = middle + n;
	}

	public String getForm() {
		return "b";
	}
}
