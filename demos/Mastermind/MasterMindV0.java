package mastermind;

import jserver.Board;
import jserver.BoardClickEvent;
import jserver.BoardClickListener;
import jserver.XSendAdapter;

// Version 1.0: 25. Juli, SE

public class MasterMindV0 implements BoardClickListener {
	private XSendAdapter xsend;
	private GameBoard gameBoard = new GameBoard();
	private int currentColor = 0;

	public static void main(String[] args) {
		MasterMindV0 e = new MasterMindV0();
		e.starten();
	}

	void starten() {
		gameBoard.setUp();

		Board board = gameBoard.getBoard();
		board.addClickListener(this);

		xsend = new XSendAdapter(board);
		xsend.statusText("Demo Farbauswahl Version 1 Simple Click");
		
		gameBoard.addTestButton();

		gameBoard.start();
	}


	@Override
	public void boardClick(BoardClickEvent ev) {
		if (ev.getY() != gameBoard.getCurrentRow()) {
			xsend.statusText("Falsche Zeile");
			return;
		}
		xsend.farbe2(ev.getX(), ev.getY(), GameBoard.COLORS[currentColor]);
		currentColor = (currentColor + 1 )  % GameBoard.getNumColors();
	}
}
