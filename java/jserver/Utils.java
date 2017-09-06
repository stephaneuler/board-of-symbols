package jserver;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * This class contains some convenience methods
 * 
 * @author Euler
 * 
 */
public class Utils {

	/**
	 * add a new item to the give menu and add an action listener
	 * 
	 * @param li
	 * @param menu
	 * @param text
	 * @return
	 */
	public static JMenuItem addMenuItem(ActionListener li, JMenu menu, String text) {
		JMenuItem mi = new JMenuItem(text);
		mi.addActionListener(li);
		menu.add(mi);
		return mi;
	}

	/**
	 * add a new item to the give menu, add an action listener and set the tool
	 * tip
	 * 
	 * @param li
	 * @param menu
	 * @param text
	 * @param tip
	 * @return the new JMenuItem
	 */
	public static JMenuItem addMenuItem(ActionListener li, JMenu menu, String text, String tip) {
		JMenuItem mi = new JMenuItem(text);
		mi.addActionListener(li);
		mi.setToolTipText(tip);
		menu.add(mi);
		return mi;
	}

	/**
	 * add a new item to the give menu, add an action listener, set the tool tip
	 * and set the keyboard accelerator
	 * 
	 * @param li
	 * @param menu
	 * @param text
	 * @param tip
	 * @param key
	 * @return
	 */
	public static JMenuItem addMenuItem(ActionListener li, JMenu menu, String text, String tip, String key) {
		JMenuItem mi = addMenuItem(li, menu, text, tip);
		mi.setAccelerator(KeyStroke.getKeyStroke(key));
		return mi;
	}

	/**
	 * Takes a resouce bundle and a variable number of strings and concatenates
	 * all strings for the given keys. A string that contains only non-word
	 * characters is included directly in the resulting string and not used as a
	 * key.
	 * 
	 * @param messages
	 *            the resource bundle
	 * @param strings
	 *            the keys for concatenation
	 * @return the resulting string
	 */
	public static String concat(ResourceBundle messages, String... strings) {
		String result = "";

		for (String s : strings) {
			if (s.matches("\\W*")) {
				result += s;
			} else {
				result += " " + messages.getString(s);
			}
		}
		return result.trim();
	}

	public static String concat(ResourceBundleWrapper messages, String... strings) {
		String result = "";

		for (String s : strings) {
			if (s.matches("\\W*")) {
				result += s;
			} else {
				result += " " + messages.getString(s);
			}
		}
		return result.trim();
	}

	public static String capitalize(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	public static void fillFontMenu(JMenu fontMenu) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fonts = ge.getAllFonts();

		List<String> names = new ArrayList<>();
		for (Font f : fonts) {
			String name = f.getFamily();
			if (!names.contains(name)) {
				addMenuItem(null, fontMenu, name);
				names.add(name);
			}
		}

	}

}
