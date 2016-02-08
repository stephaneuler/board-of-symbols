package jserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.crypto.spec.GCMParameterSpec;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import plotter.Graphic;
import plotter.Sleep;

/**
 * @author Euler
 * @version 0.1  July 2015
 * 
 */
//Version wer wann        was
//.94  se  15-10-15    prüft auf ungespeicherte Änderungen
//.95  se  15-10-22    undo
//.96  se  15-10-25    load xml file
//.98  se  15-12-05    Java support
//.98a se  15-12-15    Bug bei fehlendem ini-Eintrag compiler

/**
 * @author Euler
 * 
 */
@SuppressWarnings("serial")
public class CodeWindow extends JFrame implements ActionListener,
		DocumentListener, ExecutorListener {
	private static String version = "0.99 Dezember 2015";
	//private static final int C = 1;
	private static final int componentXSize = 160;
	private static final int componentYSize = 25;

	private static final String runText = "ausführen";
	private static final String stopText = "abbrechen";
	private static final String saveText = "speichern";
	private static final String saveAsText = "speichern untern";
	private static final String loadCommand = "loadSnippet";
	private static final String colorSelectorCommand = "selectColor";
	private static final String fontIncText = "Zoom +";
	private static final String fontDecText = "Zoom -";
	private static final String bigFontText = "Groß";
	private static final String normalFontText = "100%";
	private static final String authorText = "AutorIn";
	private static final String codeFileText = "Code Datei";
	private static final String exeCommandtext = "Befehl";
	private static final String exeNametext = "Name für Programm";
	private static final String commandsFromFiletext = "Aus Datei";
	private static final String showColorChooserText = "Farbwähler";
	private static final String helpText = "Hilfe";
	private static final String autoLayoutText = "formatieren";

	private int xsize = 500;
	private int ysize = 350;
	private int xpos = 600;
	private int ypos = 0;
	private int bigFontSize = 32;
	private int normalFontSize = 16;
	private int fontSize = normalFontSize;

	private Font normalFont = new Font("Consolas", Font.PLAIN, fontSize);
	private Font bigFont = new Font("Consolas", Font.PLAIN, bigFontSize);
	private JEditorPane codeInput = new JEditorPane();
	private JTextArea messageField = new JTextArea();
	private JTextField snippetNameField = new JTextField();
	private JLabel snippetNameLabel = new JLabel(
			"<html><em>letzte Sicherung</em></html>");
	private JLabel statusLabel = new JLabel();
	private JLabel executionInfoLabel = new JLabel();
	private JLabel infoLabel = new JLabel();
	private String snippetName;
	private JColorChooser colorChooser = new JColorChooser();
	private JButton runButton = new JButton();
	private JButton stopButton = new JButton();
	private JButton saveButton = new JButton(saveText);
	private JButton saveAsButton = new JButton(saveAsText);
	private JButton showColorChooserButton = new JButton();
	private Box center = new Box(BoxLayout.Y_AXIS);
	private Box controllBox = new Box(BoxLayout.X_AXIS);
	private Board board;
	private CodeDB codeDB = new CodeDB();
	private JComboBox<String> snippetSelector = new JComboBox<String>();
	private JComboBox<String> colorSelector = new JComboBox<String>();
	private CodeExecutor codeExecutor;
	private String authorName = "nobody";
	private String fileOpenDirectory = null;
	private boolean codeHasChanged = false;
	private String imageDir = "images/";
	private String stopImgLocation = imageDir + "Stop16.gif";
	private String playImgLocation = imageDir + "Play16.gif";
	private String colorImgLocation = imageDir + "color.gif";

	private Document editorPaneDocument;
	protected UndoHandler undoHandler = new UndoHandler();
	protected UndoManager undoManager = new UndoManager();
	private UndoAction undoAction = null;
	private RedoAction redoAction = null;
	private CodeLayouter codeLayouter = new CodeLayouter();

	public CodeWindow(Board board) {
		this.board = board;
		board.setFilterMode(true);
		String a = board.getGraphic().getProperty("author");
		if (a != null) {
			authorName = a;
		}
		fileOpenDirectory = board.getGraphic().getProperty("codeDir");
		String mode = board.getGraphic().getProperty("compiler",
				CodeExecutor.gccText);
		codeExecutor = CodeExecutor.getExecutor(mode, board, this);

		setup("CodeWindow " + version);
		updateInfoLabel();

	}

	private String readXMLFile() {
		String fileName = askCodeFileNam();
		if (fileName == null)
			return null;
		codeDB.setXmlFile(new File(fileName));
		try {
			codeDB.readXML();
		} catch (ParserConfigurationException | SAXException | IOException e2) {
			JOptionPane.showMessageDialog(this,
					"Fehler beim Lesen der Codes-Datei: " + e2.getMessage(),
					"Codes lesen", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return fileName;

	}

	private String askCodeFileNam() {
		JFileChooser chooser = new JFileChooser();
		if (fileOpenDirectory != null) {
			chooser.setCurrentDirectory(new File(fileOpenDirectory));
		}
		chooser.setDialogTitle("Code Datei");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		String fileSuffixes = "xml";
		FileNameExtensionFilter filter = new FileNameExtensionFilter(" Images",
				fileSuffixes);
		chooser.setFileFilter(filter);

		int retval = chooser.showDialog(null, null);
		if (retval == JFileChooser.APPROVE_OPTION) {
			fileOpenDirectory = chooser.getCurrentDirectory().getAbsolutePath();
			board.getGraphic().saveProperty("codeDir", fileOpenDirectory);
			String filename = chooser.getSelectedFile().getAbsolutePath();
			System.out.println(filename);
			return filename;
		} else {
			return null;
		}

	}

	private void savePosition() {
		board.getGraphic().saveProperty("codeWindowPosX", "" + getBounds().x);
		board.getGraphic().saveProperty("codeWindowPosY", "" + getBounds().y);
		board.getGraphic().saveProperty("codeWindowWidth",
				"" + getBounds().width);
		board.getGraphic().saveProperty("codeWindowHeight",
				"" + getBounds().height);
	}

	private void loadPosition() {
		Graphic g = board.getGraphic();
		String s;
		s = g.getProperty("codeWindowPosX", "" + xpos);
		xpos = Integer.parseInt(s);
		s = g.getProperty("codeWindowPosY", "" + ypos);
		ypos = Integer.parseInt(s);
		s = g.getProperty("codeWindowWidth", "" + xsize);
		xsize = Integer.parseInt(s);
		s = g.getProperty("codeWindowHeight", "" + ysize);
		ysize = Integer.parseInt(s);

	}

	private void setup(String string) {
		try {
			codeDB.readXML();
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			JOptionPane.showMessageDialog(this,
					"Fehler beim Lesen der Codes-Datei: " + e1.getMessage(),
					"Codes lesen", JOptionPane.ERROR_MESSAGE);
			readXMLFile();
		}

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (codeHasChanged) {
					int reply = JOptionPane.showConfirmDialog(null,
							"Code wurde verändert, trotzdem schließen?",
							"Änderungen", JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.NO_OPTION) {
						return;
					}
					codeHasChanged = false;

				}
				savePosition();
				dispose();
			}

		});

		loadPosition();
		setName(string);
		setTitle(string);
		setSize(xsize, ysize);
		setLocation(xpos, ypos);
		Component contents = svCreateComponents();
		getContentPane().add(contents, BorderLayout.CENTER);
		// pack();
		setVisible(true);
		System.out.println(this.getClass().getName() + " completed ");

	}

	public boolean isCodeHasChanged() {
		return codeHasChanged;
	}

	/**
	 * create all components (buttons, sliders, views, etc) and arrange them
	 */
	public Component svCreateComponents() {

		DefaultCaret caret = (DefaultCaret) messageField.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

//		Dimension minSize = new Dimension(5, 5);
//		Dimension prefSize = new Dimension(5, 5);
//		Dimension maxSize = new Dimension(Short.MAX_VALUE, 5);
		Dimension componentSize = new Dimension(componentXSize, componentYSize);

		List<String> snippetNames = codeDB.getSnippetNames();
		for (String name : snippetNames) {
			snippetSelector.addItem(name);
		}

		snippetSelector.setActionCommand(loadCommand);
		snippetSelector.setSelectedIndex(-1);
		snippetSelector.addActionListener(this);

		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		saveButton.addActionListener(this);
		saveButton.setEnabled(false);
		saveAsButton.addActionListener(this);
		showColorChooserButton.addActionListener(this);

		URL imageURL = CodeWindow.class.getResource(playImgLocation);
		runButton.setIcon(new ImageIcon(imageURL));
		runButton.setActionCommand(runText);
		runButton.setToolTipText(runText);

		imageURL = CodeWindow.class.getResource(stopImgLocation);
		stopButton.setIcon(new ImageIcon(imageURL));
		stopButton.setActionCommand(stopText);
		stopButton.setToolTipText(stopText);
		stopButton.setEnabled(false);

		imageURL = CodeWindow.class.getResource(colorImgLocation);
		showColorChooserButton.setIcon(new ImageIcon(imageURL));
		showColorChooserButton.setActionCommand(showColorChooserText);
		showColorChooserButton.setToolTipText(showColorChooserText);

		colorChooser.setPreviewPanel(new JPanel());
		AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
		for (AbstractColorChooserPanel p : panels) {
			String displayName = p.getDisplayName();
			if (!displayName.equals("RGB")) {
				colorChooser.removeChooserPanel(p);
			}
		}
		controllBox.add(colorChooser);

		List<String> colorsWithName = codeDB.getColorNames();
		if (colorsWithName != null) {

			String[] template = new String[1];
			Color[] templateC = new Color[1];
			String[] colorNames = colorsWithName.toArray(template);
			Color[] colors = codeDB.getColors().toArray(templateC);

			JLabel selectorInfo = new JLabel("Vordefinierte Farben");
			selectorInfo.setMaximumSize(componentSize);
			selectorInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
			selectorInfo.setBorder(BorderFactory
					.createLineBorder(Color.BLUE, 3));

			colorSelector = new JComboBox<String>(colorNames);
			colorSelector.setMaximumSize(componentSize);
			colorSelector.setAlignmentX(Component.LEFT_ALIGNMENT);

			ComboBoxRenderer renderer = new ComboBoxRenderer(colorSelector);
			renderer.setColors(colors);
			renderer.setStrings(colorNames);

			colorSelector.setRenderer(renderer);

			colorSelector.setActionCommand(colorSelectorCommand);
			colorSelector.setMaximumSize(new Dimension(componentXSize,
					componentYSize));
			colorSelector.addActionListener(this);

			Box color2 = Box.createVerticalBox();
			color2.add(selectorInfo);
			color2.add(colorSelector);

			executionInfoLabel.setMaximumSize(componentSize);
			executionInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			executionInfoLabel.setBorder(BorderFactory.createLineBorder(
					Color.BLUE, 3));
			color2.add(executionInfoLabel);

			controllBox.add(color2);
		} else {
			JOptionPane.showMessageDialog(this,
					"Fehler beim Lesen der Farben-Datei: "
							+ codeDB.getLastException().getMessage(),
					"Farben lesen", JOptionPane.ERROR_MESSAGE);
		}

		snippetSelector.setMaximumSize(componentSize);
		snippetNameLabel.setMaximumSize(componentSize);
		statusLabel.setMaximumSize(componentSize);
		snippetNameField.setMaximumSize(componentSize);
		// runButton.setMaximumSize(componentSize);
		saveButton.setMaximumSize(componentSize);
		saveAsButton.setMaximumSize(componentSize);

		snippetSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		snippetNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
		snippetNameLabel.setBorder(BorderFactory
				.createLineBorder(Color.BLUE, 3));
		statusLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));

