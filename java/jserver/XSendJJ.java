package jserver;

import javax.swing.JTextArea;

/**
 * This is the abstract class with the interface to a board. It implements the
 * convenience methods to send commands. A sub class has to implement the
 * abstract method send. The codeExecutorJava performs the following sequence of
 * method calls
 * <ol>
 * <li>setUp - connect the output with the message field</li>
 * <li>send - send all commands</li>
 * <li>setDown - disconnect the output</li>
 * </ol>
 * 
 * @author Euler
 *
 */
public abstract class XSendJJ extends XSend {
	int lineCount  = 0;
	int maxLines = 1000;

	private boolean incAndCheckLineCount() {
		if( lineCount > maxLines ) {
			return false;
		} else {
			++lineCount;
			return true;
			
		}
	}

	private String convCol(int f) {
		return String.format("'#%06x'", f);
	}

	@Override
	public void setUp(JTextArea messageField) {
	}

	public void groesse(int x, int y) {
		System.out.println(">> groesse(" + x + ", " + y + ")");
	}

	public void hintergrund(int i, int f) {
		System.out.println(">>b " + i + " " + f);
	}

	public void hintergrund2(int i, int j, int f) {
		System.out.println(">>#b " + i + " " + j + " " + f);
	}

	public void flaeche(int f) {
		System.out.println(">>ba " + f);
	}

	public void rahmen(int f) {
		System.out.println(">>bo " + f);
	}

	public void loeschen() {
		System.out.println(">>c");
	}

	public void farben(int f) {
		System.out.println(">> farben(" + convCol(f) + ")");
	}

	public void farbe(int i, int f) {
		result += board.receiveMessage(">>" + i + " " + f);
	}

	public void grau(int i, int g) {
		farbe(i, g << 16 | g << 8 | g);
	}

	public void farbe2(int i, int j, int f) {
		if (incAndCheckLineCount()) {
			String c = String.format("'#%06x'", f);
			System.out.println(">> farbe2( " + i + ", " + j + ", " + c + ")");
		}
	}

	public void grau2(int i, int j, int g) {
		farbe2(i, j, g << 16 | g << 8 | g);
	}

	public void formen(String f) {
		System.out.println(">>f " + f);
	}

	public void form(int i, String f) {
		System.out.println(">>fi " + i + " " + f);
	}

	public void form2(int i, int j, String f) {
		System.out.println(">>#fi " + i + " " + j + " " + f);
	}

	public void symbolGroesse(int i, double s) {
		System.out.println(">>s " + i + " " + s);
	}

	public void symbolGroesse2(int i, int j, double s) {
		System.out.println(">>#s " + i + " " + j + " " + s);
	}

	public void text(int i, String f) {
		System.out.println(">>T " + i + " " + f);
	}

	public void textFarbe(int i, int c) {
		System.out.println(">>TC " + i + " " + c);
	}

	public void textFarbe2(int i, int j, int c) {
		System.out.println(">>#TC " + i + " " + j + " " + c);
	}

	public void text2(int i, int j, String f) {
		System.out.println(">>#T " + i + " " + j + " " + f);
	}

	public void zeichen(int i, char c) {
		System.out.println(">>T " + i + " " + c);
	}

	public void zeichen2(int i, int j, char c) {
		System.out.println(">>#T " + i + " " + j + " " + c);
	}

	public void statusText(String s) {
		System.out.println(">>t " + s);
	}

	public String abfragen() {
		return board.receiveMessage(">>p \n");
	}
}
