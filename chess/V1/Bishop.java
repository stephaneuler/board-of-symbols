package chess;

import java.util.ArrayList;
import java.util.List;

//Figur Laeufer, zieht allerdings nur ein Feld weit  
//Version 1.0  Januar 2023 SE
public class Bishop {
	int x;
	int y;
	int color;

	public Bishop(int x, int y, int color) {
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
			return "\u2657";
		} else {
			return "\u265d";
		}
	}

	public List<Move> getMoves() {
		List<Move> moves = new ArrayList<>();
		moves.add(new Move(x, y, x - 1, y + 1));
		moves.add(new Move(x, y, x - 1, y - 1));

		moves.add(new Move(x, y, x + 1, y + 1));
		moves.add(new Move(x, y, x + 1, y - 1));
		return moves;
	}

	public void move(Move move) {
		x = move.getToX();
		y = move.getToY();
	}
}
