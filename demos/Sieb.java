package simulationen;

import jserver.Board;
import jserver.Symbol;
import jserver.XSendAdapter;
import plotter.Graphic;
import plotter.Sleep;
import utils.InputUnitB;
import utils.InputUnitI;

public class Sieb {
	private XSendAdapter xsend = new XSendAdapter();
	private Board board = xsend.getBoard();
	private Graphic graphic = board.getGraphic();
	int max = 60;
	int schritte = 5;
	boolean[] istPrimzahl = new boolean[max];
	String siebText = "Sieb des Eratosthenes";
	private InputUnitB pauseInput = new InputUnitB(graphic, "Pause", true, "south");
	private InputUnitI sleepInput = new InputUnitI(graphic, "Wartezeit", 100, "south");

	public static void main(String[] args) {
		Sieb sieb = new Sieb();
		sieb.setup();
		sieb.start();

	}

	private void setup() {
		graphic.setTitle("Sieb, V1.0");
		xsend.groesse(max, schritte);
		xsend.statusText(siebText);
		board.receiveMessage("fonttype Yu Gothic UI Light");
		board.setSize(1200, 300);
		Symbol.setUseAlphaWithText(true);

		for (int i = 2; i < max; i++) {
			xsend.text2(i, schritte - 1, "" + i);
			xsend.farbe2(i, schritte - 1, XSendAdapter.LIGHTGREEN);
			istPrimzahl[i] = true;
		}
		for (int y = 0; y < schritte; y++) {
			xsend.form2(0, y, "F");
			xsend.form2(1, y, "none");
		}
	}

	private void start() {
		int naechstePrimzahl = 2;
		for (int y = 1; y < schritte; y++) {
			while (!istPrimzahl[naechstePrimzahl]) {
				++naechstePrimzahl;
			}
			xsend.text2(0, schritte - y, "" + naechstePrimzahl);
			xsend.statusText(siebText + ": streiche n * " + naechstePrimzahl);
			waitSomeTime(3);

			for (int i = 2 * naechstePrimzahl; i < max; i += naechstePrimzahl) {
				istPrimzahl[i] = false;
				xsend.farbe2(i, schritte - y, XSendAdapter.LIGHTCORAL);
				waitSomeTime(1);
			}

			for (int i = 2; i < max; i++) {
				if (istPrimzahl[i]) {
					if (i <= naechstePrimzahl) {
						xsend.farbe2(i, schritte - y - 1, XSendAdapter.GREEN);
					} else {
						xsend.farbe2(i, schritte - y - 1, XSendAdapter.LIGHTGREEN);

					}
					xsend.text2(i, schritte - y - 1, "" + i);
				} else {
					xsend.form2(i, schritte - y - 1, "none");
				}
				waitSomeTime(1);
			}
			++naechstePrimzahl;
		}
	}

	private void waitSomeTime(int faktor) {
		int sleepTime = sleepInput.getValue();
		do {
			Sleep.sleep(faktor * sleepTime);
		} while (pauseInput.getValue());
	}
}
