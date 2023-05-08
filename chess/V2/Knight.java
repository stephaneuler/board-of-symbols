package chessv2;

import java.util.ArrayList;
import java.util.List;

//Figur Laeufer, zieht allerdings nur ein Feld weit  
//Version 1.0  Januar 2023 SE
public class Knight extends Piece {
	
	public Knight(int x, int y, int color) {
		super(x, y, color);
		directions  = new Direction[] { new Direction(2, 1), new Direction(2, -1), new Direction(-2, 1),
				new Direction(-2, -1), new Direction(1, 2), new Direction(-1, 2), new Direction( 1, -2),
				new Direction(-1, -2),};
		maxRange = 1;
		value = 3;
	}

	public String getText() {
		if (color == ChessBoard.WHITE) {
			return "\u2658";
		} else {
			return "\u265e";
		}
	}


	@Override
	public String toString() {
		return "Bishop [x=" + x + ", y=" + y + ", color=" + color + "]" + ChessGUI.fieldName(x, y);
	}

}
