package chessv3;

import java.util.List;

public class Promote extends Move {
	private Piece newPiece;
	private Pawn pawn;

	public Promote(Pawn pawn, int fromX, int fromY, int toX, int toY) {
		super(pawn, fromX, fromY, toX, toY);
		this.pawn = pawn;
	}

	@Override
	public String extendedAlgebra() {
		return super.extendedAlgebra() + "Q";
	}

	@Override
	public void move(List<Piece> pieces) {
		pieces.remove(pawn);
		newPiece = new Queen(toX, toY, pawn.getColor());
		pieces.add(newPiece);
	}

	@Override
	public void undo(List<Piece> pieces) {
		pieces.remove(newPiece);
		pieces.add(pawn);
	}

}
