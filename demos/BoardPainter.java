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

// ****************************************************************
// Demo fuer GUI-Moeglichkeiten mit BoS
// Version Wann      Wer  Was
// 1.0     20.12.03  SE   Erste Version
//****************************************************************

public class BoardPainter implements BoardClickListener {
	private XSendAdapter xsend;
	private JTextField indexField = new JTextField();
	private int farben[] = { XSendAdapter.GREEN, XSendAdapter.YELLOW, XSendAdapter.RED };
	private String[] farbNamen = { "Gruen", "Gelb", "Rot" };
	private int zeichenFarbe = XSendAdapter.BLUE;
	private int standardFarbe = XSendAdapter.LIGHTGRAY;
	private int groesse = 10;

	public static void main(String[] args) {
		BoardPainter e = new BoardPainter();
		e.starten();
	}

	void starten() {
		Board board = new Board();
		Graphic graphic = board.getGraphic();
		xsend = new XSendAdapter(board);
		xsend.groesse(groesse, groesse );
		xsend.farben(standardFarbe);

		graphic.setTitle("Board Painter Version 1.0");
		board.addClickListener(this);
		
		addButtons(graphic);
		addPositionInput(graphic);
		addButtonGroup( graphic );
		addMenue( graphic );

		graphic.pack();
		graphic.repaint();
	}

	private void addButtons(Graphic graphic ) {
		JButton faerbeButton = new JButton("faerben");
		faerbeButton.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = indexField.getText();
				if (text.isEmpty()) {
					JOptionPane.showMessageDialog(graphic, "Bitte Wert eintragen", "Leeres Feld",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				int index = Integer.parseInt(text);
				xsend.farbe(index, zeichenFarbe );
			}
		});
	
		JButton loescheButton = new JButton("loeschen");
		loescheButton.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				xsend.farben( standardFarbe );
			}
		});
	
		graphic.addBottomComponent(loescheButton);
		graphic.addBottomComponent(faerbeButton);
	}

	private void addPositionInput(Graphic graphic) {
		graphic.addBottomComponent(new JLabel("pos (n):"));
		graphic.addBottomComponent(indexField);
	}

	private void addButtonGroup(Graphic graphic) {
		graphic.addEastComponent(new JLabel("Click-Farben"));
		ButtonGroup group = new ButtonGroup();
		for (int f = 0; f < farben.length; f++) {
			JRadioButton farbWaehler = new JRadioButton(farbNamen[f]);
			farbWaehler.setActionCommand("" + farben[f]);
			group.add(farbWaehler);
			farbWaehler.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					zeichenFarbe = Integer.parseInt(e.getActionCommand());
					xsend.statusText("Farbe gewechselt: " + Integer.toHexString(zeichenFarbe).toUpperCase() );
				}
			});
			graphic.addEastComponent(farbWaehler);
		}

	}

	private void addMenue(Graphic graphic) {
		JMenu hilfeMenu = new JMenu("Hilfe");
		graphic.addExternMenu(hilfeMenu);
		JMenuItem ueber = new JMenuItem("Ueber");
		ueber.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(graphic, "Testanwendung Version 0.0", "Ueber",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		hilfeMenu.add(ueber);
	}

	@Override
	public void boardClick(BoardClickEvent info) {
		System.out.println(info);
		xsend.farbe2(info.getX(), info.getY(), zeichenFarbe);
	}

}
