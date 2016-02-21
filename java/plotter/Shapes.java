package plotter;

public class Shapes {

	public static String line(Plotter plotter, double x1, double y1, double x2, double y2) {
		String name = plotter.nextVector();
		plotter.add(x1, y1);
		plotter.add(x2, y2);
		
		return name;
	}
	
	public static String rect(Plotter plotter, double x1, double y1, double x2, double y2) {
		String name = plotter.nextVector();
		plotter.add(x1, y1);
		plotter.add(x2, y1);
		plotter.add(x2, y2);
		plotter.add(x1, y2);
		plotter.add(x1, y1);
		
		return name;
	}
	
}
