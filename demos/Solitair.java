package guivorlesung;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import jserver.Board;
import jserver.BoardClickEvent;
import jserver.BoardClickListener;
import jserver.XSendAdapter;
import plotter.Graphic;

//****************************************************************
// Demo fuer GUI-Moeglichkeiten mit BoS - Spiel Solitair
// Version Wann      Wer                  Was
// 1.0     20.12.21  Gruppe PG2           Basisversion
// 1.1     20.12.22  SE                   Code-Vereinfachungen, Auswahlmoeglichkeit, Muster
//****************************************************************
public class Solitair implements BoardClickListener, ActionListener {
	private static final double SYMBOL_SIZE = 0.45;
	private static final double SYMBOL_SIZE_SMALL = 0.3;
	private static final int FREI = 1;
	private static final int STEIN = 0;
	private static final int BLOCK = 2;
	
	private Board board;
	private Graphic graphic;
	private XSendAdapter xsend;
	
	private int n = 9;
	private int[][] feld = new int[n + 4][n + 4];
	// die moeglichen Zuege sind im 2dim-Feld gespeichert mit x,y-Schritten
	// hier sind nur waagrechte und senkrechte Zuege moeglich 
	private int[][] zuege = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
	private int zugNummer;
	
	// Informationen falls mehrere Zuege moeglich sind und 2. Klick notwendig ist
	private boolean zielFeldEingeben;
	private int xStart;
	private int yStart;

	public static void main(String[] args) {
		Solitair s = new Solitair();
		s.start();
	}

	private void start() {
		setUpBoard();
		addButtons();

		allesSetzen();
		setzeMuster();
		zeichnen();
		
		xsend.statusText("Bitte auf einen Stein zum Ziehen klicken");
	}

	private void setUpBoard() {
		board = new Board();
		board.setSize(700, 600);
		board.addClickListener(this);
		graphic = board.getGraphic();
		graphic.setTitle("Solitair, DEMO PG2 WS20/21");
		xsend = new XSendAdapter(board);
		xsend.groesse(n, n);
		xsend.symbolGroessen(SYMBOL_SIZE);
		xsend.flaeche(XSendAdapter.LIGHTGREEN);
	}

	private void addButtons() {
		JButton button = new JButton("Neu");
		button.addActionListener(this);
		graphic.addEastComponent(button);
		
		button = new JButton("Zufall");
		button.addActionListener(this);
		graphic.addEastComponent(button);
	}

	private void allesSetzen() {
		for (int x = 0; x < feld.length; x++) {
			for (int y = 0; y < feld[x].length; y++) {
				feld[x][y] = STEIN;
			}
		}
	}

	private void setzeMuster() {
		feld[feld.length / 2][feld.length / 2] = FREI;
		int offset = 2 * n / 3;
		for (int x = 0; x < n / 3; x++) {
			for (int y = 0; y < n / 3; y++) {
				feld[2 + x][2 + y] = BLOCK;
				feld[2 + x + offset][2 + y] = BLOCK;
				feld[2 + x][2 + y + offset] = BLOCK;
				feld[2 + x + offset][2 + y + offset] = BLOCK;
			}
		}
	}

	private void setzeZufallsMuster() {
		xsend.formen("c");
		for (int x = 2; x < feld.length - 2; x++) {
			for (int y = 2; y < feld[x].length - 2; y++) {
				if (Math.random() > 0.5) {
					feld[x][y] = FREI;
				} else {
					feld[x][y] = STEIN;
				}
			}
		}
	}

	private void zeichnen() {
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				if (feld[x + 2][y + 2] == FREI) {
					xsend.farbe2(x, y, XSendAdapter.WHITE);
				} else if (feld[x + 2][y + 2] == STEIN) {
					xsend.farbe2(x, y, XSendAdapter.BLACK);
				} else {
					xsend.form2(x, y, "none");
				}
			}
		}

		int z = gesamtzahlMoeglicheZuege();
		xsend.statusText("Zug #" + ++zugNummer + " Moegliche Zuege: " + z);
		if (z == 0) {
			JOptionPane.showMessageDialog(graphic, "Kein Zug mehr moeglich", "Zuege", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private void versuche(int x, int y, int xr, int yr) {
		if (gehtZug(x, y, xr, yr)) {
			ziehe(x, y, xr, yr);
			zeichnen();
		}
	}

	private void ziehe(int x, int y, int xr, int yr) {
		feld[x][y] = FREI;
		feld[x + xr][y + yr] = FREI;
		feld[x + xr * 2][y + yr * 2] = STEIN;
	}

	private boolean gehtZug(int x, int y, int xr, int yr) {
		return feld[x][y] == STEIN && feld[x + xr][y + yr] == STEIN && feld[x + xr * 2][y + yr * 2] == FREI;
	}

	private int gesamtzahlMoeglicheZuege() {
		int anzahl = 0;
		for (int x = 2; x < feld.length - 2; x++) {
			for (int y = 2; y < feld[x].length - 2; y++) {
				anzahl += anzahlMoeglichkeiten(x, y);
			}
		}
		return anzahl;
	}

	private int anzahlMoeglichkeiten(int x, int y) {
		int anzahl = 0;
		for (int i = 0; i < zuege.length; i++) {
			if (gehtZug(x, y, zuege[i][0], zuege[i][1])) {
				++anzahl;
			}
		}
		return anzahl;
	}

	@Override
	public void boardClick(BoardClickEvent event) {
		System.out.println(event);
		int x = 2 + event.getX();
		int y = 2 + event.getY();
		if (zielFeldEingeben) {
			if (feld[x][y] != FREI) {
				xsend.statusText("Bitte auf freies Feld klicken");
				return;
			}
			for (int i = 0; i < zuege.length; i++) {
				if (xStart + zuege[i][0] * 2 == x && yStart + zuege[i][1] * 2 == y) {
					if (gehtZug(xStart, yStart, zuege[i][0], zuege[i][1])) {
						ziehe(xStart, yStart, zuege[i][0], zuege[i][1]);
						xsend.symbolGroesse2(xStart - 2, yStart - 2, SYMBOL_SIZE);
						zeichnen();
						zielFeldEingeben = false;
					}
					return;
				}
			}
	
		} else {
			int moeglich = anzahlMoeglichkeiten(x, y);
			if (moeglich == 1) {
				for (int i = 0; i < zuege.length; i++) {
					versuche(x, y, zuege[i][0], zuege[i][1]);
				}
			} else if (moeglich > 1) {
				xsend.statusText(moeglich + " Moeglichkeiten, bitte auf Ziel klicken");
				xsend.symbolGroesse2(x - 2, y - 2, SYMBOL_SIZE_SMALL);
				zielFeldEingeben = true;
				xStart = x;
				yStart = y;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		System.out.println(cmd);
		if (cmd.equals("Neu")) {
			allesSetzen();
			setzeMuster();
			zugNummer = 0;
			zeichnen();
		} else if (cmd.equals("Zufall")) {
			setzeZufallsMuster();
			zugNummer = 0;
			zeichnen();
		}
	}
}
