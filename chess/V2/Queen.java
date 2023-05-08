package chessv2;

import java.util.ArrayList;
import java.util.List;

// Figur Dame 
// Version 1.0  April 2023 SE
public class Queen extends Piece {

	public Queen(int x, int y, int color) {
		super(x, y, color);
		directions = new Direction[] { new Direction(1, 1), new Direction(1, -1), new Direction(-1, 1),
				new Direction(-1, -1), new Direction(1, 0), new Direction(-1, 0), new Direction(0, 1),
				new Direction(0, -1), };
		value = 9;
	}

	public String getText() {
		if (color == ChessBoard.WHITE) {
			return "\u2655";
		} else {
			return "\u265b";
		}
	}

	@Override
	public String toString() {
		return "Queen [x=" + x + ", y=" + y + ", color=" + color + "]" + ChessGUI.fieldName(x, y);
	}

}
