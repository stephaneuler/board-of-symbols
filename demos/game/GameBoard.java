package game;

// Die Klasse GameBoard beinhaltet ein Spielfeld 
// sowie Methoden zum Initialisiern und Bewegen der Figuren
// Version 
// 1.0  Mai 2022 SE
// 1.1  Mai 2022 SE Aufteilung Methoden zum Setzen der Figuren

public class GameBoard {
	static final int EMPTY = 0;
	static final int PLAYER_1 = 1;
	static final int PLAYER_2 = 2;
	static final int BORDER = 3;

	private int N = 8;
	private int[][] brett;
	private int numMoves;

	public int getNumMoves() {
		return numMoves;
	}

	public void setNumMoves(int numMoves) {
		this.numMoves = numMoves;
	}

	public void init() {
		brett = new int[N + 2][N + 2];
		
		setBorder();
		setPlayer1Pieces();
		setPlayer2Pieces();
	}

	private void setBorder() {
		for (int i = 0; i < brett.length; i++) {
			brett[0][i] = BORDER;
			brett[N + 1][i] = BORDER;
			brett[i][0] = BORDER;
			brett[i][N + 1] = BORDER;
		}
	}

	private void setPlayer1Pieces() {
		for (int i = 1; i < N / 2; i++) {
			for (int j = 2 - i % 2; j <= N; j += 2) {
				brett[j][i] = PLAYER_1;
			}
		}
	}

	private void setPlayer2Pieces() {
		for (int i = N; i > N / 2 + 1; i--) {
			for (int j = 2 - i % 2; j <= N; j += 2) {
				brett[j][i] = PLAYER_2;
			}
		}
	}

	public int getWidth() {
		return N + 2;
	}

	public int getHeight() {
		return N + 2;
	}

	public int get(int x, int y) {
		return brett[x][y];
	}

	public void move(int xalt, int yalt, int x, int y) {
		brett[x][y] = brett[xalt][yalt];

		brett[xalt][yalt] = EMPTY;
		++numMoves;
	}

	public void restart() {
		init();
		numMoves = 0;
	}

}
