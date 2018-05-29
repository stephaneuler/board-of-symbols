package jserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class BoSButton extends JButton {
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
