package jserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

//import jdk.nashorn.internal.runtime.regexp.joni.constants.CCVALTYPE;
import plotter.Graphic;
import plotter.InfoBox;
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
// 1.15a   se 17-09-19 html unicode
// 1.18    se 18-06-04 image import

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
public class Board extends Adapters implements ActionListener, MouseListener, KeyListener {

	public static final double DEFAULT_SYMBOL_SIZE = 0.5;
	public static final int BIG_BOARD_SIZE = 30;
	public static final String FILTER_PREFIX = ">>";
	public static final String LS = System.lineSeparator();
	public static final String FS = System.getProperty("file.separator");

	private static final String formPrefix = "FORM_";

	private static String importImageText = "Import image";
	private static String trainingText = "Training";
	private static String historyText;
	private static String clearHistoryText;
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
	private static String analyseBoardText = "Analyse";
	private static String serialiseBoardText = "Serialise";
	private static String numberingText;
	private static String symbolSizeText;
	private static String linNumberingText = "linNum";
	private static String alphaText;
	private static String sendText;
	private static String colorText;
	private static String numberColorText;
	private static String helpFormText;
	private static String helpFunctionText;
	private static String helpEnvText = "Java info";
	private static String helpPropertiesText;
	private static String showText = "show events";
	private static String startServeText = "Start server";

	private Graphic graphic;
	private Plotter plotter;
	private JServer jserver;
	private boolean examMode = false;
	private double radius = DEFAULT_SYMBOL_SIZE;
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
	private JMenuItem linNumberingItem;
	private String lastError;
	private int errorCount;
	private boolean interactive = false;
	private boolean filterMode = false;
	private CodeWindow codeWindow;
	private Color defaultBackground;
	private boolean userDefinedStatusLine = false;
	private ResourceBundleWrapper messages;
	private Properties properties = new Properties();
	private Trainer trainer;
	private InfoBox commandInfo;
	private FontSelector fontSelector;
	private Map<String, String> lookAndFeels = new HashMap<>();
	private BoardCloser boardCloser;

	private String propertieFile = "board.properties";
	private String language = "de";
	private String country = "DE";
	private String fileOpenDirectory;

	private boolean verbose = false;
	private int hashCode;
	private int maxOutOfRange;
	private int outOfRangeCount;

	private List<String> messageHistory = Collections.synchronizedList( new LinkedList<>() );
	private List<ActionListener> commandListener = new ArrayList<>();
	private BoardModel boardModel = new BoardModel();
	private BoardAnalyser boardAnalyser;
	private InfoBox analyserInfo = null;
	private boolean saveMessages = true;

	public boolean isSaveMessages() {
		return saveMessages;
	}

	public void setSaveMessages(boolean saveMessages) {
		this.saveMessages = saveMessages;
	}

	public BoardModel getBoardModel() {
		return boardModel;
	}

	public List<String> getMessageHistory() {
		return messageHistory;
	}

	public void resetMessageHistory() {
		messageHistory.clear();
	}

	public int getMaxOutOfRange() {
		return maxOutOfRange;
	}

	public void resetMaxOutOfRange() {
		this.maxOutOfRange = 0;
		this.outOfRangeCount = 0;
	}

	public int getOutOfRangeCount() {
		return outOfRangeCount;
	}

