package chess;

import java.util.ArrayList;
import java.util.List;

//Figur Koenig 
//Version 1.0  Januar 2023 SE
public class King {
	int x;
	int y;
	int color;

	public King(int x, int y, int color) {
		super();
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getText() {
		if (color == ChessBoard.WHITE) {
			return "\u2654";
		} else {
			return "\u265a";
		}
	}

	@Override
	public String toString() {
		return "King [x=" + x + ", y=" + y + ", color=" + color + "]";
	}

	public List<Move> getMoves() {
		// Die Zuege werden in einer Liste gespeichert. Listen sind eine Datenstruktur
		// aehnlich zu Feldern.
		// Allerdings ist die Groesse nicht fest. Listen koennen nach Bedarf wachsen
		// oder schrumpfen.
		// Mit der Methode add werden Elemente an eine Liste angehaengt.

		// Lege eine Liste fuer Elemente des Typs Move an
		List<Move> moves = new ArrayList<>();

		// Fuege die einzelnen Zuege in die Liste ein. x und y geben die aktuelle
		// Position an.
		// Daraus werden die moeglichen Zielfelder bestimmt.
		// Ein Pruefung, ob das Zielfeld frei ist fehlt noch.
		moves.add(new Move(x, y, x - 1, y + 1));
		moves.add(new Move(x, y, x - 1, y));
		moves.add(new Move(x, y, x - 1, y - 1));

		moves.add(new Move(x, y, x, y + 1));
		moves.add(new Move(x, y, x, y - 1));

		moves.add(new Move(x, y, x + 1, y + 1));
		moves.add(new Move(x, y, x + 1, y));
		moves.add(new Move(x, y, x + 1, y - 1));

		return moves;
	}

	// Bewege den Koenig zu einer neuen Position.
	// Es wird noch nicht geprueft, ob dabei eine Figur geschlagen wird. 
	public void move(Move move) {
		x = move.getToX();
		y = move.getToY();

	}
}
