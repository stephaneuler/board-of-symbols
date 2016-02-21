package plotter;

import java.awt.Color;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * A DataObject contains a set of points (x,y). A common offset (xo, yo) is
 * applied to all points.
 * 
 * @author Euler
 * 
 */
public class DataObject extends PlotObject {
	private double[] data = new double[0];
	private List<Point> points = Collections
			.synchronizedList(new ArrayList<Point>());
	private boolean changed = false;
	private double xOffset = 0;
	private double yOffset = 0;
	private double xmin = Double.MAX_VALUE;
	private double xmax = Double.MIN_VALUE;
	private double ymin = Double.MAX_VALUE;
	private double ymax = Double.MIN_VALUE;
	private Point[] corners = null;
	private Color backGroundColor = Color.RED;

	public DataObject(double x, double y) {
		this();
		points.add(new Point(x, y));
		changed = true;
	}

	public DataObject() {
	}

	public void setCorners( Point p1, Point p2 ) {
		corners = new Point[2];
		corners[0] = p1;
		corners[1] = p2;
	}
	
	public Point[] getCorners() {
		Point[] currCorners  = new Point[2];
		currCorners[0] = addOffset( corners[0]);
		currCorners[1] = addOffset( corners[1]);
		return currCorners;
	}

	public Color getBackGroundColor() {
		return backGroundColor;
	}

	public void setBackGroundColor(Color backGroundColor) {
		this.backGroundColor = backGroundColor;
	}

	private Point addOffset(Point point) {
		return new Point( point.x + xOffset, point.y + yOffset );
	}

	public void setCorners(Point[] corners) {
		this.corners = corners;
	}

	public boolean hasBackground() {
		return corners != null;
	}
	
	/**
	 * @return the data as array with x,y-pairs
	 */
	public synchronized double[] getData() {
		if (changed) {
			synchronized (points) {
				data = new double[points.size() * 2];
				int i = 0;
				for (ListIterator<Point> it = points.listIterator(); it
						.hasNext();) {
					Point p = it.next();
					data[i++] = p.getX() + xOffset;
					data[i++] = p.getY() + yOffset;
				}
				xmin();
				xmax();
				ymin();
				ymax();
				changed = false;
			}
		}
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(double[] data) {
		points.clear();
		for (int i = 0; i < data.length; i += 2) {
			points.add(new Point(data[i], data[i + 1]));
		}
		changed = true;
	}

	/**
	 * @return the xOffset
	 */
	public double getxOffset() {
		return xOffset;
	}

	/**
	 * @param xOffset
	 *            the xOffset to set
	 */
	public void setxOffset(double xOffset) {
		this.xOffset = xOffset;
		changed = true;
	}

	/**
	 * @return the yOffset
	 */
	public double getyOffset() {
		return yOffset;
	}

	/**
	 * @param yOffset
	 *            the yOffset to set
	 */
	public void setyOffset(double yOffset) {
		this.yOffset = yOffset;
		changed = true;
	}

	public void add(double x, double y) {
		points.add(new Point(x, y));
		changed = true;
	}

	public synchronized void addD(double x, double y) {
		Point last = points.get(points.size() - 1);
		add(last.getX() + x, last.getY() + y);
	}

	public int getCount() {
		return points.size();
	}

	public synchronized void removeOld(int n) {
		while (points.size() > 0 & n > 0) {
			points.remove(0);
			--n;
		}
		changed = true;
	}

	public synchronized void removeNew(int n) {
		while (points.size() > 0 & n > 0) {
			points.remove(points.size() - 1);
			--n;
		}
		changed = true;
	}

	public void remove(Point next) {
		remove(next.x, next.y);

	}

	public synchronized void remove(double x, double y) {
		for (Iterator<Point> pi = points.iterator(); pi.hasNext();) {
			Point p = pi.next();
			if (p.x == x && p.y == y) {
				pi.remove();
				break;
			}
		}
		changed = true;
	}

	public synchronized void removeAllData() {
		points.clear();
		changed = true;
	}

	public void print() {
		System.out.println("******** DUMP DataObject ***********");
		for (Point p : points) {
			System.out.println(p.x + " " + p.y);
		}
		System.out.println("******** ********* ***********");

	}

	public String dumpString() {
		String s = "";
		s += "# " + points.size() +"\n";
		for (Point p : points) {
			s += String.format("%7.4g %7.4g\n", p.x,   p.y);
		}
		return s;
	}

	public void print(String s, PrintWriter pw ) {
		pw.println(s+":");
		for (Point p : points) {
			pw.println(p.x + " " + p.y);
		}
		pw.println();

	}

	@SuppressWarnings("unchecked")
	public void sort() {
		Collections.sort(points);
		changed = true;

	}

	public Point findNext(double wx, double wy) {
		Point p = null;
		double best = Double.MAX_VALUE;
		for (Point t : points) {
			double d = t.dist(wx, wy);
			if (d < best) {
				best = d;
				p = t;
			}
		}
		return p;
	}

	public boolean contains(double wx, double wy) {
		for (Point t : points) {
			if (t.dist(wx, wy) == 0)
				return true;
		}
		return false;
	}

	public double ymax() {
		if (changed) {
			ymax = Double.MIN_VALUE;
			for (ListIterator<Point> it = points.listIterator(); it.hasNext();) {
				Point p = it.next();
				if (p.getY() > ymax)
					ymax = p.getY();
			}
		}
		return ymax;
	}

	public double ymin() {
		if (changed) {
			ymin = Double.MAX_VALUE;
			for (ListIterator<Point> it = points.listIterator(); it.hasNext();) {
				Point p = it.next();
				if (p.getY() < ymin)
					ymin = p.getY();
			}
		}
		return ymin;
	}

	public double xmax() {
		if (changed) {
			xmax = Double.MIN_VALUE;
			for (ListIterator<Point> it = points.listIterator(); it.hasNext();) {
				Point p = it.next();
				if (p.getX() > xmax)
					xmax = p.getX();
			}
		}
		return xmax;
	}

	public double xmin() {
		if (changed) {
			xmin = Double.MAX_VALUE;
			for (ListIterator<Point> it = points.listIterator(); it.hasNext();) {
				Point p = it.next();
				if (p.getX() < xmin)
					xmin = p.getX();
			}
		}
		return xmin;
	}

}
