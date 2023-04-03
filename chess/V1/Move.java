package chess;

// Ein Zug mit Start und Ziel
// Eigentlich ist es ein Halbzug. Ein Zug besteht aus einem Halbzug von Weiss und einem Halbzug von Schwarz.
// In der ausfuehrlichen algebraischen Notation schreibt man die Figur, das Ausgangsfeld und das Zielfeld z. B. Ke1-d1.
// Ein kompletter Zug ist dann beispielsweise
// 1.) e2-e4 Sg8-f6    (Bauern werden nicht angegeben)
// In dieser Klasse werden die Felder mit x und y Koordinaten gespeichert. So wird aus e1 (x=6,y=2), wobei die 
// zusaetzlichen Randfelder eingerechnet sind.

//Version 1.0  Januar 2023 SE
//Version 1.0a Maerz 2023 SE: Kommentar zu Zug / Halbzug

public class Move {
	private int fromX;
	private int fromY;
	private int toX;
	private int toY;
	
	public Move(int fromX, int fromY, int toX, int toY) {
		super();
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}

	@Override
	public String toString() {
		return "Move [fromX=" + fromX + ", fromY=" + fromY + ", toX=" + toX + ", toY=" + toY + "] " + extendedAlgebra();
	}

	private String extendedAlgebra() {
		return ChessGUI.fieldName( fromX, fromY)   + "-" +  ChessGUI.fieldName( toX, toY) ;
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
	
	
}
