package plotter;
import java.awt.Font;

public class TextObject extends PlotObject {
	public static final int CENTER = 0;
	public static final int RIGHT  = 1;
	public static final int LEFT   = 2;
	
	
	String text;
	Font font = null;
	int orientation = CENTER;
	double x;
	double y;
	
	public TextObject(String text, double x, double y) {
		super();
		this.text = text;
		this.x = x;
		this.y = y;
		//System.out.println( "T: <" + text + ">");
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	public void setXY( double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void moveXY( double dx, double dy) {
		this.x += dx;
		this.y += dy;
	}
	
	/**
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @param font the font to set
	 */
	public void setFont(Font font) {
		this.font = font;
	}

}
