package plotter;

import java.awt.Image;

public class ImageObject {
	Image image;
	double x;
	double y;
	double worldWidth  = -1;
	double worldHeight = -1;
	
	public double getWorldHeight() {
		return worldHeight;
	}

	public void setWorldHeight(double worldHeight) {
		this.worldHeight = worldHeight;
	}

	public double getWorldWidth() {
		return worldWidth;
	}

	public void setWorldWidth(double worldWidth) {
		this.worldWidth = worldWidth;
	}

	public ImageObject(Image image, double x, double y) {
		super();
		this.image = image;
		this.x = x;
		this.y = y;
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
	
	public int getWidth() {
		return image.getWidth(null);
	}

	public int getHeight() {
		return image.getHeight(null);
	}

	public void setSize(int width, int height) {
		image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		
	}
}
