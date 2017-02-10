package plotter;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Plot datasets
 * 
 * 
 * @version 1.28 April 2015
 * @author Stephan Euler
 * 
 */

// Version wer wann was
// 1.21 se 1211   Linestyle Star, autoIncrementColor
// 1.22 se 121130 status line
// 1.23 se 121214 remove - methoden
// 1.24 se 130108 bessere synchronisation bei textobjekten
// 1.25 se 130226 ImageObjects, Stroke 
// 1.26 se 130308 removeText(x,y)
//                dataObjects as ConcurrentHashMap
//                verbose setter / getter
// 1.27 se 140307 clearRange
// 1.28 se 150422 lineStyle COORD

/**
 * @author Euler
 * 
 */
public class Plotter extends JPanel {

	private static final long serialVersionUID = 8220278066243017406L;

	private static final String version = "1.28 April 2015";

	static int verbose = 0;
	static Color[] plotColor = { Color.red, Color.green, Color.blue, Color.orange, Color.yellow, Color.magenta };

	Map<String, DataObject> dataObjects = new ConcurrentHashMap<String, DataObject>();
	String currV = "0";
	private Collection<TextObject> textObjects = Collections.synchronizedCollection(new ArrayList<TextObject>());
	private Collection<ImageObject> imageObjects = Collections.synchronizedCollection(new ArrayList<ImageObject>());

	private double xmin = -1;
	private double xmax = 1;
	private double ymin = -1;
	private double ymax = 1;
	private double[] xgrid = null;
	private double[] ygrid = null;
	private String[] xlabel = null;
	private boolean dynYscale = true;
	private boolean dynXscale = true;
	private double xl = 0.05;
	private double xr = 0.95;
	private double yu = 0.05;
	private double yl = 0.95;
	private List<Double> yLine = new ArrayList<Double>();
	private List<Double> xLine = new ArrayList<Double>();

	private double autoXgrid = 0; // distance between xtics (sec)
	private double ticRelHeight = 0.05;
	private int symbolSize = 2;
	private double autoYgrid = 0;
	private LineStyle plotMode = LineStyle.LINE;
	private int plotLeft;
	private double xfactor;
	private double yfactor;
	private int plotLow;
	private double yScaleKonst;
	private double xScaleKonst;

	private double halfBarWidth = 0.4;
	private String yLabelFormat = null;
	private String xLabelFormat = null;

	private boolean autoIncrementColor = true;
	private Color defaultColor = Color.BLUE;
	// private Color backGroundColor;
	private Color borderColor = Color.BLUE;

	private String statusLine;
	private String valueFormatString = "%f";
	private int paintCalls = 0;

	/**
	 * @param statusLine
	 *            the statusLine to set
	 */
	public void setStatusLine(String statusLine) {
		this.statusLine = statusLine;
	}

	/**
	 * Constructs a new <code>Plotter</code>
	 * 
	 */
	public Plotter() {
		this("Plotter");
	}

	/**
	 * Constructs a new <code>Plotter</code> with the given name
	 * 
	 * @param name
	 *            the name of the new <code>Plotter</code> object
	 */
	public Plotter(String name) {
		setName(name);

	}

	public int getPaintCalls() {
		return paintCalls;
	}

	/**
	 * same as <code>setPreferredSize(new Dimension(width, heigth))</code>
	 * 
	 * @param width
	 *            the specified width
	 * @param heigth
	 *            the specified heigth
	 */
	public void setPreferredSize(int width, int heigth) {
		setPreferredSize(new Dimension(width, heigth));
	}

	/**
	 * remove all data sets
	 */
	public void clearPlotVector() {
		dataObjects.clear();
	}

	/**
	 * Add a point to the current data set, use index as x coordinate
	 * 
	 * @param y
	 *            y coordinate
	 */
	public void add(double y) {
		add(currV, y);
	}

	/**
	 * Add a point to the given data set, use index as x coordinate
	 * 
	 * @param key
	 *            key for the data set
	 * @param y
	 *            y coordinate
	 */
	public void add(String key, double y) {
		checkKey(key);
		add(key, dataObjects.get(key).getCount(), y);
	}

	/**
	 * Add a point to the current data set
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public void add(double x, double y) {
		add(currV, x, y);
	}

	/**
	 * Add a point to the specified data set
	 * 
	 * @param key
	 *            key for the data set
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public synchronized void add(String key, double x, double y) {
		checkKey(key);
		DataObject d = dataObjects.get(key);
		d.add(x, y);
	}

	/**
	 * Set the offset for the current data set
	 * 
	 * @param xOffset
	 * @param yOffset
	 */
	public synchronized void setOffset(double xOffset, double yOffset) {
		setOffset(currV, xOffset, yOffset);
	}

