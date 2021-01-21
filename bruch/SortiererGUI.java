package bigBruch;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jserver.Board;
import jserver.BoardClickEvent;
import jserver.BoardClickListener;
import jserver.Dialogs;
import jserver.Symbol;
import jserver.XSendAdapter;
import plotter.Graphic;
import plotter.Plotter;
import plotter.Sleep;

enum SortierVerfahren {
	SELECTION, INSERTION, INSERTION_INTERVAL, BUBBLE, SHAKER, GNOME, SELECTION_M, QUICK, SHELL, BOZO
}

// Version wer wann   was
// 1.0     se  20-12  erste oeffentliche Version
// 1.1     se  21-01  Anzeige Anzahl Tauschoperationen, Klick auf Wert, ...


/**
 * This class is a BoSym based visualization of sorting algorithms.
 * 
 * @author Stephan Euler
 * @version 1.1 Januar 2021
 *
 */
public class SortiererGUI implements ActionListener, ChangeListener, BoardClickListener {
	static final String VERSION = "BoSym-Sortierer, V1.1 Jan. 21";
	static final int SLEEP_MIN = 0;
	static final int SLEEP_MAX = 500;
	static final int SLEEP_INIT = SLEEP_MAX / 2;

	private Bruch[] brueche; // Vorteil gegenüber double: Zaehler fuer Vergleiche
	private XSendAdapter xsend = new XSendAdapter();
	private Board board = xsend.getBoard();
	private Graphic graphic = board.getGraphic();
	boolean zufallModus = true;
	private int warteZeit = SLEEP_INIT;
	private int anzahlWerte = 30;
	private int anzahlTauschen = 0;
	private boolean pause = false;
	private boolean weiter = true;
	private boolean showInfo = true;
	private JButton fillButton = new JButton("Füllen");
	private JButton startButton = new JButton("Start");
	private JButton pauseButton = new JButton("Pause");
	private JButton resumeButton = new JButton("Weiter");
	private JButton abortButton = new JButton("Abbruch");
	private JButton mixButton = new JButton("Mischen");
	private JButton reverseButton = new JButton("Umkehren");
	private JLabel countLabel = new JLabel("# Vergleiche");
	private SortierVerfahren verfahren = SortierVerfahren.SELECTION;
	private JDialog dialog;
	private JTextField nField;
	private JRadioButton linearButton = new JRadioButton("Linear");
	private Random random = new Random();

	public static void main(String[] args) {
		SortiererGUI t = new SortiererGUI();
		t.start();
	}

	private void start() {
		anlegen();
		fuellen();
		anzeigen();
		Bruch.resetAnzahlVergleiche();
	}

	private void fuellen() {
		if (linearButton.isSelected()) {
			fuelleLinear(anzahlWerte);
		} else {
			fuelleZufall(anzahlWerte);
			// anzahlWerte = fuelleFeld(anzahlWerte); falls Farey Brüche gewünscht
		}
	}

	void selectionSort() {
		selectionSort( 0, brueche.length  );
	}

	void selectionSort(int start, int ende) {
		for (; ende > start + 1 & weiter; ende--) {
			int maxIndex = findeMax(start, ende);
			tausche(maxIndex, ende - 1);

			// Symbole anpassen
			xsend.farbe(maxIndex, XSendAdapter.BLUE);
			setzeGroesse(maxIndex, brueche[maxIndex]);
			xsend.farbe(ende - 1, XSendAdapter.LIGHTGREEN);
			setzeGroesse(ende - 1, brueche[ende - 1]);
		}
		xsend.farbe(start, XSendAdapter.LIGHTGREEN);
	}

	void selectionSortMiddle() {
		showInfo = false;
		xsend.statusText("Erste Hälfte");
		selectionSort(0, brueche.length / 2);

		xsend.statusText("Zweite Hälfte");
		selectionSort(brueche.length / 2, brueche.length);

		xsend.statusText("Mischen");
		xsend.farben(XSendAdapter.BLUE);
		int d = 0;
		for (int up = brueche.length / 2; up < brueche.length; up++) {
			aendere(up, XSendAdapter.RED);
			while (brueche[d].kleiner(brueche[up])) {
				aendere(d, XSendAdapter.LIGHTGREEN);
				++d;
			}
			Bruch tmp = brueche[up];
			aendere(d, XSendAdapter.LIGHTGREEN);
			for (int k = up; k > d; k--) {
				brueche[k] = brueche[k - 1];
				aendere(k, XSendAdapter.LIGHTGREEN);
				aendere(k - 1, XSendAdapter.LIGHTYELLOW);
				warten();
			}
			brueche[d] = tmp;
			aendere(d, XSendAdapter.LIGHTGREEN);

		}

	}

