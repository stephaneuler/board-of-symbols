package bosdemos;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JButton;

import jserver.Board;
import jserver.XSendAdapter;
import plotter.Graphic;
import utils.InputUnitS;

/**
* Evaluation of an expression with JavaScript
*
* @author Stephan Euler
* @version June 2023
*
*/
public class LogikTester {
	private Board board = new Board();
	private XSendAdapter xsend = new XSendAdapter(board);
	private Graphic graphic = board.getGraphic();
	private InputUnitS formInput = new InputUnitS(graphic, "Ausdruck", "a & b & c", "south");
	private ScriptEngineManager factory = new ScriptEngineManager();
	private ScriptEngine engine = factory.getEngineByName("JavaScript");

	public static void main(String[] args) {
		(new LogikTester()).setUp();
	}

	private void setUp() {
		xsend.groesse(4, 9);
		xsend.formen("F");
		xsend.flaeche(XSendAdapter.PAPAYAWHIP);
		xsend.rahmen(XSendAdapter.BLACK);
		board.receiveMessage("fontsize 18");

		JButton startButton = new JButton("Auswerten");
		startButton.addActionListener(e -> generateTable());
		graphic.addSouthComponent(startButton);

		generateTable();

	}

	private void generateTable() {
		boolean[] values = { true, false };
		String exp = formInput.getValue();

		xsend.text2(0, 8, "a");
		xsend.text2(1, 8, "b");
		xsend.text2(2, 8, "c");
		xsend.text2(3, 8, exp);

		int zeile = 0;
		for (boolean c : values) {
			for (boolean b : values) {
				for (boolean a : values) {
					xsend.text2(0, zeile, asText(a));
					xsend.text2(1, zeile, asText(b));
					xsend.text2(2, zeile, asText(c));
					evaluate(a, b, c, exp, zeile);
					++zeile;
				}
			}
		}
	}

	private void evaluate(boolean a, boolean b, boolean c, String exp, int zeile) {
		engine.put("a", a);
		engine.put("b", b);
		engine.put("c", c);
		try {
			xsend.text2(3, zeile, "" + engine.eval(exp));
			xsend.statusText("okay");
		} catch (ScriptException e) {
			xsend.statusText(e.getMessage());
		}
		
	}

	private String asText(boolean a) {
		return a ? "1" : "0";
	}
}
