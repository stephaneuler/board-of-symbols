package chessv3;

import java.util.ArrayList;
import java.util.List;

//Abstrakte Oberklasse fuer alle Figuren
//Version 1.0  April 2023 SE

public abstract class Piece {
	protected int x;
	protected int y;
	protected int color;
	protected double value;   // Wert in Bauerneinheiten (z. B. https://de.wikipedia.org/wiki/Bauerneinheit)
	protected Direction directions[]; // Zugmoeglichkeiten 
	protected int maxRange = Integer.MAX_VALUE;  // Reichweite, Standard "beliebig weit"
	public int numMoves;
	
	public abstract String getText();

	public Piece(int x, int y, int color) {
		super();
		this.x = x;
		this.y = y;
		this.color = color;
	}
	public double getValue() {
		return value;
	}

	public int getColor() {
		return color;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}


	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean isColor(int color) {
		return this.color == color;
	}

	// Bewege die Figur zu einer neuen Position.
	// Es wird dabei nicht geprueft, ob eine Figur geschlagen wird.
	public void moveToDestination(Move move) {
		x = move.getToX();
		y = move.getToY();

	}
	
	// Erzeuge alle Zuege: bewege die Figur in alle Richtungen
	// Ein Zug ist unmoeglich, wenn das Zielfeld Rand oder eine eigene Figur ist.
	// Eine gegnerische Figur wird geschlagen, aber es geht dann nicht weiter in diese Richtung
	public List<Move> getMoves(ChessBoard chessBoard) {
		List<Move> moves = new ArrayList<>();
		for (Direction direction : directions) {
			for (int n = 1; n <= maxRange ; n++) {
				Move move = new Move(x, y, direction, n);
				if (chessBoard.allows(move)) {
					if (move.isTake(chessBoard)) {
						TakeMove takeMove = new TakeMove(x, y, direction, n);
						takeMove.setPiece(this);
						Piece pieceAtDestination = chessBoard.pieceAtField(move.getToX(), move.getToY());
						takeMove.setTaken(pieceAtDestination);
						moves.add(takeMove);
						break;
					} else {
						move.setPiece(this);
						moves.add(move);						
					}
				} else {
					break;
				}
			}
		}
		return moves;
	}

	public void incNumMoves() {
		++numMoves;
	}

	public void decNumMoves() {
		--numMoves;
	}

	public boolean hasNotMoved() {
		return numMoves == 0;
	}

}
