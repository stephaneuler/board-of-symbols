package plotter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * A demo application for Plotter
 * 
 * @version 1.1 March 2011
 * @author Euler
 *
 */
public class GDemo extends JFrame
{

	static int verbose = 0;
	static int xsize = 800;
	static int ysize = 550;
	private static String version ="1.1 Maerz. 2011";
	private String[] demos = {"Sinus", "Collatz", "Kreise", "Spirale" , 
			"Text Spirale" , "Zufallsregen", "Histogram", "Tick", "Fonts" };


	/**
	 * 
	 * 
	 */
	public GDemo() {
		this( "GDemo " + version + "         Plotter " + Plotter.getVersion() );
	}

	public GDemo(String string) {
		System.out.println("creating  GDemo ");
		setName(string);
		setTitle(string);
		setSize(xsize, ysize);
		setLocation(500, 500);
		Component contents = svCreateComponents();
		getContentPane().add(contents, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		System.out.println("Graphic completed ");
	}

	/**
	 * create all components (buttons, sliders, views, etc and arrange them
	 */
	public Component svCreateComponents() {

		JPanel basePane = new JPanel();
		basePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		for( int i=0; i<demos.length; i++ ) {
			JButton b = new JButton(demos[i]);
			b.addActionListener( new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String s = e.getActionCommand();
					System.out.println( s );
					ThreadPlot tp = new ThreadPlot();
					tp.setMode(indexOf( s ));
					new Thread(tp).start();
 					//(new PlotDemo()).plot( indexOf( s ));		
				}
				
			}
			);
			basePane.add( b );
		}
		return basePane;
	}


	protected int indexOf(String s) {
		for( int i=0; i<demos.length; i++ ) {
			if( demos[i].equals(s) ) return i;
		}
		return -1;
	}

	/**
	 * 
	 */

	public static void main(String[] args) throws Exception {
		GDemo graphic = new GDemo();
	}
}
