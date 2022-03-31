package traffic;

import javax.swing.JButton;
import javax.swing.JSlider;

import jserver.Board;
import jserver.XSendAdapter;
import plotter.Graphic;
import plotter.SliderFactory;

// Visualisierung der Karte und der Objekte
// Version 
// 1.0 Maerz 2022 SE: erste Version

public class TrafficGui {
	private Board board;
	private Graphic graphic;
	private XSendAdapter xsend;
	private Map map;
	private int streetColor = XSendAdapter.WHITE;
	private int borderColor = XSendAdapter.BLACK;
	private boolean pause = false;

	public boolean isPause() {
		return pause;
	}

	public TrafficGui(Map map) {
		board = new Board();
		graphic = board.getGraphic();
		xsend = new XSendAdapter(board);
		this.map = map;
		addButtons();

		drawMap();
	}

	private void addButtons() {
		JButton button = new JButton("Pause");
		button.addActionListener(e -> pause = !pause);
		graphic.addSouthComponent(button);
	}

	public void addSlider(TrafficSimulator trafficSimulator) {
		JSlider sleepSlider = SliderFactory.getSleepTimeSlider(trafficSimulator, 0, 200);
		board.getGraphic().addSouthComponent(sleepSlider);
		board.getGraphic().revalidate();
	}

	private void drawMap() {
		xsend.groesse(map.getWidth(), map.getHeight());
		xsend.statusText("Traffic Simulatior");
		xsend.formen("none");
		xsend.rahmen(borderColor);

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				drawCell(x, y);
			}
		}
	}

	private void drawCell(int x, int y) {
		if (map.get(x, y) == Map.STREET) {
			xsend.hintergrund2(x, y, streetColor);
		} else if (map.get(x, y) == Map.TREE) {
			xsend.form2(x, y, "c");
			xsend.farbe2(x, y, XSendAdapter.GREEN);
			xsend.symbolGroesse2(x, y, Math.random() / 2 + 0.2);
		} else if (map.get(x, y) == Map.WATER) {
			xsend.form2(x, y, "none");
			xsend.hintergrund2(x, y, XSendAdapter.BLUE);
		} else if (map.get(x, y) == Map.BORDER) {
			xsend.symbolGroesse2(x, y, 0.2);
			xsend.form2(x, y, "c");
			xsend.farbe2(x, y, borderColor);
		}
	}

	public void show(RCar rcar) {
		xsend.form2(rcar.getX(), rcar.getY(), rcar.getForm());
		xsend.farbe2(rcar.getX(), rcar.getY(), rcar.getColor());
	}

	public void remove(RCar rcar) {
		xsend.form2(rcar.getX(), rcar.getY(), "none");
	}

	public void show(UpCar upcar) {
		xsend.form2(upcar.getX(), upcar.getY(), upcar.getForm());
		xsend.farbe2(upcar.getX(), upcar.getY(), upcar.getColor());
	}

	public void remove(UpCar upcar) {
		xsend.form2(upcar.getX(), upcar.getY(), "none");
	}

	public void show(Dog dog) {
		xsend.form2(dog.getX(), dog.getY(), dog.getForm());
		xsend.farbe2(dog.getX(), dog.getY(), dog.getColor());
	}

	public void remove(Dog dog) {
		xsend.form2(dog.getX(), dog.getY(), "none");
	}


}
