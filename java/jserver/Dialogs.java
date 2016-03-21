package jserver;

import java.util.ResourceBundle;

import javax.swing.JOptionPane;

public class Dialogs {

	public static int codeHasChangedDialog(ResourceBundle messages) {
		int reply = JOptionPane.showConfirmDialog(null,
				messages.getString("changesClose"),
				messages.getString("changes"), JOptionPane.YES_NO_OPTION);

		return reply;
	}

	public static int codeHasChangedDialog(ResourceBundle messages,
			String prompt) {
		int reply = JOptionPane.showConfirmDialog(null,
				messages.getString(prompt), messages.getString("changes"),
				JOptionPane.YES_NO_OPTION);
		return reply;
	}

}