	void bubbleSort() {
		xsend.statusText("Bubble Sort");
		for (int n = brueche.length; n > 1 & weiter; n = n - 1) {
			boolean changed = false;
			for (int i = 0; i < n - 1 & weiter; i = i + 1) {
				aendere(i, XSendAdapter.YELLOW);
				// aendere(i+1, XSendAdapter.YELLOW);
				Sleep.sleep(warteZeit);
				xsend.statusText("Bubble Sort " + countText());
				if (brueche[i].groesser(brueche[i + 1])) {
					tausche(i, i + 1);
					// aendere(i, XSendAdapter.RED);
					aendere(i + 1, XSendAdapter.RED);
					Sleep.sleep(warteZeit);
					changed = true;
				}
				warten();
				aendere(i, XSendAdapter.BLUE);
				aendere(i + 1, XSendAdapter.BLUE);
			}
			if( ! changed ) {
				return;
			}
			aendere(n - 1, XSendAdapter.LIGHTGREEN);
		}
		aendere(0, XSendAdapter.LIGHTGREEN);
	}

	void shakerSort() {
		xsend.statusText("Shaker Sort");
		boolean austausch;
		int links = 1;
		int rechts = brueche.length - 1;
		int fertig = rechts;

		do {
			austausch = false;
			for (int ab = rechts; ab >= links & weiter; ab--) {
				if (brueche[ab - 1].groesser(brueche[ab])) {
					austausch = true;
					fertig = ab;
					tausche(ab, ab - 1);
					aendere(ab, XSendAdapter.RED);
					aendere(ab - 1, XSendAdapter.RED);
					warten();
				}
			}
			aendere(links - 1, XSendAdapter.LIGHTGREEN);
			links = fertig + 1;
			for (int auf = links; auf <= rechts & weiter; auf++) {
				if (brueche[auf - 1].groesser(brueche[auf])) {
					austausch = true;
					fertig = auf;
					tausche(auf, auf - 1);
					aendere(auf, XSendAdapter.RED);
					aendere(auf - 1, XSendAdapter.RED);
					warten();
				}
			}
			aendere(rechts, XSendAdapter.LIGHTGREEN);
			rechts = fertig - 1;
		} while (austausch & weiter);
	}

	void gnomeSort() {
		int pos = 0;
		xsend.statusText("Gnome Sort");
		while (pos < brueche.length & weiter) {
			xsend.statusText("Gnome Sort " + countText() );
			aendere(pos, XSendAdapter.YELLOW);
			Sleep.sleep(warteZeit);
			warten();
			aendere(pos, XSendAdapter.BLUE);

			if (pos == 0 || brueche[pos].groesser(brueche[pos - 1]) || brueche[pos].gleich(brueche[pos - 1])) {
				pos = pos + 1;
			} else {
				tausche(pos, pos - 1);
				aendere(pos, XSendAdapter.BLUE);
				aendere(pos - 1, XSendAdapter.BLUE);
				pos = pos - 1;
				// Sleep.sleep(warteZeit);
			}
		}
	}