//		Box buttonBox = Box.createVerticalBox();
//		// buttonBox.setBorder(BorderFactory.createCompoundBorder(
//		// BorderFactory.createLineBorder(Color.BLUE),
//		// buttonBox.getBorder()));
//		buttonBox.setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createEmptyBorder(), buttonBox.getBorder()));
//		// buttonBox.setBorder(BorderFactory.createLineBorder(Color.BLUE, 10 )
//		// );
//		buttonBox.add(snippetNameLabel);
//		buttonBox.add(new Box.Filler(minSize, prefSize, maxSize));
//		buttonBox.add(runButton);
//		// buttonBox.add(statusLabel);
//		buttonBox.add(new Box.Filler(minSize, prefSize, maxSize));
//		// buttonBox.add(new JLabel("Laden"));
//		buttonBox.add(snippetSelector);
//		buttonBox.add(new Box.Filler(minSize, prefSize, maxSize));
//		buttonBox.add(saveButton);
//		buttonBox.add(new Box.Filler(minSize, prefSize, maxSize));
//		buttonBox.add(saveAsButton);
//		buttonBox.add(new Box.Filler(minSize, prefSize, maxSize));
//		buttonBox.add(snippetNameField);
//		buttonBox.add(new Box.Filler(minSize, prefSize, maxSize));
//
//		controllBox.add(buttonBox);
//
//		controllBox.add(Box.createHorizontalGlue());

		// codeInput.setColumns(60);
		// codeInput.setRows(20);

		// System.out.println( codeInput.getEditorKit() );
		// codeInput.setEditorKit( new StyledEditorKit() );
		// System.out.println( codeInput.getEditorKit() );
		codeInput.setPreferredSize(new Dimension(500, 300));
		codeInput.setFont(normalFont);
		codeInput.getDocument().addDocumentListener(this);
		editorPaneDocument = codeInput.getDocument();
		editorPaneDocument.addUndoableEditListener(undoHandler);

		KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				Event.META_MASK);
		KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				Event.META_MASK);

		undoAction = new UndoAction();
		codeInput.getInputMap().put(undoKeystroke, "undoKeystroke");
		codeInput.getActionMap().put("undoKeystroke", undoAction);

		redoAction = new RedoAction();
		codeInput.getInputMap().put(redoKeystroke, "redoKeystroke");
		codeInput.getActionMap().put("redoKeystroke", redoAction);

		JScrollPane scrollPane = new JScrollPane(codeInput);

		// Versuch mit jsyntaxpane, funktioniert mit java (nur JS
		// felhermeldung), C-Mode kein sichtbarer Effekt
		// DefaultSyntaxKit.initKit();
		// codeInput.setContentType("text/C");
		codeInput.setText(codeDB.getLastEditElement().getTextContent());
		codeHasChanged = false;

		messageField.setColumns(60);
		messageField.setRows(10);
		messageField.setFont(normalFont);
		JScrollPane scrollPane2 = new JScrollPane(messageField);

		center.add(scrollPane);
		// center.add(controllBox);
		center.add(scrollPane2);

		JMenu menuPropertier;
		JMenu menuCompile;
		JMenuBar menuBar = new JMenuBar();

		menuPropertier = new JMenu("Eigenschaften");

		Utils.addMenuItem(this, menuPropertier, fontIncText, "vergrößert Font",
				"alt I");
		Utils.addMenuItem(this, menuPropertier, fontDecText,
				"verkleinert Font", "alt D");
		Utils.addMenuItem(this, menuPropertier, bigFontText, "Großer Font",
				"alt B");
		Utils.addMenuItem(this, menuPropertier, normalFontText,
				"normal großer Font", "alt N");
		menuPropertier.addSeparator();
		Utils.addMenuItem(this, menuPropertier, authorText,
				"Name des Autors / der Autorin der Code-Schnipsel");
		menuPropertier.addSeparator();
		Utils.addMenuItem(this, menuPropertier, codeFileText,
				"Andere Datei mit Code-Schnipsel laden");

		menuCompile = new JMenu("Compiler");

		Utils.addMenuItem(this, menuCompile, CodeExecutor.boSLText,
				"BoS Language");
		Utils.addMenuItem(this, menuCompile, CodeExecutor.gccText, "GCC");
		Utils.addMenuItem(this, menuCompile, CodeExecutor.devCText, "Dev C++");
		Utils.addMenuItem(this, menuCompile, CodeExecutor.vsText,
				"MS Visual Studio");
		Utils.addMenuItem(this, menuCompile, CodeExecutor.javaText, "Java");

		menuCompile.addSeparator();
		Utils.addMenuItem(this, menuCompile, runText,
				"compiliern und ausführen", "alt X");
		Utils.addMenuItem(this, menuCompile, stopText, "Ausführung abbrechen");
		Utils.addMenuItem(this, menuCompile, commandsFromFiletext,
				"Befehle aus Datei commands.txt ausführen", "alt Y");
		Utils.addMenuItem(this, menuCompile, exeNametext,
				"Name für Programm (generierte C-Datei)");

		// mi = new JMenuItem(exeCommandtext);
		// mi.addActionListener(this);
		// mi.setToolTipText("Befehl zum Ausführen");
		// menuCompile.add(mi);

		JMenu editMenu = new JMenu("Edit");
		JMenuItem undoMenuItem = new JMenuItem(undoAction);
		undoMenuItem.setAccelerator(KeyStroke.getKeyStroke("control Z"));
		JMenuItem redoMenuItem = new JMenuItem(redoAction);
		redoMenuItem.setAccelerator(KeyStroke.getKeyStroke("control Y"));
		editMenu.add(undoMenuItem);
		editMenu.add(redoMenuItem);
		Utils.addMenuItem(this, editMenu, autoLayoutText);

		JMenu menuHelp = new JMenu("Hilfe");
		Utils.addMenuItem(this, menuHelp, helpText);

		// menuBar.add(menuPropertier);
		// menuBar.add(menuCompile);
		// menuBar.add(editMenu);
		// menuBar.add(Box.createHorizontalGlue());
		// menuBar.add(infoLabel);

		GroupLayout layout = new GroupLayout(menuBar);
		layout.setAutoCreateGaps(true);
		menuBar.setLayout(layout);

		SequentialGroup verticalGroup = layout.createSequentialGroup();
		verticalGroup.addGroup(layout.createParallelGroup()
				.addComponent(menuPropertier).addComponent(menuCompile)
				.addComponent(editMenu).addComponent(menuHelp)
				.addComponent(infoLabel));
		verticalGroup.addGroup(layout.createParallelGroup()
				.addComponent(runButton).addComponent(stopButton)
				.addComponent(executionInfoLabel)
				.addComponent(showColorChooserButton)
				.addComponent(colorSelector));
		verticalGroup.addGroup(layout.createParallelGroup()
				.addComponent(snippetNameLabel).addComponent(snippetSelector)
				.addComponent(saveButton).addComponent(saveAsButton)
				.addComponent(snippetNameField));

		ParallelGroup horizontalGroup = layout.createParallelGroup();
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(menuPropertier).addComponent(menuCompile)
				.addComponent(editMenu).addComponent(menuHelp)
				.addGap(0, 0, Short.MAX_VALUE).addComponent(infoLabel));
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(runButton).addComponent(stopButton)
				.addComponent(executionInfoLabel)
				.addComponent(showColorChooserButton)
				.addComponent(colorSelector));
		horizontalGroup.addGroup(layout.createSequentialGroup()
				.addComponent(snippetNameLabel).addComponent(snippetSelector)
				.addComponent(saveButton).addComponent(saveAsButton)
				.addComponent(snippetNameField));

		layout.setVerticalGroup(verticalGroup);
		layout.setHorizontalGroup(horizontalGroup);

		setJMenuBar(menuBar);

		return center;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		System.out.println("CodeWindow cmd: " + cmd);

		if (cmd.equals(runText)) {
			SwingWorker<Boolean, Void> worker = new CodeRunner<Boolean, Void>();
			runButton.setEnabled(false);
			stopButton.setEnabled(true);
			worker.execute();

		} else if (cmd.equals(stopText)) {
			codeExecutor.stopExecution();

		} else if (cmd.equals(commandsFromFiletext)) {
			CodeExecutorBoSL fileExecuter = new CodeExecutorBoSL( board );
			String result = fileExecuter.compileAndExecute("commands.txt");
			messageField.setText(result);

		} else if (cmd.equals(loadCommand)) {
			if (codeHasChanged) {
				int reply = JOptionPane.showConfirmDialog(this,
						"Code wurde verändert, trotzdem neu laden?",
						"Änderungen", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.NO_OPTION) {
					return;
				}
			}
			JComboBox<String> cb = (JComboBox<String>) event.getSource();
			String action = (String) cb.getSelectedItem();
			System.out.println("combo: " + action);
			codeInput.setText(codeDB.getSnippetCode(action));
			snippetName = action;
			snippetNameLabel.setText(snippetName);
			saveButton.setEnabled(true);
			codeHasChanged = false;
			updateInfoLabel();

		} else if (cmd.equals(saveText)) {
			codeDB.overwriteSnippet(snippetName, codeInput.getText());
			codeHasChanged = false;
			updateInfoLabel();

		} else if (cmd.equals(saveAsText)) {
			String text = snippetNameField.getText();
			if (text.equals("")) {
				JOptionPane.showMessageDialog(this, "Bitte Name eintragen.",
						"No Name", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (codeDB.hasSnippet(text)) {
				JOptionPane.showMessageDialog(this, text
						+ " bereits vorhanden.", "Overwrite",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			codeDB.saveAsSnippet(text, codeInput.getText(), authorName);
			snippetSelector.addItem(text);
			snippetName = text;
			snippetNameLabel.setText(snippetName);
			snippetNameField.setText("");
			saveButton.setEnabled(true);
			codeHasChanged = false;
			updateInfoLabel();

		} else if (cmd.equals(showColorChooserText)) {
			JFrame colorChooserFrame = new JFrame();
			colorChooserFrame.setTitle(cmd);
			colorChooserFrame.setSize(450, 300);
			colorChooserFrame.getContentPane().add(colorChooser);
			colorChooserFrame.setVisible(true);

		} else if (cmd.equals(colorSelectorCommand)) {
			JComboBox<String> cb = (JComboBox<String>) event.getSource();
			String color = (String) cb.getSelectedItem();
			Document doc = codeInput.getDocument();
			int pos = codeInput.getCaretPosition(); // get the cursor position
			try {
				doc.insertString(pos, color, null);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // insert your text

		} else if (cmd.equals(fontIncText)) {
			Font font = new Font("Consolas", Font.PLAIN, ++fontSize);
			codeInput.setFont(font);
			messageField.setFont(font);
			// updateInfoLabel();

		} else if (cmd.equals(fontDecText)) {
			Font font = new Font("Consolas", Font.PLAIN, --fontSize);
			codeInput.setFont(font);
			messageField.setFont(font);
			// updateInfoLabel(); // verändert größe der Fenster ???

		} else if (cmd.equals(bigFontText)) {
			fontSize = bigFontSize;
			codeInput.setFont(bigFont);
			messageField.setFont(bigFont);
			// updateInfoLabel();

		} else if (cmd.equals(normalFontText)) {
			fontSize = normalFontSize;
			codeInput.setFont(normalFont);
			messageField.setFont(normalFont);
			// updateInfoLabel();

		} else if (cmd.equals(authorText)) {
			String a = JOptionPane.showInputDialog(this, "AutorIn", authorName);
			if (a != null && a.trim().length() > 0) {
				authorName = a;
				board.getGraphic().saveProperty("author", authorName);
			}

		} else if (cmd.equals(autoLayoutText)) {
			codeInput.setText(codeLayouter.autoLayout(codeInput.getText()));

		} else if (cmd.equals(exeCommandtext)) {
			String a = JOptionPane.showInputDialog(this, "Befehl",
					codeExecutor.getExeCommand());
			if (a != null && a.trim().length() > 0) {
				codeExecutor.setExeCommand(a);
				board.getGraphic().saveProperty("exeCommand", a);
			}

		} else if (cmd.equals(exeNametext)) {
			String a = JOptionPane
					.showInputDialog(
							this,
							"Name für Programmdatei, bei VS noch in mscomp.bat eintragen",
							codeExecutor.getExeName());
			if (a != null && a.trim().length() > 0) {
				if (!a.endsWith(".c")) {
					a += ".c";
				}
				codeExecutor.setExeName(a);
				board.getGraphic().saveProperty("exeName", a);
			}
		} else if (CodeExecutor.isCodeExecuterSelection(cmd)) {
			codeExecutor = CodeExecutor.getExecutor(cmd, board, this);
			board.getGraphic().saveProperty("compiler", cmd);
			updateInfoLabel();

		} else if (cmd.equals(codeFileText)) {
			if (codeHasChanged) {
				int reply = JOptionPane.showConfirmDialog(null,
						"Code wurde verändert, trotzdem neue Datei laden?",
						"Änderungen", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.NO_OPTION) {
					return;
				}
				codeHasChanged = false;
			}
			String newFileName = readXMLFile();
			if (newFileName == null)
				return;
			// update the selector
			snippetSelector.removeActionListener(this);
			snippetSelector.setSelectedIndex(-1);
			snippetSelector.removeAllItems();
			snippetSelector.addActionListener(this);
			List<String> snippetNames = codeDB.getSnippetNames();
			for (String name : snippetNames) {
				snippetSelector.addItem(name);
			}

		}
	}

	private void updateInfoLabel() {
		String text = "";
		if (codeHasChanged) {
			text += "* ";
		}
		text += codeExecutor.getCompileMode();
		infoLabel.setText(text);
		// infoLabel.setText( codeExecutor.getCompileMode() + " " + fontSize
		// +"pt");

	}

	class CodeRunner<T, V> extends SwingWorker {

		@Override
		public Boolean doInBackground() {
			String result = "";
			result += "compile ...\n";
			messageField.setText(result);
			codeExecutor.setMessageField(messageField);
			String fileName = codeExecutor.createTmpSourceFile(codeInput
					.getText());
			System.out.println("CodeRunner fileName: " + fileName);
			if (fileName == null) {
				result += "ERROR " + board.getLastError();
			} else {
				result += codeExecutor.compileAndExecute(fileName);
			}

			int maxLength = 5000;
			if (result.length() > maxLength) {
				System.out.println("Result too long: " + result.length());
				result = "... \n"
						+ result.substring(result.length() - maxLength);
				// result = "zu viele Zeilen \n";
			}
			if (!(codeExecutor instanceof CodeExecutorJava)) {
				messageField.setText(result);
			}
			if (board.getErrorCount() > 0) {
				statusLabel.setText(board.getErrorCount() + " Fehler");
				statusLabel.setForeground(Color.RED);
			} else {
				statusLabel.setText("keine Fehler");
				statusLabel.setForeground(Color.GREEN);

			}

			codeDB.saveAsLast(codeInput.getText());
			return true;
		}

	}

	private void codeChanged() {
		if (!codeHasChanged) {
			codeHasChanged = true;
			updateInfoLabel();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// System.out.println("changedUpdate");
		codeChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		// System.out.println("insertUpdate");
		codeChanged();

	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		// System.out.println("removeUpdate");
		codeChanged();
	}

	class UndoHandler implements UndoableEditListener {

		/**
		 * Messaged when the Document has created an edit, the edit is added to
		 * <code>undoManager</code>, an instance of UndoManager.
		 */
		public void undoableEditHappened(UndoableEditEvent e) {
			undoManager.addEdit(e.getEdit());
			undoAction.update();
			redoAction.update();
		}
	}

	/**
	 * @author Alvin Alexander http://alvinalexander.com/java/java-undo-redo
	 * 
	 */
	class UndoAction extends AbstractAction {
		public UndoAction() {
			super("Undo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.undo();
			} catch (CannotUndoException ex) {
				// TODO deal with this
				// ex.printStackTrace();
			}
			update();
			redoAction.update();
		}

		protected void update() {
			if (undoManager.canUndo()) {
				setEnabled(true);
				putValue(Action.NAME, undoManager.getUndoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Undo");
			}
		}
	}

	class RedoAction extends AbstractAction {
		public RedoAction() {
			super("Redo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.redo();
			} catch (CannotRedoException ex) {
				// TODO deal with this
				ex.printStackTrace();
			}
			update();
			undoAction.update();
		}

		protected void update() {
			if (undoManager.canRedo()) {
				setEnabled(true);
				putValue(Action.NAME, undoManager.getRedoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Redo");
			}
		}
	}

	@Override
	public void startCompilation() {
		executionInfoLabel.setText("starte Compilierung");
		executionInfoLabel.setBackground(Color.RED);
		executionInfoLabel.setOpaque(true);
		Sleep.sleep(100);

	}

	@Override
	public void failedCompilation() {
		executionInfoLabel.setText("Compilierung gescheitert");
		executionInfoLabel.setBackground(Color.RED);
		executionInfoLabel.setOpaque(true);
		runButton.setEnabled(true);
		stopButton.setEnabled(false);

	}

	@Override
	public void endCompilation() {
		executionInfoLabel.setText("Compilierung beendet");
		executionInfoLabel.setOpaque(false);
		Sleep.sleep(100);

	}

	@Override
	public void startExecution() {
		executionInfoLabel.setText("starte Ausführung");
		executionInfoLabel.setBackground(Color.GREEN);
		executionInfoLabel.setOpaque(true);
		Sleep.sleep(100);
	}

	@Override
	public void endExecution() {
		executionInfoLabel.setText("Ausführung beendet");
		executionInfoLabel.setOpaque(false);
		runButton.setEnabled(true);
		stopButton.setEnabled(false);

	}

}
