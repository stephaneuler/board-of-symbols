package mastermind;

import javax.swing.JButton;

import jserver.Board;
import jserver.XSendAdapter;
import plotter.Graphic;

public class GameBoard {
	public static final int COLORS[] = { XSendAdapter.GREEN, XSendAdapter.YELLOW, XSendAdapter.RED, XSendAdapter.BLUE,
			XSendAdapter.CHOCOLATE, XSendAdapter.PINK  };
	private int rowSize = 4;
	private int rowCount = 5;
	private int currentRow = 0;
	private Board board = new Board();
	private XSendAdapter xsend = new XSendAdapter(board);

	public static int getNumColors() {
		return COLORS.length;
	}

	public int getCurrentRow() {
		return currentRow;
	}

	public int getRowSize() {
		return rowSize;
	}

	public Board getBoard() {
		return board;
	}

	public void setUp() {
		xsend.groesse(rowSize, rowCount);
		xsend.farben(XSendAdapter.WHITE);
		xsend.flaeche(XSendAdapter.SANDYBROWN);
	}

	public void start() {
		Graphic graphic = board.getGraphic();
		graphic.pack();
		graphic.repaint();
		
	}

	public void addTestButton() {
		JButton testButton = new JButton("Prüfen");
		testButton.addActionListener(e -> testInput());
		board.getGraphic().addSouthComponent(testButton);		
	}

	private void testInput() {
		++currentRow;
		xsend.statusText("Naechste Zeile: " + currentRow);
	}


}
