package mastermind;

import java.awt.Color;

import jserver.Adapters;
import jserver.Board;
import jserver.BoardClickEvent;
import jserver.BoardClickListener;
import jserver.Position;
import jserver.Symbol;
import jserver.SymbolType;
import jserver.XSendAdapter;

// Version 1.0: 25. Juli, SE

public class MasterMindV2 implements BoardClickListener {
	private XSendAdapter xsend;
	private GameBoard gameBoard = new GameBoard();

	public static void main(String[] args) {
		MasterMindV2 e = new MasterMindV2();
		e.starten();
	}

	void starten() {
		gameBoard.setUp();

		Board board = gameBoard.getBoard();
		board.addClickListener(this);

		xsend = new XSendAdapter(board);
		xsend.statusText("Demo Farbauswahl Version 2 More symbols");

		gameBoard.addTestButton();
		buildColorSelectors();

		gameBoard.start();
	}

	private void buildColorSelectors() {
		Board board = gameBoard.getBoard();
		double selectorWidth = .8;
		double diff = selectorWidth / GameBoard.getNumColors();
		for (int i = 0; i < gameBoard.getRowSize(); i++) {
			double x = i - GameBoard.getNumColors() * diff / 2;
			for (int color : GameBoard.COLORS) {
				Position pos = new Position(x, -selectorWidth);
				x += diff;
				Symbol s = new Symbol(pos, 0.9 * diff / 2);
				s.setFarbe(new Color(color));
				s.setType(SymbolType.SQUARE);
				board.addSymbol(s);
			}
		}
		board.redrawSymbols();
	}

	@Override
	public void boardClick(BoardClickEvent info) {
		double wx = info.getWx();
		double wy = info.getWy();
		int symbolNumber = (int) Math.round(wx);
		Symbol s = gameBoard.getBoard().getNearestSymbol(new Position(wx, wy));
		xsend.farbe2(symbolNumber, gameBoard.getCurrentRow(), s.getFarbe().getRGB());

	}

}
