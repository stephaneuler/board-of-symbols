package chessv3;

import java.util.List;

public class CastleLong extends Move {
	Piece rook;

	public CastleLong(Piece piece) {
		super(piece, piece.getX(), piece.getY(), piece.getX() - 2, piece.getY());
	}

	@Override
	public void move(List<Piece> pieces) {
		super.move(pieces);
		rook = ChessBoard.pieceAtField(fromX - 4, piece.y, pieces);
		rook.setPosition(toX + 1, toY);
	}

	@Override
	public void undo(List<Piece> pieces) {
		super.undo(pieces);
		rook.setPosition(fromX - 4, toY);
	}

	@Override
	public String toString() {
		return "0-0-0";
	}

	@Override
	public String extendedAlgebra() {
		return "0-0-0";
	}

}