	void insertionSort() {
		for (int i = brueche.length - 2; i >= 0 & weiter; i--) {
			xsend.farbe(i, XSendAdapter.RED);
			Bruch tmp = brueche[i];
			String info = String.format("%d. %s = %.3f", i, brueche[i].toString(), brueche[i].toDouble());
			info = countText() + " " + info;
			xsend.statusText(info);
			warten();

			int ziel = brueche.length - 1;
			for (int j = i + 1; j < brueche.length & weiter; j++) {
				aendere(j, XSendAdapter.YELLOW);
				warten();
				// System.out.println( tmp.toDouble() + " < " +
				// brueche[j].toDouble() + " = " + tmp.kleiner(brueche[j]));
				// suche ersten größeren Wert
				warten();
				if (tmp.kleiner(brueche[j])) {
					ziel = j - 1;
					break;
				}
				aendere(j, XSendAdapter.LIGHTGREEN);
			}
			xsend.statusText("Verschieben " + ziel);
			aendere(i, XSendAdapter.LIGHTGREEN);
			for (int k = i; k < ziel; k++) {
				brueche[k] = brueche[k + 1];
				++anzahlTauschen;
				aendere(k, XSendAdapter.LIGHTGREEN);
				aendere(k + 1, XSendAdapter.LIGHTYELLOW);
				warten();
			}
			brueche[ziel] = tmp;
			aendere(ziel, XSendAdapter.LIGHTGREEN);
			if (ziel != brueche.length - 1) {
				aendere(ziel + 1, XSendAdapter.LIGHTGREEN);

			}
		}
	}

	void insertionSortInterval() {
		for (int i = brueche.length - 2; i >= 0 & weiter; i--) {
			xsend.farbe(i, XSendAdapter.RED);
			Bruch tmp = brueche[i];
			String info = String.format("%d. %s = %.3f", i, brueche[i].toString(), brueche[i].toDouble());
			info = countText() + " " + info;
			xsend.statusText(info);
			warten();

			int ziel = positionBinarySearch(brueche[i], brueche, i + 1);

			xsend.statusText("Verschieben " + ziel);
			aendere(i, XSendAdapter.LIGHTGREEN);
			for (int k = i; k < ziel; k++) {
				brueche[k] = brueche[k + 1];
				++anzahlTauschen;
				aendere(k, XSendAdapter.LIGHTGREEN);
				aendere(k + 1, XSendAdapter.LIGHTYELLOW);
				warten();
			}
			brueche[ziel] = tmp;
			aendere(ziel, XSendAdapter.LIGHTGREEN);
			if (ziel != brueche.length - 1) {
				aendere(ziel + 1, XSendAdapter.LIGHTGREEN);

			}
		}
	}

	int positionBinarySearch(Bruch wert, Bruch[] feld, int start) {
		int lower = start, upper = feld.length - 1;

		if (wert.kleiner(feld[start])) {
			return start - 1;
		}

		for (int i = start; i < feld.length; i++) {
			xsend.farbe(i, XSendAdapter.YELLOW);
		}
		warten();

		while (upper - lower > 1) {
			System.out.println("Interval: " + lower + " -- " + upper);
			int m = (upper + lower) / 2; /* Mitte bestimmen */
			if (wert == feld[m])
				return m;
			else if (wert.groesser(feld[m]))
				lower = m;
			else
				upper = m;

			for (int i = start; i < feld.length; i++) {
				if (i < lower | i > upper) {
					xsend.farbe(i, XSendAdapter.LIGHTGREEN);
				} else {
					xsend.farbe(i, XSendAdapter.YELLOW);

				}
			}
			warten();
		}
		if (wert.groesser(feld[upper]))
			return upper;
		else
			return lower;
	}

	void shellSort() {
		int i, j, ink;
		Bruch v;

		xsend.statusText("Shell Sort");
		// showNextStep(true);
		for (ink = 1; ink <= brueche.length / 9; ink = 3 * ink + 1)
			;

		for (; ink > 0 & weiter; ink /= 3) {
			xsend.statusText("ShellSort, Schrittweite: " + ink);
			for (i = ink + 1; i <= brueche.length & weiter; i++) {
				aendere(i - 1, XSendAdapter.YELLOW);
				v = brueche[i - 1];
				j = i - 1;
				/* Verschieben (Platz machen) */
				while (j >= ink && brueche[j - ink].groesser(v) & weiter) {
					brueche[j] = brueche[j - ink];
					aendere(j, XSendAdapter.LIGHTGREEN);
					aendere(j - ink, XSendAdapter.LIGHTYELLOW);
					warten();
					aendere(j, XSendAdapter.BLUE);
					aendere(j - ink, XSendAdapter.BLUE);
					j -= ink;
				}
				brueche[j] = v; // Einfügen
				aendere(j, XSendAdapter.LIGHTGREEN);
				warten();
				aendere(j, XSendAdapter.BLUE);
			}
		}

	}

