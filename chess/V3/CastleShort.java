package chessv3;

import java.util.List;

public class CastleShort extends Move {
	Piece rook;

	public CastleShort(Piece piece) {
		super(piece.getX(), piece.getY(), piece.getX() + 2, piece.getY());
		this.piece = piece;
	}

	@Override
	public void move(List<Piece> pieces) {
		rook = ChessBoard.pieceAtField(fromX + 3, fromY, pieces);
		super.move(pieces);
		rook.setPosition( toX - 1, toY);
	}

	@Override
	public void undo(List<Piece> pieces) {
		super.undo(pieces);
		rook.setPosition( fromX + 3, toY);
	}

	@Override
	public String toString() {
		return "0-0";
	}

	@Override
	public String extendedAlgebra() {
		return "0-0";
	}

}
