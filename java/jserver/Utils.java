package jserver;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * This class contains some convenience  methods 
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
	public static JMenuItem addMenuItem(ActionListener li, JMenu menu,	String text) {
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
	 * add a new item to the give menu, add an action listener, set the tool tip and set the keyboard accelerator 
	 *  
	 * @param li
	 * @param menu
	 * @param text
	 * @param tip
	 * @param key
	 * @return
	 */
	public static  JMenuItem addMenuItem(ActionListener li, JMenu menu, String text, String tip, String key) {
		JMenuItem mi = addMenuItem(li,  menu, text, tip );
		mi.setAccelerator(KeyStroke.getKeyStroke( key ));
		return mi;
	}


}
