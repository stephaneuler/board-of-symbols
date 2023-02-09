package chess;

// Die Klasse ChessBoard beinhaltet ein Spielfeld 
// sowie Methoden zum Initialisiern der Figuren
// Das eigentliche Spielfeld ist durch jeweils zwei Reihen (Zeilen) und Linien (Spalten) begrenzt.
// Diese Felder sind als "besetzt" markiert und die Figuren duerfen sie nicht betreten. 
// Dadurch ist bei der Generierung der Zuege keine Sonderbehandlung am Rand notwendig. 
// Zwei zusaetzliche Reihen bzw. Spalten sind notwendig, da die Figur Springer ein besetztes Feld ueberspringen kann. 
//
// Version 
// 1.0  Januar 2023 SE

public class ChessBoard {
	public static final int EMPTY = 0;
	public static final int BORDER = 1;
	public static final int WHITE = 0;
	public static final int BLACK = 1;

	private int N = 8;
	private int[][] brett;
	private King[] kings;
	private Bishop[] bishops;
	private int numMoves;
	private int nextToMove = WHITE;

	public int getNextToMove() {
		return nextToMove;
	}

	public Bishop[] getBishops() {
		return bishops;
	}

	public King[] getKings() {
		return kings;
	}

	public int getNumMoves() {
		return numMoves;
	}

	public void setNumMoves(int numMoves) {
		this.numMoves = numMoves;
	}

	public void init() {
		brett = new int[N + 4][N + 4];

		setBorder();
		setPieces();
	}

	private void setPieces() {
		kings = new King[] { new King(3, 3, WHITE), new King(7, 8, BLACK) };
		bishops = new Bishop[] { new Bishop(3, 5, WHITE), new Bishop(3, 4, WHITE), new Bishop(5, 6, BLACK) };
	}

	private void setBorder() {
		fill(0, 0, 0, 1, N + 4, BORDER);
		fill(1, 0, 0, 1, N + 4, BORDER);

		fill(N + 2, 0, 0, 1, N + 4, BORDER);
		fill(N + 3, 0, 0, 1, N + 4, BORDER);

		fill(0, 0, 1, 0, N + 4, BORDER);
		fill(0, 1, 1, 0, N + 4, BORDER);

		fill(0, N + 2, 1, 0, N + 4, BORDER);
		fill(0, N + 3, 1, 0, N + 4, BORDER);
	}

	/**
	 * Fill a number of fields with a given value
	 * 
	 * @param x      start position x
	 * @param y      start position y
	 * @param dx     direction x
	 * @param dy     direction y
	 * @param length number of values
	 * @param value
	 */
	private void fill(int x, int y, int dx, int dy, int length, int value) {
		for (int i = 0; i < length; i++) {
			brett[x][y] = value;
			x += dx;
			y += dy;
		}
	}

	public int getSize() {
		return brett.length;
	}

	public void restart() {
		init();
		numMoves = 0;
	}

	public void incMoves() {
		++numMoves;
		if (nextToMove == WHITE) {
			nextToMove = BLACK;
		} else
			nextToMove = WHITE;
	}

	public boolean allows(Move move) {
		return brett[move.getToX()][move.getToY()] == EMPTY;
	}

	public boolean isBorder(int x, int y) {
		return brett[x][y] == BORDER;
	}

}
