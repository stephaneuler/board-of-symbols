package chess;

// Hauptklasse zum Starten der Spielsimulation
// Version 1.0  Januar 2023 SE

public class StartChessGame {

	public static void main(String[] args) {
		(new StartChessGame()).setUp();
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
