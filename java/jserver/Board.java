package jserver;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import plotter.Graphic;
import plotter.Plotter;



// Version wer wann was
// .94 se 15-07 erste stabile Version
// .95 se 15-09 kleinere Erweiterungen (Hintergrund, ... )
// .96 se 15-09 hintergGrund2, symbolGroesse2
// .97 se 15-10-08 diverse Kleinigkeiten
// .973 se 15-10-18 Short cuts
// .974b se 15-11-10 Bugfix filter mode
// .974c se 15-12-01 save font size


/**
 * This class implements a NxM board as used for board games like chess or checkers. 
 * An instance of Plotter is used for drawing. 
 * The plotter is embedded in a Plotter.Graphic Object that provides a simple GUI with menus, buttons, labels, etc. 
 * A board has a list of symbols. These symbols can be accessed by index. 
 * The method receiveMessage handles commands in the BoS Language (BoSL).
 * 
 * 
 * @author Stephan Euler
 * @version {@value #VERSION}
 * 
 */
public class Board implements ActionListener, MouseListener {

	public static final String FILTER_PREFIX = ">>";
	public static final String VERSION = " 0.974c Dezember 2015";

	private static final String clearText = "Farben zurück setzten";
	private static final String clearTextText = "Texte löschen";
	private static final String fontSizeText = "Texte Größe";
	private static final String growText = "wachsen";
	private static final String shrinkText = "schrumpfen";
	private static final String bigText = "groß";
	private static final String rowText = "Zeilen";
	private static final String colText = "Spalten";
	private static final String quadText = "Quadrat";
	private static final String numberingText = "Nummerierung Ein/Aus";
	private static final String alphaText = "Alpha-Wert";
	private static final String sendText = "senden";
	private static final String runText = "ausführen";
	private static final String interactiveText = "Interaktiv";
	private static final String codingText = "C-Code Eingabe";
	private static final String codingWindowText = "Fenster für Code Eingabe";
	private static final int RAW = 0;
	private static final int C = 1;

	private Graphic graphic = new Graphic("Board, Version " + VERSION);
	private Plotter plotter = graphic.getPlotter();
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
	private JButton sendButton = new JButton(sendText);
	private boolean interactive = false;
	private boolean coding = false;
	private JTextArea codeInput = new JTextArea();
	private JTextArea messageField = new JTextArea();
	private JButton runButton = new JButton(runText);
	private String lastError;
	private int errorCount;
	private boolean filterMode = false;
	private CodeExecutorC codeExecutor = new CodeExecutorC(this);
	private CodeWindow codeWindow;
	private Color defaultBackground;
	private boolean userDefinedStatusLine = false;

