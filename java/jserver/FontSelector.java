package jserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class FontSelector extends JFrame implements ItemListener, ActionListener {
	JTextArea textArea;
	JComboBox<String> comboBox = new JComboBox<>();
	JTextField charField = new JTextField("");
	Board board;
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	Font[] fonts = ge.getAllFonts();

	public FontSelector(Board board) throws HeadlessException {
		this();
		this.board = board;
		Rectangle bounds = board.getGraphic().getBounds();
		setLocation(bounds.x + bounds.width, bounds.y);
		// setLocationRelativeTo( board.getGraphic() );
	}

	public FontSelector() {
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setTitle("Font selector");

		System.out.println("total of " + fonts.length + " fonts found");

		Font font = new Font("Courier New", Font.PLAIN, 16);
		font = new Font("Consolas", Font.PLAIN, 16);

		List<String> names = new ArrayList<>();
		for (Font f : fonts) {
			String name = f.getFamily();
			if (!names.contains(name)) {
				comboBox.addItem(name);
				names.add(name);
			}
		}

		comboBox.setFont(font);
		comboBox.addItemListener(this);
		add(comboBox, BorderLayout.SOUTH);

		Box charInput = new Box(BoxLayout.X_AXIS);
		charInput.add(new JLabel("int value for char: "));
		charField.setMaximumSize(new Dimension(60, 20));
		charInput.add(charField);
		JButton addButton = new JButton("+");
		addButton.addActionListener(this);
		JButton findButton = new JButton("find font");
		findButton.setToolTipText("Find the first font that can display the text");
		findButton.addActionListener(this);

		charInput.add(addButton);
		charInput.add(findButton);
		add(charInput, BorderLayout.NORTH);

		textArea = new JTextArea("Board of Symbols", 3, 20);
		textArea.setFont(font.deriveFont(24.0f));
		JScrollPane preview = new JScrollPane(textArea);
		add(preview);
		pack();
	}

	protected void selectFont(String name) {
		for( int i = 0; i < comboBox.getItemCount(); i++) {
			if(comboBox.getItemAt(i).equals(name)) {
				comboBox.setSelectedIndex(i);
				return;
			}
		}
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void itemStateChanged(ItemEvent e) {
		String type = e.getItem().toString();
		Font font = new Font(type, Font.PLAIN, 16);
		textArea.setFont(font.deriveFont(24.0f));
		if (board != null) {
			board.receiveMessage(Board.FILTER_PREFIX + "fonttype " + type);
		}
	}

	/**
	 * Search a font with a glyph for a given char
	 * 
	 * @return the font or null if no matching font was found
	 */
	private Font testFonts(int v) {
		for (Font f : fonts) {
			if (f.canDisplay(v)) {
				System.out.println("Font : " + f);
				return f;
			}
		}
		System.out.println("No Font found");
		return null;
	}

	private Font testFonts(String text) {
		fontLoop: for (Font f : fonts) {
			for (int i = 0; i < text.length(); i++) {
				if (! f.canDisplay(text.charAt(i))) {
					continue fontLoop;
				}
			}
			return f;
		}
		System.out.println("No Font found");
		return null;
	}

	public static void main(String[] args) {
		FontSelector frame = new FontSelector();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		String cmd = ev.getActionCommand();
		
		if (cmd.equals("find font")) {
			Font font = testFonts(textArea.getText());
			if (font != null) {
				selectFont(font.getFamily());
			}
		} else {
			try {
				int v = Integer.decode(charField.getText());
				textArea.append("" + ((char) v));
				charField.setBackground(Color.white);
			} catch (Exception e) {
				charField.setBackground(Color.red);
			}
		}

	}
}