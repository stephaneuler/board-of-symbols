package game;

// Hauptklasse zum Starten der Spielsimulation
// Version 1.0  Mai 2022 SE

public class StartGame {

	public static void main(String[] args) {
		StartGame sg = new StartGame();
		sg.setUp();

	}

	private void setUp() {
		GameBoard gameBoard = new GameBoard();
		gameBoard.init();
		GameGUI gameGui = new GameGUI(gameBoard);
		gameGui.setUp();
		new GameController(gameBoard, gameGui);
		
	}

}
