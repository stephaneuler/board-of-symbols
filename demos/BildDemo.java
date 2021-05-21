package demos;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

import jserver.Board;
import jserver.Symbol;
import jserver.XSendAdapter;
import plotter.Graphic;

// Beispiel fuer Bild in BoSym
// Stephan Euler
// 20.05.2021 

public class BildDemo implements ActionListener {
	private int anzahlSchritte = 50;
	private int gesamtDauer = 3000; 
	private Timer timer;
	private String bildDatei = "blume.jpg";
	// Dateiname kann Unterverzeichnisse enthalten
	//private String bildDatei = "pictures/b7.jpg";
	private String hintergrundDatei = "kiesel.jpg";
	private Board board = new Board();
	private XSendAdapter xsend = new XSendAdapter(board);
	private JTextField nField = new JTextField("" + anzahlSchritte);
	private JTextField gesamtDauerField = new JTextField("" + gesamtDauer);

	public static void main(String[] args) {
		(new BildDemo()).setUp();
	}


	private void setUp() {
		board.setSize(800, 120);
		xsend.groesse(1, 2);
		xsend.formen("none");
		xsend.groesse(anzahlSchritte, 1);
		board.receiveMessage("bgImage " + hintergrundDatei);
		
		JButton startButton = new JButton("Animation");
		Graphic graphic = board.getGraphic();
		graphic.addSouthComponent(startButton);
		startButton.addActionListener(  this );

		graphic.addSouthComponent( new JLabel( " #Schritte: "));
		Dimension d = new Dimension(60, 30);
		nField.setMaximumSize(d);
		nField.setHorizontalAlignment(JTextField.CENTER);
		graphic.addSouthComponent(nField);
		
		graphic.addSouthComponent( new JLabel( " Dauer [ms]: "));
		gesamtDauerField.setMaximumSize(d);
		gesamtDauerField.setHorizontalAlignment(JTextField.CENTER);
		graphic.addSouthComponent(gesamtDauerField);
		
		
		graphic.revalidate();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		board.receiveMessage("image "+ (anzahlSchritte-1) + " 0 - "  );
		anzahlSchritte = Integer.parseInt( nField.getText() );
		gesamtDauer = Integer.parseInt( gesamtDauerField.getText() );
		xsend.groesse(anzahlSchritte, 1);		
		timer = new Timer(gesamtDauer / anzahlSchritte , taskPerformer );
		timer.start();		
	}
	
	ActionListener taskPerformer = new ActionListener() {
		int x=0;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println( x );
			xsend.symbolGroesse2(x, 0, .1 + (double) x / anzahlSchritte );
			xsend.statusText( "x= " + x + " / " + anzahlSchritte);
			board.receiveMessage("image "+ x + " 0 " + bildDatei  );
			if( x > 0 ) {
				board.receiveMessage("image "+ (x-1) + " 0 -");
			}
			
			Symbol symbol = board.getSymbol(x, 0);
			// Breite wird nicht auf Symbolgroesse skaliert 
			// benoetigt neueste Version jserver.jar
			  symbol.getImageObject().setWorldWidth(0);
			
			++x;
			if( x == anzahlSchritte ) {
				x = 0;
				timer.stop();
			}
			
		}
	};

}
