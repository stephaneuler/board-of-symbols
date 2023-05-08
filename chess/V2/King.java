package chessv2;

import java.util.ArrayList;
import java.util.List;

// Figur Koenig 
// Version 2.0  April 2023 SE
//
// value ist der relative Wert einer Figur in Bauerneinheiten. 
// Ein Koenig ist eigentlich unendlich wertvoll, der Wert 4 bezieht sich auf seine Beweglichkeit. 
// https://en.wikipedia.org/wiki/Chess_piece_relative_value
public class King extends Piece {

	public King(int x, int y, int color) {
		super(x, y, color);
		directions = new Direction[] { new Direction(1, 1), new Direction(1, -1), new Direction(-1, 1),
				new Direction(-1, -1), new Direction(1, 0), new Direction(-1, 0), new Direction(0, 1),
				new Direction(0, -1), };
		maxRange = 1;
		value = 4;
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
		return "King [x=" + x + ", y=" + y + ", color=" + color + "]" + ChessGUI.fieldName(x, y);
	}

}
