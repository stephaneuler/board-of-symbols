package chess;

import java.util.List;
import java.util.Random;

// Der Controller wartet auf Nachrichten vom GUI und führt entsprechende Aktionen aus. 
// Version 1.0  Januar 2023 SE

public class ChessController {

	private ChessBoard chessBoard;
	private ChessGUI chessGui;
	private Random random = new Random();

	public ChessController(ChessBoard gameBoard, ChessGUI gameGui) {
		super();
		this.chessBoard = gameBoard;
		this.chessGui = gameGui;
	}

	public void move() {
		// Auswahl Koenig oder Laeufer, danach zufaellige Auswahl eines Zugs
		// random.nextInt(2) gibt zufaellig 0 oder 1 zurueck
		switch (random.nextInt(2)) {
		case 0:
			for (King king : chessBoard.getKings()) {
				if (king.getColor() == chessBoard.getNextToMove()) {
					List<Move> moves = king.getMoves();
					Move move = randomLegalMove(moves);
					king.move(move);
					chessBoard.incMoves();
					chessGui.drawPosition();
					System.out.println(move);
					return;
				}
			}
			break;

		case 1:
			for (Bishop bishop : chessBoard.getBishops()) {
				if (bishop.getColor() == chessBoard.getNextToMove()) {
					List<Move> moves = bishop.getMoves();
					Move move = randomLegalMove(moves);
					bishop.move(move);
					chessBoard.incMoves();
					chessGui.drawPosition();
					System.out.println(move);
					return;
				}
			}
			break;
		}
	}

	private Move randomLegalMove(List<Move> moves) {
		Move move;
		do {
			move = moves.get(random.nextInt(moves.size()));
		} while (! chessBoard.allows(move));
		return move;
	}
}