	/**
	 * Set the offset for the specifed data set
	 * 
	 * @param key
	 * @param xOffset
	 * @param yOffset
	 */
	public synchronized void setOffset(String key, double xOffset, double yOffset) {
		checkKey(key);
		DataObject d = dataObjects.get(key);
		d.setxOffset(xOffset);
		d.setyOffset(yOffset);
	}

	public void addD(double y) {
		addD(1, y);

	}

	/**
	 * Add a point to the current data set using coordinates relative to the
	 * last point
	 * 
	 * @param x
	 *            dx coordinate
	 * @param y
	 *            dy coordinate
	 */
	public synchronized void addD(double x, double y) {
		addD(currV, x, y);
	}

	/**
	 * Add a point to the specified data set using coordinates relative to the
	 * last point
	 * 
	 * @param key
	 *            key for the data set
	 * @param x
	 *            dx coordinate
	 * @param y
	 *            dy coordinate
	 */
	public synchronized void addD(String key, double x, double y) {
		checkKey(key);
		DataObject d = dataObjects.get(key);
		d.addD(x, y);
	}

	/**
	 * check for given key, create a new DataObject if necessary
	 * 
	 * @param key
	 */
	private void checkKey(String key) {
		if (!dataObjects.containsKey(key)) {
			DataObject d = new DataObject();
			if (autoIncrementColor) {
				d.setColor(plotColor[dataObjects.size() % plotColor.length]);
			} else {
				d.setColor(defaultColor);
			}
			dataObjects.put(key, d);
		}
	}

	/**
	 * Add an array of integers to the current data set
	 * 
	 * @param feld
	 */
	public void add(int[] feld) {
		for (int w : feld)
			add(w);

	}

	/**
	 * Add an array of double values to the current data set
	 * 
	 * @param feld
	 */
	public void add(double[] feld) {
		for (double w : feld)
			add(w);

	}

	/**
	 * Add a list of double values to the current data set
	 * 
	 * @param list
	 */
	public void add(List<Double> list) {
		for (double w : list)
			add(w);
	}

	/**
	 * @return the autoIncrementColor
	 */
	public boolean isAutoIncrementColor() {
		return autoIncrementColor;
	}

	/**
	 * set the autoIncrementColor mode. If true (default) the color changes with
	 * each data set.
	 * 
	 * @param autoIncrementColor
	 *            the autoIncrementColor to set
	 */
	public void setAutoIncrementColor(boolean autoIncrementColor) {
		this.autoIncrementColor = autoIncrementColor;
	}

	public static int getVerbose() {
		return verbose;
	}

	public static void setVerbose(int verbose) {
		Plotter.verbose = verbose;
	}

	/**
	 * Set the color for the current data set.
	 * 
	 * @param c
	 *            color
	 */
	public void setDataColor(Color color) {
		setDataColor(currV, color);
	}

	/**
	 * Set the color for the specified data set.
	 * 
	 * @param key
	 *            key for the data set
	 * @param c
	 *            color
	 */
	public void setDataColor(String key, Color c) {
		checkKey(key);
		dataObjects.get(key).setColor(c);
	}

	// public Color getBackGroundColor() {
	// return backGroundColor;
	// }

	// public void setBackGroundColor(Color backGroundColor) {
	// this.backGroundColor = backGroundColor;
	// if( backGroundColor == null ) {
	// backGroundColor = defaultBackGroundColor;
	// }
	// System.out.println( "Background: " + backGroundColor);
	// System.out.println( "Background: " + getBackground() );
	// }

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public void setDataStroke(Stroke stroke) {
		setDataStroke(currV, stroke);
	}

	public void setDataStroke(String key, Stroke stroke) {
		checkKey(key);
		dataObjects.get(key).setStroke(stroke);
	}

	/**
	 * Draw a horizontal line
	 * 
	 * @param y
	 *            where to draw the line
	 */
	public void setYLine(double y) {
		yLine.add(y);
	}

	/**
	 * Draw a vertical line
	 * 
	 * @param x
	 *            where to draw the line
	 */
	public void setXLine(double x) {
		xLine.add(x);
	}

	/**
	 * Draw a horizontal and a vertical line
	 * 
	 * @param x
	 *            where to draw the line
	 */
	public void setLine(double x) {
		setXLine(x);
		setYLine(x);
	}

	/**
	 * Set values for drawing x and ticks with default labels.
	 * 
	 * @param x
	 */
	public void setGrid(double[] x) {
		setXGrid(x);
		setYGrid(x);
	}

	/**
	 * Set values for drawing x ticks with default labels.
	 * 
	 * @param x
	 */
	public void setXGrid(double[] x) {
		xgrid = new double[x.length];
		xlabel = new String[x.length];
		for (int i = 0; i < x.length; i++) {
			xgrid[i] = x[i];
			xlabel[i] = getXLabel(x[i]);
		}
		// yl = 0.9; // make more space for labels
	}

