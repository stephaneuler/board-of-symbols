package simulationen;

import java.util.Random;

import javax.swing.JButton;

import jserver.Board;
import jserver.XSendAdapter;
import plotter.Graphic;
import plotter.Sleep;
import utils.InputUnitB;
import utils.InputUnitD;
import utils.InputUnitI;
import utils.InputUnitS;

// Simulation of an Elementary cellular automaton
// https://en.wikipedia.org/wiki/Elementary_cellular_automaton
// SE Feb 2022

public class Cellular {
	private static final double SYMBOL_GROESSE = 0.45;
	private static final int SLEEP_TIME = 75;

	private int breite = 100;
	private int hoehe = breite / 2;
	private int[] muster = { 0, 1, 1, 1, 1, 0, 0, 0 };
	private Random random = new Random();

	private XSendAdapter xsend = new XSendAdapter();
	private Board board = xsend.getBoard();
	private Graphic graphic = board.getGraphic();

	private InputUnitI regelInput = new InputUnitI(graphic, "Regel", 30, "south");
	private InputUnitI sleepInput = new InputUnitI(graphic, "Wartezeit", SLEEP_TIME, "east");
	private InputUnitI farbInput = new InputUnitI(graphic, "Farbe", XSendAdapter.DARKGRAY, "south");
	private InputUnitD groesseInput = new InputUnitD(graphic, "Symbolgroesse", SYMBOL_GROESSE, "south");
	private InputUnitS formInput = new InputUnitS(graphic, "Form", "s", "south");
	private InputUnitB zufallInput = new InputUnitB(graphic, "ZufallStart", false, "south");

	public static void main(String[] args) {
		Cellular game = new Cellular();
		game.setUp();
	}

	private void setUp() {
		xsend.groesse(breite + 2, hoehe);
		board.setSize(1200, 600);
		xsend.formen("s");
		// xsend.flaeche(XSendAdapter.BLACK);
		xsend.farben(XSendAdapter.WHITE);
		xsend.symbolGroessen(SYMBOL_GROESSE);
		xsend.statusText("Elementary cellular automaton");

		graphic.setTitle("Cellular, V1.0");

		JButton startButton = new JButton("Zeichnen");
		startButton.addActionListener(e -> zeichnen());
		graphic.addEastComponent(startButton);

		sleepInput.setInputSize( 120, 20);

	}

	private void zeichnen() {
		if (regelInput.hasValue()) {
			erzeugeMuster();
			xsend.farben(XSendAdapter.WHITE);
			xsend.formen("s");
			xsend.symbolGroessen(groesseInput.getOptValue().orElse(SYMBOL_GROESSE));

			new Thread() {
				public void run() {
					int sleepTime = sleepInput.getOptValue().orElse(SLEEP_TIME);
					int[] reihe = ersteReihe();
					for (int y = 0; y < hoehe; y++) {
						Sleep.sleep(sleepTime);
						zeichneReihe(reihe, y);
						reihe = naechsteReihe(reihe);
					}
				}
			}.start();
		} else {
			xsend.statusText("Bitte gueltigen Regelwert eintragen");
		}
	}

	private int[] ersteReihe() {
		int[] reihe = new int[breite + 2];
		if (zufallInput.getValue()) {
			for (int x = 1; x < reihe.length - 1; x++) {
				reihe[x] = random.nextInt(2);
			}
		} else {
			reihe[breite / 2] = 1;
		}
		return reihe;
	}

	private void erzeugeMuster() {
		int regelWert = regelInput.getValue();
		for (int i = 0; i < 8; i++) {
			muster[i] = regelWert % 2;
			regelWert = regelWert / 2;
		}
	}

	private int[] naechsteReihe(int[] reihe) {
		int[] naechste = new int[reihe.length];
		for (int i = 1; i < reihe.length - 1; i++) {
			naechste[i] = wert(reihe[i - 1], reihe[i], reihe[i + 1]);
		}
		return naechste;
	}

	private int wert(int i, int j, int k) {
		int index = i * 4 + j * 2 + k;
		return muster[index];
	}

	private void zeichneReihe(int[] reihe, int y) {
		for (int x = 0; x < reihe.length; x++) {
			if (reihe[x] == 1) {
				xsend.farbe2(x, y, farbInput.getValue() );
				xsend.form2(x, y, formInput.getValue());
			}
		}
	}

}
