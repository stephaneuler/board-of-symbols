package jserver;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import plotter.Graphic;
import plotter.Plotter;
import plotter.Sleep;

// Version wer wann was
// .94     se 15-07    erste stabile Version
// .95     se 15-09    kleinere Erweiterungen (Hintergrund, ... )
// .96     se 15-09    hintergGrund2, symbolGroesse2
// .97     se 15-10-08 diverse Kleinigkeiten
// .973    se 15-10-18 Short cuts
// .974b   se 15-11-10 Bugfix filter mode
// .974c   se 15-12-01 save font size
//         se 16-03    start internationalization
//         se 17-01    keyboard listener
// 1.15    se 17-08    font selector

/**
 * This class implements a NxM board as used for board games like chess or
 * checkers. An instance of Plotter is used for drawing. The plotter is embedded
 * in a Plotter.Graphic object that provides a simple GUI with menus, buttons,
 * labels, etc. A board has a set of symbols. These symbols can be accessed by
 * index. The method receiveMessage() handles commands in the BoS Language
 * (BoSL).
 * 
 * 
 * @author Stephan Euler
 * 
 */
public class Board implements ActionListener, MouseListener, KeyListener {

	public static final String FILTER_PREFIX = ">>";

	private static String trainingText = "Training";
	private static String hashCodeText;
	private static String clearColorsText;
	private static String clearTextText;
	private static String fontSizeText;
	private static String fontTypeText = "Font";
	private static String growText;
	private static String shrinkText;
	private static String bigText;
	private static String rowText;
	private static String colText;
	private static String quadText;
	private static String interactiveText;
	private static String codingWindowText;
	private static String numberingText;
	private static String linNumberingText = "linNum";
	private static String alphaText;
	private static String sendText;
	private static String showText = "show events";
	private static final String runText = "ausführen";
	private static final String codingText = "C-Code Eingabe";
	private static final String formPrefix = "FORM_";
	private static final int RAW = 0;
	private static final int C = 1;

	private Graphic graphic;
	private Plotter plotter;
	private boolean examMode = false;
	private int messageCount = 0;
	private int rows = 10;
	private int columns = 10;
	private double radius = 0.5;
	private int symbolFontSize = Symbol.getFontSize();
	private int nSymbols = 0;
	private List<Symbol> symbols = new ArrayList<Symbol>();
	private SymbolType symbolType = SymbolType.CIRCLE;
	private JMenuItem alphaMenuItem;
	private JTextField input = new JTextField();
	private JLabel waitingCommands = new JLabel(" 0 ");
	private List<String> commands = new ArrayList<String>();
	private List<BoardClickListener> boardClickListener = new ArrayList<BoardClickListener>();
	private JButton sendButton;
	private JButton showButton;
	private boolean interactive = false;
	private boolean coding = false;
	private JTextArea codeInput = new JTextArea();
	private JTextArea messageField = new JTextArea();
	private JButton runButton = new JButton(runText);
	private JMenuItem linNumberingItem;
	private String lastError;
	private int errorCount;
	private boolean filterMode = false;
	private CodeExecutorC codeExecutor = new CodeExecutorC(this);
	private CodeWindow codeWindow;
	private Color defaultBackground;
	private boolean userDefinedStatusLine = false;
	private ResourceBundleWrapper messages;
	private Properties properties = new Properties();
	private Trainer trainer;
	private InfoBox commandInfo; 
	private FontSelector fontSelector;


	private String propertieFile = "board.properties";
	private String language = "de";
	private String country = "DE";

	private boolean verbose = false;
	private int hashCode;

