package quiz;

import javax.swing.JOptionPane;

/**
 * open question, String as answer
 * 
 * @author Euler
 *
 */
public class OpenQuestion extends Question{
	String answer;
	
	public OpenQuestion(String question, String answer) {
		super();
		this.question = question;
		this.answer = answer;
	}

	public boolean ask() {
		String answer =  JOptionPane.showInputDialog(null,  question);
		if( answer == null ) {
			return false;
		}
		return  this.answer.equalsIgnoreCase( answer.trim() );
	}

}
