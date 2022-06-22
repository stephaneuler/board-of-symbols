package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

import jserver.Board;
import jserver.BoardClickListener;
import jserver.XSendAdapter;
import plotter.Graphic;

// Das GUI zeigt das Spielbrett und enthält Bedienelemente
// Version 1.0  Mai 2022 SE

public class GameGUI implements ActionListener {
	private Board board = new Board();
	private XSendAdapter xsend = new XSendAdapter(board);
	private Graphic graphic = board.getGraphic();
	private GameBoard gameBoard;
	private int playerColors[] = { 0, XSendAdapter.WHITE, XSendAdapter.RED };
	private double rad = 0.35;
	private double radActive = 0.45;
	private List<ActionListener> actionListeners = new ArrayList<>();
	private JLabel moveCounter = new JLabel("noch keine Zuege");

	public GameGUI(GameBoard gameBoard) {
		super();
		this.gameBoard = gameBoard;
	}

	public void setUp() {
		xsend.groesse(gameBoard.getWidth(), gameBoard.getHeight());
		xsend.rahmen(XSendAdapter.SADDLEBROWN);
		board.receiveMessage("borderWidth  28");
		
		addButton();
		
		graphic.addSouthComponent(moveCounter);
		graphic.removeMenu("Brett");
		
		xsend.statusText("Spiel startet, SpielerIn 1 am Zug");
		showGameBoard();
	}

	private void addButton() {
		JButton restartButton = new JButton("Neustart");
		restartButton.addActionListener( this );
		graphic.addSouthComponent(restartButton);
	}

	void showGameBoard() {
		xsend.formen("none");
		for (int x = 0; x < gameBoard.getWidth(); x++) {
			for (int y = 0; y < gameBoard.getHeight(); y++) {
				int piece = gameBoard.get(x, y);
				if (piece == GameBoard.PLAYER_1 | piece == GameBoard.PLAYER_2) {
					showPiece(x, y, piece);
				}
				if (piece == GameBoard.BORDER) {
					showBorder(x, y);
				} else {
					showBackground(x, y);
				}
			}
		}
		moveCounter.setText( gameBoard.getNumMoves() + " Zuege");
	}

	private void showBorder(int x, int y) {
		xsend.form2(x, y, "*");
		xsend.symbolGroesse2(x, y, 0.2);
	}

	private void showBackground(int x, int y) {
		if ((x + y) % 2 == 0) {
			xsend.hintergrund2(x, y, 0xAFAFAF);
		} else {
			xsend.hintergrund2(x, y, 0xFFF7A4);
		}
	}

	private void showPiece(int x, int y, int piece) {
		xsend.farbe2(x, y, playerColors[piece]);
		xsend.form2(x, y, "c");
		xsend.symbolGroesse2(x, y, rad);
	}

	public void showInfo(String info) {
		xsend.statusText(info);
		
	}

	public void setActive(int x, int y) {
		xsend.symbolGroesse2(x, y, radActive);
		
	}
	public void unsetActive(int x, int y) {
		xsend.symbolGroesse2(x, y, rad);
	}

	public void addClickListener(BoardClickListener listener) {
		board.addClickListener(listener);	
	}

	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for( ActionListener al : actionListeners ) {
			al.actionPerformed(e);
		}
		
	}
}
