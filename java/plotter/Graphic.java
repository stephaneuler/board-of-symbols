package plotter;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

// Version wer wann was
// 1.2     se  1303 Screen-Dump, print
// 1.21    se  1312 addExternMenu
// 1.5     se  1609 diverse Ergänzungen

/**
 * A JFrame to host the plotter panel
 * 
 * @version 1.5 Sep. 2016
 * @author Stephan Euler
 * 
 * 
 */

public class Graphic extends JFrame implements ActionListener, Printable {

	private static final long serialVersionUID = -2263967270489771875L;
	static int verbose = 0;
	static int xsize = 800;
	static int ysize = 550;
	private static String version = "1.21 Dez. 2013";

	private Plotter plotter = null;
	private JLabel status = new JLabel();
	private JMenuBar menuBar = new JMenuBar();
	private JMenu dataMenu;
	private JMenu fileMenu;
	private String[] fileSuffixes = ImageIO.getWriterFileSuffixes();
	private String fileOpenDirectory = "";
	private String dumpText = "Zeige Daten";
	private String saveText = "Bild speichern unter ...";
	private String saveTTText = "Speichert Screenshot in Datei";
	private String exportText = "Daten exportieren nach ...";
	private String deleteDataText = "Daten löschen";
	private String showStatusText = "Status";
	private String showStatusTTText = "Zeigt Plotter Status";
	private String printText = "Drucken ...";
	private String printTTText = "Senden an Drucker";
	private String fileText = "Datei";
	private Properties properties = new Properties();
	private String propertieFile = "graphic.ini";
	private Box top = new Box(BoxLayout.X_AXIS);
	private Box bottom = new Box(BoxLayout.X_AXIS);
	private Box east = new Box(BoxLayout.Y_AXIS);
	private Box west = new Box(BoxLayout.Y_AXIS);

	/**
	 * 
	 * 
	 */
	public Graphic() {
		this("Graphic " + version);
	}

	public Graphic(String string) {
		plotter = new Plotter("Plotter");
		plotter.setPreferredSize(new Dimension(500, 300));
		setup(string);
	}

	public Graphic(String string, Plotter plotter) {
		this.plotter = plotter;
		setup(string);
	}

	public Graphic(ResourceBundle messages) {
		this(messages, null);

	}

	public Graphic(ResourceBundle messages, Image icon) {
		fileText = messages.getString("file");
		saveText = messages.getString("imageSave");
		saveTTText = messages.getString("tooltip.imageSave");
		printText = messages.getString("print");
		printTTText = messages.getString("tooltip.print");
		showStatusText = messages.getString("status");
		showStatusTTText = messages.getString("tooltip.status");

		if (icon != null) {
			setIconImage(icon);
		}

		plotter = new Plotter("Plotter");
		plotter.setPreferredSize(new Dimension(500, 300));
		setup("Graphic " + version);
	}

	public void setup(String string) {
		System.out.println("creating  Graphic");

		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(propertieFile));
			properties.load(stream);
			stream.close();
			fileOpenDirectory = properties.getProperty("saveDir");
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			System.out.println("property file " + propertieFile + " not found");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setName(string);
		setTitle(string);
		setSize(xsize, ysize);
		Component contents = svCreateComponents();
		getContentPane().add(contents, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
		System.out.println("Graphic completed ");
	}

	/**
	 * create all components (buttons, sliders, views, etc) and arrange them
	 */
	public Component svCreateComponents() {

		top.add(status);

		// now combine all panes into base pane
		JPanel basePane = new JPanel();
		basePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		basePane.setLayout(new BorderLayout());
		basePane.add("Center", plotter);
		basePane.add("North", top);
		basePane.add("South", bottom);
		basePane.add("East", east);
		basePane.add("West", west);

		JMenuItem mi;

		fileMenu = new JMenu(fileText);
		menuBar.add(fileMenu);
		mi = new JMenuItem(saveText);
		mi.addActionListener(this);
		mi.setToolTipText(saveTTText);
		fileMenu.add(mi);

		mi = new JMenuItem(printText);
		mi.addActionListener(this);
		mi.setToolTipText(printTTText);
		fileMenu.add(mi);

		mi = new JMenuItem(showStatusText);
		mi.addActionListener(this);
		mi.setToolTipText(showStatusTTText);
		fileMenu.add(mi);

		setJMenuBar(menuBar);

		dataMenu = new JMenu("Daten");
		menuBar.add(dataMenu);

		mi = new JMenuItem(dumpText);
		mi.addActionListener(this);
		mi.setToolTipText("Zeigt alle Daten an");
		dataMenu.add(mi);

		mi = new JMenuItem(exportText);
		mi.addActionListener(this);
		mi.setToolTipText("Speichert Daten in Datei");
		dataMenu.add(mi);

		mi = new JMenuItem(deleteDataText);
		mi.addActionListener(this);
		mi.setToolTipText("Alle Daten löschen");
		dataMenu.add(mi);

		return basePane;
	}

