package chessv3;

import java.util.ArrayList;
import java.util.List;

//Figur Laeufer 
//Version 2.0  April 2023 SE
public class Bishop extends Piece {
	
	public Bishop(int x, int y, int color) {
		super(x, y, color);
		directions  = new Direction[] { new Direction(1, 1), new Direction(1, -1), new Direction(-1, 1),
				new Direction(-1, -1), };
		value = 3.5;
	}

	public String getText() {
		if (color == ChessBoard.WHITE) {
			return "\u2657";
		} else {
			return "\u265d";
		}
	}


	@Override
	public String toString() {
		return "Bishop [x=" + x + ", y=" + y + ", color=" + color + "]" + ChessGUI.fieldName(x, y);
	}

}
