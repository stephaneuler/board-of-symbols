package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jserver.Board;
import jserver.XSendAdapter;
import plotter.Graphic;
import plotter.Sleep;

public class Walker implements ActionListener, ChangeListener {
	static final String VERSION = "Walker V0.1 Dez 16";
	static final int SLEEP_MIN = 30;
	static final int SLEEP_MAX = 300;
	static final int SLEEP_INIT = SLEEP_MAX / 2;

	private Board board;
	private Graphic graphic;
	private XSendAdapter xsend;
	private int N = 20;
	private int s = N / 2;
	private int z = N / 2;
	private int farbe = XSendAdapter.BROWN;
	private int besucht = XSendAdapter.LIGHTGREEN;
	private Random random = new Random();
	private boolean pause = false;
	private int sleepTime = SLEEP_INIT;
	private JButton startKnopf = new JButton("Start");

	public static void main(String[] args) {
		Walker e = new Walker();
		e.starten();
	}

	void starten() {
		board = new Board();
		graphic = board.getGraphic();
		graphic.setTitle(VERSION);
		board.setSize(500, 500);

		xsend = new XSendAdapter(board);
		xsend.groesse(N, N);

		// Standardfarbe
		xsend.farben(XSendAdapter.LIGHTGRAY);
		xsend.farbe2(s, z, farbe);

		startKnopf.addActionListener(this);
		JButton lpauseKnopf = new JButton("Pause");
		lpauseKnopf.addActionListener(this);

		graphic.addBottomComponent(startKnopf);
		graphic.addSouthComponent(lpauseKnopf);

		JSlider sleepTimeSlider = new JSlider(JSlider.VERTICAL, SLEEP_MIN, SLEEP_MAX, SLEEP_INIT);
		sleepTimeSlider.setMajorTickSpacing(100);
		sleepTimeSlider.setMinorTickSpacing(10);
		sleepTimeSlider.setPaintTicks(true);
		sleepTimeSlider.setPaintLabels(true);
		sleepTimeSlider.addChangeListener(this);

		graphic.addEastComponent(sleepTimeSlider);

		graphic.pack();
		graphic.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		System.out.println(cmd);
		if (cmd.equals("Start")) {
			startKnopf.setEnabled(false);
			new Thread() {
				public void run() {
					for (int n = 1;; ++n) {
						if (!pause) {
							xsend.farbe2(s, z, besucht);
							s = bewege(s);
							z = bewege(z);
							xsend.farbe2(s, z, farbe);
							xsend.statusText("Schritt " + n);
						}
						Sleep.sleep(sleepTime);
					}
				}
			}.start();

		} else if (cmd.equals("Pause")) {
			pause = !pause;
		}

	}

	private int bewege(int coord) {
		coord += random.nextInt(3) - 1;
		if (coord < 0)
			return 0;
		if (coord >= N)
			return N - 1;
		return coord;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			sleepTime = (int) source.getValue();
			System.out.println("SleepTime_: " + sleepTime);
		}

	}
}
