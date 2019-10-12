package jserver;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
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
	 * add a new item to the give menu, add an action listener and set the tool tip
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
	 * add a new item to the give menu, add an action listener, set the tool tip and
	 * set the keyboard accelerator
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
	 * Takes a resouce bundle and a variable number of strings and concatenates all
	 * strings for the given keys. A string that contains only non-word characters
	 * is included directly in the resulting string and not used as a key.
	 * 
	 * @param messages the resource bundle
	 * @param strings  the keys for concatenation
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

	public static String clipText(String text, int maxLength) {
		if (text.length() <= maxLength) {
			return text;
		} else {
			return text.substring(0, maxLength) + "...";
		}
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

	static String getJavaHelp() {
		Properties p = System.getProperties();
		String text = "";

		text += "Java Runtime Environment version: " + p.getProperty("java.version") + System.lineSeparator();
		text += "Java installation directory: " + p.getProperty("java.home") + System.lineSeparator();
		text += "Java class path: " + p.getProperty("java.class.path") + System.lineSeparator();
		text += "Java class format version number: " + p.getProperty("java.class.version") + System.lineSeparator();
		// text += "compiled with javac version : " + majorToJavaVersion(
		// p.getProperty("java.class.version") ) + System.lineSeparator();
		text += "Operating system name: " + p.getProperty("os.name") + System.lineSeparator();
		text += "Operating system version: " + p.getProperty("os.version") + System.lineSeparator();

		return text;
	}

	static String majorToJavaVersion(String major) {
		switch (major) {
		case "52.0":
			return "8";
		case "53.0":
			return "9";
		case "54.0":
			return "10";
		}
		return "???";
	}

	static String guessJDKVersion(String pathname) {
		int n = pathname.indexOf("jdk-");
		if (n >= 0) {
			return pathname.substring(n);
		}
		n = pathname.indexOf("jdk");
		if (n >= 0) {
			return pathname.substring(n);
		}
		return "??";
	}

	// http://www.baeldung.com/java-levenshtein-distance
	public static int calculateLevenshtein(String x, String y) {
		int[][] dp = new int[x.length() + 1][y.length() + 1];

		for (int i = 0; i <= x.length(); i++) {
			for (int j = 0; j <= y.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
							dp[i - 1][j] + 1, dp[i][j - 1] + 1);
				}
			}
		}

		return dp[x.length()][y.length()];
	}

	public static int min(int... numbers) {
		return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
	}

	public static int costOfSubstitution(char a, char b) {
		return a == b ? 0 : 1;
	}

	public static String toText(Properties properties) {
		List<String> keys = new ArrayList<String>();
		for (String key : properties.stringPropertyNames()) {
			keys.add(key);
		}
		Collections.sort(keys);
		final StringBuffer text = new StringBuffer();
		keys.forEach((key) -> text.append( key +": " +properties.getProperty(key) + "\n"));
		return text.toString();
	}

}