	void bozoSort() {
		xsend.statusText("Bozo Sort: wähle zwei zufällige Werte und tausche falls notwendig");
		while (weiter & !isSorted(brueche)) {
			int p1, p2;
			p1 = random.nextInt(brueche.length);
			do {
				p2 = random.nextInt(brueche.length);
			} while (p1 == p2);
			if (p1 > p2) {
				int tmp = p1;
				p1 = p2;
				p2 = tmp;
			}
			aendere(p1, XSendAdapter.YELLOW);
			aendere(p2, XSendAdapter.YELLOW);
			Sleep.sleep(warteZeit);
			if (brueche[p1].groesser(brueche[p2])) {
				tausche(p1, p2);
			}
			aendere(p1, XSendAdapter.BLUE);
			aendere(p2, XSendAdapter.BLUE);
			Sleep.sleep(warteZeit);
			warten();

		}

	}

	private boolean isSorted(Bruch[] f) {
		for (int i = 0; i < f.length - 1; i++) {
			if (f[i].groesser(f[i + 1])) {
				return false;
			}
		}
		return true;
	}

	void quickSort(int links, int rechts) {
		xsend.statusText("Quick Sort "  + countText() );
		if (links >= rechts) {
			if (links < brueche.length) {
				aendere(links, XSendAdapter.LIGHTGREEN);
			}
			return;
		}

		if (!weiter) {
			return;
		}

		int i = aufteilen(links, rechts);
		quickSort(links, i - 1);
		quickSort(i + 1, rechts);
		xsend.statusText("Fertig " + countText());

	}

	private int aufteilen(int links, int rechts) {
		int p = (links + rechts) / 2;
		aendere(p, XSendAdapter.RED);
		warten();
		warten();

		tausche(p, rechts);
		aendere(p, XSendAdapter.BLUE);
		aendere(rechts, XSendAdapter.RED);
		warten();
		warten();

		int il = links;
		int ir = rechts - 1;
		while (il <= ir) {
			aendere(il, XSendAdapter.YELLOW);
			warten();
			aendere(il, XSendAdapter.BLUE);
			if (brueche[il].groesser(brueche[rechts])) {
				tausche(il, ir);
				xsend.statusText("Tausche " + il + " mit " + ir);
				aendere(ir, XSendAdapter.YELLOW);
				ir--;
			} else
				il++;
		}
		tausche(il, rechts);
		xsend.statusText("Tausche " + il + " mit " + rechts);
		aendere(il, XSendAdapter.LIGHTGREEN);
		for (int i = il + 1; i <= rechts; i++) {
			aendere(i, XSendAdapter.BLUE);
		}
		warten();

		return il;
	}

	private void warten(int time) {
		do {
			Sleep.sleep(time);
		} while (pause);
	}

	private void warten() {
		warten(warteZeit);
	}

	private void aendere(int i, int farbe) {
		xsend.farbe(i, farbe);
		setzeGroesse(i, brueche[i]);
	}

	private void tausche(int i, int j) {
		++anzahlTauschen;
		Bruch tmp = brueche[i];
		brueche[i] = brueche[j];
		brueche[j] = tmp;
	}

	int findeMax(int ende) {
		return findeMax(0, ende);
	}

	int findeMax(int maxIndex, int ende) {
		xsend.farbe(maxIndex, XSendAdapter.RED);
		for (int i = maxIndex + 1; i < ende; i++) {
			xsend.farbe(i, XSendAdapter.YELLOW);
			String info = String.format("%d. %s = %.3f", i, brueche[i].toString(), brueche[i].toDouble());
			info += " " + countText();
			if (showInfo) {
				xsend.statusText(info);
			}
			warten();
			if (brueche[i].groesser(brueche[maxIndex])) {
				xsend.farbe(i, XSendAdapter.RED);
				xsend.farbe(maxIndex, XSendAdapter.BLUE);
				maxIndex = i;
			} else {
				xsend.farbe(i, XSendAdapter.BLUE);
			}
		}
		return maxIndex;
	}

	private String countText() {
		return Bruch.getAnzahlVergleiche() + " Vergleiche, " + anzahlTauschen + " Vertauschungen";
	}

	private void fuelleZufall(int N) {
		brueche = new Bruch[N];
		for (int i = 0; i < brueche.length; i++) {
			brueche[i] = Bruch.zufallsBruch01(500);
			System.out.println(brueche[i]);
		}
	}

