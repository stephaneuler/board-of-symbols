package jserver;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class ComboBoxRenderer extends JPanel implements ListCellRenderer {

	private static final long serialVersionUID = -1L;
	private Color[] colors;
	private String[] strings;

	JPanel textPanel;
	JLabel text;
	JLabel square = new JLabel( "           ");
	Box box = new Box( BoxLayout.X_AXIS);

	public ComboBoxRenderer(JComboBox combo) {

		textPanel = new JPanel();
		textPanel.add(this);
		square.setOpaque(true);
		text = new JLabel("Farbe");
		text.setOpaque(true);
		text.setFont(combo.getFont());
		box.add( square);
		box.add( text );
		textPanel.add(box);
	}

	public void setColors(Color[] col) {
		colors = col;
	}

	public void setStrings(String[] str) {
		strings = str;
	}

	public Color[] getColors() {
		return colors;
	}

	public String[] getStrings() {
		return strings;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
		} else {
			setBackground(Color.WHITE);
		}

		if (colors.length != strings.length) {
			System.out.println("colors.length does not equal strings.length");
			return this;
		} else if (colors == null) {
			System.out.println("use setColors first.");
			return this;
		} else if (strings == null) {
			System.out.println("use setStrings first.");
			return this;
		}

		//text.setBackground(getBackground());
		
		if (index > -1) {
			square.setBackground(colors[index]);
			text.setForeground(colors[index]);
		}

		text.setText(value.toString());
		return box;
	}
}