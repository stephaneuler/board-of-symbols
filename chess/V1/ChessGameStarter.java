package chess;

// Hauptklasse zum Starten der Spielsimulation
// Version 1.0  Januar 2023 SE
// Version 1.0a Maerz 2023 SE: Klasse umbenannt

public class ChessGameStarter {

	public static void main(String[] args) {
		(new ChessGameStarter()).setUp();
	}

	private void setUp() {
		ChessBoard chessBoard = new ChessBoard();
		chessBoard.init();
		ChessGUI chessGui = new ChessGUI(chessBoard);
		chessGui.setUp();
		ChessController  chessController = new ChessController(chessBoard, chessGui);
		chessGui.setChessController(chessController);
	}

}
