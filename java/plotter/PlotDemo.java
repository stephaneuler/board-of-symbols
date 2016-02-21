package plotter;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Random;

/**
 * Examples for using the Plotter class to draw data
 * 
 * @author Euler
 * 
 */
public class PlotDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PlotDemo pd = new PlotDemo();
		int mode = 8;
		pd.plot( mode );
		
	}

	public void plot(int mode) {
		switch (mode) {
		case 0:
			rechne0(); // Sinus
			break;
		case 1:
			rechne1(); // Collatz
			break;
		case 2:
			rechne2(); // Kreise
			break;
		case 3:
			spirale(); // Spirale
			break;
		case 4:
			textSpirale(); // Text Spirale
			break;
		case 5:
			rechne5(); // Einheitskreise
			break;
		case 6:
			rechne6(); // Histogram
			break;
		case 7:
			rechne7(); // Ticks
			break;
		case 8:
			rechne8(); // Fonts
			
			break;
		}
	}

	public void rechne0() {
		Graphic graphic = new Graphic("Sinus");
		Plotter plotter = graphic.getPlotter();
		plotter.setAutoYgrid(.5);
		plotter.setAutoXgrid(.5);
		plotter.setYLine(1);
		plotter.setYLine(-1);
		plotter.setXLine(Math.PI);
		plotter.setDataLineStyle("sin", LineStyle.IMPULS);
		plotter.setDataColor("sin", Color.CYAN);

		for (double x = 0; x < 2 * Math.PI; x += 0.05) {
			double y = Math.sin(x);
			// System.out.println( y );
			plotter.add("sin2", x, y);
			plotter.add("sin", x, y * y);
		}

	}

	public void rechne2() {
		Graphic graphic = new Graphic("Kreise");
		Plotter plotter = graphic.getPlotter();
		plotter.setXLine(0);
		plotter.setYLine(0);
		plotter.setXrange(-1.25, 1.25);

		for (double r = 1.; r > 0; r -= 0.1) {
			for (double t = 0; t < 2 * Math.PI; t += 0.01) {
				plotter.add(r * Math.sin(t), r * Math.cos(t));
				// Kardioide
				// plotter.add(r * Math.cos(t)*(1+Math.cos(t)), r *
				// Math.sin(t)*(1+Math.cos(t)));
			}
			plotter.nextVector();
		}
		graphic.repaint();
	}

	public void spirale() {
		Graphic graphic = new Graphic("Spirale");
		Plotter plotter = graphic.getPlotter();
		plotter.setText("Eine Spirale", 0.8, 0.85);
		TextObject tO = plotter.setText("hier auch", -0.8, -0.85);
		tO.setColor(Color.BLACK);
		plotter.setXLine(0);
		plotter.setYLine(0);
		plotter.setXrange(-1.25, 1.25);
		plotter.setYrange(-1.25, 1.25);

		double r = 1;
		double delta = 0.01;
		while (r > 0) {
			for (double t = 0; t < 2 * Math.PI; t += delta) {
				plotter.add(r * Math.sin(t), r * Math.cos(t));
				r -= 0.2 * delta / 2 / Math.PI;
				graphic.repaint();
				Sleep.sleep(20);
			}
		}
	}

	public void textSpirale() {
		Graphic graphic = new Graphic("Schlangen-Spirale");
		Plotter plotter = graphic.getPlotter();
		plotter.setXLine(0);
		plotter.setYLine(0);
		plotter.setXrange(-1.25, 1.25);
		plotter.setYrange(-1.25, 1.25);

		double r = 1;
		double delta = 0.1;
		int count = 0;
		while (r > 0) {
			for (double t = 0; t < 2 * Math.PI; t += delta) {
				plotter.setText("O", r * Math.sin(t), r * Math.cos(t));
				r -= 0.1 * delta / 2 / Math.PI;
				graphic.repaint();
				Sleep.sleep(30);
				++count;
				if (count > 15) {
					plotter.removeText("O");
					plotter.setText("*", r * Math.sin(t - 15 * delta), r
							* Math.cos(t - 15 * delta));
				}
			}
		}
	}

	public void rechne5() {
		int anzahl = 30000;
		Graphic graphic = new Graphic("Zufallsregen");
		graphic.setBounds(new Rectangle(0,0,500,500));
		Plotter plotter = graphic.getPlotter();
		plotter.setXrange(0, 1);
		plotter.setYrange(0, 1);

		Graphic graphic2 = new Graphic("Pi");
		graphic2.setBounds(new Rectangle(500,0,500,500));
		Plotter plotter2 = graphic2.getPlotter();
		plotter2.setYrange(3, 3.25);
		plotter2.setXrange(0, anzahl);
		plotter2.setYLine( Math.PI);
		
		plotter.setDataLineStyle(LineStyle.DOT);
		plotter.setDataColor("in", Color.RED);

		Random random = new Random();
		int drin = 0;
		for (int n = 0; n < anzahl ; n++) {
			double x = random.nextFloat();
			double y = random.nextFloat();
			if (x * x + y * y < 1) {
				plotter.add("in", x, y);
				++drin;
			} else {
				plotter.add("out", x, y);
			}
			if( n % 1000 == 0 & n > 0) {
				System.out.println((double) n + " " + 4. * drin / n );
				plotter2.add( (double) n, 4. * drin / n);
				plotter2.repaint();
				plotter.repaint();
			}
		}
		
	}

	/**
	 * Collatz Problem <br>
	 * See <a href="http://de.wikipedia.org/wiki/Collatz-Problem"
	 * target="_top">Wikipedia</a>
	 */
	public void rechne1() {
		Graphic graphic = new Graphic();
		Plotter plotter = graphic.getPlotter();
		plotter.setAutoXgrid(50);
		plotter.setAutoYgrid(2500);
		plotter.setYrange(0, 20000);
		plotter.setDataLineStyle(LineStyle.BOTH);
		plotter.setSymbolSize(5);

		int anz = 100;
		for (int nc = 1; nc < anz; nc++) {
			int count = 1;
			long n = nc;
			plotter.add(n + nc * 100);
			while (n != 1) {
				++count;
				if (n % 2 == 1)
					n = 3 * n + 1;
				else
					n = n / 2;
				plotter.add(n + nc * 100);
			}
			System.out.println(nc + " : " + count);
			plotter.nextVector();
			graphic.repaint();
		}
	}

	public void rechne6() {
		int anz = 50;

		Graphic graphic = new Graphic("Histogramm");
		Plotter plotter = graphic.getPlotter();
		plotter.setXrange(-0.5, anz - 1 + 0.5);
		plotter.setYrange(1./anz*.7, 1./anz * 1.3);
		plotter.setAutoYgrid(.01);
		plotter.setYLine(1./anz);

		int[] feld = new int[anz];

		plotter.setDataLineStyle(LineStyle.HISTOGRAM);
		plotter.setHalfBarWidth(0.3);
		plotter.setYLabelFormat("%.2f%%");

		Random random = new Random();

		double sum = 0;
		do {
			plotter.clearPlotVector();
			for (int n = 0; n < 200; n++) {
				int w = random.nextInt(anz);
				++feld[w];
			}
			sum = 0;
			for (int n : feld)
				sum += n;
			for (int n : feld)
				plotter.add(n / sum);
			Sleep.sleep(300);
			graphic.repaint();
		} while (sum < 1000000);
	}

	public void rechne7() {
		Graphic graphic = new Graphic("Ticks");
		Plotter plotter = graphic.getPlotter();
		double[] xtick = {1998, 2005};
		String[] xl = {"min", "max" };
		plotter.setXGrid(xtick);
		plotter.setXLabel(xl);
		plotter.setXLine(2000);
		plotter.setAutoYgrid(1000);
		plotter.setYLabelFormat("%.0f");
	
		double[] studis = { 1500, 1300, 1500, 1200, 1600, 4000, 4400, 5000, 4900, 5200, 5400, 4800, 4700 };
		for( int i=0; i<studis.length; i++  ) {
		plotter.add(1995+i, studis[i]);
		}
	
	}
	public void rechne8() {
		Graphic graphic = new Graphic("Fonts");
		Plotter plotter = graphic.getPlotter();
		String text = "Technische Hochschule Mittelhessen";
		int len = text.length();
		plotter.setXrange(-10, 10);
		plotter.setYrange(-2, 12);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    Font[] fonts = ge.getAllFonts();
	    System.out.println( "found " + fonts.length + " fonts");
		Random random = new Random();
	    

		// 10 Texte übereinander
		for (int i = 0; i < 10; i++) {
			// Schreibe Text zentriert um Position 0,i
			// Rückgabe ist das neu angelegte Textobjekt
			TextObject t = plotter.setText(text, 0, i);
			// nimmt den ersten Font, leite davon eine Variante in Größe 20 ab
			float size = 12 + random.nextInt(16);;
			Font testFont = fonts[(int) (Math.random() * fonts.length)]
					.deriveFont(size);
			// Zusätzliche Ausgabe des Fontnamens
			System.out.println( testFont.getFontName());
			// setzte den neuen Font für den Text
			t.setFont(testFont);
			// jetzt noch Farbe ändern
			t.setColor(new Color( random.nextInt(255), i*255/len, random.nextInt(255)));
		}

		plotter.repaint();
	}
}
