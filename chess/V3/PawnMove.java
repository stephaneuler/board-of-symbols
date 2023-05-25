package chessv3;

import java.util.List;

public class PawnMove extends Move {
	public PawnMove(Pawn pawn, int fromX, int fromY, int toX, int toY) {
		super(pawn, fromX, fromY, toX, toY);
	}

	@Override
	public void move(List<Piece> pieces) {
		super.move(pieces);
		if (Math.abs(toY - fromY) == 2) {
			System.out.println("Allow e.p. next move");
		}
	}

	@Override
	public void undo(List<Piece> pieces) {
		super.undo(pieces);
	}

}