	private void fuelleLinear(int N) {
		brueche = new Bruch[N];
		for (int i = 0; i < N; i++) {
			brueche[i] = new Bruch(i, N);
		}

	}

	int fuelleFeld(int NF) {
		brueche = new Bruch[alleBrueche(NF, false)];
		return alleBrueche(NF, true);
	}

	private int alleBrueche(int NF, boolean speichern) {
		int anz = 0;
		for (int n = 1; n <= NF; n++) {
			for (int z = 0; z <= n; z++) {
				if (Bruch.euclid(z, n) == 1) {
					if (speichern) {
						brueche[anz] = new Bruch(z, n);
					}
					++anz;
				}
			}
		}
		return anz;
	}

	private void setzeGroesse(int i, Bruch bruch) {
		xsend.symbolGroesse(i, 0.03 + 0.5 * bruch.toDouble());
	}

	void anlegen() {
		board.setSize(900, 260);
		board.receiveMessage("statusfontsize 16");
		board.addClickListener(this);
		Symbol.setFontSize(24);
		Symbol.setBarWidth(0.3);
		xsend.formen("b");

		graphic.setTitle(VERSION);

		addButtons();
		addSlider();
		addAlgoButtons();
	
		Plotter plotter = graphic.getPlotter();
		plotter.setYrange(-.55, .6);
		xsend.statusText("BoSym Sortierer");
	}

	private void addButtons() {
		fillButton.addActionListener(this);
		mixButton.addActionListener(this);
		reverseButton.addActionListener(this);
		startButton.addActionListener(this);
		pauseButton.addActionListener(this);
		resumeButton.addActionListener(this);
		resumeButton.setEnabled(false);
		abortButton.addActionListener(this);

		graphic.addBottomComponent(fillButton);
		graphic.addBottomComponent(mixButton);
		graphic.addBottomComponent(reverseButton);
 		graphic.addBottomComponent(Box.createHorizontalStrut(16));

		graphic.addBottomComponent(startButton);
		graphic.addBottomComponent(pauseButton);
		graphic.addBottomComponent(resumeButton);
		graphic.addBottomComponent(abortButton);
		graphic.addBottomComponent(Box.createHorizontalGlue());		
	}

	private void addAlgoButtons() {
		List<JRadioButton> sortButtons = new ArrayList<>();
		for (SortierVerfahren type : SortierVerfahren.values()) {
			sortButtons.add(new JRadioButton(type.toString()));
		}

		sortButtons.get(0).setSelected(true);

		ButtonGroup group = new ButtonGroup();
		for (JRadioButton button : sortButtons) {
			button.addActionListener(this);
			group.add(button);
			graphic.addEastComponent(button);
		}
	}

	private void addSlider() {
		JSlider sleepTimeSlider = new JSlider(JSlider.HORIZONTAL, SLEEP_MIN, SLEEP_MAX, SLEEP_INIT);
		sleepTimeSlider.setMajorTickSpacing(100);
		sleepTimeSlider.setMinorTickSpacing(10);
		sleepTimeSlider.setPaintTicks(true);
		sleepTimeSlider.setPaintLabels(true);
		sleepTimeSlider.addChangeListener(this);

		graphic.addBottomComponent(sleepTimeSlider);
	}

