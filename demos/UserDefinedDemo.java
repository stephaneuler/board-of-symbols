package tests;

import jserver.Board;
import jserver.Symbol;
import jserver.SymbolDrawer;
import jserver.XSendAdapter;
import plotter.Plotter;
import plotter.Point;
import plotter.Point;

// Demo: user defined symbol
// - get the symbol 
// - set a own SymbolDrawer 
// - change the type to 
// SE, July 2024

public class UserDefinedDemo {
	XSendAdapter xs = new XSendAdapter();
	Board board = xs.getBoard();

	public static void main(String[] args) {
		UserDefinedDemo userDefinedDemo = new UserDefinedDemo();
		userDefinedDemo.demo();

	}

	private void demo() {
		int px = 2;
		int py = 2;
		xs.groesse(5, 5);
		
		Symbol symbol = board.getSymbol(px, py);
		symbol.setSymbolDrawer(new SymbolDrawer() {

			@Override
			public void drawSymbol(Plotter plotter, String key, Point center, double size) {
				
				plotter.setText("Eigenes", center.getX(), center.getY());
				
				Point[] points = { new Point(0, -1), new Point(1, 0), new Point(.5, 1), new Point(0, .5),
						new Point(-.5, 1), new Point(-1, 0), new Point(0, -1), };
				for (Point point : points) {
					plotter.add(key, center.add( point.mpy(size)));
				}

			}
		});
		xs.form2(px, py, "U");
		xs.farbe2(px, py, XSendAdapter.ORANGERED);
		xs.symbolGroesse2(py, px, 0.4);
	}

}
