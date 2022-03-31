package traffic;

import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jserver.XSendAdapter;

// Simulation einer Kreuzung mit umgebender Landschaft
// In der ersten Version bewegen sich Autos und Hunde. 
// Bei Autos gibt es zur Vereinfachung zwei getrennte Typen fuer waagrechte und senkrechte Bewegung.
// Die Objekte werden in einzelnen Arrays gespeichert. 
// In einem Schritt wird jedes Objekt zunaechst entfernt, dann bewegt und anschliessend wieder gezeichnet. 

// Version 
// 1.0 Maerz 2022 SE: erste Version

public class TrafficSimulator implements ChangeListener {
	private Map map = new Map(80, 80);
	private TrafficGui gui;
	private RCar[] rcars = { new RCar(), new RCar() };
	private UpCar[] upcars = { new UpCar(), new UpCar() };
	private Dog[] dogs = { new Dog(12, 12) };
	private Timer timer;

	public static void main(String[] args) {
		new TrafficSimulator().start();

	}

	private void start() {
		map.fill();
		setUpObjects();
		setUpBoard();
		timer = new Timer(30, e -> iterate());
		timer.start();
	}

	private void setUpBoard() {
		gui = new TrafficGui(map);
		gui.addSlider(this);
		for (RCar rcar : rcars) {
			gui.show(rcar);
		}
	}

	private void setUpObjects() {
		rcars[0].setLane(2, map.getHeight() / 2);
		rcars[0].setSpeed(1);
		rcars[1].setLane(1, map.getHeight() / 2);
		rcars[1].setSpeed(1.4);
		rcars[1].setColor(XSendAdapter.RED);

		upcars[0].setLane(2, map.getHeight() / 2);
		upcars[0].setSpeed(1);
		upcars[1].setLane(1, map.getHeight() / 2);
		upcars[1].setSpeed(1.3);
		upcars[1].setColor(XSendAdapter.ORANGE);
	}

	private void iterate() {
		if (gui.isPause()) {
			return;
		}
		for (RCar car : rcars) {
			gui.remove(car);
			car.move(map);
			gui.show(car);
		}
		for (UpCar car : upcars) {
			gui.remove(car);
			car.move(map);
			gui.show(car);
		}
		for (Dog dog : dogs) {
			gui.remove(dog);
			dog.move(map);
			gui.show(dog);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			timer.setDelay((int) source.getValue());
		}

	}

}
