package chessv3;

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
			addIfPossible(moves, 1, chessBoard);
			if (y == 3) {
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
		int toY = y + dy;
		Piece piece = chessBoard.pieceAtField(x + dx, toY);
		if (piece != null && !piece.isColor(color)) {
			TakeMove takeMove;
			if (isEighthRank(toY)) {
				takeMove = new TakeMovePromote(this, x, y, x + dx, toY);
			} else {
				takeMove = new TakeMove(this, x, y, x + dx, toY);
			}
			takeMove.setTaken(piece);
			moves.add(takeMove);
		}

	}

	private void addIfPossible(List<Move> moves, int dy, ChessBoard chessBoard) {
		int toY = y + dy;
		Piece piece = chessBoard.pieceAtField(x, toY);
		if (piece == null) {
			if (isEighthRank(toY)) {
				moves.add(new Promote(this, x, y, x, toY));
			} else {
				moves.add(new PawnMove(this, x, y, x, toY));
			}
		}
	}

	private boolean isEighthRank(int y) {
		return y == 2 || y == 9;
	}

	@Override
	public String toString() {
		return "Pawn [x=" + x + ", y=" + y + ", color=" + color + "]" + ChessGUI.fieldName(x, y);
	}

}
