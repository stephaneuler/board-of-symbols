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

// Die Klasse zeigt eine Stack. Man kann per Knopfdruck Werte einfuegen und entnehmen.
// Alternativ kann man dies automatisch mit waehlbaren Wahrscheinlichkeiten ablaufen lassen. 
// Version 1.0, Juni 2022 Stephan Euler

public class StackDemo {
	private int stackSize = 10;
	private int limit = 100;
	
	private Board board = new Board();
	private XSendAdapter xsend = new XSendAdapter(board);
	private Graphic graphic = board.getGraphic();

	private Random random = new Random();
	private int next = random.nextInt(limit);
	private LinkedList<Integer> stack = new LinkedList<>();
	private JButton pushButton = new JButton("Push");
	private JButton popButton = new JButton("Pop");
	private InputUnitI limitInput;
	private InputUnitB zufallPush = new InputUnitB(graphic, "ZufallPush", false, "south");
	private InputUnitD pushProb = new InputUnitD(graphic, "prob", 0.5, "south");
	private InputUnitB zufallPop = new InputUnitB(graphic, "ZufallPop", false, "south");
	private InputUnitD popProb = new InputUnitD(graphic, "prob", 0.5, "south");
	private int stepCount = 0;

	public static void main(String[] args) {
		StackDemo qd = new StackDemo();
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
		board.setSize(500, 600);
		xsend.groesse(5, stackSize + 1 );
		board.receiveMessage("fonttype Dialog");
		board.receiveMessage("fontsize 32");
		xsend.formen("none");
		xsend.form2(0,stackSize, "F");
		xsend.form2(4,stackSize, "F");
		xsend.form(stackSize + 2, "none");
		xsend.zeichen2(1, stackSize - 1, (char) 8664);
		xsend.zeichen2(3, stackSize - 1, (char) 8663);
		xsend.text2(0,stackSize, "" + next);
		xsend.text2(2,stackSize, "Stack");
		
		for( int y = 0; y < stackSize; y++ ) {
			xsend.form2(2, y, "F");
		}
	}

	private void addInputs() {
		pushButton.addActionListener(e -> push());
		board.getGraphic().addSouthComponent(pushButton);

		popButton.setEnabled(false);
		popButton.addActionListener(e -> pop());
		board.getGraphic().addSouthComponent(popButton);

		limitInput = new InputUnitI(graphic, "LIMIT", limit, "south");
		limitInput.setInputSize(120, 20);
		pushProb.setInputSize(50, 20);
		popProb.setInputSize(50, 20);
		
		graphic.repaint();
	}

	private void iterate() {
		++stepCount;
		if (stepCount % 2 == 0) {
			double prob = pushProb.getOptValue().orElse(0.5);
			if (zufallPush.getValue() & Math.random() < prob & stack.size() < stackSize) {
				push();
			}
		} else {
			double prob = popProb.getOptValue().orElse(0.5);
			if (zufallPop.getValue() & Math.random() < prob & stack.size() > 0) {
				pop();
			}
		}
	}

	private void pop() {
		Integer w = stack.pop();
		xsend.text2(4, stackSize, "" + w);
		xsend.text2(2, stack.size(), "");
		xsend.hintergrund2(2, stack.size(), 0xeeeeee);
		pushButton.setEnabled(true);
		if (stack.size() == 0) {
			popButton.setEnabled(false);
		}
		xsend.statusText("Wert entnommen, in Stack: " + stack.size());
	}

	private void push() {
		stack.push(next);
		next = random.nextInt(limitInput.getOptValue().orElse(limit));
		xsend.text2(0, stackSize, "" + next);
		showStack();
		if (stack.size() == stackSize) {
			pushButton.setEnabled(false);
		}
		popButton.setEnabled(true);
		xsend.statusText("Wert eingfügt, in Stack: " + stack.size());
	}

	private void showStack() {
		for (int i = 0; i < stack.size(); i++) {
			int y = stack.size() - i - 1;
			xsend.text2(2, y, "" + stack.get(i));
			xsend.hintergrund2(2, y, XSendAdapter.GREENYELLOW);
		}

	}
}
