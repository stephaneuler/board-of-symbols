package mastermind;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import jserver.Board;
import jserver.BoardClickEvent;
import jserver.BoardClickListener;
import jserver.ColorNames;
import jserver.XSendAdapter;

// Version 1.0: 25. Juli, SE

public class MasterMindV1 implements BoardClickListener {
	private Board board;
	private XSendAdapter xsend;
	private GameBoard gameBoard = new GameBoard();
	private int currentColor = -1;

	public static void main(String[] args) {
		MasterMindV1 e = new MasterMindV1();
		e.starten();
	}

	void starten() {
		board = gameBoard.getBoard();
		gameBoard.setUp();
		gameBoard.addTestButton();

		xsend = new XSendAdapter(board);
		xsend.statusText("Demo Farbauswahl Version 1 CheckBox");
		board.addClickListener(this);
		addColorButtons();

		gameBoard.start();
	}

	private void addColorButtons() {

		ButtonGroup group = new ButtonGroup();
		for (int color : GameBoard.COLORS) {
			JRadioButton colorChooser = new JRadioButton(ColorNames.getText(color));
			colorChooser.setActionCommand("" + color);
			group.add(colorChooser);
			colorChooser.addActionListener(e -> changeCurrentColor(color));
			board.getGraphic().addEastComponent(colorChooser);
		}
	}

	private void changeCurrentColor(int color ) {
		currentColor = color;
		xsend.statusText("Farbe gewechselt: " + ColorNames.getText(color));
	}

	@Override
	public void boardClick(BoardClickEvent ev) {
		if (ev.getY() != gameBoard.getCurrentRow()) {
			xsend.statusText("Falsche Zeile");
			return;
		}
		if (currentColor < 0) {
			xsend.statusText("keine Farbe");
			return;
		}
		if (ev.getClicks() == 2) {
			xsend.farbe2(ev.getX(), ev.getY(), XSendAdapter.WHITE);
		} else {
			xsend.farbe2(ev.getX(), ev.getY(), currentColor);
		}
	}
}