	public void addExternMenu(JMenu menu) {
		menuBar.add(menu);
	}

	public void addTopComponent(Component comp) {
		top.add(comp);
	}

	public void addBottomComponent(Component comp) {
		bottom.add(comp);
	}

	public void removeBottomComponent(Component comp) {
		bottom.remove(comp);
	}

	public void addNorthComponent(Component comp) {
		top.add(comp);
	}

	public void addSouthComponent(Component comp) {
		bottom.add(comp);
	}

	public void addEastComponent(Component comp) {
		east.add(comp);
	}

	public void addWestComponent(Component comp) {
		west.add(comp);
	}

	/**
	 * 
	 */

	public static void main(String[] args) throws Exception {
		Graphic graphic = new Graphic();
		Plotter plotter = graphic.getPlotter();

		System.out.println(Arrays.toString(ImageIO.getWriterFileSuffixes()));
		System.out.println(Arrays.toString(ImageIO.getWriterFormatNames()));

		plotter.setAutoYgrid(.5);
		plotter.setAutoXgrid(.5);
		plotter.setYLine(1);
		plotter.setYLine(-1);
		plotter.setXLine(Math.PI);

		for (double x = 0; x < 2 * Math.PI; x += 0.05) {
			double y = Math.sin(x);
			plotter.add("sin2", x, y);
			plotter.add("sin", x, y * y);
		}
	}

	/**
	 * @return the plotter
	 */
	public Plotter getPlotter() {
		return plotter;
	}

	public JMenu getFileMenu() {
		return fileMenu;
	}

	/**
	 * 
	 */
	public void paint(Graphics g) {
		status.setText(plotter.getStatusLine());
		try {
			super.paint(g);
		} catch (ConcurrentModificationException e) {
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		System.out.println("cmd: " + cmd);

		if (cmd.equals(saveText)) {
			@SuppressWarnings("unchecked")
			SwingWorker<Boolean, Void> worker = new ScreenShoter<Boolean, Void>();
			worker.execute();
		} else if (cmd.equals(exportText)) {
			SwingWorker<Boolean, Void> worker = new DataSaver<Boolean, Void>();
			worker.execute();
		} else if (cmd.equals(deleteDataText)) {
			plotter.removeAllDataObjects();
			plotter.repaint();
		} else if (cmd.equals(showStatusText)) {
			String status = "";
			status += "# objects: \n";
			status += "data: \t" + plotter.getDataObjects().size() + "\n";
			status += "text: \t" + plotter.getTextObjectsCount() + "\n";
			status += "image: \t" + plotter.getImageObjectsCount() + "\n";
			status += "#paints: \t" + plotter.getPaintCalls() + "\n";
			JOptionPane.showMessageDialog(this, status, "Plotter status", JOptionPane.INFORMATION_MESSAGE);

		} else if (cmd.equals(dumpText)) {
			SwingWorker<Boolean, Void> worker = new DataDumper<Boolean, Void>();
			worker.execute();
		} else if (cmd.equals(printText)) {
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setPrintable(this);
			boolean ok = job.printDialog();
			if (ok) {
				try {
					job.print();
				} catch (PrinterException ex) {
					/* The job did not successfully complete */
				}
			}

		}

	}

	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		if (page > 0) { /* We have only one page, and 'page' is zero-based */
			return NO_SUCH_PAGE;
		}

		/*
		 * User (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
		 */
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());

		/* Now print the window and its visible contents */
		printAll(g);