	public Board() {

		// graphic.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		graphic.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		graphic.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (codeWindow != null && codeWindow.isCodeHasChanged()) {
					int reply = JOptionPane.showConfirmDialog(null,
							"Code wurde verändert, trotzdem schließen?",
							"Änderungen", JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.NO_OPTION) {
						return;
					}

				}
				System.exit(0);
			}
		});

		plotter.setPreferredSize(500, 500);
		defaultBackground = plotter.getBackground();
		graphic.pack();

		String p = graphic.getProperty("columns");
		if (p != null) {
			columns = Integer.parseInt(p);
		}
		p = graphic.getProperty("rows");
		if (p != null) {
			rows = Integer.parseInt(p);
		}

		Symbol.setNumbering(Boolean.parseBoolean(graphic
				.getProperty("numbering")));
		drawSymbols();

		symbolFontSize = Integer.parseInt(graphic.getProperty("fontSize", ""
				+ symbolFontSize));
		Symbol.setFontSize(symbolFontSize);

		sendButton.addActionListener(this);
		runButton.addActionListener(this);
		messageField.setEditable(false);

		if (interactive) {
			graphic.addBottomComponent(input);
			graphic.addBottomComponent(sendButton);
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

		setStatusLineInfo();
		plotter.addMouseListener(this);

		graphic.repaint();
	}

	public Graphic getGraphic() {
		return graphic;
	}

	public Plotter getPlotter() {
		return plotter;
	}

	public Symbol getSymbol(int i) {
		return symbols.get(i);
	}

	private void setStatusLineInfo() {
		if (!userDefinedStatusLine) {
			String info = columns + "x" + rows + " Feld";
			plotter.setStatusLine(info);
		}
	}

	private JMenu myFormMenu() {
		JMenu menu = new JMenu("Formen");
		for (SymbolType type : SymbolType.values()) {
			Utils.addMenuItem(this, menu, SymbolType.texts.get(type),
					SymbolType.tooltips.get(type));
		}

		menu.addSeparator();
		Utils.addMenuItem(this, menu, numberingText,
				"Nummerierung der Elemente anzeigen", "alt N");

		alphaMenuItem = Utils.addMenuItem(this, menu, alphaText,
				"Alpha Wert für  Transparenz (Durchsichtigkeit)");
		alphaMenuItem.setEnabled(Symbol.isNumbering());

		return menu;
	}

	private JMenu myBoardMenu() {
		JMenu menu = new JMenu("Brett");

		Utils.addMenuItem(this, menu, clearText, "alles löschen", "alt R");
		Utils.addMenuItem(this, menu, clearTextText, "Texte löschen" );
		Utils.addMenuItem(this, menu, fontSizeText, "Schriftgröße für Texte");
		Utils.addMenuItem(this, menu, growText, "wachsen", "alt PLUS");
		Utils.addMenuItem(this, menu, shrinkText, "schrumpfen", "alt MINUS");
		Utils.addMenuItem(this, menu, bigText, "groß");
		Utils.addMenuItem(this, menu, rowText, "Anzahl der Zeilen");
		Utils.addMenuItem(this, menu, colText, "Anzahl der Spalten");
		Utils.addMenuItem(this, menu, quadText, "Anzahl der Zeilen, Spalten", "alt Q");
		Utils.addMenuItem(this, menu, interactiveText,
				"Einblenden des Eingabefeldes", "alt I");
		// Utils.addMenuItem(this, menu, codingText,
		// "Einblenden der Code-Eingabe");
		Utils.addMenuItem(this, menu, codingWindowText,
				"Öffnet Fenster für Code-Eingabe", "alt C");

		return menu;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Board board = new Board();
		// Random r = new Random();
		// for (;;) {
		// Sleep.sleep(600);
		// board.receiveMessage("" + r.nextInt(board.nSymbols) + " "
		// + r.nextInt(2));
		// }

	}

	public String status() {
		return "okay";
	}

	/**
	 * Commands in BoSL can be send with this method. 
	 * The method parses the line and executes the command. 
	 * In filter mode lines not starting with FILTER_PREFIX are ignored. 
	 * This is helpfull when the commands are mixed with other output. 
	 * 
	 * 
	 * @param line a line with (currently) one command
	 * @return okay or an error message
	 */
	public String receiveMessage(String line) {
		// System.out.println( "receiveMessage: " + line);
		line = line.trim();
		++messageCount;
		String info = columns + "x" + rows + " Feld, Nachricht #" + messageCount
				+ ": " + line;

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

		if (line.equals("s")) {
			return "" + columns + " " + rows;
		}

		// poll last command
		if (line.equals("p")) {
			if (commands.isEmpty())
				return "";
			waitingCommands.setText(" " + (commands.size() - 1) + " ");
			return commands.remove(0);
		}

		if (line.equals("c")) {
			actionPerformed(new ActionEvent(this, 0, clearText));
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
				return "Error: unknown form";
			}
			int index = Integer.parseInt(p[1]);
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			symbols.get(index).setType(s);
			redrawSymbol( symbols.get(index) );
			//redrawSymbols();
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
			int index = Integer.parseInt(p[1]) + Integer.parseInt(p[2])
					* columns;
			if (index < 0 | index >= nSymbols) {
				return "ERROR - " + index + " out of Range ";
			}
			symbols.get(index).setType(s);
			redrawSymbol( symbols.get(index) );
			//redrawSymbols();
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
			redrawSymbol( symbols.get(index) );
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
			redrawSymbol( symbols.get(index) );
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
			if( p2.length >= 3 ) {
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
			if( p2.length >= 4 ) {
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
		System.out.println("World cmd: " + cmd);

		if (cmd.equals(clearText)) {
			for (Symbol s : symbols) {
				s.reset();
				s.setSize(radius);
				s.zeichnen(plotter);
			}
			plotter.setBackground(defaultBackground);
			plotter.setBorderColor(Color.BLUE);
			graphic.repaint();

		} else if (cmd.equals(fontSizeText)) {
			String a = JOptionPane.showInputDialog(graphic, "Schriftgröße",
					symbolFontSize);
			if (a != null) {
				symbolFontSize = Integer.parseInt(a);
				Symbol.setFontSize(symbolFontSize);
			}

		} else if (cmd.equals(clearTextText)) {
			plotter.removeAllText();
			for (Symbol s : symbols) {
				s.setText(null);
			}
			redrawSymbols();
			graphic.repaint();
		} else if (SymbolType.hasType(cmd)) {
			setNewSymbolType(SymbolType.getTypeFromText(cmd));
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
			String a = JOptionPane.showInputDialog(graphic, "Anzahl Zeilen",
					new Integer(rows));
			if (a != null) {
				rows = Integer.parseInt(a);
				drawSymbols();
				setStatusLineInfo();
				graphic.repaint();
			}
		} else if (cmd.equals(colText)) {
			String a = JOptionPane.showInputDialog(graphic, "Anzahl Spalten",
					new Integer(columns));
			if (a != null) {
				columns = Integer.parseInt(a);
				drawSymbols();
				setStatusLineInfo();
				graphic.repaint();
			}
		} else if (cmd.equals(quadText)) {
			String a = JOptionPane.showInputDialog(graphic,
					"Anzahl Zeilen, Spalten", new Integer(columns));
			if (a != null) {
				columns = Integer.parseInt(a);
				rows = columns;
				drawSymbols();
				setStatusLineInfo();
				graphic.repaint();
			}
		} else if (cmd.equals(alphaText)) {
			String a = JOptionPane.showInputDialog(graphic,
					"Neuer Wert Alpha (0..255)", Symbol.getAlpha());
			if (a != null) {
				int alpha = Integer.parseInt(a);
				Symbol.setAlpha(alpha);
				redrawSymbols();
				graphic.repaint();
			}
		} else if (cmd.equals(sendText)) {
			if ("".equals(input.getText()))
				return;
			commands.add(input.getText());
			input.setText("");
			waitingCommands.setText(" " + commands.size() + " ");
			input.requestFocusInWindow();

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
				String fileName = codeExecutor.createTmpSourceFile(codeInput
						.getText());
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
				graphic.removeBottomComponent(waitingCommands);
			} else {
				graphic.addBottomComponent(input);
				graphic.addBottomComponent(sendButton);
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
		}
		graphic.saveProperty("rows", "" + rows);
		graphic.saveProperty("columns", "" + columns);
		graphic.saveProperty("coding", "" + coding);
		graphic.saveProperty("numbering", "" + Symbol.isNumbering());
		graphic.saveProperty("fontSize", "" + symbolFontSize);

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

	@Override
	public void mouseClicked(MouseEvent e) {
		String message = "";
		// System.out.println(e);
		int x, y;
		x = e.getX();
		y = e.getY();
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

		// if (e.getClickCount() == 2) {
		// // System.out.println("pos: " + pos );
		// updateSignal(pos, 0xff0000);
		// graphic.repaint();
		// }

		for (int i = 0; i < e.getClickCount(); i++) {
			message += "#";
		}
		message += " " + pos + " " + ix + " " + iy;
		// System.out.println(message);
		commands.add(message);
		waitingCommands.setText(" " + commands.size() + " ");

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

	public void clearSymbols() {
		symbols.clear();
		plotter.removeAllDataObjects();

	}

	public int getSymbolCount() {
		return symbols.size();
	}

}
