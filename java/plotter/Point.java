package plotter;

public class Point implements Comparable<Object> {
	double x, y;

	public Point(double x, double y) {
		super();
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
	
	@Override
	public int compareTo(Object arg0) {
		Point p2 = (Point) arg0;
		return Double.compare(x, p2.x);
	}
	
	public double dist( double x1, double y1 ) {
		return Math.sqrt( (x-x1)*(x-x1) + (y-y1)*(y-y1));
	}
}
