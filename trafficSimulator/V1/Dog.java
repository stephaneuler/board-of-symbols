package traffic;

import jserver.XSendAdapter;

// Ein Hund, der ziemlich planlos zufaellig durch die Gegend laeuft

// Version 
// 1.0 Maerz 2022 SE: erste Version

public class Dog {
	private double y = 0;
	private double x = 0;
	private int color = XSendAdapter.BROWN;
	
	public Dog(double x, double y) {
		super();
		this.y = y;
		this.x = x;
	}
	
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getX() {
		return (int) Math.round( x ) ;
	}
	public void setX(double x) {
		this.x = x;
	}
	public int getY() {
		return (int) Math.round( y ) ;
	}
	public void setY(double y) {
		this.y = y;
	}
	
	public void move(Map map) {
		x  = map.checkBorderX(x + dogMove());
		y  = map.checkBorderY(y + dogMove());
	}
	
	private double dogMove() {
		return 0.2 * (Math.random() - 0.5);
	}
	
	public void setPos(double x, double y) {
		setX( x );
		setY( y );
	}
	
	public String getForm() {
		return "*";
	}

}