		/* tell the caller that this page is part of the printed document */
		return PAGE_EXISTS;
	}

	public String getProperty(String string) {
		System.out.println(string + " :  " + properties.getProperty(string));
		return properties.getProperty(string);
	}

	public String getProperty(String string, String defValue) {
		// System.out.println(string + " : "
		// + properties.getProperty(string, defValue));
		return properties.getProperty(string, defValue);
	}

	public void saveProperty(String name, String value) {
		properties.setProperty(name, value);
		try {
			properties.store(new FileWriter(propertieFile), "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
	}

	private String getExtension(String filename) {
		String[] parts = filename.split("\\.");
		if( parts.length == 1 ) {
			return null;
		}
		return parts[parts.length - 1];
	}

	private boolean testSuffix(String filename) {
		String extension = getExtension(filename);
		System.out.println(extension);
		for (String s : fileSuffixes) {
			if (s.equals(extension))
				return true;
		}
		return false;
	}

	class ScreenShoter<T, V> extends SwingWorker {

		@Override
		public Boolean doInBackground() {
			Robot robot = null;
			String dest;
			System.out.println("Save to file , using directory " + fileOpenDirectory);

			JFileChooser chooser = new JFileChooser();
			if (fileOpenDirectory != null) {
				chooser.setCurrentDirectory(new File(fileOpenDirectory));
			}
			chooser.setDialogTitle("Screen Shot");
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(" Images", fileSuffixes);
			chooser.setFileFilter(filter);

			int retval = chooser.showDialog(plotter, null);
			if (retval == JFileChooser.APPROVE_OPTION) {
				fileOpenDirectory = chooser.getCurrentDirectory().getAbsolutePath();
				properties.setProperty("saveDir", fileOpenDirectory);
				try {
					properties.store(new FileWriter(propertieFile), "");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String filename = chooser.getSelectedFile().getAbsolutePath();
				if( getExtension(filename) == null ) {
					filename += ".png";
				}
 				System.out.println(filename);
				if (!testSuffix(filename)) {
					System.out.println("Unknown extension");
					JOptionPane.showMessageDialog(plotter,
							"Unbekannter Dateityp, bitte wählen aus:" + Arrays.toString(fileSuffixes));
					return false;
				}
				dest = filename;
			} else {
				return false;
			}

			try {
				robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
				return false;
			}

			Sleep.sleep(500);

			Rectangle r = getBounds();
			BufferedImage screenShot = robot.createScreenCapture(r);
			try {
				ImageIO.write(screenShot, getExtension(dest), new File(dest));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

	}

	public boolean saveImageToFile(String dest) {
		Robot robot;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			return false;
		}

		Sleep.sleep(500);

		Rectangle r = getBounds();
		BufferedImage screenShot = robot.createScreenCapture(r);
		try {
			ImageIO.write(screenShot, getExtension(dest), new File(dest));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public BufferedImage getImage() {
		Component component = plotter;
		BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		// call the Component's paint method, using
		// the Graphics object of the image.
		component.paint(image.getGraphics()); // alternately use .printAll(..)
		return image;
	}

	class DataSaver<T, V> extends SwingWorker {

		@Override
		public Boolean doInBackground() {
			String dest = "dataset.dat";
			System.out.println(fileOpenDirectory);
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(fileOpenDirectory));
			chooser.setDialogTitle("Export Data");
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);

			int retval = chooser.showDialog(plotter, null);
			if (retval == JFileChooser.APPROVE_OPTION) {
				fileOpenDirectory = chooser.getCurrentDirectory().getAbsolutePath();
				properties.setProperty("saveDir", fileOpenDirectory);
				try {
					properties.store(new FileWriter(propertieFile), "");
				} catch (IOException e) {
					e.printStackTrace();
				}
				String filename = chooser.getSelectedFile().getAbsolutePath();
				System.out.println(filename);
				dest = filename;
			} else {
				return false;
			}

			PrintWriter pw;
			try {
				pw = new PrintWriter(dest);
				Map<String, DataObject> m = plotter.getDataObjects();
				for (String o : m.keySet()) {
					m.get(o).print(o, pw);
				}
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}

	}

	class DataDumper<T, V> extends SwingWorker {

		@Override
		public Boolean doInBackground() {
			Map<String, DataObject> m = plotter.getDataObjects();
			int i = 0;
			for (String o : m.keySet()) {
				System.out.println(i + ". " + o + ": ");
				++i;
				System.out.println(m.get(o).dumpString());
			}
			return true;
		}

	}

	public void removeDataMenu() {
		menuBar.remove(dataMenu);

	}

	public JLabel getStatusLabel() {
		return status;

	}
}