	private String getXLabel(double d) {
		if (xLabelFormat == null)
			return "" + d;
		else
			return String.format(xLabelFormat, d);
	}

	/**
	 * Define labels for the x ticks.
	 * 
	 * @param label
	 */
	public void setXLabel(String[] label) {
		xlabel = new String[label.length];
		for (int i = 0; i < label.length; i++) {
			xlabel[i] = label[i];
		}
	}

	/**
	 * Set values for drawing y ticks with default labels.
	 * 
	 */
	public void setYGrid(double[] y) {
		ygrid = new double[y.length];
		for (int i = 0; i < y.length; i++) {
			ygrid[i] = y[i];
		}
		xl = 0.1; // make more space for labels
		yu = 0.1;
	}

	/**
	 * Set the xy-range for plotting
	 * 
	 * @param min
	 *            Minimum value to plot
	 * @param max
	 *            Maximuma value to plot
	 */
	public void setRange(double min, double max) {
		setXrange(min, max);
		setYrange(min, max);
	}

	/**
	 * Set the x range for plotting
	 * 
	 * @param min
	 *            Minimum value to plot
	 * @param max
	 *            Maximuma value to plot
	 */
	public void setXrange(double min, double max) {
		xmin = min;
		xmax = max;
		dynXscale = false;
	}

	/**
	 * Set the y range for plotting
	 * 
	 * @param min
	 *            Minimum value to plot
	 * @param max
	 *            Maximuma value to plot
	 */
	public void setYrange(double min, double max) {
		ymin = min;
		ymax = max;
		dynYscale = false;
	}

	/**
	 * set dynamic scaling for x and y
	 */
	public void clearRange() {
		clearXrange();
		clearYrange();
	}

	/**
	 * set dynamic scaling for x
	 */
	public void clearXrange() {
		dynXscale = true;
	}

