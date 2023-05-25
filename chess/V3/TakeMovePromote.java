package chessv3;

import java.util.List;

public class TakeMovePromote extends TakeMove {
	private Piece newPiece;
	private Pawn pawn;

	public TakeMovePromote(Pawn pawn, int fromX, int fromY, int toX, int toY) {
		super(pawn, fromX, fromY, toX, toY);
		this.pawn = pawn;
	}

	@Override
	public String extendedAlgebra() {
		return super.extendedAlgebra() + "Q";
	}

	@Override
	public void move(List<Piece> pieces) {
		super.move(pieces);
		pieces.remove(pawn);
		newPiece = new Queen(toX, toY, pawn.getColor());
		pieces.add(newPiece);
	}

	@Override
	public void undo(List<Piece> pieces) {
		super.undo(pieces);
		pieces.remove(newPiece);
		pieces.add(pawn);
	}
}
