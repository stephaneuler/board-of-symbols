package chessv2;

import java.util.ArrayList;
import java.util.List;

//Figur Bauer 
//Version 2.0  April 2023 SE
public class Pawn extends Piece {

	public Pawn(int x, int y, int color) {
		super(x, y, color);
		value = 1;
	}

	public String getText() {
		if (color == ChessBoard.WHITE) {
			return "\u2659";
		} else {
			return "\u265f";
		}
	}

	@Override
	public List<Move> getMoves(ChessBoard chessBoard) {
		List<Move> moves = new ArrayList<>();
		if (color == ChessBoard.WHITE) {
			System.out.println("check pawn 1 " + this);
			addIfPossible(moves, 1, chessBoard);
			if (y == 3) {
				System.out.println("check pawn 2");
				addIfPossible(moves, 2, chessBoard);
			}
			addTakeIfPossible(moves, 1, 1, chessBoard);
			addTakeIfPossible(moves, -1, 1, chessBoard);
		} else {
			addIfPossible(moves, -1, chessBoard);
			if (y == 8) {
				addIfPossible(moves, -2, chessBoard);
			}
			addTakeIfPossible(moves, 1, -1, chessBoard);
			addTakeIfPossible(moves, -1, -1, chessBoard);
		}
		return moves;
	}

	private void addTakeIfPossible(List<Move> moves, int dx, int dy, ChessBoard chessBoard) {
		Piece piece = chessBoard.pieceAtField(x + dx, y + dy);
		if (piece != null && ! piece.isColor( color) ) {
			TakeMove takeMove = new TakeMove(this, x, y, x + dx, y + dy);
			takeMove.setTaken(piece);
			moves.add(takeMove);
		}
		
	}

	private void addIfPossible(List<Move> moves, int dy, ChessBoard chessBoard) {
		Piece piece = chessBoard.pieceAtField(x, y + dy);
		if (piece == null) {
			moves.add(new Move(this, x, y, x, y + dy));
		}
	}

	@Override
	public String toString() {
		return "Pawn [x=" + x + ", y=" + y + ", color=" + color + "]" + ChessGUI.fieldName(x, y);
	}

}