	/**
	 * set dynamic scaling for y
	 */
	public void clearYrange() {
		dynYscale = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		++paintCalls;

		Graphics2D g2 = (Graphics2D) g;

		// if (backGroundColor != null) {
		// setBackground(backGroundColor);
		// }

		if (verbose > 1) {
			System.out.println("PAINT");
			System.out.println(getBackground());
		}

		int rectWidth = getSize().width - getInsets().left - getInsets().right;
		int rectHeight = getSize().height - getInsets().top - getInsets().bottom;
		// System.out.println("x: "
		// + getSize().width+" "+getInsets().left+" "+getInsets().right);
		// System.out.println("y: "
		// + getSize().height+" "+getInsets().top+" "+getInsets().bottom);

		plotLeft = (int) (rectWidth * xl) + getInsets().left;
		int plotRight = (int) (rectWidth * xr) + getInsets().left;
		int plotWidth = plotRight - plotLeft;
		int plotUp = (int) (rectHeight * yu) + getInsets().top;
		plotLow = (int) (rectHeight * yl) + getInsets().top;
		int plotHeight = plotLow - plotUp;

		double yw;
		if (dynYscale && dataObjects.size() > 0) {
			ymax = Double.MIN_VALUE;
			ymin = Double.MAX_VALUE;
			for (DataObject d : dataObjects.values()) {
				ymax = Math.max(ymax, d.ymax());
				ymin = Math.min(ymin, d.ymin());
			}
			yw = ymax - ymin;
			ymin -= 0.125 * yw;
			yw *= 1.25;
			ymax = ymin + yw;

		} else {
			yw = (ymax - ymin);
		}
		yfactor = plotHeight / yw;
		yScaleKonst = plotLow + ymin * yfactor;
		// return (int) (plotLow - (d - ymin) * yfactor);

		double xw = (xmax - xmin);
		if (dynXscale && dataObjects.size() > 0) {
			xmax = Double.MIN_VALUE;
			xmin = Double.MAX_VALUE;
			for (DataObject d : dataObjects.values()) {
				// double[] vek = d.getData();
				// xmax = Vis.vmax(xmax, vek, 0, 2);
				// xmin = Vis.vmin(xmin, vek, 0, 2);
				xmax = Math.max(xmax, d.xmax());
				xmin = Math.min(xmin, d.xmin());
			}
			xw = xmax - xmin;
		}
		xfactor = plotWidth / xw;
		// (int) (plotLeft + (d - xmin) * xfactor);
		xScaleKonst = plotLeft - xmin * xfactor;

		g2.setColor(Color.BLUE);
		for (double yL : yLine) {
			int yg = plotLow - (int) ((yL - ymin) * yfactor);
			g2.drawLine(plotLeft, yg, plotRight, yg);
		}

		for (double xL : xLine) {
			int xg = (int) (plotLeft + (xL - xmin) * xfactor);
			g2.drawLine(xg, plotLow, xg, plotUp);
		}

		g2.setColor(borderColor);
		g2.drawRect(plotLeft, plotUp, plotWidth, plotHeight);

		synchronized (imageObjects) {
			for (ImageObject imageOject : imageObjects) {
				Image image = imageOject.image;
				System.out.println("Image: " + image.getHeight(null) + " x " + image.getWidth(null));
				if (imageOject.worldWidth > 0 | imageOject.worldHeight > 0) {
					int newWidth = -1;
					int newHeight = -1;
					if (imageOject.worldWidth > 0) {
						newWidth = Math.abs( scaleX(imageOject.worldWidth) - scaleX( 0. ) );
						System.out.println( "W: " + imageOject.worldWidth + " -> " + newWidth );
					}
					if (imageOject.worldHeight > 0) {
						newHeight = Math.abs( scaleY(imageOject.worldHeight) - scaleY( 0.) );
						System.out.println( "H: " + imageOject.worldHeight + " -> " + newHeight );
					}
					image = imageOject.image.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
					System.out.println("rescaled Image: " + newHeight + " x " + newWidth);
					System.out.println("rescaled Image: " + image.getHeight(null) + " x " + image.getWidth(null));
				}
				int x = scaleX(imageOject.getX()) - image.getWidth(null) / 2;
				int y = scaleY(imageOject.getY()) - image.getHeight(null) / 2;
				System.out.println("Pos: " + imageOject.getX() + ", " + imageOject.getY() + "-> " + x + "," + y);
				g2.drawImage(image, x, y, null);
			}
		}

		FontMetrics fm = g2.getFontMetrics();

		if (autoXgrid > 0) {
			List<Double> xValues = calcAutoGrid(xmin, xmax, autoXgrid);

			for (double xL : xValues) {
				int xg = plotLeft + (int) ((xL - xmin) * xfactor);
				g2.drawLine(xg, plotLow, xg, plotLow - (int) (ticRelHeight * plotHeight));
				String label = getLabelString(xL, xLabelFormat);
				int xlabpos = xg - fm.stringWidth(label) / 2;
				g2.drawString(label, xlabpos, getSize().height - getInsets().bottom);
			}
		}

		if (autoYgrid > 0) {
			// first get all y labels
			List<Double> yValues = calcAutoGrid(ymin, ymax, autoYgrid);

			// now draw them
			for (double yL : yValues) {
				int yg = scaleY(yL); // plotLow - (int) ((yL - ymin) * yfactor);
				g2.drawLine(plotLeft, yg, plotLeft + (int) (ticRelHeight * plotWidth), yg);
				g2.drawString(getLabelString(yL, yLabelFormat), getInsets().left, yg);
			}
		}

		if (xgrid != null) {
			for (int i = 0; i < xgrid.length; i++) {
				int xg = plotLeft + (int) ((xgrid[i] - xmin) * xfactor);
				g2.drawLine(xg, plotLow, xg, plotLow - (int) (ticRelHeight * plotHeight));
				if (xlabel != null) {
					int xlabpos = xg - fm.stringWidth(xlabel[i]) / 2;
					g2.drawString(xlabel[i], xlabpos, getSize().height - getInsets().bottom);
				}
			}
		}

		if (ygrid != null) {
			for (int i = 0; i < ygrid.length; i++) {
				if (ygrid[i] >= ymin && ygrid[i] <= ymax) {
					int yg = plotLow - (int) ((ygrid[i] - ymin) * yfactor);
					g2.drawLine(plotLeft, yg, plotRight, yg);
					g2.drawString("" + ygrid[i], getInsets().left, yg);
				}
			}
		}

		// if (dataObjects.size() > 0) {
		int xcol = 0;
		for (DataObject d : dataObjects.values()) {
			int starX = 0;
			int starY = 0;

			double[] tmpXVector = d.getData();
			if (verbose > 1)
				System.out.println("plotting  points");

			if (d.hasBackground()) {
				Point[] corners = d.getCorners();
				// for (int i = 0; i < tmpXVector.length; i += 2) {
				// System.out.println(tmpXVector[i] + " " + tmpXVector[i+1]
				// + " " + scaleX(tmpXVector[i]) + " " + scaleY(tmpXVector[i+1]
				// ));
				// }
				int iw = scaleX(corners[1].x) - scaleX(corners[0].x);
				int ih = scaleY(corners[1].y) - scaleY(corners[0].y);
				// System.out.println(" iw: " + iw);
				// System.out.println(corners[0].x + " " + corners[0].y + " " +
				// width + " x " + height);
				Shape s = new Rectangle2D.Double(scaleX(corners[0].x), scaleY(corners[0].y), iw, ih);
				// System.out.println( s );
				g2.setColor(d.getBackGroundColor());
				g2.draw(s);
				g2.fill(s);
			}

			LineStyle currStyle = plotMode;
			if (d.getLineStyle() != LineStyle.UNDEFINED)
				currStyle = d.getLineStyle();
			if (d.getStroke() != null)
				g2.setStroke(d.getStroke());

			Path2D.Double pd = new Path2D.Double();
			g2.setColor(d.getColor());

			for (int i = 0; i < tmpXVector.length; i += 2) {
				double x = scaleX(tmpXVector[i]);
				double y = scaleY(tmpXVector[i + 1]);

				if (currStyle == LineStyle.LINE | currStyle == LineStyle.BOTH | currStyle == LineStyle.FILL) {
					if (i == 0) {
						pd.moveTo(x, y);
					} else {
						pd.lineTo(x, y);
					}
				}

				double yref = 0;
				if (ymin > 0)
					yref = ymin;

				double xref = 0;
				if (xmin > 0)
					xref = xmin;

				if (currStyle == LineStyle.IMPULS) {
					g2.drawLine((int) x, scaleY(yref), (int) x, (int) y);
				}

				if (currStyle == LineStyle.YIMPULS) {
					g2.drawLine(scaleX(xref), (int) y, (int) x, (int) y);
				}

				if (currStyle == LineStyle.HISTOGRAM) {
					int width = scaleX(tmpXVector[i] + halfBarWidth) - scaleX(tmpXVector[i] - halfBarWidth);
					int height = scaleY(yref) - (int) y;
					Shape s;
					if (height > 0) {
						s = new Rectangle2D.Double(scaleX(tmpXVector[i] - halfBarWidth), (int) y, width, height);
					} else {
						s = new Rectangle2D.Double(scaleX(tmpXVector[i] - halfBarWidth), scaleY(yref), width, -height);
					}
					g2.draw(s);
					g2.fill(s);
				}

				if (currStyle == LineStyle.YHISTOGRAM) {
					int width = (int) x - scaleX(xref);
					Shape s;
					s = new Rectangle2D.Double(scaleX(xref), y, width, 2 * halfBarWidth);
					g2.draw(s);
					g2.fill(s);
				}

				if (currStyle == LineStyle.SYMBOL || currStyle == LineStyle.BOTH || d.hasLineStyle(LineStyle.SYMBOL)) {
					g2.drawOval((int) (x - symbolSize / 2), (int) (y - symbolSize / 2), symbolSize, symbolSize);
				}

				if (currStyle == LineStyle.FILLED_SYMBOL || d.hasLineStyle(LineStyle.FILLED_SYMBOL)) {
					Shape s = new Ellipse2D.Double((int) (x - symbolSize / 2), (int) (y - symbolSize / 2), symbolSize,
							symbolSize);
					g2.fill(s);
					g2.setColor(Color.BLACK);
					g2.draw(s);
					g2.setColor(d.getColor());
				}
				if (currStyle == LineStyle.DOT) {
					g2.drawOval((int) x, (int) y, 1, 1);
				}

				if (currStyle == LineStyle.VALUE || d.hasLineStyle(LineStyle.VALUE)) {
					String t = String.format(valueFormatString, tmpXVector[i + 1]);
					t = t.replaceAll("[,.]0+", "");
					g2.drawString(t, (int) x - fm.stringWidth(t) / 2, (int) y);
				}

				if (currStyle == LineStyle.COORD || d.hasLineStyle(LineStyle.COORD)) {
					String t = String.format("(%.1f;%.1f)", tmpXVector[i], tmpXVector[i + 1]);
					t = t.replaceAll(",0", "");
					g2.drawString(t, (int) x - fm.stringWidth(t) / 2, (int) y);
				}

				if (currStyle == LineStyle.STAR) {
					if (i == 0) {
						starX = (int) x;
						starY = (int) y;
					} else {
						g2.drawLine(starX, starY, (int) x, (int) y);
					}
				}

				++xcol;
			}
			if (currStyle == LineStyle.LINE | currStyle == LineStyle.BOTH) {
				// Stroke drawingStroke3 = new BasicStroke(3,
				// BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0,
				// new float[] { 5 }, 0);
				g2.draw(pd);
			}
			if (currStyle == LineStyle.FILL) {
				g2.fill(pd);
			}
		}
		drawTextObjects(g2);

	}

