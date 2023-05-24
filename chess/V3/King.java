package chessv3;

import java.util.ArrayList;
import java.util.List;

// Figur Koenig 
// Version 2.0  April 2023 SE
//
// value ist der relative Wert einer Figur in Bauerneinheiten. 
// Ein Koenig ist eigentlich unendlich wertvoll, der Wert 4 bezieht sich auf seine Beweglichkeit. 
// https://en.wikipedia.org/wiki/Chess_piece_relative_value
public class King extends Piece {

	@Override
	public List<Move> getMoves(ChessBoard chessBoard) {
		List<Move> moves = super.getMoves(chessBoard);
		checkCastle(moves, chessBoard);
		return moves;
	}

	private void checkCastle(List<Move> moves, ChessBoard chessBoard) {
		if (numMoves != 0) {
			return;
		}
		if (chessBoard.pieceAtField(x + 1, y) == null && chessBoard.pieceAtField(x + 2, y) == null) {
			if (rookCanCastle(chessBoard, 3)) {
				moves.add(new CastleShort(this));
			}
		}
		if (chessBoard.pieceAtField(x - 1, y) == null && chessBoard.pieceAtField(x - 2, y) == null
				&& chessBoard.pieceAtField(x - 3, y) == null) {
			if (rookCanCastle(chessBoard, -4)) {
				moves.add(new CastleLong(this));
			}
		}
	}

	private boolean rookCanCastle(ChessBoard chessBoard, int dx) {
		Piece rook = chessBoard.pieceAtField(x + dx, y);
		return rook instanceof Rook && rook.isColor(color) && rook.hasNotMoved();
	}

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
