package chess;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;

import jserver.Board;
import jserver.XSendAdapter;
import plotter.Graphic;

// Das GUI zeigt das Spielbrett und enthält Bedienelemente
// Version 1.0  Januar 2023 SE

public class ChessGUI implements ActionListener {
	private Board board = new Board();
	private XSendAdapter xsend = new XSendAdapter(board);
	private Graphic graphic = board.getGraphic();
	private ChessBoard chessBoard;
	private ChessController chessController;
	private JLabel moveCounter = new JLabel();
	private int[] squareColors = { 0xAFAFAF, 0xFFF7A4 };

	public ChessGUI(ChessBoard chessBoard) {
		super();
		this.chessBoard = chessBoard;
	}

	public void setChessController(ChessController chessController) {
		this.chessController = chessController;
	}

	public void setUp() {
		xsend.groesse(chessBoard.getSize(), chessBoard.getSize());
		xsend.rahmen(XSendAdapter.SADDLEBROWN);
		xsend.statusText("Schach Spiel V0.0");
		board.receiveMessage("borderWidth  12");
		board.receiveMessage("fonttype Dialog");
		board.receiveMessage("fontsize 28");
		graphic.removeMenu("Brett");

		addButton();

		drawChessBoard();
		drawCoordinates();
		drawPosition();
	}

	private void addButton() {
		JButton startButton = new JButton("Ziehe");
		startButton.addActionListener(this);
		graphic.addSouthComponent(startButton);
		// Abstand zwischen Button und Label
		graphic.addSouthComponent(Box.createRigidArea(new Dimension(10, 0)));
		moveCounter.setBorder(BorderFactory.createEtchedBorder());
		graphic.addSouthComponent(moveCounter);
	}

	void drawChessBoard() {
		xsend.formen("s");
		xsend.farben(XSendAdapter.LIGHTGRAY);
		for (int x = 0; x < chessBoard.getSize(); x++) {
			for (int y = 0; y < chessBoard.getSize(); y++) {
				if (chessBoard.isBorder(x, y)) {
					xsend.form2(x, y, "*");
				} else {
					xsend.farbe2(x, y, squareColors[(x + y) % 2]);
				}
			}
		}
	}

	private void drawCoordinates() {
		int N = chessBoard.getSize();
		for (int s = 2; s < N - 2; s++) {
			xsend.text2(s, 1, fileName(s));
			xsend.text2(1, s, rankName(s));
		}
	}

	// Die Zeilen im Schachbrett nennt man Reihe bzw auf Englisch rank.
	static String rankName(int s) {
		return "" + "  12345678   ".charAt(s);
	}

	// Die Spalten im Schachbrett nennt man Linien bzw auf Englisch files.
	static String fileName(int s) {
		return "" + "  ABCDEFGH   ".charAt(s);
	}

	static String fieldName(int x, int y) {
		return fileName(x) + rankName(y);
	}

	public void showInfo(String info) {
		xsend.statusText(info);
	}

	public void drawPosition() {
		clearPieces();

		for (King king : chessBoard.getKings()) {
			xsend.text2(king.getX(), king.getY(), king.getText());
		}
		for (Bishop bishop : chessBoard.getBishops()) {
			xsend.text2(bishop.getX(), bishop.getY(), bishop.getText());
		}
		moveCounter.setText(chessBoard.getNumMoves() + " Zuege");
		xsend.statusText("SpielerIn " + (1 + chessBoard.getNextToMove()) + " am Zug");
	}

	private void clearPieces() {
		for (int x = 2; x < chessBoard.getSize() - 2; x++) {
			for (int y = 2; y < chessBoard.getSize() - 2; y++) {
				xsend.text2(x, y, "");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		chessController.move();
	}

}
