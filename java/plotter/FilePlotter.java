package plotter;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

//Version wer wann was
//1.0 se 1305 erste Version

/**
 * @author Stephan Euler
 * 
 */
public class FilePlotter {
	private static final String version = "1.0 Mai 13";
	private boolean xyMode = false;
	private String colorCommand = "#color";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FilePlotter fp = new FilePlotter();
		String fileName = "test.txt";
		System.out.println(Arrays.toString(args));

		if (args.length > 0)
			fileName = args[args.length - 1];
		fp.readFile(fileName);

	}

	private void readFile(String fileName) {
		LineNumberReader lnr = null;
		try {
			FileReader fr = new FileReader(fileName);
			lnr = new LineNumberReader(fr);
		} catch (FileNotFoundException ex) {
			System.out.println(fileName + " not found");
			System.exit(1);
		}

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat();
		String time = formater.format(cal.getTime());
		Graphic graphic = new Graphic("Plot from " + fileName + " @ " + time
				+ "    V" + version);
		Plotter plotter = graphic.getPlotter();

		for (;;) {
			String line = null;
			try {
				line = lnr.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null)
				break;

			line = line.trim().toLowerCase();
			Scanner scanner = new Scanner(line);
			scanner.useLocale(Locale.US);

			System.out.println(line);
			if (line.startsWith("#linestyle")) {
				String[] parts = line.split(" +", 2);
				LineStyle ls = LineStyle.getLineStyleByName(parts[1]);
				if (ls != null) {
					plotter.setDataLineStyle(ls);
				}
			} else if (line.equals("#next")) {
				plotter.nextVector();
			} else if (line.startsWith(colorCommand)) {
				String name = line.substring(colorCommand.length()).trim();
				Color color = stringToColor(name);
				plotter.setDataColor(color);
			} else if (line.startsWith("#range")) {
				scanner.next();
				double min = scanner.nextDouble();
				double max = scanner.nextDouble();
				System.out.println("new range: " + min + " - " + max);
				plotter.setRange(min, max);
			} else if (line.startsWith("#xrange")) {
				scanner.next();
				double min = scanner.nextDouble();
				double max = scanner.nextDouble();
				System.out.println("new range: " + min + " - " + max);
				plotter.setXrange(min, max);
			} else if (line.startsWith("#yrange")) {
				scanner.next();
				double min = scanner.nextDouble();
				double max = scanner.nextDouble();
				System.out.println("new range: " + min + " - " + max);
				plotter.setYrange(min, max);
			} else if (line.startsWith("#xline")) {
				scanner.next();
				double x = scanner.nextDouble();
				plotter.setXLine(x);
			} else if (line.startsWith("#yline")) {
				scanner.next();
				double y = scanner.nextDouble();
				plotter.setYLine(y);
			} else if (line.equals("#xy")) {
				xyMode = true;
				System.out.println("Switch to xy-mode");
			} else {
				while (scanner.hasNextDouble()) {
					if (xyMode) {
						double x = scanner.nextDouble();
						double y = scanner.nextDouble();
						plotter.add(x, y);

					} else {
						double x = scanner.nextDouble();
						plotter.add(x);
					}
				}
			}
			scanner.close();
		}
		graphic.repaint();
	}

	public Color stringToColor(String value) {
		try {
			// first try hex or octal value
			return Color.decode(value);
		} catch (NumberFormatException nfe) {
			// try name
			try {
				Field f = Color.class.getField(value);
				return (Color) f.get(null);
			} catch (Exception ce) {
				return null;
			}
		}
	}
}
