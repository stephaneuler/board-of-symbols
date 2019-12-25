package quiz;

import javax.swing.JOptionPane;

/**
 * only Yes or No as answers
 * @author Euler
 *
 */
public class YesNoQuestion extends Question {
	boolean answer;

	public YesNoQuestion(String question, boolean answer) {
		super();
		this.question = question;
		this.answer = answer;
	}

	public boolean check(boolean answer) {
		return answer == this.answer;
	}

	public boolean ask() {
		int ret = JOptionPane.showConfirmDialog(null, question);
		if (answer & ret == JOptionPane.OK_OPTION) {
			return true;
		} else if (!answer & ret == JOptionPane.NO_OPTION) {
			return true;
		} else {
			return false;
		}
	}

}
