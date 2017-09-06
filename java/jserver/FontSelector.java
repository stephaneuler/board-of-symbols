package jserver;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

@SuppressWarnings("serial")
public class FontSelector extends JFrame implements ItemListener
{
    JTextArea textArea;
    JComboBox<String> comboBox = new JComboBox<>();
    Board board;

    public FontSelector(Board board) throws HeadlessException {
 		this();
 		this.board = board;
 		 Rectangle bounds = board.getGraphic().getBounds();
 	      setLocation(bounds.x + bounds.width, bounds.y );
 		//setLocationRelativeTo( board.getGraphic() );
 	}

	public FontSelector()
    {
        setDefaultCloseOperation( HIDE_ON_CLOSE );
    	setTitle("Font selector");
    	
        Font font = new Font("Courier New", Font.PLAIN, 16);
        font = new Font("Consolas", Font.PLAIN, 16);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
        Font [] fonts = ge.getAllFonts ();
        
		List<String> names = new ArrayList<>();
		for (Font f : fonts) {
			String name = f.getFamily();
			if (!names.contains(name)) {
				comboBox.addItem(name);
				names.add(name);
			}
		}

        comboBox.setFont( font);
        comboBox.addItemListener( this );
        add( comboBox, BorderLayout.SOUTH );

        textArea= new JTextArea("Board of Symbols", 3, 20);
        textArea.setFont( font.deriveFont( 24.0f) );
        JScrollPane preview = new JScrollPane( textArea );
        add( preview  );
        pack();
    }

 	public void setBoard(Board board) {
		this.board = board;
	}

    public void itemStateChanged(ItemEvent e)
    {
    	String type = e.getItem().toString();
        Font font = new Font(type, Font.PLAIN, 16);
        textArea.setFont( font.deriveFont( 24.0f ) );
        board.receiveMessage(Board.FILTER_PREFIX + "fonttype " + type );
    }

    public static void main(String[] args)
    {
        FontSelector frame = new FontSelector();
        frame.setDefaultCloseOperation( EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }
}