	private void drawTextObjects(Graphics2D g2) {
		synchronized (textObjects) {
			for (TextObject tO : textObjects) {
				Font currentFont = g2.getFont(); // remember old font
				if (tO.getFont() != null)
					g2.setFont(tO.getFont());
				FontMetrics fm = g2.getFontMetrics();

				// use the glyphVector to get the correct y-position
				FontRenderContext renderContext = g2.getFontRenderContext();
				GlyphVector glyphVector = g2.getFont().createGlyphVector(renderContext, tO.getText());
				Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();

				int xlabpos, ylabpos;
				if (tO.getOrientation() == TextObject.RIGHT) {
					xlabpos = scaleX(tO.getX());
				} else if (tO.getOrientation() == TextObject.LEFT) {
					xlabpos = scaleX(tO.getX()) - fm.stringWidth(tO.getText());
				} else {
					xlabpos = scaleX(tO.getX()) - fm.stringWidth(tO.getText()) / 2;
				}
				ylabpos = scaleY(tO.getY()) - visualBounds.height / 2 - visualBounds.y;

				// System.out.println(tO.getText() + " " +tO.getColor());
				g2.setColor(tO.getColor());
				g2.drawString(tO.getText(), xlabpos, ylabpos);
				g2.setFont(currentFont); // restore default font
			}
		}

	}

