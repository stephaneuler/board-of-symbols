package tests;

import jserver.Board;
import jserver.Symbol;
import jserver.XSendAdapter;

public class UniCodeTest {

	/**
	 * Draw some symbols with Unicode supplementary characters
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int n = 10;
		Board board = new Board();
		XSendAdapter xs = new XSendAdapter(board);
		xs.groesse(n, 2);
		xs.formen("none");
		board.receiveMessage("fonttype Dialog");
		Symbol.setFontSize(48);

		// zeichne n Symbole ab dem Startwert
		// der Zahlenwert ist in diesem Fall zu gross fuer einen char
		// Character.toChars gibt einen Array mit 2 Werten zurueck,
		// aus dem dann ein String erzeugt wird
		int symbol = 0x1F4A4;
		int cards = 0x1F0A1;
		for (int i = 0; i < n; i++) {
			xs.text2(i, 0, new String(Character.toChars(symbol + i)));
			xs.text2(i, 1, new String(Character.toChars(cards + i)));
		}
	}

}