	public Board() {

		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(propertieFile));
			properties.load(stream);
			stream.close();
			fileOpenDirectory = properties.getProperty("imageOpenDir");
		} catch (FileNotFoundException e) {
			System.out.println("property file " + propertieFile + " not found");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (properties.containsKey("lookAndFeel")) {
			try {
				UIManager.setLookAndFeel(properties.getProperty("lookAndFeel"));
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e1) {
				JOptionPane.showMessageDialog(graphic, e1.toString(), "Look & Feel", JOptionPane.ERROR_MESSAGE);
			}

		}
		loadMessages();
		setStrings();
		SymbolType.setSymbolTexts(messages.getBundle());
		Symbol.setBoard(this);
		boardAnalyser = new BoardAnalyser(this);

		URL imageURL = Board.class.getResource("images/bos.jpg");
		graphic = new Graphic(messages.getBundle(), (new ImageIcon(imageURL)).getImage());
		plotter = graphic.getPlotter();

		graphic.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		boardCloser = new BoardCloser(this);
		graphic.addWindowListener(boardCloser);

		String title = "Board, " + messages.getString("boardVersion");

		CodeExecutorC.setGccPath(properties.getProperty("gccPath"));

		if (examMode) {
			graphic.setResizable(false);
			title += " Exam Mode";
		} else {
			String p = properties.getProperty("columns");
			if (p != null) {
				boardModel.setColumns(Integer.parseInt(p));
			}
			p = properties.getProperty("rows");
			if (p != null) {
				boardModel.setRows(Integer.parseInt(p));
			}

			Symbol.setNumbering(Boolean.parseBoolean(properties.getProperty("numbering")));
			Symbol.setLinearNumbering(Boolean.parseBoolean(properties.getProperty("linNumbering")));
			String colorText = properties.getProperty("numberColor");
			if (colorText != null) {
				Symbol.setNumberTextColor(new Color(parseColor(colorText)));
			}
			colorText = properties.getProperty("symbolColor");
			if (colorText != null) {
				Symbol.setBoSColor(new Color(parseColor(colorText)));
			}
		}

		graphic.setTitle(title);
		plotter.setPreferredSize(500, 500);
		defaultBackground = plotter.getBackground();
		graphic.pack();
		drawSymbols();

		System.out.println("CE: " + boardAnalyser.getKC());

		commandInfo = new InfoBox(graphic, "", 400, 400);
		commandInfo.setTitle(messages.getString("Inputs"));
		commandInfo.setVisible(false);

		symbolFontSize = Integer.parseInt(properties.getProperty("fontSize", "" + symbolFontSize));
		Symbol.setFontSize(symbolFontSize);

		sendButton = new JButton(sendText);
		sendButton.setToolTipText(messages.getString("tooltip.sendButton"));
		sendButton.addActionListener(this);

		showButton = new JButton(showText);
		// showButton.setToolTipText(messages.getString("tooltip.showButton"));
		showButton.addActionListener(this);

		if (interactive) {
			graphic.addBottomComponent(input);
			graphic.addBottomComponent(sendButton);
			graphic.addBottomComponent(showButton);
			graphic.addBottomComponent(waitingCommands);
		}

		setMenus();

		setStatusLineInfo();
		plotter.addMouseListener(this);
		graphic.addKeyListener(this);
		graphic.repaint();
	}

	public Properties getProperties() {
		return properties;
	}

	public void addCommandListener(ActionListener listener) {
		commandListener.add(listener);
	}

	public boolean removeCommandListener(ActionListener listener) {
		return commandListener.remove(listener);
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
		showText = messages.getString("showInputs");
		codingWindowText = messages.getString("codingWindow");
		importImageText = messages.getString("importImage");
		historyText = messages.getString("showCommandHistory");
		clearHistoryText = messages.getString("clearCommandHistory");

		numberingText = Utils.concat(messages, "numbering", "onOff");
		symbolSizeText = messages.getString("symbolSize");
		alphaText = "Alpha " + messages.getString("value");
		sendText = messages.getString("send");
		helpFormText = messages.getString("forms");
		helpFunctionText = messages.getString("functions");
		colorText = messages.getString("symbolColor");
		numberColorText = messages.getString("numberColor");

		helpPropertiesText = messages.getString("properties");
	}

	/**
	 * Gets a resource bundle with the locale specified in the properties.
	 */
	private void loadMessages() {

		if (properties.containsKey("language")) {
			language = properties.getProperty("language", language);
			country = properties.getProperty("country", country);
		} else {
			askLanguage();
			properties.setProperty("language", language);
			properties.setProperty("country", country);
			saveProperties();
		}
		Locale locale = new Locale(language, country);

		JComponent.setDefaultLocale(locale);

		ResourceBundle bundle = ResourceBundle.getBundle("config/MessagesBundle", locale);
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

	private void askLanguage() {
		String localeInfo = Dialogs.askLanguage();
		String[] parts = localeInfo.split("_");
		language = parts[0];
		country = parts[1];
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
		return symbols.get(x + y * boardModel.getColumns());
	}

	private void setStatusLineInfo() {
		if (!userDefinedStatusLine) {
			String info = boardModel.getColumns() + "x" + boardModel.getRows() + " " + messages.getString("board");
			// info += " KC: " + boardAnalyser.getKC();
			plotter.setStatusLine(info);
		}
	}

	private void setMenus() {
		graphic.removeDataMenu();
		graphic.addExternMenu(myBoardMenu());
		graphic.addExternMenu(myFormMenu());
		graphic.addExternMenu(myOptionsMenu());
		graphic.addExternMenu(Box.createHorizontalGlue());
		graphic.addExternMenu(myHelpMenu());

		extendFileMenu(graphic.getFileMenu());
	}

	private void extendFileMenu(JMenu fileMenu) {
		fileMenu.addSeparator();
		Utils.addMenuItem(this, fileMenu, trainingText, "Start Trainer", "control T");
		Utils.addMenuItem(this, fileMenu, importImageText);
		fileMenu.addSeparator();
		Utils.addMenuItem(this, fileMenu, historyText);
		Utils.addMenuItem(this, fileMenu, clearHistoryText);
		Utils.addMenuItem(this, fileMenu, analyseBoardText);
		Utils.addMenuItem(this, fileMenu, serialiseBoardText);
		Utils.addMenuItem(this, fileMenu, startServeText);

	}

	private JMenu myFormMenu() {
		JMenu menu = new JMenu(messages.getString("forms"));
		for (SymbolType type : SymbolType.values()) {
			JMenuItem mi = Utils.addMenuItem(this, menu, SymbolType.texts.get(type), SymbolType.tooltips.get(type));
			mi.setActionCommand(formPrefix + SymbolType.texts.get(type));
		}

		menu.addSeparator();
		Utils.addMenuItem(this, menu, numberingText, messages.getString("tooltip.numbering"), "alt N");
		Utils.addMenuItem(this, menu, symbolSizeText,  messages.getString("tooltip.symbolSize") );

		alphaMenuItem = Utils.addMenuItem(this, menu, alphaText, "Alpha Wert f�r  Transparenz (Durchsichtigkeit)");
		alphaMenuItem.setEnabled(Symbol.isNumbering());

		return menu;
	}

	private JMenu myBoardMenu() {
		JMenu menu = new JMenu(messages.getString("board"));

		Utils.addMenuItem(this, menu, clearColorsText, messages.getString("tooltip.clearColors"), "alt R");
		Utils.addMenuItem(this, menu, clearTextText, messages.getString("tooltip.clearTexts"));
		Utils.addMenuItem(this, menu, fontSizeText, messages.getString("tooltip.fontSize"));
		Utils.addMenuItem(this, menu, fontTypeText, messages.getString("tooltip.fontType"));
		menu.addSeparator();
		if (!examMode) {
			Utils.addMenuItem(this, menu, growText, messages.getString("tooltip.grow"), "alt PLUS");
			Utils.addMenuItem(this, menu, shrinkText, messages.getString("tooltip.shrink"), "alt MINUS");
			Utils.addMenuItem(this, menu, bigText, messages.getString("tooltip.big"));
			Utils.addMenuItem(this, menu, rowText, messages.getString("tooltip.columns"));
			Utils.addMenuItem(this, menu, colText, messages.getString("tooltip.rows"));
			Utils.addMenuItem(this, menu, quadText, messages.getString("tooltip.setSquare"), "alt Q");
			menu.addSeparator();
			Utils.addMenuItem(this, menu, interactiveText, messages.getString("tooltip.interactive"), "alt I");
		}
		// Utils.addMenuItem(this, menu, codingText,
		// "Einblenden der Code-Eingabe");
		Utils.addMenuItem(this, menu, codingWindowText, messages.getString("tooltip.codingWindow"), "alt C");

		return menu;
	}

	private JMenu myOptionsMenu() {
		JMenu menu = new JMenu(messages.getString("options"));

		Properties languages = getAvailableLanguages();
		for (String key : languages.stringPropertyNames()) {
			JMenuItem mi = Utils.addMenuItem(this, menu, key);
			mi.setActionCommand("LANG_" + languages.getProperty(key));
		}
		linNumberingItem = new JCheckBoxMenuItem(linNumberingText, Symbol.isLinearNumbering());
		linNumberingItem.addActionListener(this);
		menu.add(linNumberingItem);
		Utils.addMenuItem(this, menu, colorText);
		Utils.addMenuItem(this, menu, numberColorText);

		JMenu lookAndFeelsMenu = new JMenu("Look & Feel");
		LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo info : plafs) {
			// System.out.println(info.getName());
			lookAndFeels.put(info.getName(), info.getClassName());
			Utils.addMenuItem(this, lookAndFeelsMenu, info.getName());
		}
		menu.add(lookAndFeelsMenu);

		return menu;
	}

	private JMenu myHelpMenu() {
		JMenu menu = new JMenu(messages.getString("help"));

		Utils.addMenuItem(this, menu, helpFormText);
		Utils.addMenuItem(this, menu, helpFunctionText);
		Utils.addMenuItem(this, menu, helpEnvText);
		Utils.addMenuItem(this, menu, helpPropertiesText);

		return menu;
	}

	static Properties getAvailableLanguages() {
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
		return languages;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Board();
			}
		});
	}

	public String status() {
		return "okay";
	}

	/**
	 * Commands in BoSL can be send with this method. The method parses the line and
	 * executes the command. In filter mode lines not starting with FILTER_PREFIX
	 * are ignored. This is helpful when the commands are mixed with other output.
	 * 
	 * 
	 * @param line a line with (currently) one command
	 * @return okay or an error message
	 */
	public String receiveMessage(String line) {
		// System.out.println( "receiveMessage: " + line);
		line = line.trim();
		// ++messageCount;
		String info = boardModel.getColumns() + "x" + boardModel.getRows() + " " + messages.getString("board") + " , "
				+ messages.getString("message") + "#" + messageHistory.size() + ": " + line;

		if (line.startsWith(FILTER_PREFIX)) {
			line = line.substring(FILTER_PREFIX.length()).trim();
		} else {
			if (filterMode) {
				return null;
			}
		}
		if (saveMessages) {
			messageHistory.add(line);
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
			String type = line.substring("fonttype".length() + 1);
			System.out.println(">>>" + type + "<<<");
			Symbol.setFontType(type);
			redrawSymbols();
			return "Okay";
		}

		if (line.startsWith("numberTextColor")) {
			Symbol.setNumberTextColor(new Color(parseColor(p[1])));
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
			int index = indexFromColRow(p[1], p[2]);
			if (index < 0 | index >= nSymbols) {
				return rangeError(p);
			}
			symbols.get(index).setImage(p[3], plotter);
			redrawSymbol(symbols.get(index));
			return "image " + p[3];
		}

		if (line.startsWith("bgImage")) {
			File file = new File(p[1]);
			try {
				BufferedImage img = ImageIO.read(file);
				plotter.setBackgroundImage(img);
				graphic.repaint();
			} catch (IOException e) {
				try {
					JOptionPane.showMessageDialog(graphic, e.getMessage() + " " + file.getCanonicalPath(), "bgImage",
							JOptionPane.ERROR_MESSAGE);
				} catch (HeadlessException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return e.getMessage();
			}
			return "image " + p[1];
		}

		if (line.startsWith("button")) {
			JButton b = new BoSButton(p[1], this);
			if (p.length < 3) {
				graphic.addEastComponent(b);
			} else {
				graphic.addComponent(b, p[2]);
			}
			graphic.pack();
			graphic.repaint();
			return "Okay";
		}

		if (line.startsWith("removeAllButtons")) {
			graphic.removeBorderComponents();
			interactive = false;
			graphic.pack();
			graphic.repaint();
			return "Okay";
		}

		if (line.equals("s")) {
			return "" + boardModel.getColumns() + " " + boardModel.getRows();
		}

		// poll last command
		if (line.equals("p")) {
			if (commands.isEmpty())
				return "";
			String command = commands.remove(0);
			waitingCommands.setText(" " + commands.size() + " ");
			commandInfo.setText(commands);
			return command;
		}

		if (line.equals("clearCommands")) {
			if (commands.isEmpty())
				return "";
			commands.clear();
			waitingCommands.setText(" " + commands.size() + " ");
			commandInfo.setText(commands);
			return "okay";
		}

		if (line.equals("c") | line.equals("clear")) {
			actionPerformed(new ActionEvent(this, 0, clearColorsText));
			graphic.repaint();
			return "okay";
		}

		// set forms, first test with alternative BoSL command
		if (line.startsWith("f ") | line.startsWith("forms ") ) {
			SymbolType s = SymbolType.getTypeFromShortName(p[1]);
			if (s == null) {
				return "Error: unknown form " + line.substring(2);
			}
			setNewSymbolType(s);
			graphic.repaint();
			return "okay";
		}

		// set forms i
		if (line.startsWith("fi")  | line.startsWith("form ")) {
			if (p.length != 3) {
				return "ERROR - " + p.length + " args, should be  2";
			}
			SymbolType s = SymbolType.getTypeFromShortName(p[2]);
			if (s == null) {
				return "Error: unknown form: " + p[2];
			}
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				updateOutOfRange(index);
				return "ERROR - " + index + " out of range\n ";
			}
			symbols.get(index).setType(s);
			redrawSymbol(symbols.get(index));
			return "okay";
		}

		// set forms xy
		if (line.startsWith("#fi")  | line.startsWith("form2 ") ) {
			if (p.length != 4) {
				return "ERROR - " + p.length + " args, should be  4";
			}
			SymbolType s = SymbolType.getTypeFromShortName(p[3]);
			if (s == null) {
				return "Error: unknown form";
			}
			int index = indexFromColRow(p[1], p[2]);
			if (index < 0 | index >= nSymbols) {
				return rangeError(p);
			}
			symbols.get(index).setType(s);
			redrawSymbol(symbols.get(index));
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
		if (line.startsWith("s " )  | line.startsWith("symbolSize ")  ) {
			if (p.length != 3) {
				return "ERROR - " + p.length + " args, should be  3";
			}
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				updateOutOfRange(index);
				return "ERROR - " + index + " out of range\n ";
			}
			updateSymbolSize(index, p[2]);
			return "okay";

		}

		// resize form
		if (line.startsWith("#s")  | line.startsWith("symbolSize2 ") ) {
			if (p.length != 4) {
				return "ERROR - " + p.length + " args, should be  3";
			}
			int index = indexFromColRow(p[1], p[2]);
			if (index < 0 | index >= nSymbols) {
				return rangeError(p);
			}
			updateSymbolSize(index, p[3]);
			return "okay";

		}

		// resize board
		if (line.startsWith("r ") | line.startsWith("resize ") ) {
			if (p.length != 3) {
				return "ERROR - " + p.length + " args, should be  2";
			}
			int newColumns = Integer.parseInt(p[1]);
			int newRows = Integer.parseInt(p[2]);
			// draw new symbols if size as changed
			if (newColumns != boardModel.getColumns() | newRows != boardModel.getRows()) {
				boardModel.setColumns(newColumns);
				boardModel.setRows(newRows);
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

		if (line.startsWith("t ")  | line.startsWith("statusText ")  ) {
			plotter.setStatusLine(line.substring( line.indexOf(" ") ));
			userDefinedStatusLine = true;
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("ba ")) {
			plotter.setBackground(new Color(parseColor(p[1])));
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("bo ") | line.startsWith("border ")) {
			plotter.setBorderColor(new Color(parseColor(p[1])));
			graphic.repaint();
			return "okay";
		}

		if (line.startsWith("b ")  | line.startsWith("background ") ) {
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				updateOutOfRange(index);
				return "ERROR - " + index + " out of range\n ";
			}
			updateSymbolBackgroundColor( index, p[2]);
			return "okay";
		}

		if (line.startsWith("#b ")  | line.startsWith("background2 ") ) {
			int index = indexFromColRow(p[1], p[2]);
			if (index < 0 | index >= nSymbols) {
				return rangeError(p);
			}
			updateSymbolBackgroundColor( index, p[3]);
			return "okay";
		}

		if (line.startsWith("T ")  | line.startsWith("text ") ) {
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				updateOutOfRange(index);
				return "ERROR - " + index + " out of range\n ";
			}
			String[] p2 = line.split("\\s+", 3);
			updateSymbolText( index, p2, 3 );
			return "okay";
		}

		if (line.startsWith("#T ")  | line.startsWith("text2 ") ) {
			int index = indexFromColRow(p[1], p[2]);
			if (index < 0 | index >= nSymbols) {
				return rangeError(p);
			}
			String[] p2 = line.split("\\s+", 4);
			updateSymbolText( index, p2, 4 );
			return "okay";
		}

		if (line.startsWith("textColor ")) {
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				updateOutOfRange(index);
				return "ERROR - " + index + " out of range\n ";
			}
			updateSymbolTextColor( index, p[2] );
			return "okay";
		}

		if (line.startsWith("textColor2 ")) {
			int index = indexFromColRow(p[1], p[2]);
			if (index < 0 | index >= nSymbols) {
				return rangeError(p);
			}
			updateSymbolTextColor( index, p[3] );
			return "okay";
		}

		if (line.startsWith("clearAllText")) {
			actionPerformed(new ActionEvent(this, 0, clearTextText));
			return "okay";
		}

		if (line.startsWith("toggleInput")) {
			actionPerformed(new ActionEvent(this, 0, interactiveText));
			return "okay";
		}

		if (line.startsWith("showInput")) {
			System.out.println("ShowInput: " + interactive);
			if (!interactive) {
				actionPerformed(new ActionEvent(this, 0, interactiveText));
			}
			return "okay";
		}

		if (line.startsWith("a ") | line.startsWith("colors ") ) {
			for (int i = 0; i < symbols.size(); i++) {
				updateSymbolColor(i, p[1]);
			}
			graphic.repaint();
			return "okay";
		}

		if ( line.startsWith("color ")  ) {
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				updateOutOfRange(index);
				return "ERROR - " + index + " out of range\n ";
			}
			if( updateSymbolColor(index, p[2])) {
				graphic.repaint();
			}
			return "okay";
		}

		if (line.startsWith("# ")  | line.startsWith("color2 ")  ) {
			int index = indexFromColRow(p[1], p[2]);
			if (index < 0 | index >= nSymbols) {
				return rangeError(p);
			}
			if (updateSymbolColor(index, p[3])) {
				graphic.repaint();
			}

			return "okay";
		}

		if (p.length < 2) {
			return "ERROR - only " + p.length + " ints, should be >= 2";
		}

		// letzte M�glichkeit: Index Farbe [Index Farbe ]*
		try {
			boolean change = false;
			for (int n = 0; n < p.length; n += 2) {
				int index = Integer.parseInt(p[n]);
				if (index < 0 | index >= nSymbols) {
					updateOutOfRange(index);
					return "ERROR - " + index + " out of range\n ";
				}
				change = updateSymbolColor(index, p[n + 1]) | change;
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

	private void updateSymbolText(int index, String[] p2, int textIndex) {
		if (p2.length >= textIndex) {
			symbols.get(index).setText(decodeUni(p2[textIndex-1]));
		} else {
			symbols.get(index).setText("");
		}
		symbols.get(index).zeichnen(plotter);
		graphic.repaint();
	}

	private void updateSymbolTextColor(int index, String string) {
		int icolor = parseColor( string );
		symbols.get(index).setTextFarbe(new Color(icolor));
		symbols.get(index).zeichnen(plotter);
		graphic.repaint();
	}

	private void updateSymbolSize(int index, String string) {
		double size = Double.parseDouble( string );
		symbols.get(index).setSize(size);
		redrawSymbol(symbols.get(index));
	}

	private void updateSymbolBackgroundColor(int index, String string) {
		int icolor = parseColor( string);
		if (icolor >= 0) {
			symbols.get(index).setHintergrund(new Color(icolor));
		} else {
			symbols.get(index).clearHintergrund();
		}
		symbols.get(index).zeichnen(plotter);
		graphic.repaint();
	}

	private String rangeError(String[] p) {
		return "ERROR -  (" + Integer.parseInt(p[1]) + "," + Integer.parseInt(p[2]) + ") out of range\n";
	}

	private void updateOutOfRange(int index) {
		++outOfRangeCount;
		maxOutOfRange = Integer.max(maxOutOfRange, index);
	}

	private int indexFromColRow(String s1, String s2) {
		int col = Integer.parseInt(s1);
		int row = Integer.parseInt(s2);
		if (col >= boardModel.getColumns() | row >= boardModel.getRows()) {
			updateOutOfRange(col + row * boardModel.getColumns());
			return -1;
		} else {
			return col + row * boardModel.getColumns();
		}
	}

	private String decodeUni(String text) {
		Matcher m = Pattern.compile("&#x([0-9a-fA-F]{1,6});").matcher(text);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String u = String.valueOf(Character.toChars(Integer.parseInt(m.group(1), 16)));
			m.appendReplacement(sb, u);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private void redrawSymbol(Symbol symbol) {
		symbol.clearForm(plotter);
		symbol.zeichnen(plotter);
		graphic.repaint();
	}

	private boolean updateSymbolColor(int index, String colorString) {
		int colorInt = parseColor(colorString);
		return updateSymbolColor(index, colorInt);
	}

	private boolean updateSymbolColor(int index, int colorInt) {
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
		if (colorString.startsWith("0x")) {
			return Integer.parseInt(colorString.substring(2), 16);
		} else {
			return Integer.parseInt(colorString);
		}
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
			if (fontSelector == null) {
				fontSelector = new FontSelector(this);
			}
			fontSelector.setExtendedState(JFrame.NORMAL);
			fontSelector.setVisible(true);

		} else if (cmd.equals(clearTextText)) {
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

		} else if (cmd.equals(symbolSizeText)) {
			String a = JOptionPane.showInputDialog(graphic, messages.getString("tooltip.symbolSize"),
					"" + 0.5);
			if (a != null) {
				double size = Double.parseDouble(a);
				for (Symbol s : symbols) {
					s.setSize(size);
				}
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
			boardModel.incColumns(1);
			boardModel.incRows(1);
			drawSymbols();
			setStatusLineInfo();
			graphic.repaint();
		} else if (cmd.equals(shrinkText)) {
			boardModel.incColumns(-1);
			boardModel.incRows(-1);
			drawSymbols();
			setStatusLineInfo();
			graphic.repaint();
		} else if (cmd.equals(bigText)) {
			if (boardModel.getColumns() == BIG_BOARD_SIZE & boardModel.getRows() == BIG_BOARD_SIZE)
				return;
			boardModel.setColumns(BIG_BOARD_SIZE);
			boardModel.setRows(BIG_BOARD_SIZE);
			drawSymbols();
			setStatusLineInfo();
			graphic.repaint();
		} else if (cmd.equals(rowText)) {
			String a = JOptionPane.showInputDialog(graphic, messages.getString("tooltip.rows"),
					"" +boardModel.getRows());
			if (a != null) {
				boardModel.setRows(Integer.parseInt(a));
				drawSymbols();
				setStatusLineInfo();
				graphic.repaint();
			}
		} else if (cmd.equals(colText)) {
			String a = JOptionPane.showInputDialog(graphic, messages.getString("tooltip.columns"),
					""+ boardModel.getColumns() );
			if (a != null) {
				boardModel.setColumns(Integer.parseInt(a));
				drawSymbols();
				setStatusLineInfo();
				graphic.repaint();
			}
		} else if (cmd.equals(quadText)) {
			String a = JOptionPane.showInputDialog(graphic, messages.getString("tooltip.setSquare"),
					new Integer(boardModel.getColumns()));
			if (a != null) {
				int size = Integer.parseInt(a);
				boardModel.setColumns(size);
				boardModel.setRows(size);
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
			// Dialogs.showStrings(commands, "Commands", graphic );
			commandInfo.setVisible(!commandInfo.isVisible());
			graphic.requestFocusInWindow();

		} else if (cmd.equals(sendText)) {
			for (ActionListener listener : commandListener) {
				listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, input.getText()));
			}
			if ("".equals(input.getText()))
				return;
			addCommand(input.getText());
			input.setText("");
			graphic.requestFocusInWindow();

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

		} else if (cmd.equals(codingWindowText)) {
			codeWindow = new CodeWindow(this);

		} else if (cmd.startsWith("LANG_")) {
			String[] parts = cmd.split("_");
			language = parts[1];
			country = parts[2];
			JOptionPane.showMessageDialog(graphic,
					messages.getString("languageChanged") + " " + messages.getString("restartRequired"), "Language",
					JOptionPane.INFORMATION_MESSAGE);

		} else if (cmd.equals(colorText)) {
			Color c = Symbol.getBoSColor();
			int v = c.getRed() * 256 * 256 + c.getGreen() * 256 + c.getBlue();
			String cs = "0x" + Integer.toHexString(v);
			String a = JOptionPane.showInputDialog(graphic, colorText, cs);
			if (a != null && a.trim().length() > 0) {
				try {
					Color cnew = Color.decode(a);
					Symbol.setBoSColor(cnew);
					for (Symbol s : symbols) {
						if (s.getFarbe() == c) {
							s.reset();
							s.zeichnen(plotter);
						}
					}
					graphic.repaint();
					properties.setProperty("symbolColor", a);
					saveProperties();
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(graphic, ex.toString(), "Number coversion",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		} else if (lookAndFeels.containsKey(cmd)) {
			try {
				UIManager.setLookAndFeel(lookAndFeels.get(cmd));
				properties.setProperty("lookAndFeel", lookAndFeels.get(cmd));
				SwingUtilities.updateComponentTreeUI(graphic);
				graphic.pack();
				if (codeWindow != null) {
					SwingUtilities.updateComponentTreeUI(codeWindow);
					codeWindow.pack();
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}

		} else if (cmd.equals(numberColorText)) {
			Color c = Symbol.getNumberTextColor();
			int v = c.getRed() * 256 * 256 + c.getGreen() * 256 + c.getBlue();
			String cs = "0x" + Integer.toHexString(v);
			String a = JOptionPane.showInputDialog(graphic, numberColorText, cs);
			if (a != null && a.trim().length() > 0) {
				try {
					Symbol.setNumberTextColor(Color.decode(a));
					redrawSymbols();
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(graphic, ex.toString(), "Number coversion",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		} else if (cmd.equals(helpFormText)) {
			InfoBox info = new InfoBox(graphic, "", 400, 500);
			info.setTitle(messages.getString("forms"));
			info.setIconImage(Dialogs.infoImage);
			info.getTextArea().setText(getFormsHelp());
			info.setVisible(true);

		} else if (cmd.equals(helpFunctionText)) {
			InfoBox info = new InfoBox(graphic, "", 600, 500);
			info.setTitle(messages.getString("functions"));
			info.setIconImage(Dialogs.infoImage);
			info.getTextArea().setContentType("text/html");
			info.getTextArea().setFont(new Font("Consolas", Font.PLAIN, 12));
			info.getTextArea().setText(getFunctionsHelp());
			info.setVisible(true);
			System.out.println(info.getTextArea().getFont());

		} else if (cmd.equals(helpEnvText)) {
			InfoBox info = new InfoBox(graphic, "", 400, 300);
			info.setTitle(messages.getString("Java info"));
			info.setIconImage(Dialogs.infoImage);
			info.getTextArea().setText(Utils.getJavaHelp());
			info.setVisible(true);

		} else if (cmd.equals(helpPropertiesText)) {
			InfoBox info = new InfoBox(graphic, "", 400, 300);
			info.setTitle(messages.getString("properties"));
			info.setIconImage(Dialogs.infoImage);
			info.getTextArea().setText( Utils.toText( properties ) );
			info.setVisible(true);

		} else if (cmd.equals(clearHistoryText)) {
			messageHistory.clear();

		} else if (cmd.equals(historyText)) {
			InfoBox info = new InfoBox(graphic, "", 400, 500);
			info.setTitle("BoSL Commands");
			info.setIconImage(Dialogs.infoImage);
			String text = "Received " + messageHistory.size() + " BoSL commands" + LS;
			int l = 1;
			if (messageHistory.size() > 200) {
				text += "... " + LS;
			}
			for (String m : messageHistory) {
				if (l >= messageHistory.size() - 200) {
					text += l + ": " + m + LS;
				}
				++l;
			}
			Map<String, Integer> doubles = boardAnalyser.getDuplicateMessages();
			if (doubles.size() > 0) {
				text += "duplicate messages: " + doubles.size() + LS;
				for (String message : doubles.keySet()) {
					text += "cmd : " + message + " Count : " + doubles.get(message) + LS;
				}
			}
			info.getTextArea().setText(text);
			info.setVisible(true);

		} else if (cmd.equals(analyseBoardText)) {
			if (analyserInfo == null) {
				analyserInfo = new InfoBox(graphic, "", 400, 500);
				JButton b = new JButton(analyseBoardText);
				b.addActionListener(this);
				analyserInfo.add(b, BorderLayout.NORTH);
				analyserInfo.setTitle("Board analyser");
				analyserInfo.setIconImage(Dialogs.infoImage);
			}

			boardAnalyser.getKC();
			int size = boardAnalyser.getSerialSize();
			int comp = boardAnalyser.getCompressedSize();
			String text = analyserInfo.getTextArea().getText();
			text += "*** " + Calendar.getInstance().getTime().toString() + LS;
			text += boardModel.getNumberSymbols() + " Symbols" + LS;
			text += "Serialised form, size: " + size + " Byte" + LS;
			text += "Serialised form, zip-size: " + comp + " Byte" + LS;
			// text += "Ratio: " + String.format("%.4f", 1000. * comp / size) + " mBit" +
			// LS;
			text += "required BoSL commands: " + boardAnalyser.calcNumBoSLCommands() + LS;
			if (codeWindow != null) {
				CodeAnalyser codeAnalyser = new CodeAnalyser(codeWindow.getCode());
				text += codeAnalyser.getResult();
			}
			analyserInfo.getTextArea().setText(text);
			analyserInfo.setVisible(true);

		} else if (cmd.equals(startServeText)) {
			if (jserver == null) {
				jserver = new JServer(this);
				JOptionPane.showMessageDialog(null, "Server started", "JServer", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "Server ist already running", "JServer",
						JOptionPane.INFORMATION_MESSAGE);
			}

		} else if (cmd.equals(serialiseBoardText)) {
			InfoBox serialiseInfo = new InfoBox(graphic, "", 400, 500);
			BoardSerializer bs = new BoardSerializer();
			bs.buildDocument(this);
			String text = bs.write();
			serialiseInfo.setTitle("Serialised board");
			serialiseInfo.setIconImage(Dialogs.infoImage);
			serialiseInfo.getTextArea().setText(text);
			serialiseInfo.setVisible(true);

		} else if (cmd.equals(importImageText)) {
			JFileChooser chooser = new JFileChooser();
			if (fileOpenDirectory != null) {
				chooser.setCurrentDirectory(new File(fileOpenDirectory));
			}
			chooser.setDialogTitle("Image import");
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			String[] fileSuffixes = ImageIO.getWriterFileSuffixes();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(" Images", fileSuffixes);
			chooser.setFileFilter(filter);

			int retval = chooser.showDialog(plotter, null);
			if (retval == JFileChooser.APPROVE_OPTION) {
				fileOpenDirectory = chooser.getCurrentDirectory().getAbsolutePath();
				properties.setProperty("imageOpenDir", fileOpenDirectory);
				try {
					properties.store(new FileWriter(propertieFile), "");
				} catch (IOException e) {
					e.printStackTrace();
				}
				String filename = chooser.getSelectedFile().getAbsolutePath();
				importImage(filename);
			} else {
				return;
			}

		}

		properties.setProperty("language", language);
		properties.setProperty("country", country);
		properties.setProperty("columns", "" + boardModel.getColumns());
		properties.setProperty("rows", "" + boardModel.getRows());
		properties.setProperty("numbering", "" + Symbol.isNumbering());
		properties.setProperty("numberColor", "" + Symbol.getNumberTextColor().getRGB());
		properties.setProperty("linNumbering", "" + Symbol.isLinearNumbering());
		properties.setProperty("fontSize", "" + symbolFontSize);
		saveProperties();
	}

	private void importImage(String filename) {
		File file = new File(filename);
		BufferedImage img;
		try {
			img = ImageIO.read(file);
			System.out.println("Image loaded: " + file.getName());
		} catch (IOException e) {
			System.out.println("Image failed: " + file.getName());
			return;
		}
		// System.out.println(img.getWidth() + " x " + img.getHeight());

		BufferedImage resized = ImageUtils.scaleImage(img, boardModel.getColumns(), boardModel.getRows());
		ImageUtils.drawImageWithSymbols(resized, this);

		// show code to generate this image
		InfoBox info = new InfoBox(graphic, "", 400, 300);
		info.setTitle(messages.getString("Image code"));
		info.setIconImage(Dialogs.infoImage);
		info.getTextArea().setText(ImageUtils.getImageCode(resized));
		info.setVisible(true);

	}

	private String getFormsHelp() {
		String text = "";
		for (SymbolType type : SymbolType.values()) {
			text += SymbolType.getShort(type) + "\t" + SymbolType.texts.get(type) + LS;
		}

		return text;
	}

	private String getFunctionsHelp() {
		String text = "<html><table>";
		String className = "jserver.XSend" + messages.getLocale().getLanguage().toUpperCase();
		String[] methods = MethodExtractor.getMethods(className);
		for (String method : methods) {
			text += "<tr><td><pre><small>" + method + "</small></pre></td><td>" + getFunctionHelp(method)
					+ "</td></tr>";
		}
		text += "</table></html>";

		return text;
	}

	private String getFunctionHelp(String method) {
		String[] parts = method.split("\\(");
		return messages.getString("fct_" + parts[0]);
	}

	public void addCommand(String text) {
		commands.add(text);
		waitingCommands.setText(" " + commands.size() + " ");
		commandInfo.setText(commands);
	}

	public void saveProperties() {
		try {
			properties.store(new FileWriter(propertieFile), "");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	void addRecentFilesToProperties() {
		if (codeWindow != null) {
			List<String> fileNames = codeWindow.getRecentFiles();
			for (int i = 0; i < fileNames.size(); i++) {
				properties.setProperty("RF_" + i, fileNames.get(i));
			}
		}
	}

	private void drawSymbols() {
		plotter.removeAllDataObjects();
		plotter.removeAllText();
		plotter.removeAllCirlces();
		plotter.setXrange(-1, boardModel.getColumns());
		plotter.setYrange(-1, boardModel.getRows());

		nSymbols = 0;
		// symbols = new Symbol[rows * columns];
		symbols.clear();
		for (int m = 0; m < boardModel.getRows(); m++) {
			for (int n = 0; n < boardModel.getColumns(); n++) {
				Symbol s = new Symbol(new Position(n, m), radius);
				s.setType(symbolType);
				s.setFarbe(Symbol.getBoSColor());
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
		return boardModel.getRows();
	}

	public int getColumns() {
		return boardModel.getColumns();
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
		int pos = ix + iy * boardModel.getColumns();

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

	public int getMessageCount() {
		return messageHistory.size();
	}

	public void reset() {
		actionPerformed(new ActionEvent(this, 0, clearColorsText));
		actionPerformed(new ActionEvent(this, 0, clearTextText));
		plotter.removeAllCirlces();
		setNewSymbolType(SymbolType.CIRCLE);
		plotter.setBackground(null);
		plotter.setBorderColor(Color.BLUE);

	}

	public void addClickListener(BoardClickListener listener) {
		boardClickListener.add(listener);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// System.out.println( e );
		String message = "$ " + e.getKeyCode() + " " + KeyEvent.getKeyText(e.getKeyCode());
		// System.out.println(message);
		addCommand(message);
	}

	public void removeBoardCloser() {
		graphic.removeWindowListener(boardCloser);

	}

	public void showCodingWindow() {
		codeWindow = new CodeWindow(this);
	}

}
