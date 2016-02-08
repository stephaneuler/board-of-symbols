package jserver;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class Utils {

	public static JMenuItem addMenuItem(ActionListener li, JMenu menu, String text, String tip) {
		JMenuItem mi = new JMenuItem(text);
		mi.addActionListener(li);
		mi.setToolTipText(tip);
		menu.add(mi);
		return mi;
	}

	public static  JMenuItem addMenuItem(ActionListener li, JMenu menu, String text, String tip, String key) {
		JMenuItem mi = addMenuItem(li,  menu, text, tip );
		mi.setAccelerator(KeyStroke.getKeyStroke( key ));
		return mi;
	}

	public static JMenuItem addMenuItem(ActionListener li, JMenu menu,	String text) {
		JMenuItem mi = new JMenuItem(text);
		mi.addActionListener(li);
		menu.add(mi);
		return mi;
	}


}