	/**
	 * @param min
	 * @param max
	 * @param autoGrid
	 * @return gridValues
	 */
	private List<Double> calcAutoGrid(double min, double max, double autoGrid) {
		List<Double> gridValues = new ArrayList<Double>();
		if (min * max < 0) {
			for (double yL = 0; yL <= max; yL += autoGrid) {
				gridValues.add(yL);
			}
			for (double yL = 0; yL >= min; yL -= autoGrid) {
				gridValues.add(yL);
			}

		} else {
			double ystart = min;
			if (Math.IEEEremainder(ystart, autoGrid) < 1.e-10) {
				int m = (int) (ystart / autoGrid);
				ystart = (m + 1) * autoGrid;
			}
			for (double yL = ystart; yL <= max; yL += autoGrid) {
				gridValues.add(yL);
			}
		}
		return gridValues;
	}

	private String getLabelString(double y, String labelFormat) {
		if (labelFormat == null)
			return "" + y;
		else
			return String.format(labelFormat, y);
	}

	public int scaleY(double yWorld) {
		// return (int) (plotLow - (d - ymin) * yfactor);
		return (int) (yScaleKonst - yWorld * yfactor);
	}

	/**
	 * Calculates the device x-coordinate for a value given in world coordinates.
	 * If distances are needed: use something like Math.abs( scaleX(1.) - scaleX(0.)) 
	 * to get the length of 1
	 * 
	 * @param xWorld
	 * @return
	 */
	public int scaleX(double xWorld) {
		// return (int) (plotLeft + (d - xmin) * xfactor);
		return (int) (xScaleKonst + xWorld * xfactor);
	}

	public double scaleXR(int i) {
		return (i - xScaleKonst) / xfactor;
	}

	public double scaleYR(int i) {
		return (yScaleKonst - i) / yfactor;
	}

	/**
	 * Switch to next data set.
	 * 
	 * This creates a new empty data set. The new set is default target for
	 * subsequent calls of the methode <code>add</code>.
	 */
	public String nextVector() {
		int i = 1;
		do {
			currV = "" + i;
			++i;
		} while (dataObjects.containsKey(currV));
		if (verbose > 0)
			System.out.println("next vector: " + currV);
		return currV;
	}

	/**
	 * Switch to next data set and use the given name.
	 * 
	 * If necessary this creates a new empty data set. The new set is default
	 * target for subsequent calls of the methode <code>add</code>.
	 * 
	 * @param name
	 *            Name of the new data set
	 */
	public void nextVector(String name) {
		currV = name;
	}

	/**
	 * Set the spacing of labels on both axes.
	 * 
	 * @param autoGrid
	 *            the grid spacing to set
	 */
	public void setAutoGrid(double autoGrid) {
		setAutoXgrid(autoGrid);
		setAutoYgrid(autoGrid);
	}

	/**
	 * Set the spacing of labels on the x axis.
	 * 
	 * @param autoXgrid
	 *            the autoXgrid to set
	 */
	public void setAutoXgrid(double autoXgrid) {
		this.autoXgrid = autoXgrid;
	}

	/**
	 * Set the spacing of labels on the y axis.
	 * 
	 * @param autoYgrid
	 *            the autoYgrid to set
	 */
	public void setAutoYgrid(double autoYgrid) {
		this.autoYgrid = autoYgrid;
	}

	/**
	 * Get the current plot mode.
	 * 
	 * @return the plotMode
	 */
	public LineStyle getPlotMode() {
		return plotMode;
	}

