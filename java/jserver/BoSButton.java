package jserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * A BoSButton has a default action: on click the given text is added to the board command queue.
 * This is usefull for non-Java applications. They can add buttons (BoSL command: button) and 
 * then poll the command queue for clicks. 
 * 
 * @author Euler
 *
 */
public class BoSButton extends JButton {
	private static final long serialVersionUID = 6805554723932504926L;
	Board commands;

	public BoSButton(String text, Board board) {
		super(text);
		this.commands = board;
		
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.addCommand(text);
			}
		});
	}

}
