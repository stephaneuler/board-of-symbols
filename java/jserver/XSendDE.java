package jserver;


/**
 * This is the abstract class with the interface to a board. 
 * It implements the convenience methods to send commands. 
 * A sub class has to implement the abstract method send. 
 * The codeExecutorJava performs the following sequence of method calls
 * <ol>
 * <li>setUp - connect the output with the message field</li>
 * <li>send - send all commands</li>
 * <li>setDown  - disconnect the output</li>
 * </ol>
 * 
 * @author Euler
 *
 */
public abstract class XSendDE extends XSend {
	
	public void groesse(int x, int y) {
		result +=  board.receiveMessage(">>r " + x + " " + y );
	}

	public void hintergrund(int i, int f) {
		result +=  board.receiveMessage( ">>b " + i + " " + f );
	}

	public void hintergrund2(int i, int j, int f) {
		result +=  board.receiveMessage(">>#b " + i + " " + j + " " + f );
	}

	public void flaeche(int f) {
		result +=  board.receiveMessage(">>ba " + f );
	}

	public void rahmen(int f) {
		result +=  board.receiveMessage(">>bo " + f );
	}

	public void loeschen() {
		result +=  board.receiveMessage(">>c");
	}

	public void farben(int f) {
		result +=  board.receiveMessage(">>a " + f );
	}

	public  void farbe(int i, int f) {
		result += board.receiveMessage(  ">>" + i + " " + f );
	}

	public void grau(int i, int g) {
		farbe(i, g << 16 | g << 8 | g);
	}

	public void farbe2(int i, int j, int f) {
		result +=  board.receiveMessage(">># " + i + " " + j + " " + f );
	}

	public void grau2(int i, int j, int g) {
		farbe2(i, j, g << 16 | g << 8 | g);
	}

	public void formen(String f) {
		result +=  board.receiveMessage(">>f " + f );
	}

	public void form(int i, String f) {
		result +=  board.receiveMessage(">>fi " + i + " " + f );
	}

	public void form2(int i, int j, String f) {
		result +=  board.receiveMessage(">>#fi " + i + " " + j + " " + f );
	}

	public void symbolGroesse(int i, double s) {
		result +=  board.receiveMessage(">>s " + i + " " + s );
	}

	public void symbolGroesse2(int i, int j, double s) {
		result +=  board.receiveMessage(">>#s " + i + " " + j + " " + s );
	}

	public void text(int i, String f) {
		result +=  board.receiveMessage(">>T " + i + " " + f );
	}

	public void textFarbe(int i, int c) {
		result +=  board.receiveMessage(">>TC " + i + " " + c );
	}

	public void textFarbe2(int i, int j, int c) {
		result +=  board.receiveMessage(">>#TC " + i + " " + j + " " + c );
	}

	public void text2(int i, int j, String f) {
		result +=  board.receiveMessage(">>#T " + i + " " + j + " " + f );
	}

	public void zeichen(int i, char c) {
		result +=  board.receiveMessage(">>T " + i + " " + c );
	}

	public void zeichen2(int i, int j, char c) {
		result +=  board.receiveMessage(">>#T " + i + " " + j + " " + c );
	}

	public void statusText(String s) {
		result +=  board.receiveMessage(">>t " + s );
	}
	
	public String abfragen() {
		return board.receiveMessage(">>p \n"  );
	}
}
