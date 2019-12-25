package quiz;

import javax.swing.JOptionPane;

/**
 * open question, String as answer
 * 
 * @author Euler
 *
 */
public class CorrectQuestion extends Question{
	String answer;
	
	public CorrectQuestion(String question, String answer) {
		super();
		this.question = question;
		this.answer = answer;
	}

	public boolean ask() {
		String answer =  JOptionPane.showInputDialog(null, "Bitte korrigieren", question);
		if( answer == null ) {
			return false;
		}
		return  this.answer.equalsIgnoreCase( answer.trim() );
	}

}
