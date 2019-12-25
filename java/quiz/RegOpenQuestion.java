package quiz;

import javax.swing.JOptionPane;

/**
 * open question, String as answer
 * 
 * @author Euler
 *
 */
public class RegOpenQuestion extends Question{
	String answer;
	
	public RegOpenQuestion(String question, String answer) {
		super();
		this.question = question;
		this.answer = answer;
	}

	public boolean ask() {
		String answer =  JOptionPane.showInputDialog(null,  question);
		if( answer == null ) {
			return false;
		}
		return answer.matches(this.answer);
	}

}