	/**
	 * Set the plot mode. Currently available:
	 * <ul>
	 * <li>LINE: data points are connected with lines</li>
	 * <li>SYMBOL: draw a symbol at each data point</li>
	 * <li>BOTH: combination of LINE and SYMBOL</li>
	 * <li>IMPULS: verticle line to point</li>
	 * <li>HISTOGRAM: verticle box to pointL</li>
	 * </ul>
	 * 
	 * @param lineStyle
	 *            the plotMode to set
	 */
	public void setDataLineStyle(LineStyle lineStyle) {
		setDataLineStyle(currV, lineStyle);
		plotMode = lineStyle;
	}

	public void addDataLineStyle(LineStyle lineStyle) {
		addDataLineStyle(currV, lineStyle);
	}

	/**
	 * Set the plot mode.
	 * 
	 * @see setDataLineStyle
	 * 
	 * @param key
	 * @param lineStyle
	 */
	public void setDataLineStyle(String key, LineStyle lineStyle) {
		checkKey(key);
		dataObjects.get(key).setLineStyle(lineStyle);
	}

	public void addDataLineStyle(String key, LineStyle lineStyle) {
		checkKey(key);
		dataObjects.get(key).addLineStyle(lineStyle);
	}

	/**
	 * @return the symbolSize
	 */
	public int getSymbolSize() {
		return symbolSize;
	}

	/**
	 * Set the size of the symbols used in some plotting styles
	 * 
	 * @param symbolSize
	 *            the symbolSize to set
	 */
	public void setSymbolSize(int symbolSize) {
		this.symbolSize = symbolSize;
	}

	/**
	 * Returns a string with status information
	 * 
	 * @return the string
	 */
	public String getStatusLine() {
		if (statusLine != null)
			return statusLine;
		String status = "";
		status += dataObjects.size();
		if (dataObjects.size() == 1) {
			status += " Set, ";
		} else {
			status += " Sets, ";
		}
		if (textObjects.size() == 1) {
			status += " 1 text object, ";
		}
		if (textObjects.size() > 1) {
			status += textObjects.size() + " text objects, ";
		}
		status += "x: [" + String.format("%.3g", xmin) + "," + String.format("%.3g", xmax) + "] ";
		status += "y: [" + String.format("%.3g", ymin) + "," + String.format("%.3g", ymax) + "] ";
		return status;
	}

	/**
	 * Get the value of half the width of the boxes in plot mode HISTOGRAM
	 * 
	 * @return the halfBarWidth
	 */
	public double getHalfBarWidth() {
		return halfBarWidth;
	}

	/**
	 * Set the half the width of the boxes in plot mode HISTOGRAM
	 * 
	 * @param halfBarWidth
	 *            the halfBarWidth to set
	 */
	public void setHalfBarWidth(double halfBarWidth) {
		this.halfBarWidth = halfBarWidth;
	}

	/**
	 * Define the formatting of the labels of the y-axis. The given string will
	 * be used in <code>String.format(labelFormat, y)</code> to format a value
	 * <code>y</code>. For example <code>"%f.2%%"</code> will print numbers with
	 * two decimal place and a %-symbol. Default is <code>null</code>, i. e. the
	 * label is converted directly into a String.
	 * 
	 * @param labelFormat
	 *            the yLabelFormat to set
	 */
	public void setYLabelFormat(String labelFormat) {
		yLabelFormat = labelFormat;
	}

	/**
	 * Define the formatting of the labels of the x-axis. The given string will
	 * be used in <code>String.format(labelFormat, x)</code> to format a value
	 * <code>x</code>. For example <code>"%.2f%%"</code> will print numbers with
	 * two decimal place and a %-symbol. Default is <code>null</code>, i. e. the
	 * label is converted directly into a String.
	 * 
	 * @param labelFormat
	 *            the xLabelFormat to set
	 */
	public void setXLabelFormat(String labelFormat) {
		xLabelFormat = labelFormat;
	}

	/**
	 * Set same format for both x- and y-labels
	 * 
	 * @param string
	 */
	public void setLabelFormat(String string) {
		setXLabelFormat(string);
		setYLabelFormat(string);

	}

	/**
	 * @param string
	 *            The name of a data set
	 * @return The data set
	 * 
	 */
	public DataObject getDataSet(String string) {
		return dataObjects.get(string);
	}

	public DataObject getDataSet() {
		return dataObjects.get(currV);
	}

	/**
	 * @param key
	 *            The name of a data set
	 * @return The data set
	 * 
	 */
	public DataObject getDataObject(String key) {
		return dataObjects.get(key);
	}

	public DataObject getDataObject() {
		return dataObjects.get(currV);
	}

	public Map<String, DataObject> getDataObjects() {
		return dataObjects;
	}

