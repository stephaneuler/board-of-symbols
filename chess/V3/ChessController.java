package chessv3;

import static chessv3.ChessBoard.*;

import java.util.List;
import java.util.Random;

import javax.swing.JMenuItem;

// Der Controller wartet auf Nachrichten vom GUI und führt entsprechende Aktionen aus. 
//Version 1.0  Januar 2023 SE
//Version 2.0  April 2023 SE Vereinfachung durch gemeinsame Liste mit allen Figuren
//Version 3.0  Mai 2023 SE Ziehen durch Klicken 

public class ChessController {
	enum State {
		P1_TO_MOVE, P1_MOVES, P2_TO_MOVE, P2_MOVES
	};

	private ChessBoard chessBoard;
	private ChessGUI chessGui;
	private Random random = new Random();
	private State state = State.P1_TO_MOVE;
	private Piece activePiece;

	public ChessController(ChessBoard gameBoard, ChessGUI gameGui) {
		super();
		this.chessBoard = gameBoard;
		this.chessGui = gameGui;
		chessGui.showState(state);
	}

	public void randomMove() {
		List<Move> moves = chessBoard.getAllMoves();
		if( moves.size() == 0 ) {
			chessBoard.toggleColor();
			if( chessBoard.isInCheck()) {
				chessGui.showInfo("Matt - Glueckwunsch!");
			} else {
				chessGui.showInfo("Patt - nur ein halber Punkt!");		
			}
			chessBoard.toggleColor();
			return;
		}
		Move move = getNextMove( moves );
		chessBoard.move(move);

		if (activePiece != null) {
			chessGui.unsetSelected(activePiece.getX(), activePiece.getY());
		}
		if (chessBoard.getNextToMove() == WHITE) {
			state = State.P1_TO_MOVE;
		} else {
			state = State.P2_TO_MOVE;
		}
		chessGui.showState(state);
		chessGui.drawPosition();
		return;
	}

	private Move getNextMove(List<Move> moves) {
		// if possible castle
		for( Move move : moves ) {
			if( move instanceof CastleShort || move instanceof CastleLong ) {
				return move;
			}
		}
		return moves.get(random.nextInt(moves.size()));
	}

	public void setPosition(String positionName) {
		chessBoard.removeAllPieces();
		chessBoard.resetMoveCounter();
		for (Position position : Position.getPositions()) {
			if (positionName.equals(position.getName())) {
				chessBoard.addPieces(position.getPieces());
			}
		}
		chessGui.drawPosition();
	}

	public void clicked(int x, int y) {
		switch (state) {
		case P1_TO_MOVE:
			activatePiece(x, y, State.P1_MOVES);
			break;
		case P1_MOVES:
			if( moveIfPossible(x, y, State.P2_TO_MOVE) ) {
				if( chessGui.isAutoMove() ) {
					randomMove();
					state = State.P1_TO_MOVE;
				}
 			}
			break;
		case P2_TO_MOVE:
			activatePiece(x, y, State.P2_MOVES);
			break;
		case P2_MOVES:
			moveIfPossible(x, y, State.P1_TO_MOVE);
			break;
		}
		chessGui.showState(state);

	}

	private boolean moveIfPossible(int x, int y, State nextState) {
		List<Move> moves = chessBoard.getLegalMoves(activePiece);
		Move move = isInMoves(moves, x, y);
		if (move != null) {
			chessGui.unsetSelected(activePiece.getX(), activePiece.getY());
			chessBoard.move(move);
			chessGui.drawPosition();
			state = nextState;
			return true;
		}
		return false;
	}

	private void activatePiece(int x, int y, State nextState) {
		Piece piece = chessBoard.pieceAtField(x, y);
		if (piece != null && chessBoard.isPieceToMove(piece)) {
			if (chessBoard.getLegalMoves(piece).size() > 0) {
				chessGui.setSelected(x, y);
				activePiece = piece;
				state = nextState;
			}
		}
	}

	private Move isInMoves(List<Move> moves, int x, int y) {
		for (Move move : moves) {
			if (move.getToX() == x && move.getToY() == y) {
				return move;
			}
		}
		return null;
	}

}
