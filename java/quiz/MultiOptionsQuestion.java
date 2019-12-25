package quiz;

import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

public class MultiOptionsQuestion extends Question {
	String[] options;
	boolean[] correct;

	/**
	 * @param question the question
	 * @param options  list of possible answers
	 * @param correct  index of the correct answer
	 */
	public MultiOptionsQuestion(String question, String[] options, String corrects) {
		super();
		this.question = question;
		this.options = options;
		String[] cs = corrects.split(",");
		correct = new boolean[options.length];
		for (String s : cs) {
			correct[Integer.parseInt(s)] = true;
		}
		System.out.println(Arrays.toString(correct));
	}

	public static void main(String[] args) {
		String[] possibleValues = { "Alan Turing", "Claude Shannon", "Konrad Zuse", "Bill Gates" };
		MultiOptionsQuestion q = new MultiOptionsQuestion("KI-Pionier", possibleValues, "1,3");
		System.out.println(q.ask());
	}

	public boolean ask() {
		DefaultListModel<JCheckBox> model = new DefaultListModel<>();
		for (String s : options) {
			model.addElement(new JCheckBox(s));
		}
		JCheckBoxList list = new JCheckBoxList(model);
		JOptionPane.showMessageDialog(null, list, question, JOptionPane.PLAIN_MESSAGE);
		for (int i = 0; i < correct.length; i++) {
			if (model.getElementAt(i).isSelected() != correct[i]) {
				return false;
			}
		}

//		JCheckBox[] jb = new JCheckBox[options.length];
//		for( int i=0; i<options.length; i++ ) {
//			jb[i] = new JCheckBox( options[i] );
//		}
//		String answer =  (String) JOptionPane.showInputDialog(null,
//	            question, "Frage",
//	             JOptionPane.INFORMATION_MESSAGE, null,
//	             jb, null);
//		if( answer == null ) {
//			return false;
//		}
		return true;
	}

}
