package bosdemos;

import java.util.LinkedList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.Timer;

import jserver.Board;
import jserver.XSendAdapter;
import plotter.Graphic;

import utils.InputUnitB;
import utils.InputUnitD;
import utils.InputUnitI;

public class QueueDemo {
	private int queueSize = 10;
	private int limit = 100;
	
	private Board board = new Board();
	private XSendAdapter xsend = new XSendAdapter(board);
	private Graphic graphic = board.getGraphic();

	private Random random = new Random();
	private int next = random.nextInt(limit);
	private LinkedList<Integer> queue = new LinkedList<>();
	private JButton addButton = new JButton("add");
	private JButton getButton = new JButton("get");
	private InputUnitI limitInput;
	private InputUnitB zufallAdd = new InputUnitB(graphic, "ZufallAdd", false, "south");
	private InputUnitD addProb = new InputUnitD(graphic, "prob", 0.5, "south");
	private InputUnitB zufallGet = new InputUnitB(graphic, "ZufallGet", false, "south");
	private InputUnitD getProb = new InputUnitD(graphic, "prob", 0.5, "south");
	private int stepCount = 0;

	public static void main(String[] args) {
		QueueDemo qd = new QueueDemo();
		qd.setUp();

	}

	private void setUp() {
		setUpBoard();
		addInputs();
		startTimer();
	}

	private void startTimer() {
		int delay = 500;
		Timer timer = new Timer(delay, e -> iterate());
		timer.start();
	}

	private void setUpBoard() {
		board.setSize(900, 260);
		xsend.groesse(queueSize + 4, 1);
		board.receiveMessage("fontsize 32");
		xsend.formen("F");
		xsend.form(1, "none");
		xsend.form(queueSize + 2, "none");
		xsend.text(1, "->");
		xsend.text(queueSize + 2, "->");
		xsend.text(0, "" + next);
	}

	private void addInputs() {
		addButton.addActionListener(e -> add());
		board.getGraphic().addSouthComponent(addButton);

		getButton.setEnabled(false);
		getButton.addActionListener(e -> get());
		board.getGraphic().addSouthComponent(getButton);

		limitInput = new InputUnitI(graphic, "LIMIT", limit, "south");
		limitInput.setInputSize(120, 20);
		addProb.setInputSize(50, 20);
		getProb.setInputSize(50, 20);
		
		graphic.repaint();
	}

	private void iterate() {
		++stepCount;
		if (stepCount % 2 == 0) {
			double prob = addProb.getOptValue().orElse(0.5);
			if (zufallAdd.getValue() & Math.random() < prob & queue.size() < queueSize) {
				add();
			}
		} else {
			double prob = getProb.getOptValue().orElse(0.5);
			if (zufallGet.getValue() & Math.random() < prob & queue.size() > 0) {
				get();
			}
		}
	}

	private void get() {
		Integer w = queue.pollLast();
		xsend.text(queueSize + 3, "" + w);
		xsend.text(2 + queue.size(), "");
		xsend.hintergrund(2 + queue.size(), 0xeeeeee);
		addButton.setEnabled(true);
		if (queue.size() == 0) {
			getButton.setEnabled(false);
		}
		xsend.statusText("Wert entnommen, in Warteschlange: " + queue.size());
	}

	private void add() {
		queue.add(0, next);
		next = random.nextInt(limitInput.getOptValue().orElse(limit));
		xsend.text(0, "" + next);
		showQueue();
		if (queue.size() == queueSize) {
			addButton.setEnabled(false);
		}
		getButton.setEnabled(true);
		xsend.statusText("Wert eingfügt, in Warteschlange: " + queue.size());
	}

	private void showQueue() {
		for (int i = 0; i < queue.size(); i++) {
			xsend.text(i + 2, "" + queue.get(i));
			xsend.hintergrund(i + 2, XSendAdapter.GREENYELLOW);
		}

	}
}
