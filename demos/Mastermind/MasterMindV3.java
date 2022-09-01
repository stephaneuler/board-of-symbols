package mastermind;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import jserver.Adapters;
import jserver.Board;
import jserver.ColorNames;
import jserver.XSendAdapter;

// Version 1.0: 25. Juli, SE

public class MasterMindV3 extends Adapters implements MouseListener, ActionListener {
	private XSendAdapter xsend;
	private GameBoard gameBoard = new GameBoard();
	private JPopupMenu popup = new JPopupMenu();
	private int symbolNumber;;

	public static void main(String[] args) {
		MasterMindV3 e = new MasterMindV3();
		e.starten();
	}

	void starten() {
		gameBoard.setUp();
		gameBoard.addTestButton();

		Board board = gameBoard.getBoard();
		board.getPlotter().addMouseListener(this);

		xsend = new XSendAdapter(board);
		xsend.statusText("Demo Farbauswahl Version 3 - popup Menu");

		buildPopup();

		gameBoard.start();
	}

	private void buildPopup() {
		for (int color : GameBoard.COLORS) {
			String text = ColorNames.getName(color);
			 addMenuItem(popup, text, "ctrl " + text.substring(0, 1));
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			Board board = gameBoard.getBoard();
			double wx = board.getPlotter().scaleXR(e.getX());
			symbolNumber = (int) Math.round(wx);
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	void addMenuItem(JPopupMenu p, String itemName, String keys) {
		JMenuItem menuItem = new JMenuItem(itemName);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keys));
		menuItem.addActionListener(this);
		p.add(menuItem);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		xsend.farbe2(symbolNumber, gameBoard.getCurrentRow(), ColorNames.getColor(cmd));
	}

}
