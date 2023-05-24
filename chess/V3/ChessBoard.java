package chessv3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Die Klasse ChessBoard beinhaltet ein Spielfeld 
// sowie Methoden zum Initialisiern der Figuren
// Das eigentliche Spielfeld ist durch jeweils zwei Reihen (Zeilen) und Linien (Spalten) begrenzt.
// Diese Felder sind als "besetzt" markiert und die Figuren duerfen sie nicht betreten. 
// Dadurch ist bei der Generierung der Zuege keine Sonderbehandlung am Rand notwendig. 
// Zwei zusaetzliche Reihen bzw. Spalten sind notwendig, da die Figur Springer ein besetztes Feld ueberspringen kann. 
//
// Version 
//1.0  Januar 2023 SE
//2.0  April 2023 SE Umbau durch Vererbung der Figuren, Pruefung auf legale Zuege

public class ChessBoard {
	public static final int EMPTY = 0;
	public static final int BORDER = 1;
	public static final int WHITE = 0;
	public static final int BLACK = 1;

	private int N = 8;
	private int[][] brett;
	private int numberOfMovesInGame;
	private int nextToMove = WHITE;
	private List<Piece> pieces = new ArrayList<>();
	private List<Move> gameMoves = new ArrayList<>();

	public int getNextToMove() {
		return nextToMove;
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public int getNumMoves() {
		return numberOfMovesInGame;
	}

	public void setNumMoves(int numMoves) {
		this.numberOfMovesInGame = numMoves;
	}

	public void init() {
		brett = new int[N + 4][N + 4];

		setBorder();
		setPieces();
	}

	private void setPieces() {
		pieces.add(new King(3, 3, WHITE));
		pieces.add(new King(7, 8, BLACK));
		pieces.add(new Knight(3, 5, WHITE));
		pieces.add(new Bishop(3, 4, WHITE));
		pieces.add(new Rook(5, 4, BLACK));
		pieces.add(new Queen(5, 7, BLACK));
		pieces.add(new Pawn(6, 3, WHITE));
		pieces.add(new Pawn(6, 7, BLACK));
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
		numberOfMovesInGame = 0;
	}

	public void incMoves() {
		++numberOfMovesInGame;
		toggleColor();
	}

	public void decMoves() {
		--numberOfMovesInGame;
		toggleColor();
	}

	void toggleColor() {
		if (nextToMove == WHITE) {
			nextToMove = BLACK;
		} else {
			nextToMove = WHITE;
		}
	}

	public boolean allows(Move move) {
		int toX = move.getToX();
		int toY = move.getToY();
		if (isBorder(toX, toY)) {
			return false;
		}

		Piece pieceAtDestination = pieceAtField(toX, toY);
		if (pieceAtDestination != null) {
			Piece pieceAtStart = pieceAtField(move.getFromX(), move.getFromY());
			if (pieceAtDestination.getColor() == pieceAtStart.getColor()) {
				return false;
			}
		}
		return true;
	}

	public Piece pieceAtField(int x, int y) {
		return pieceAtField(x, y, pieces);
	}

	public static Piece pieceAtField(int x, int y, List<Piece> pieces) {
		for (Piece piece : pieces) {
			if (piece.getX() == x & piece.getY() == y) {
				return piece;
			}
		}
		return null;
	}

	public boolean isBorder(int x, int y) {
		return brett[x][y] == BORDER;
	}

	public void move(Move move) {
		move.move(pieces);
		gameMoves.add(move);
		incMoves();
	}

	/**
	 * Nehme Zug zurueck. Falls Figur geschlagen wurde, wird sie wieder eingesetzt.
	 * 
	 * @param move
	 */
	private void undo(Move move) {
		move.undo(pieces);
		gameMoves.remove(move);
		decMoves();

	}

	/**
	 * Die Generierung aller moeglichen Zuege erfolgt in zwei Schritten. Zuerst
	 * werden alle moeglichen Zuege erzeugt. Darunter koennen auch Zuege sein, nach
	 * denen der Koenig im Schach steht. Das ist nicht erlaubt. Daher werden mit
	 * isLegal alle derartigen Zuege entfernt.
	 * 
	 * @return
	 */
	public List<Move> getAllMoves() {
		List<Move> moves = getCandidateMoves();
		List<Move> legalMoves = moves.stream().filter(m -> isLegal((Move) m)).collect(Collectors.toList());
		return legalMoves;
	}

	public List<Move> getLegalMoves(Piece piece) {
		List<Move> moves = piece.getMoves(this);
		List<Move> legalMoves = moves.stream().filter(m -> isLegal((Move) m)).collect(Collectors.toList());
		return legalMoves;
	}

	/**
	 * Gibt eine Liste von Strings fuer die Zuege zurueck
	 * 
	 * @return
	 */
	public List getAllMovesNiceText() {
		return getAllMovesNiceText(getAllMoves());
	}

	/**
	 * Gibt eine Liste von Strings fuer die Zuege zurueck
	 * 
	 * @return
	 */
	public List getAllMovesNiceText(List<Move> moves) {
		List<String> text = new ArrayList<>();
		for (Move move : moves) {
			text.add(move.extendedAlgebra());
		}
		return text;
	}

	public List getAllGameMovesNiceText() {
		List<String> text = new ArrayList<>();
		String line = "";
		for (Move move : gameMoves) {
			if (move.getPiece().getColor() == WHITE) {
				line = move.extendedAlgebra();
			} else {
				line += "\t" + move.extendedAlgebra();
			text.add(line);
			}
		}
		return text;

	}

	private List<Move> getCandidateMoves() {
		List<Move> moves = new ArrayList<>();
		for (Piece piece : pieces) {
			if (piece.getColor() == nextToMove) {
				moves.addAll(piece.getMoves(this));
			}
		}
		return moves;
	}

	// check if opponent can take king
	// make move, can opponent take king?, undo move
	private boolean isLegal(Move move) {
		move(move);
		boolean legal = !isInCheck();
		undo(move);
		return legal;
	}

	public boolean isInCheck() {
		List<Move> moves = getCandidateMoves();
		for (Move move : moves) {
			Piece p = pieceAtField(move.getToX(), move.getToY());
			if (p != null & p instanceof King) {
				return true;
			}
		}
		return false;
	}

	public int getTotalValue(int color) {
		int totalValue = 0;
		for (Piece piece : pieces) {
			if (piece.isColor(color)) {
				totalValue += piece.getValue();
			}
		}
		return totalValue;
	}

	public void removeAllPieces() {
		pieces.clear();

	}

	public void addPieces(Piece[] pieces2) {
		pieces.addAll(Arrays.asList(pieces2));

	}

	public void addPiece(Piece piece) {
		pieces.add(piece);

	}

	public boolean isPieceToMove(Piece piece) {
		return piece.getColor() == nextToMove;
	}

	public void resetMoveCounter() {
		numberOfMovesInGame = 0;
		gameMoves.clear();
	}

}