	public Board() {

		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(propertieFile));
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			System.out.println("property file " + propertieFile + " not found");
		} catch (IOException e) {
			e.printStackTrace();
		}

		loadMessages();
		setStrings();
		SymbolType.setSymbolTexts(messages.getBundle());
		Symbol.setBoard(this);

		URL imageURL = Board.class.getResource("images/bos.jpg");
		graphic = new Graphic(messages.getBundle(), (new ImageIcon(imageURL)).getImage());
		plotter = graphic.getPlotter();

		graphic.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		graphic.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (codeWindow != null) {
					codeWindow.savePosition();
					if (codeWindow.isCodeHasChanged()) {
						int reply = Dialogs.codeHasChangedDialog(messages.getBundle());
						if (reply == JOptionPane.NO_OPTION) {
							return;
						}
					}
				}
				System.exit(0);
			}
		});

		String title = "Board, " + messages.getString("boardVersion");

		CodeExecutorC.setGccPath(properties.getProperty("gccPath"));

		if (examMode) {
			graphic.setResizable(false);
			title += " Exam Mode";
		} else {
			String p = properties.getProperty("columns");
			if (p != null) {
				columns = Integer.parseInt(p);
			}
			p = properties.getProperty("rows");
			if (p != null) {
				rows = Integer.parseInt(p);
			}

			Symbol.setNumbering(Boolean.parseBoolean(properties.getProperty("numbering")));
			Symbol.setLinearNumbering(Boolean.parseBoolean(properties.getProperty("linNumbering")));
		}

		graphic.setTitle(title);
		plotter.setPreferredSize(500, 500);
		defaultBackground = plotter.getBackground();
		graphic.pack();
		drawSymbols();

		commandInfo = new InfoBox(graphic, "", 400, 400);
		commandInfo.setTitle( "Commands");
		commandInfo.setVisible(false);
		
		symbolFontSize = Integer.parseInt(properties.getProperty("fontSize", "" + symbolFontSize));
		Symbol.setFontSize(symbolFontSize);

		sendButton = new JButton(sendText);
		sendButton.setToolTipText(messages.getString("tooltip.sendButton"));
		sendButton.addActionListener(this);
		
		showButton = new JButton(showText);
		//showButton.setToolTipText(messages.getString("tooltip.showButton"));
		showButton.addActionListener(this);
		
		
		runButton.addActionListener(this);
		messageField.setEditable(false);

		if (interactive) {
			graphic.addBottomComponent(input);
			graphic.addBottomComponent(sendButton);
			graphic.addBottomComponent(showButton);
			graphic.addBottomComponent(waitingCommands);
		}

		if (coding) {
			graphic.addEastComponent(codeInput);
			graphic.addEastComponent(runButton);
			graphic.addEastComponent(messageField);
		}

		graphic.removeDataMenu();
		graphic.addExternMenu(myBoardMenu());
		graphic.addExternMenu(myFormMenu());
		graphic.addExternMenu(myOptionsMenu());

		JMenu fileMenu = graphic.getFileMenu();
		// Utils.addMenuItem(this, fileMenu, hashCodeText,
		// messages.getString("tooltip.hashCode"));
		Utils.addMenuItem(this, fileMenu, trainingText);

		setStatusLineInfo();
		plotter.addMouseListener(this);
		graphic.addKeyListener(this);
		graphic.repaint();
	}

	public Properties getProperties() {
		return properties;
	}

	public void setSize(int width, int heigth) {
		plotter.setPreferredSize(width, heigth);
		graphic.pack();

	}

	/**
	 * Gets the texts from the resource bundle and copies them into variables.
	 */
	private void setStrings() {
		hashCodeText = messages.getString("hashCode");
		clearColorsText = messages.getString("clearColors");
		clearTextText = messages.getString("clearTexts");
		fontSizeText = messages.getString("fontSize");
		growText = messages.getString("grow");
		shrinkText = messages.getString("shrink");
		bigText = messages.getString("big");
		rowText = messages.getString("rows");
		colText = messages.getString("columns");
		quadText = messages.getString("square");
		interactiveText = messages.getString("interactive");
		codingWindowText = messages.getString("codingWindow");

		numberingText = Utils.concat(messages, "numbering", "onOff");
		alphaText = "Alpha " + messages.getString("value");
		sendText = messages.getString("send");

	}

	/**
	 * Gets a resource bundle with the locale specified in the properties.
	 */
	private void loadMessages() {

		language = properties.getProperty("language", language);
		country = properties.getProperty("country", country);

		Locale locale = new Locale(language, country);

		JComponent.setDefaultLocale(locale);

		ResourceBundle bundle = ResourceBundle.getBundle("config/MessagesBundle", locale);
		System.out.println( messages );
		if (verbose) {
			System.out.println(locale);
			System.out.println(bundle.getLocale());
			System.out.println(bundle.getString("boardVersion"));
		}

		if (!locale.equals(bundle.getLocale())) {
			String notFound = bundle.getString("notFoundUsing");
			String text = String.format(notFound, "" + locale, "" + bundle.getLocale());
			JOptionPane.showMessageDialog(graphic, text, "Language", JOptionPane.INFORMATION_MESSAGE);
		}
		messages = new ResourceBundleWrapper(bundle);
	}

	public Graphic getGraphic() {
		return graphic;
	}

	public CodeWindow getCodeWindow() {
		return codeWindow;
	}

	public Plotter getPlotter() {
		return plotter;
	}

	public ResourceBundle getMessages() {
		return messages.getBundle();
	}

	public ResourceBundleWrapper getMessageWrapper() {
		return messages;
	}


	public Symbol getSymbol(int i) {
		return symbols.get(i);
	}

	public Symbol getSymbol(int x, int y) {
		return symbols.get(x + y * columns);
	}

	private void setStatusLineInfo() {
		if (!userDefinedStatusLine) {
			String info = columns + "x" + rows + " " + messages.getString("board");
			plotter.setStatusLine(info);
		}
	}

	private JMenu myFormMenu() {
		JMenu menu = new JMenu(messages.getString("forms"));
		for (SymbolType type : SymbolType.values()) {
			JMenuItem mi = Utils.addMenuItem(this, menu, SymbolType.texts.get(type), SymbolType.tooltips.get(type));
			mi.setActionCommand(formPrefix + SymbolType.texts.get(type));
		}

		menu.addSeparator();
		Utils.addMenuItem(this, menu, numberingText, "Nummerierung der Elemente anzeigen", "alt N");

		alphaMenuItem = Utils.addMenuItem(this, menu, alphaText, "Alpha Wert für  Transparenz (Durchsichtigkeit)");
		alphaMenuItem.setEnabled(Symbol.isNumbering());

		return menu;
	}

	private JMenu myOptionsMenu() {
		JMenu menu = new JMenu(messages.getString("options"));

		Properties languages = new Properties();
		String languagesFile = "config/languages.properties";
		try {
			InputStream stream;
			stream = ClassLoader.getSystemClassLoader().getResourceAsStream(languagesFile);
			if (stream == null) {
				System.out.println("property file " + languagesFile + " not found");
				languages.setProperty("Deutsch", "de_DE");
				languages.setProperty("English (US)", "en_US");
			} else {
				languages.load(stream);
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String key : languages.stringPropertyNames()) {
			JMenuItem mi = Utils.addMenuItem(this, menu, key);
			mi.setActionCommand("LANG_" + languages.getProperty(key));
		}
		linNumberingItem = new JCheckBoxMenuItem(linNumberingText, Symbol.isLinearNumbering());
		linNumberingItem.addActionListener(this);
		menu.add(linNumberingItem);
		return menu;
	}

	private JMenu myBoardMenu() {
		JMenu menu = new JMenu(messages.getString("board"));

		Utils.addMenuItem(this, menu, clearColorsText, messages.getString("tooltip.clearColors"), "alt R");
		Utils.addMenuItem(this, menu, clearTextText, messages.getString("tooltip.clearTexts"));
		Utils.addMenuItem(this, menu, fontSizeText, messages.getString("tooltip.fontSize"));
		Utils.addMenuItem(this, menu, fontTypeText, messages.getString("tooltip.fontSize"));
		if (!examMode) {
			Utils.addMenuItem(this, menu, growText, messages.getString("tooltip.grow"), "alt PLUS");
			Utils.addMenuItem(this, menu, shrinkText, messages.getString("tooltip.shrink"), "alt MINUS");
			Utils.addMenuItem(this, menu, bigText, messages.getString("tooltip.big"));
			Utils.addMenuItem(this, menu, rowText, messages.getString("tooltip.columns"));
			Utils.addMenuItem(this, menu, colText, messages.getString("tooltip.rows"));
			Utils.addMenuItem(this, menu, quadText, messages.getString("tooltip.setSquare"), "alt Q");
			Utils.addMenuItem(this, menu, interactiveText, messages.getString("tooltip.interactive"), "alt I");
		}
		// Utils.addMenuItem(this, menu, codingText,
		// "Einblenden der Code-Eingabe");
		Utils.addMenuItem(this, menu, codingWindowText, messages.getString("tooltip.codingWindow"), "alt C");
		

		
		return menu;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Board();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public String status() {
		return "okay";
	}

	/**
	 * Commands in BoSL can be send with this method. The method parses the line
	 * and executes the command. In filter mode lines not starting with
	 * FILTER_PREFIX are ignored. This is helpful when the commands are mixed
	 * with other output.
	 * 
	 * 
	 * @param line
	 *            a line with (currently) one command
	 * @return okay or an error message
	 */
	public String receiveMessage(String line) {
		// System.out.println( "receiveMessage: " + line);
		line = line.trim();
		++messageCount;
		String info = columns + "x" + rows + " " + messages.getString("board") + " , " + messages.getString("message")
				+ "#" + messageCount + ": " + line;

		if (line.startsWith(FILTER_PREFIX)) {
			line = line.substring(FILTER_PREFIX.length()).trim();
		} else {
			if (filterMode) {
				return null;
			}
		}

		if (!userDefinedStatusLine) {
			plotter.setStatusLine(info);
		}

		String[] p = line.split("\\s+");

		if (line.startsWith("sleep")) {
			int time = Integer.parseInt(p[1]);
			Sleep.sleep(time);
			return "Slept for " + time + " ms";
		}

		if (line.startsWith("fontsize")) {
			symbolFontSize = Integer.parseInt(p[1]);
			Symbol.setFontSize(symbolFontSize);
			return "Okay";
		}

		if (line.startsWith("fonttype")) {
			String type = line.substring("fonttype".length() + 1 );
			System.out.println( ">>>" + type + "<<<" );
			Symbol.setFontType(type);
			redrawSymbols();
			return "Okay";
		}

		if (line.startsWith("statusfontsize")) {
			float fs = Float.parseFloat(p[1]);
			JLabel lab = graphic.getStatusLabel();
			Font f = lab.getFont();
			lab.setFont(f.deriveFont(fs));
			return "Okay";
		}

		if (line.startsWith("image")) {
			int index = Integer.parseInt(p[1]) + Integer.parseInt(p[2]) * columns;
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			symbols.get(index).setImage(p[3], plotter);
			redrawSymbol(symbols.get(index));
			return "image " + p[3];
		}

		if (line.equals("s")) {
			return "" + columns + " " + rows;
		}

		// poll last command
		if (line.equals("p")) {
			if (commands.isEmpty())
				return "";
			String command = commands.remove(0);
			waitingCommands.setText(" " + commands.size() + " ");
			commandInfo.setText( commands );
			return command;
		}

		if (line.equals("clearCommands")) {
			if (commands.isEmpty())
				return "";
			commands.clear();
			waitingCommands.setText(" " + commands.size() + " ");
			commandInfo.setText( commands );
			return "okay";
		}

		if (line.equals("c")) {
			actionPerformed(new ActionEvent(this, 0, clearColorsText));
			graphic.repaint();
			return "okay";
		}

		// set forms
		if (line.startsWith("f ")) {
			SymbolType s = SymbolType.getTypeFromShortName(line.substring(2));
			if (s == null) {
				return "Error: unknown form";
			}
			setNewSymbolType(s);
			graphic.repaint();
			return "okay";
		}

		// set forms
		if (line.startsWith("fi")) {
			if (p.length != 3) {
				return "ERROR - " + p.length + " args, should be  2";
			}
			SymbolType s = SymbolType.getTypeFromShortName(p[2]);
			if (s == null) {
				System.out.println("Error: unknown form: " + p[2]);
				return "Error: unknown form";
			}
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			symbols.get(index).setType(s);
			redrawSymbol(symbols.get(index));
			// redrawSymbols();
			return "okay";
		}

		// set forms
		if (line.startsWith("#fi")) {
			if (p.length != 4) {
				return "ERROR - " + p.length + " args, should be  4";
			}
			SymbolType s = SymbolType.getTypeFromShortName(p[3]);
			if (s == null) {
				return "Error: unknown form";
			}
			int index = Integer.parseInt(p[1]) + Integer.parseInt(p[2]) * columns;
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			symbols.get(index).setType(s);
			redrawSymbol(symbols.get(index));
			// redrawSymbols();
			return "okay";
		}

		// resize forms
		if (line.startsWith("symbolSizes ")) {
			double size = Double.parseDouble(p[1]);
			for (Symbol s : symbols) {
				s.setSize(size);
			}
			redrawSymbols();
			return "okay";
		}

		// resize form
		if (line.startsWith("s ")) {
			// System.out.println("Size: " + line);
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			if (p.length != 3) {
				return "ERROR - " + p.length + " args, should be  3";
			}
			double size = Double.parseDouble(p[2]);
			symbols.get(index).setSize(size);
			redrawSymbol(symbols.get(index));
			return "okay";

		}

		// resize form
		if (line.startsWith("#s")) {
			int col = Integer.parseInt(p[1]);
			int row = Integer.parseInt(p[2]);
			int index = col + row * columns;
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			if (p.length != 4) {
				return "ERROR - " + p.length + " args, should be  3";
			}
			double size = Double.parseDouble(p[3]);
			symbols.get(index).setSize(size);
			redrawSymbol(symbols.get(index));
			return "okay";

		}

		// resize board
		if (line.startsWith("r ")) {
			if (p.length != 3) {
				return "ERROR - " + p.length + " args, should be  2";
			}
			int newColumns = Integer.parseInt(p[1]);
			int newRows = Integer.parseInt(p[2]);
			// draw new symbols if size as changed
			if (newColumns != columns | newRows != rows) {
				columns = newColumns;
				rows = newRows;
				drawSymbols();
				graphic.repaint();
			}
			return "okay";
		}

		if (line.equals("n")) {
			actionPerformed(new ActionEvent(this, 0, numberingText));
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("t ")) {
			plotter.setStatusLine(line.substring(2));
			userDefinedStatusLine = true;
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("ba ")) {
			plotter.setBackground(new Color(parseColor(p[1])));
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("bo ")) {
			plotter.setBorderColor(new Color(parseColor(p[1])));
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("b ")) {
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			int icolor = parseColor(p[2]);
			if (icolor >= 0) {
				symbols.get(index).setHintergrund(new Color(icolor));
			} else {
				symbols.get(index).clearHintergrund();
			}
			symbols.get(index).zeichnen(plotter);
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("#b ")) {
			int col = Integer.parseInt(p[1]);
			int row = Integer.parseInt(p[2]);
			int index = col + row * columns;
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			int icolor = parseColor(p[3]);
			if (icolor >= 0) {
				symbols.get(index).setHintergrund(new Color(icolor));
			} else {
				symbols.get(index).clearHintergrund();
			}
			symbols.get(index).zeichnen(plotter);
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("T ")) {
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			String[] p2 = line.split("\\s+", 3);
			if (p2.length >= 3) {
				symbols.get(index).setText(p2[2]);
			} else {
				symbols.get(index).setText("");
			}
			symbols.get(index).zeichnen(plotter);
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("#T ")) {
			int col = Integer.parseInt(p[1]);
			int row = Integer.parseInt(p[2]);
			int index = col + row * columns;
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			String[] p2 = line.split("\\s+", 4);
			if (p2.length >= 4) {
				symbols.get(index).setText(p2[3]);
			} else {
				symbols.get(index).setText("");
			}
			symbols.get(index).zeichnen(plotter);
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("TC ")) {
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			int icolor = parseColor(p[2]);
			symbols.get(index).setTextFarbe(new Color(icolor));
			symbols.get(index).zeichnen(plotter);
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("clearAllText")) {
			actionPerformed(new ActionEvent(this, 0, clearTextText));
			return "okay";
		}

		if (line.startsWith("#TC ")) {
			int col = Integer.parseInt(p[1]);
			int row = Integer.parseInt(p[2]);
			int index = col + row * columns;
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			int icolor = parseColor(p[3]);
			symbols.get(index).setTextFarbe(new Color(icolor));
			symbols.get(index).zeichnen(plotter);
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("a ")) {
			for (int i = 0; i < symbols.size(); i++) {
				updateSymbol(i, p[1]);
			}
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("# ")) {
			int col = Integer.parseInt(p[1]);
			int row = Integer.parseInt(p[2]);
			int index = col + row * columns;
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			if (updateSymbol(index, p[3]))
				graphic.repaint();

			return "okay";
		}

		if (p.length < 2) {
			return "ERROR - only " + p.length + " ints, should be >= 2";
		}

		// letzte Möglichkeit: Index Farbe [Index Farbe ]*
		try {
			boolean change = false;
			for (int n = 0; n < p.length; n += 2) {
				int index = Integer.parseInt(p[n]);
				if (index < 0 | index >= nSymbols) {
					return "ERROR - " + index + " out of Range ";
				}
				change = updateSymbol(index, p[n + 1]) | change;
			}
			if (change) {
				// System.out.print("+");
				graphic.repaint();
			} else {
				// System.out.print("-");
			}

			return "okay";
		} catch (Exception e) {
			return "ERROR - can not parse line :" + line;
		}

	}

	private void redrawSymbol(Symbol symbol) {
		symbol.clearForm(plotter);
		symbol.zeichnen(plotter);
		graphic.repaint();
	}

	private boolean updateSymbol(int index, String colorString) {
		int colorInt = parseColor(colorString);
		return updateSymbol(index, colorInt);
	}

	private boolean updateSymbol(int index, int colorInt) {
		Color color = new Color(colorInt);
		if (color.equals(symbols.get(index).getFarbe())) {
			return false;
		} else {
			symbols.get(index).setFarbe(color);
			symbols.get(index).zeichnen(plotter);
			return true;
		}
	}

	public static int parseColor(String colorString) {
		int color = 0;
		if (colorString.startsWith("0x")) {
			color = Integer.parseInt(colorString.substring(2), 16);
		} else {
			color = Integer.parseInt(colorString);
		}
		return color;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		if (verbose) {
			System.out.println("Board cmd: " + cmd);
		}

		if (cmd.equals(clearColorsText)) {
			for (Symbol s : symbols) {
				s.reset();
				s.setSize(radius);
				s.zeichnen(plotter);
			}
			plotter.setBackground(defaultBackground);
			plotter.setBorderColor(Color.BLUE);
			graphic.repaint();

		} else if (cmd.equals(fontSizeText)) {
			String a = JOptionPane.showInputDialog(graphic, messages.getString("tooltip.fontSize"), symbolFontSize);
			if (a != null) {
				symbolFontSize = Integer.parseInt(a);
				Symbol.setFontSize(symbolFontSize);
			}

		} else if (cmd.equals(fontTypeText)) {
			if( fontSelector == null ) {
				fontSelector = new FontSelector( this );
			}
			fontSelector.setExtendedState(JFrame.NORMAL);
			fontSelector.setVisible(true);

		} else if (cmd.equals(clearTextText)) {
			System.out.println("Board:" + clearTextText);
			plotter.removeAllText();
			for (Symbol s : symbols) {
				s.setText(null);
			}
			redrawSymbols();
			graphic.repaint();

		} else if (cmd.startsWith(formPrefix)) {
			String formName = cmd.substring(formPrefix.length());
			if (SymbolType.hasType(formName)) {
				setNewSymbolType(SymbolType.getTypeFromText(formName));
			}

		} else if (cmd.equals(linNumberingText)) {
			Symbol.setLinearNumbering(linNumberingItem.isSelected());
			System.out.println(linNumberingText);
			if (Symbol.isNumbering()) {
				plotter.removeAllText();
				System.out.println("redraw after linNum change");
				redrawSymbols();
			}

		} else if (cmd.equals(numberingText)) {
			Symbol.toggleNumbering();
			if (!Symbol.isNumbering()) {
				plotter.removeAllText();
			}
			redrawSymbols();
			alphaMenuItem.setEnabled(Symbol.isNumbering());
		} else if (cmd.equals(growText)) {
			++columns;
			++rows;
			drawSymbols();
			setStatusLineInfo();
			graphic.repaint();
		} else if (cmd.equals(shrinkText)) {
			--columns;
			--rows;
			drawSymbols();
			setStatusLineInfo();
			graphic.repaint();
		} else if (cmd.equals(bigText)) {
			if (columns == 30 & rows == 30)
				return;
			columns = 30;
			rows = 30;
			drawSymbols();
			setStatusLineInfo();
			graphic.repaint();
		} else if (cmd.equals(rowText)) {
			String a = JOptionPane.showInputDialog(graphic, messages.getString("tooltip.rows"), new Integer(rows));
			if (a != null) {
				rows = Integer.parseInt(a);
				drawSymbols();
				setStatusLineInfo();
				graphic.repaint();
			}
		} else if (cmd.equals(colText)) {
			String a = JOptionPane.showInputDialog(graphic, messages.getString("tooltip.columns"),
					new Integer(columns));
			if (a != null) {
				columns = Integer.parseInt(a);
				drawSymbols();
				setStatusLineInfo();
				graphic.repaint();
			}
		} else if (cmd.equals(quadText)) {
			String a = JOptionPane.showInputDialog(graphic, messages.getString("tooltip.setSquare"),
					new Integer(columns));
			if (a != null) {
				columns = Integer.parseInt(a);
				rows = columns;
				drawSymbols();
				setStatusLineInfo();
				graphic.repaint();
			}
		} else if (cmd.equals(alphaText)) {
			String a = JOptionPane.showInputDialog(graphic, "Neuer Wert Alpha (0..255)", Symbol.getAlpha());
			if (a != null) {
				int alpha = Integer.parseInt(a);
				Symbol.setAlpha(alpha);
				redrawSymbols();
				graphic.repaint();
			}
		} else if (cmd.equals(hashCodeText)) {
			BoardSerializer bs = new BoardSerializer();

			bs.buildDocument(this);
			String s = bs.write();
			int hc = s.hashCode();
			String message = "HashCode; ";
			int mtype = JOptionPane.INFORMATION_MESSAGE;
			if (hc == hashCode) {
				message += " OKAY !!!";
			} else {
				message += " FAILED";
				mtype = JOptionPane.ERROR_MESSAGE;
			}
			JOptionPane.showMessageDialog(graphic, message, "Hash Code Check", mtype);

		} else if (cmd.equals(trainingText)) {
			if (trainer == null) {
				trainer = new Trainer(this);
				trainer.loadImage();
			}
			trainer.setVisible(true);

		} else if (cmd.equals(showText)) {
			//Dialogs.showStrings(commands, "Commands", graphic );
			commandInfo.setVisible( ! commandInfo.isVisible() );
			graphic.requestFocusInWindow();
			
		} else if (cmd.equals(sendText)) {
			if ("".equals(input.getText()))
				return;
			addCommand( input.getText() );
			input.setText("");
			graphic.requestFocusInWindow();

		} else if (cmd.equals(runText)) {
			String result = "";
			int scriptMode = C;
			if (scriptMode == RAW) {
				String[] lines = codeInput.getText().split("\\n");
				for (String line : lines) {
					if (!line.equals("")) {
						result += receiveMessage(line) + "\n";
					}
				}
			} else {
				result += "compile ...\n";
				String fileName = codeExecutor.createTmpSourceFile(codeInput.getText());
				if (fileName == null) {
					result += "ERROR : " + getLastError();
				} else {
					result += codeExecutor.compileAndExecute(fileName);
				}
			}
			messageField.setText(result);

		} else if (cmd.equals(interactiveText)) {
			if (interactive) {
				graphic.removeBottomComponent(input);
				graphic.removeBottomComponent(sendButton);
				graphic.removeBottomComponent(showButton);
				graphic.removeBottomComponent(waitingCommands);
			} else {
				graphic.addBottomComponent(input);
				graphic.addBottomComponent(sendButton);
				graphic.addBottomComponent(showButton);
				graphic.addBottomComponent(waitingCommands);
			}
			interactive = !interactive;
			graphic.pack();
			graphic.repaint();

		} else if (cmd.equals(codingText)) {
			if (coding)
				return;
			graphic.addEastComponent(codeInput);
			graphic.addEastComponent(runButton);
			graphic.addEastComponent(messageField);

			graphic.pack();
			graphic.repaint();
			coding = true;

		} else if (cmd.equals(codingWindowText)) {
			codeWindow = new CodeWindow(this);
		} else if (cmd.startsWith("LANG_")) {
			String[] parts = cmd.split("_");
			language = parts[1];
			country = parts[2];
			JOptionPane.showMessageDialog(graphic,
					messages.getString("languageChanged") + " " + messages.getString("restartRequired"), "Language",
					JOptionPane.INFORMATION_MESSAGE);

		}

		properties.setProperty("language", language);
		properties.setProperty("country", country);
		properties.setProperty("columns", "" + columns);
		properties.setProperty("rows", "" + rows);
		properties.setProperty("coding", "" + coding);
		properties.setProperty("numbering", "" + Symbol.isNumbering());
		properties.setProperty("linNumbering", "" + Symbol.isLinearNumbering());
		properties.setProperty("fontSize", "" + symbolFontSize);
		saveProperties();
	}

	private void addCommand(String text) {
		commands.add(text);
		waitingCommands.setText(" " + commands.size() + " ");
		commandInfo.setText( commands );
	}

	public void saveProperties() {
		try {
			properties.store(new FileWriter(propertieFile), "");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void drawSymbols() {
		plotter.removeAllDataObjects();
		plotter.removeAllText();
		plotter.setXrange(-1, columns);
		plotter.setYrange(-1, rows);

		nSymbols = 0;
		// symbols = new Symbol[rows * columns];
		symbols.clear();
		for (int m = 0; m < rows; m++) {
			for (int n = 0; n < columns; n++) {
				Symbol s = new Symbol(new Position(n, m), radius);
				s.setType(symbolType);
				s.setFarbe(Color.LIGHT_GRAY);
				s.setIndex(nSymbols);
				s.zeichnen(plotter);
				symbols.add(s);
				++nSymbols;
			}
		}

	}

	public void redrawSymbols() {
		for (Symbol s : symbols) {
			s.clearForm(plotter);
			s.zeichnen(plotter);
		}
		graphic.repaint();

	}

	private void setNewSymbolType(SymbolType type) {
		symbolType = type;
		for (Symbol s : symbols) {
			if (symbolType == SymbolType.RANDOM) {
				s.setType(SymbolType.getRandom());
			} else {
				s.setType(symbolType);
			}
			s.clearForm(plotter);
			s.zeichnen(plotter);
		}
		graphic.repaint();

	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public boolean isFilterMode() {
		return filterMode;
	}

	public void setFilterMode(boolean filterMode) {
		this.filterMode = filterMode;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public List<Symbol> getSymbols() {
		return symbols;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String message = "";
		int x = e.getX();
		int y = e.getY();
		// System.out.println("Mouse released at " + x + ", " + y);
		// System.out.println("# of clicks: " + e.getClickCount());
		// System.out.println("# button: " + e.getButton());

		double wx = plotter.scaleXR(x);
		double wy = plotter.scaleYR(y);
		// System.out.println("in world coordinates: " + wx + ", " + wy);
		int ix = (int) (wx + 0.5);
		int iy = (int) (wy + 0.5);
		// System.out.println("col, row: " + ix + ", " + iy);
		int pos = ix + iy * columns;

		for (int i = 0; i < e.getClickCount(); i++) {
			message += "#";
		}
		message += " " + pos + " " + ix + " " + iy + " " + e.getButton();
		// System.out.println(message);
		addCommand(message);

		for (BoardClickListener bcl : boardClickListener) {
			bcl.boardClick(new BoardClickEvent(ix, iy, e.getClickCount(), e.getButton()));
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	public void setSymbol(int i, Symbol s) {
		symbols.add(i, s);

	}

	public void addSymbol(Symbol s) {
		symbols.add(s);
	}

	public int getHashCode() {
		BoardSerializer bs = new BoardSerializer();

		bs.buildDocument(this);
		String s = bs.write();
		return s.hashCode();
	}

	public void clearSymbols() {
		symbols.clear();
		plotter.removeAllDataObjects();

	}

	public int getSymbolCount() {
		return symbols.size();
	}

	public void reset() {
		actionPerformed(new ActionEvent(this, 0, clearColorsText));
		actionPerformed(new ActionEvent(this, 0, clearTextText));
		setNewSymbolType(SymbolType.CIRCLE);
		plotter.setBackground(null);
		plotter.setBorderColor(Color.BLUE);

	}

	public void addClickListener(BoardClickListener listener) {
		boardClickListener.add(listener);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// System.out.println( e );
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// System.out.println( e );
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// System.out.println( e );
		String message = "$ " + e.getKeyCode() + " " + KeyEvent.getKeyText(e.getKeyCode());
		// System.out.println(message);
		addCommand(message);
	}

}
