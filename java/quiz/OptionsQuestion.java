package quiz;

import java.util.Arrays;

import javax.swing.JOptionPane;


/**
 * one out of a list of given possible answers is correct
 * @author Euler
 *
 */
public class OptionsQuestion  extends Question {
	String[] options;
	int correct;
	
	/**
	 * @param question the question
	 * @param options list of possible answers
	 * @param correct index of the correct answer
	 */
	public OptionsQuestion(String question, String[] options, int correct) {
		super();
		this.question = question;
		this.options = new String[ options.length + 1];
		this.options[0]  = "??";
		System.arraycopy(options, 0, this.options, 1, options.length );
		this.correct = correct;
	}

	public static void main(String[] args) {
		String[] possibleValues = { "Alan Turing", "Claude Shannon", "Konrad Zuse", "Bill Gates" };
		OptionsQuestion q = new OptionsQuestion("KI-Pionier", possibleValues, 0);
		System.out.println( q.ask() );
	}


	public boolean ask() {
		String answer =  (String) JOptionPane.showInputDialog(null,
	            question, "Frage",
	             JOptionPane.INFORMATION_MESSAGE, null,
	             options, null);
		if( answer == null ) {
			return false;
		}
		return answer.equals( options[correct+1]);
	}

}
