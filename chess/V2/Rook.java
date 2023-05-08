package chessv2;

import java.util.ArrayList;
import java.util.List;

//Figur Turm
//Version 2.0  Januar 2023 SE
public class Rook extends Piece {
	
	public Rook(int x, int y, int color) {
		super(x, y, color);
		directions  = new Direction[] { new Direction(1, 0), new Direction(-1, 0), new Direction(0, 1),
				new Direction(0, -1), };
		value = 5;
	}

	public String getText() {
		if (color == ChessBoard.WHITE) {
			return "\u2656";
		} else {
			return "\u265c";
		}
	}


	@Override
	public String toString() {
		return "Rook [x=" + x + ", y=" + y + ", color=" + color + "]" + ChessGUI.fieldName(x, y);
	}

}
