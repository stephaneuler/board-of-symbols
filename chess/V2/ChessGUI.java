package chessv2;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import jserver.Board;
import jserver.Dialogs;
import jserver.XSendAdapter;
import plotter.Graphic;

// Das GUI zeigt das Spielbrett und enthält Bedienelemente
//Version 1.0  Januar 2023 SE
//Version 2.0  April 2023 SE Div. Erweiterungen (Anzeige der Zugmoeglichkeiten, ...)

public class ChessGUI implements ActionListener {
	private static final String MOVE = "Ziehe";
	private static final String SHOW_MOVES = "Zeige Zuege";
	private Board board = new Board();
	private XSendAdapter xsend = new XSendAdapter(board);
	private Graphic graphic = board.getGraphic();
	private ChessBoard chessBoard;
	private ChessController chessController;
	private JLabel moveCounter = new JLabel();
	private int[] squareColors = { 0xAFAFAF, 0xFFF7A4 };
	private int chessBoardSize;

	public ChessGUI(ChessBoard chessBoard) {
		super();
		this.chessBoard = chessBoard;
		chessBoardSize = chessBoard.getSize();
	}

	public void setChessController(ChessController chessController) {
		this.chessController = chessController;
	}

	public void setUp() {
		setUpBoSym();

		addButtons();
		addMoveCounter();
		addMenu();

		drawChessBoard();
		drawCoordinates();
		drawPosition();
		makeSpaceForValues();
	}

	private void addMenu() {
		JMenu chessMenu = new JMenu("Schach");
		graphic.addExternMenu(chessMenu);
		
		JMenuItem posPawn = new JMenuItem("Bauern-Test");
		posPawn.addActionListener(event -> chessController.setPosition("Pawn"));
		chessMenu.add(posPawn);
		
		JMenuItem promotePawn = new JMenuItem("Umwandlung");
		promotePawn.addActionListener(event -> chessController.setPosition("Promote"));
		chessMenu.add(promotePawn);
		
	}

	private void setUpBoSym() {
		xsend.groesse(chessBoard.getSize(), chessBoard.getSize());
		xsend.rahmen(XSendAdapter.SADDLEBROWN);
		xsend.statusText("Schach Spiel V0.8");
		board.receiveMessage("borderWidth  12");
		board.receiveMessage("fonttype Dialog");
		board.receiveMessage("fontsize 28");
		graphic.removeMenu("Brett");
		graphic.setTitle("Schach Spiel V0.8");
	}

	private void addMoveCounter() {
		// Abstand vor Label
		graphic.addSouthComponent(Box.createRigidArea(new Dimension(10, 0)));
		moveCounter.setBorder(BorderFactory.createEtchedBorder());
		graphic.addSouthComponent(moveCounter);
		
	}

	private void makeSpaceForValues() {
		for (int i = 0; i < 3; i++) {
			xsend.form2(i, 0, "none");
			xsend.form2(chessBoardSize - 3 + i, chessBoardSize - 1, "none");
		}

	}

	private void addButtons() {
		JButton zeigeButton = new JButton(SHOW_MOVES);
		zeigeButton.addActionListener(this);
		graphic.addSouthComponent(zeigeButton);

		JButton startButton = new JButton(MOVE);
		startButton.addActionListener(this);
		graphic.addSouthComponent(startButton);
		
	}

	void drawChessBoard() {
		xsend.formen("s");
		xsend.farben(XSendAdapter.LIGHTGRAY);
		for (int x = 0; x < chessBoardSize; x++) {
			for (int y = 0; y < chessBoardSize; y++) {
				if (chessBoard.isBorder(x, y)) {
					xsend.form2(x, y, "*");
				} else {
					xsend.farbe2(x, y, squareColors[(x + y) % 2]);
				}
			}
		}
	}

	private void drawCoordinates() {
		int N = chessBoardSize;
		for (int s = 2; s < N - 2; s++) {
			xsend.text2(s, 1, fileName(s));
			xsend.text2(1, s, rankName(s));
			xsend.form2(s, 1, "none");
			xsend.form2(1, s, "none");
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

		for (Piece piece : chessBoard.getPieces()) {
			xsend.text2(piece.getX(), piece.getY(), piece.getText());
		}
		moveCounter.setText(chessBoard.getNumMoves() + " Zuege");
		xsend.statusText("SpielerIn " + (1 + chessBoard.getNextToMove()) + " am Zug");
		xsend.text2(1, 0, "W: " + chessBoard.getTotalValue(ChessBoard.WHITE) + "\u2659");
		xsend.text2(chessBoardSize - 2, chessBoardSize - 1,
				"B: " + chessBoard.getTotalValue(ChessBoard.BLACK) + "\u265f");
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
		String cmd = e.getActionCommand();
		// System.out.println("cmd: " + cmd);
		if (cmd.equals(MOVE)) {
			chessController.randomMove();
		} else if (cmd.equals(SHOW_MOVES)) {
			Dialogs.showList(chessBoard.getAllMovesNiceText(), "Moves", graphic);
		}
	}

}
