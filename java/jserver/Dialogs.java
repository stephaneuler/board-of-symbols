package jserver;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import plotter.Graphic;
import plotter.InfoBox;

public class Dialogs {
	public static Image infoImage = new ImageIcon(Board.class.getResource("images/info.jpg")).getImage();

	public static int codeHasChangedDialog(ResourceBundle messages) {
		int reply = JOptionPane.showConfirmDialog(null, messages.getString("changesClose"),
				messages.getString("changes"), JOptionPane.YES_NO_OPTION);

		return reply;
	}

	public static int codeHasChangedDialog(ResourceBundle messages, String prompt) {
		int reply = JOptionPane.showConfirmDialog(null, messages.getString(prompt), messages.getString("changes"),
				JOptionPane.YES_NO_OPTION);
		return reply;
	}

	public static boolean useCodesXMLStandard(ResourceBundle messages, String defaultXMLFileName) {
		String text = messages.getString("useXMLStandard") + " <" + defaultXMLFileName + ">";
		int reply = JOptionPane.showConfirmDialog(null, text, messages.getString("changes"), JOptionPane.YES_NO_OPTION);

		return reply == JOptionPane.YES_OPTION;
	}

	public static String askJavacPath(ResourceBundle messages, String path) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(messages.getString("javacPath"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (path != null) {
			chooser.setCurrentDirectory(new File(path));
		}

		int retval = chooser.showDialog(null, null);
		if (retval == JFileChooser.APPROVE_OPTION) {
			String filename = chooser.getSelectedFile().getAbsolutePath();
			return filename;
		} else {
			return null;
		}
	}

	public static String askString(String title) {
		String message = "Bitte Text eingeben";
		return JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
	}

	public static int askInteger(String title) {
		return askInteger(title, 0, 0);
	}

	public static int askInteger(String title, int lower, int upper) {
		String message = "Ganze Zahl";
		String answer = null;

		if (upper > lower) {
			message += " zwischen " + lower + " und " + upper;
		}
		String lastError = "";

		for (int attempt = 0;; ++attempt) {
			String fullMessage = message;
			if (attempt > 0) {
				fullMessage += "\nFehler: " + lastError;
			}
			answer = (String) JOptionPane.showInputDialog(null, fullMessage, title, JOptionPane.QUESTION_MESSAGE, null, null,
					answer);
			if (answer == null) {
				lastError = "keine Eingabe";
				continue;
			}
			try {
				int n = Integer.parseInt(answer);
				if (upper <= lower | (n >= lower & n < upper)) {
					return n;
				} else {
					lastError = n + ": Außerhalb des Bereichs";
				}
			} catch (Exception e) {
				lastError = "<<" + answer + ">> ist keine Zahl";
			}
		}
	}

	public static String askLanguage() {
		Properties languages = Board.getAvailableLanguages();
		JPanel panel = new JPanel();
		ButtonGroup buttonGroup = new ButtonGroup();

		Set<Entry<Object, Object>> entries = languages.entrySet();
		for (Entry<Object, Object> entry : entries) {
			JCheckBox b = new JCheckBox((String) entry.getKey());
			if (entry.getValue().equals("en_US")) {
				b.setSelected(true);
			}
			b.setActionCommand((String) entry.getValue());
			panel.add(b);
			buttonGroup.add(b);
		}
		JOptionPane.showConfirmDialog(null, panel, "Language", JOptionPane.DEFAULT_OPTION);
		if (buttonGroup.getSelection() != null) {
			return buttonGroup.getSelection().getActionCommand();
		} else {
			return "en_US";
		}
	}

	public static int replaceDialog(JTextField source, JTextField dest, ResourceBundle messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabel(messages.getString("find")));
		panel.add(source);
		panel.add(new JLabel(messages.getString("replace")));
		panel.add(dest);
		return JOptionPane.showConfirmDialog(null, panel, "Replace", JOptionPane.OK_CANCEL_OPTION);
	}

	public static void showStrings(List<String> commands, String string, Graphic graphic) {
		showStrings(commands, string, graphic, null);
	}

	public static void showStrings(List<String> lines, String title, Frame component, Font font) {
		String code = "";
		for (int l = 0; l < lines.size(); l++) {
			code += String.format("%3d", l + 1) + " " + lines.get(l) + "\n";
		}

		InfoBox info = new InfoBox(component, "", 400, 400);
		info.setTitle(title);
		if (font != null) {
			info.getTextArea().setFont(font);
		}
		info.getTextArea().setText(code);
		info.setVisible(true);
	}

}
