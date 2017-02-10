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
public abstract class XSendEN  extends XSend {

	public void size(int x, int y) {
		result +=  board.receiveMessage(">>r " + x + " " + y );
	}

	public void background(int i, int f) {
		result +=  board.receiveMessage( ">>b " + i + " " + f );
	}

	public void background2(int i, int j, int f) {
		result +=  board.receiveMessage(">>#b " + i + " " + j + " " + f );
	}

	public void area(int f) {
		result +=  board.receiveMessage(">>ba " + f );
	}

	public void border(int f) {
		result +=  board.receiveMessage(">>bo " + f );
	}

	public void clear() {
		result +=  board.receiveMessage(">>c");
	}

	public void colors(int f) {
		result +=  board.receiveMessage(">>a " + f );
	}

	public  void color(int i, int f) {
		result += board.receiveMessage(  ">>" + i + " " + f );
	}

	public void color2(int i, int j, int f) {
		result +=  board.receiveMessage(">># " + i + " " + j + " " + f );
	}

	public void grey(int i, int g) {
		color(i, g << 16 | g << 8 | g);
	}

	public void grey2(int i, int j, int g) {
		color2(i, j, g << 16 | g << 8 | g);
	}

	public void forms(String f) {
		result +=  board.receiveMessage(">>f " + f );
	}

	public void form(int i, String f) {
		result +=  board.receiveMessage(">>fi " + i + " " + f );
	}

	public void form2(int i, int j, String f) {
		result +=  board.receiveMessage(">>#fi " + i + " " + j + " " + f );
	}

	public void symbolSize(int i, double s) {
		result +=  board.receiveMessage(">>s " + i + " " + s );
	}

	public void symbolSize2(int i, int j, double s) {
		result +=  board.receiveMessage(">>#s " + i + " " + j + " " + s );
	}

	public void text(int i, String f) {
		result +=  board.receiveMessage(">>T " + i + " " + f );
	}

	public void textColor(int i, int c) {
		result +=  board.receiveMessage(">>TC " + i + " " + c );
	}

	public void textColor2(int i, int j, int c) {
		result +=  board.receiveMessage(">>#TC " + i + " " + j + " " + c );
	}

	public void text2(int i, int j, String f) {
		result +=  board.receiveMessage(">>#T " + i + " " + j + " " + f );
	}

	public void character(int i, char c) {
		result +=  board.receiveMessage(">>T " + i + " " + c );
	}

	public void character2(int i, int j, char c) {
		result +=  board.receiveMessage(">>#T " + i + " " + j + " " + c );
	}

	public void statusText(String s) {
		result +=  board.receiveMessage(">>t " + s );
	}
	
	public String ask() {
		return board.receiveMessage(">>p \n"  );
	}
}