	/**
	 * Return a Path-Object for the requested data set in World coordinates
	 * 
	 * @param name
	 *            The name of a data set
	 * @return The Path2D object
	 */
	public Path2D.Double getPath(String name) {

		DataObject d = getDataSet(name);
		if (d == null)
			return null;
		double[] tmpXVector = d.getData();

		Path2D.Double pd = new Path2D.Double();
		for (int i = 0; i < tmpXVector.length; i += 2) {
			double x = tmpXVector[i];
			double y = tmpXVector[i + 1];

			if (i == 0) {
				pd.moveTo(x, y);
			} else {
				pd.lineTo(x, y);
			}
		}
		return pd;
	}

	public void removeOld(int n) {
		removeOld(currV, n);
	}

	public void removeOld(String key, int n) {
		DataObject d = dataObjects.get(key);
		d.removeOld(n);

	}

	public void removeNew(int n) {
		removeNew(currV, n);
	}

	public void removeNew(String key, int n) {
		DataObject d = dataObjects.get(key);
		d.removeNew(n);

	}

	public void removeAll() {
		removeAll(currV);
	}

	public void removeAll(String key) {
		DataObject d = dataObjects.get(key);
		if (d != null) {
			d.removeAllData();
		}
	}

	public void removeDataObject(String key) {
		dataObjects.remove(key);
	}

	public void removeAllDataObjects() {
		dataObjects.clear();
	}

	public void listDataObjects() {
		System.out.println("---- currV: " + currV + " ----");
		if (dataObjects.isEmpty()) {
			System.out.println("no DataObjects");
		} else {
			for (String key : dataObjects.keySet()) {
				System.out.println(key + ": " + dataObjects.get(key));
			}
		}

	}

	/**
	 * Remove the first text object that matches the given string. Returns
	 * <code>true</code> if this plotter contained the specified element.
	 * 
	 * @param string
	 *            element to be removed from this plotter, if present
	 * @return <code>true</code> if this plotter contained the specified element
	 */
	public boolean removeText(String string) {
		synchronized (textObjects) {
			for (Iterator<TextObject> it = textObjects.iterator(); it.hasNext();) {
				String test = it.next().getText();
				if (string.equals(test)) {
					System.out.println(string + " == " + test);
					it.remove();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Remove the first text object at the given position. Returns
	 * <code>true</code> if this plotter contained the specified element.
	 * 
	 * @param x
	 *            x postion
	 * @param y
	 *            y position
	 * @return <code>true</code> if this plotter contained the specified element
	 */
	public boolean removeText(double x, double y) {
		synchronized (textObjects) {
			for (Iterator<TextObject> it = textObjects.iterator(); it.hasNext();) {
				TextObject test = it.next();
				if (test.x == x && test.y == y) {
					it.remove();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Remove all text objects
	 */
	public void removeAllText() {
		synchronized (textObjects) {
			textObjects.clear();
		}
	}

	public void dump() {
		dump(currV);
	}

	public void dump(String key) {
		if (dataObjects.containsKey(key)) {
			DataObject d = dataObjects.get(key);
			d.print();
		} else {
			System.out.println("No data object yet");
		}
	}

	public void dumpTextObjects() {
		synchronized (textObjects) {
			for (Iterator<TextObject> it = textObjects.iterator(); it.hasNext();) {
				System.out.println("<<<" + it.next().getText() + ">>>");
			}
		}
	}

	public TextObject setText(String string, double i, double j, Color color) {
		TextObject to = setText(string, i, j);
		to.setColor(color);
		return to;
	}

	public TextObject setText(String string, double i, double j, Color color, Font font) {
		TextObject to = setText(string, i, j, color);
		to.setFont(font);
		return to;
	}

	/**
	 * Write text at the specified position
	 * 
	 * @param s
	 *            text to write
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public TextObject setText(String s, double x, double y) {
		TextObject t = new TextObject(s, x, y);
		synchronized (textObjects) {
			textObjects.add(t);
		}
		return t;
	}

	/**
	 * Write a charcter at the specified position
	 * 
	 * @param c
	 *            character to write
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public TextObject setText(char c, double x, double y) {
		return setText("" + c, x, y);
	}

	public ImageObject setImage(String name, double i, double j) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(name));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return setImage(img, i, j);
	}

	public ImageObject setImage(Image image, double i, double j) {
		ImageObject io = new ImageObject(image, i, j);
		synchronized (imageObjects) {
			imageObjects.add(io);
		}
		return io;
	}

	public void removeImageObject(ImageObject io) {
		imageObjects.remove(io);	
	}

	/**
	 * Returns a string with information on the current version of Plotter
	 * 
	 * @return the current version
	 */
	public static String getVersion() {
		return version;
	}

	public int getTextObjectsCount() {
		return textObjects.size();
	}

	public int getImageObjectsCount() {
		return imageObjects.size();
	}


}
