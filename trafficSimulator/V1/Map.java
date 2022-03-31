package traffic;

// Die Karte enthaelt ein rechteckiges Feld, wobei jedes Feld einen Landschaftstyp hat.
// Die x-Koordinaten sind 0 ... (width - 1). 
// Als double-Werte sind dann -0.5 ... (width - 0.5) moeglich, 
// die dann durch Rundung zu gueltigen Koordinaten fuehren.
// Die Hilfsmethoden checkBorderX/Y pruefen Koordinaten. 
// Dabei werden Werte "rechts bzw oben" ausserhalb wieder nach "links bzw unten" gefuehrt.

// Version 
// 1.0 Maerz 2022 SE: erste Version

public class Map {
	public static final int EMPTY = 0;
	public static final int STREET = 1;
	public static final int BORDER = 2;
	public static final int TREE = 3;
	public static final int WATER = 4;

	private int width;
	private int height;
	private int lanes = 2;
	int[][] feld;

	public Map(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		feld = new int[width][height];
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void fill() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int yd = Math.abs(y - height / 2);
				int xd = Math.abs(x - width / 2);
				if (yd == 0 | xd == 0) {
					feld[x][y] = BORDER;
				} else if (xd <= lanes | yd <= lanes) {
					feld[x][y] = STREET;
				} else if (x < width / 2 - lanes && y > height / 2 + lanes) {
					feld[x][y] = TREE;
				} else {
					feld[x][y] = EMPTY;
				}
			}
		}

		addWater(3 * width / 4, height / 4, width / 6, height / 6);
	}

	private void addWater(int x, int y, int dx, int dy) {
		for (int px = -dx; px < dx; ++px) {
			for (int py = -dy; py < dy; ++py) {
				if (px * px + py * py < dx * dy) {
					feld[x + px][y + py] = WATER;
				}
			}
		}

	}

	public boolean isWater(double x, double y) {
		int ix = (int) Math.round(x);
		int iy = (int) Math.round(y);
		return feld[ix][iy] == WATER;
	}
	
	public int get(int x, int y) {
		return feld[x][y];
	}

	public double checkBorderX(double x) {
		if( x < -0.5 ) {
			return 0;
		}
		if( x > width - 0.5 ) {
			return x - width;
		}
		return x;
	}

	public double checkBorderY(double y) {
		if( y < -0.5 ) {
			return 0;
		}
		if( y > height - 0.5 ) {
			return y - height;
		}
		return y;
	}


}
