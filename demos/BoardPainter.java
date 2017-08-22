package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import jserver.Board;
import jserver.BoardClickEvent;
import jserver.BoardClickListener;
import jserver.XSendAdapter;
import plotter.Graphic;

/**
 * A demo application using BoS to implement a simple painter. 
 * 
 * @author Euler
 *
 */
public class BoardPainter implements BoardClickListener {
	private Board board;
	private Graphic graphic;
	private XSendAdapter xsend;
	private JTextField indexField = new JTextField();
	private int farben[] = {XSendAdapter.GREEN, XSendAdapter.YELLOW, XSendAdapter.RED };
	private String[] farbNamen = {"Grün", "Gelb", "Rot" };
	private int zeichenFarbe = XSendAdapter.BLUE;

	public static void main(String[] args) {
		BoardPainter e = new BoardPainter();
		e.starten();

	}

	void starten() {
		board = new Board();
		graphic = board.getGraphic();
		xsend = new XSendAdapter(board);
		
		// Standardfarbe
		xsend.groesse(20, 20);
		xsend.farben(XSendAdapter.LIGHTGRAY);

		board.addClickListener(this);

		JButton faerbeButton = new JButton("färben");
		faerbeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = indexField.getText();
				if( text.isEmpty() ) {
					JOptionPane.showMessageDialog(graphic, "Bitte Wert eintragen", "Leeres Feld",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				int index = Integer.parseInt(text);
				xsend.farbe(index, XSendAdapter.ORANGE);
			}
		});

		JButton loescheButton = new JButton("löschen");
		loescheButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				xsend.farben(XSendAdapter.LIGHTGRAY);
			}
		});

		graphic.addBottomComponent(loescheButton);
		graphic.addBottomComponent(faerbeButton);
		graphic.addBottomComponent(new JLabel("pos:"));
		graphic.addBottomComponent(indexField);
		
		graphic.addEastComponent(new JLabel("Click-Farben"));
		ButtonGroup group = new ButtonGroup();
		for( int f=0; f<farben.length; f++ ) {
			JRadioButton farbWaehler = new JRadioButton( farbNamen[f]);
			farbWaehler.setActionCommand(""+farben[f]);
			group.add(farbWaehler);
			farbWaehler.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					zeichenFarbe = Integer.parseInt(e.getActionCommand());
					xsend.statusText("Farbe gewechselt: " + Integer.toHexString(zeichenFarbe ) );
				}
			});
			graphic.addEastComponent(farbWaehler);
		}
		
		JMenu hilfeMenu = new JMenu("Hilfe");
		graphic.addExternMenu(hilfeMenu);
		JMenuItem ueber = new JMenuItem("Über");
		ueber.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(graphic, "Testanwendung Version 0.0", "Über",
							JOptionPane.INFORMATION_MESSAGE);
				}
			});
		hilfeMenu.add(ueber);


		graphic.pack();
		graphic.repaint();
	}

	@Override
	public void boardClick(BoardClickEvent info) {
		System.out.println(info);
		xsend.farbe2( info.getX(), info.getY(), zeichenFarbe );
	}

}
