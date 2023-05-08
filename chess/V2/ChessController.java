package chessv2;

import java.util.List;
import java.util.Random;

// Der Controller wartet auf Nachrichten vom GUI und führt entsprechende Aktionen aus. 
//Version 1.0  Januar 2023 SE
//Version 2.0  April 2023 SE Vereinfachung durch gemeinsame Liste mit allen Figuren

public class ChessController {

	private ChessBoard chessBoard;
	private ChessGUI chessGui;
	private Random random = new Random();

	public ChessController(ChessBoard gameBoard, ChessGUI gameGui) {
		super();
		this.chessBoard = gameBoard;
		this.chessGui = gameGui;
	}

	public void randomMove() {
		List<Move> moves = chessBoard.getAllMoves();
		Move move = moves.get(random.nextInt(moves.size()));
		chessBoard.move(move);

		chessGui.drawPosition();
		System.out.println(move);
		return;
	}

	public void setPosition(String position) {
		chessBoard.removeAllPieces();
		if (position.equals("Pawn")) {
			Piece[] pieces = { new King(3, 3, ChessBoard.WHITE), new King(5, 7, ChessBoard.BLACK),
					new Pawn(2, 3, ChessBoard.WHITE), new Pawn(6, 4, ChessBoard.WHITE),
					new Pawn(7, 5, ChessBoard.BLACK), new Pawn(6, 7, ChessBoard.BLACK) };
			chessBoard.addPieces(pieces);
		} else if( position.equals("Promote")) {
			Piece[] pieces = { new King(3, 3, ChessBoard.WHITE), new King(5, 7, ChessBoard.BLACK),
					new Pawn(2, 8, ChessBoard.WHITE), new Pawn(6, 3, ChessBoard.BLACK) };
			chessBoard.addPieces(pieces);
		}
		chessGui.drawPosition();
	}

}
