package chessv2;

import java.util.List;

// Ein Zug mit Start und Ziel
// Eigentlich ist es ein Halbzug. Ein Zug besteht aus einem Halbzug von Weiss und einem Halbzug von Schwarz.
// In der ausfuehrlichen algebraischen Notation schreibt man die Figur, das Ausgangsfeld und das Zielfeld z. B. Ke1-d1.
// Ein kompletter Zug ist dann beispielsweise
// 1.) e2-e4 Sg8-f6    (Bauern werden nicht angegeben)
// In dieser Klasse werden die Felder mit x und y Koordinaten gespeichert. So wird aus e1 (x=6,y=2), wobei die 
// zusaetzlichen Randfelder eingerechnet sind.

//Version 1.0  Januar 2023 SE
//Version 1.0a Maerz 2023 SE: Kommentar zu Zug / Halbzug
//Version 2.0  April 2023 SE: kleinere Erweiterungen 

public class Move {
	protected int fromX;
	protected int fromY;
	protected int toX;
	protected int toY;
	protected Piece piece;

	public boolean isTake(ChessBoard chessBoard) {
		return chessBoard.pieceAtField(toX, toY) != null;
	}

	public Move(int fromX, int fromY, int toX, int toY) {
		super();
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}

	public Move(int fromX, int fromY, Direction direction, int width) {
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = fromX + direction.getDx() * width;
		this.toY = fromY + direction.getDy() * width;
	}

	public Move(Piece piece, int x, int y, int x2, int i) {
		this(x, y, x2, i);
		this.piece = piece;
	}

	@Override
	public String toString() {
		return "Move (" + fromX + ", " + fromY + ") -> (" + toX + ", " + toY + ") " + extendedAlgebra();
	}

	public String extendedAlgebra() {
		return piece.getText() + ChessGUI.fieldName(fromX, fromY) + " - " + ChessGUI.fieldName(toX, toY);
	}

	public int getFromX() {
		return fromX;
	}

	public void setFromX(int fromX) {
		this.fromX = fromX;
	}

	public int getFromY() {
		return fromY;
	}

	public void setFromY(int fromY) {
		this.fromY = fromY;
	}

	public int getToX() {
		return toX;
	}

	public void setToX(int toX) {
		this.toX = toX;
	}

	public int getToY() {
		return toY;
	}

	public void setToY(int toY) {
		this.toY = toY;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}


	public Move inverse() {
		return new Move(toX, toY, fromX, fromY);
	}

	public void move(List<Piece> pieces) {
		piece.moveToDestination(this);
	}

	public void undo(List<Piece> pieces) {
		piece.moveToDestination(this.inverse());
	}

}
