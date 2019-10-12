package jserver;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

public class BoardCloser extends WindowAdapter {
	Board board;

	public BoardCloser(Board board) {
		this.board = board;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		CodeWindow codeWindow = board.getCodeWindow();
		if (codeWindow != null) {
			codeWindow.savePosition();
			if (codeWindow.isCodeHasChanged()) {
				int reply = Dialogs.codeHasChangedDialog( board.getMessages() );
				if (reply == JOptionPane.NO_OPTION) {
					return;
				}
			}
		}
		board.addRecentFilesToProperties();
		board.saveProperties();
		System.exit(0);
		super.windowClosing(e);
	}

}
