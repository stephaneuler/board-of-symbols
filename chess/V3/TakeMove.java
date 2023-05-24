package chessv3;

import java.util.List;

public class TakeMove extends Move {
	protected Piece taken;

	public TakeMove(int fromX, int fromY, int toX, int toY) {
		super(fromX, fromY, toX, toY);
	}

	public TakeMove(Piece piece, int fromX, int fromY, int toX, int toY) {
		super(piece, fromX, fromY, toX, toY);
	}

	public TakeMove(int fromX, int fromY, Direction direction, int width) {
		super(fromX, fromY, direction, width);
	}

	@Override
	public String extendedAlgebra() {
		return piece.getText() + ChessGUI.fieldName(fromX, fromY) + " x " + ChessGUI.fieldName(toX, toY);
	}

	public void setTaken(Piece taken) {
		this.taken = taken;
	}
	
	public Piece getTaken() {
		return taken;
	}

	@Override
	public void move(List<Piece> pieces) {
		super.move(pieces);
		pieces.remove(taken);
	}

	@Override
	public void undo(List<Piece> pieces) {
		super.undo(pieces);
		pieces.add(taken);
	}

}
