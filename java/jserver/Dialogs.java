package jserver;

import java.awt.Font;
import java.awt.Frame;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import plotter.Graphic;

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

	public static int replaceDialog(JTextField source, JTextField dest, ResourceBundle messages) {
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add( new JLabel( "Suche") );
		panel.add( source );
		panel.add( new JLabel( "Ersetze") );
		panel.add( dest );
		return JOptionPane.showConfirmDialog(null, panel, "Replace", JOptionPane.OK_CANCEL_OPTION);		
	}

	public static void showStrings(List<String> commands, String string, Graphic graphic) {
		showStrings(commands, string, graphic, null);		
	}
	
	public static void showStrings(List<String> lines, String title, Frame component, Font font) {
		String code = "";
			for( int l=0; l<lines.size(); l++ ) {
				code += String.format("%3d", l + 1) + " " + lines.get(l) + "\n";
			}

	
		InfoBox info = new InfoBox(component, "", 400, 400);
		info.setTitle( title);
		if( font != null ) {
			info.getTextArea().setFont( font );
		}
		info.getTextArea().setText( code );
		info.setVisible(true);
	}


}
