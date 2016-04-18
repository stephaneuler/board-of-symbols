package jserver;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

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

	
	public static boolean useCodesXMLStandard(ResourceBundle messages) {
		int reply = JOptionPane.showConfirmDialog(null,
				messages.getString("useXMLStandard"),
				messages.getString("changes"), JOptionPane.YES_NO_OPTION);

		return reply==JOptionPane.YES_OPTION;
	}

	public static String askJavacPath(ResourceBundle messages) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle( messages.getString("javacPath"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int retval = chooser.showDialog(null, null);
		if (retval == JFileChooser.APPROVE_OPTION) {
			String filename = chooser.getSelectedFile().getAbsolutePath();
			// System.out.println(filename);
			return filename;
		} else {
			return null;
		}

	}

}