	void anzeigen() {
		xsend.groesse(brueche.length, 1);
		if (brueche.length > 100) {
			xsend.formen("b");
		}

		for (int i = 0; i < brueche.length; i++) {
			xsend.farbe(i, XSendAdapter.BLUE);
			setzeGroesse(i, brueche[i]);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		System.out.println("Board cmd: " + cmd);

		if (cmd.equals("Start")) {
			weiter = true;
			System.out.println("Verfahren: " + verfahren);
			new Thread() {
				public void run() {
					fillButton.setEnabled(false);
					startButton.setEnabled(false);
					mixButton.setEnabled(false);
					reverseButton.setEnabled(false);
					Bruch.resetAnzahlVergleiche();
					anzahlTauschen = 0;
					countLabel.setText( countText() );
					showInfo = true;
					switch (verfahren) {
					case SELECTION:
						selectionSort();
						break;
					case SELECTION_M:
						selectionSortMiddle();
						break;
					case INSERTION:
						insertionSort();
						break;
					case INSERTION_INTERVAL:
						insertionSortInterval();
						break;
					case QUICK:
						quickSort(0, brueche.length - 1);
						break;
					case BUBBLE:
						bubbleSort();
						break;
					case SHAKER:
						shakerSort();
						break;
					case SHELL:
						shellSort();
						break;
					case BOZO:
						bozoSort();
						break;
					case GNOME:
						gnomeSort();
						break;
					default:
						break;
					}
					xsend.statusText("Fertig " + countText());
					fillButton.setEnabled(true);
					startButton.setEnabled(true);
					mixButton.setEnabled(true);
					reverseButton.setEnabled(true);
				}
			}.start();
		} else if (cmd.equals("Pause")) {
			pause = true;
			pauseButton.setEnabled(false);
			resumeButton.setEnabled(true);

		} else if (cmd.equals("Abbruch")) {
			weiter = false;

		} else if (cmd.equals("Weiter")) {
			pause = false;
			pauseButton.setEnabled(true);
			resumeButton.setEnabled(false);

		} else if (cmd.equals("Mischen")) {
			mischen();

		} else if (cmd.equals(reverseButton.getActionCommand())) {
			Collections.reverse(Arrays.asList(brueche));
			alleAktualisieren();

		} else if (cmd.equals("Füllen")) {
			frageUndFuelle();

		} else if (cmd.equals("Fertig")) {
			anzahlWerte = Integer.parseInt(nField.getText());

			dialog.dispose();
			fuellen();
			anzeigen();

		} else if (SortierVerfahren.valueOf(cmd) != null) {
			verfahren = SortierVerfahren.valueOf(cmd);

		} else {
			System.out.println("Unbekannter Befehl: " + cmd);
		}
	}

	private void frageUndFuelle() {
		dialog = new JDialog();
		dialog.setTitle("Füllen");
		dialog.setModal(true);
		BorderLayout lm = new BorderLayout();
		lm.setHgap(4);
		dialog.setLayout(lm);

		Box centerBox = new Box(BoxLayout.X_AXIS);

		nField = new JTextField("" + anzahlWerte);
		nField.setPreferredSize(new Dimension(20, 10));

		JButton doneButton = new JButton("Fertig");
		doneButton.addActionListener(this);

		centerBox.add(new JLabel("Anzahl: "));
		centerBox.add(nField);
		centerBox.add(linearButton);
		centerBox.add(Box.createHorizontalGlue());
		centerBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		Box southBox = new Box(BoxLayout.X_AXIS);
		southBox.add(Box.createHorizontalGlue());
		southBox.add(doneButton);
		southBox.add(Box.createHorizontalGlue());

		dialog.add(centerBox);
		dialog.add(southBox, BorderLayout.SOUTH);
		dialog.add(new JLabel("Neue Werte"), BorderLayout.NORTH);

		dialog.setSize(300, 120);
		dialog.setVisible(true);
	}

	private void mischen() {
		for (int i = 0; i < brueche.length; i++) {
			tausche(i, random.nextInt(brueche.length));
		}
		alleAktualisieren();
	}

	private void alleAktualisieren() {
		for (int i = 0; i < brueche.length; i++) {
			xsend.farbe(i, XSendAdapter.BLUE);
			setzeGroesse(i, brueche[i]);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			warteZeit = (int) source.getValue();
		}
	}

	@Override
	public void boardClick(BoardClickEvent info) {
		System.out.println( info );
		int index = info.getX();
		if( info.getButton() == 1  ) {
			xsend.statusText( index + ". Wert: " + brueche[index]  );
		} else {
			String s = Dialogs.askString("Neuer Wert fuer "+ brueche[index] );
			if( s != null && s.matches("[0-9]+/[0-9]+" )) {
				String[] parts = s.split("/");
				int zaehler = Integer.parseInt(parts[0]);
				int nenner  = Integer.parseInt(parts[1]);
				brueche[index]  = new Bruch( zaehler, nenner);
				alleAktualisieren();
				xsend.statusText( index + ". Wert neu: " + brueche[index]  );
			} else {
				xsend.statusText( "Bitte Format n/z nutzen"  );
			}
		}
		
	}
}
