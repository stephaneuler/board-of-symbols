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
		log( board.receiveMessage(">>r " + x + " " + y ) );
	}

	public void background(int i, int f) {
		log( board.receiveMessage( ">>background " + i + " " + f ) );
	}

	public void background2(int i, int j, int f) {
		log( board.receiveMessage(">>background2 " + i + " " + j + " " + f ) );
	}

	public void area(int f) {
		log( board.receiveMessage(">>ba " + f ) );
	}

	public void border(int f) {
		log( board.receiveMessage(">>border " + f ) ) ;
	}

	public void clear() {
		log( board.receiveMessage(">>clear") );
	}

	public void colors(int f) {
		log( board.receiveMessage(">>colors " + f ) ) ;
	}

	public  void color(int i, int f) {
		log( board.receiveMessage(  ">>color " + i + " " + f ) ) ;
	}

	public void color2(int i, int j, int f) {
		log( board.receiveMessage(">>color2 " + i + " " + j + " " + f ) ) ;
	}

//	public void grey(int i, int g) {
//		color(i, g << 16 | g << 8 | g);
//	}
//
//	public void grey2(int i, int j, int g) {
//		color2(i, j, g << 16 | g << 8 | g);
//	}
//
	public void forms(String f) {
		log( board.receiveMessage(">>forms " + f ) ) ;
	}

	public void form(int i, String f) {
		log( board.receiveMessage(">>form " + i + " " + f ) ) ;
	}

	public void form2(int i, int j, String f) {
		log( board.receiveMessage(">>form2 " + i + " " + j + " " + f ) ) ;
	}

	public void symbolSize(int i, double s) {
		log( board.receiveMessage(">>symbolSize " + i + " " + s ) ) ;
	}

	public void symbolSize2(int i, int j, double s) {
		log( board.receiveMessage(">>symbolSize2 " + i + " " + j + " " + s ) ) ;
	}

	public void symbolSizes( double s) {
		log( board.receiveMessage(">>symbolSizes "  + s ) ) ;
	}

	public void text(int i, String f) {
		log( board.receiveMessage(">>text " + i + " " + f ) ) ;
	}

	public void text2(int i, int j, String f) {
		log( board.receiveMessage(">>text2 " + i + " " + j + " " + f ) ) ;
	}

	public void textColor(int i, int c) {
		log( board.receiveMessage(">>textColor " + i + " " + c ) ) ;
	}

	public void textColor2(int i, int j, int c) {
		log( board.receiveMessage(">>textColor2 " + i + " " + j + " " + c ) ) ;
	}

	public void character(int i, char c) {
		log( board.receiveMessage(">>T " + i + " " + c ) ) ;
	}

	public void character2(int i, int j, char c) {
		log( board.receiveMessage(">>#T " + i + " " + j + " " + c ) ) ;
	}

	public void statusText(String s) {
		log( board.receiveMessage(">>statusText " + s ) ) ;
	}
	
	public String ask() {
		return board.receiveMessage(">>p \n"  );
	}
}
