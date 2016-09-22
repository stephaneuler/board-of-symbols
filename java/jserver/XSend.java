package jserver;

import java.io.PrintStream;

import javax.swing.JTextArea;

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
public abstract class XSend {
	Board board = null;
	public String result = "";
	static PrintStream stdout = System.out;

	abstract public void send() throws InterruptedException ;

	public void setUp(JTextArea messageField) {
		TOutputStream tos = new TOutputStream( System.out, messageField);
		PrintStream outStream = new PrintStream(tos, true); 
		System.setOut(outStream);
	}
	
	public void setDown() {
		System.setOut(stdout);
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public  void setBoard(Board board) {
		this.board = board;
	}

	public Board getBoard() {
		return board;
	}

}
