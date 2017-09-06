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
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

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
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
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

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import plotter.Sleep;

/**
 * This is the GUI for writing and executing code for BoS.
 * 
 * @author Euler
 * @version 1.11 May 2017
 * 
 */
// Version wer wann was
// .94 se 15-10-15 prüft auf ungespeicherte Änderungen
// .95 se 15-10-22 undo
// .96 se 15-10-25 load xml file
// .98 se 15-12-05 Java support
// .98a se 15-12-15 Bug bei fehlendem ini-Eintrag compiler
// 1.11 se 17-06-06 templates fuer complete snippets

@SuppressWarnings("serial")
public class CodeWindow extends JFrame implements ActionListener, DocumentListener, ExecutorListener {
	private static final String METHOD_PREFIX = "m:";
	private static final int componentXSize = 160;
	private static final int componentYSize = 25;

	private static String version;
	private static String runText;
	private static String stopText;
	private static String saveText;
	private static String saveAsText;
	private static final String loadCommand = "loadSnippet";
	private static final String colorSelectorCommand = "selectColor";
	private static final String fontIncText = "Zoom +";
	private static final String fontDecText = "Zoom -";
	private static String bigFontText;
	private static String fontSizeText;
	private static final String normalFontText = "100%";
	private static String authorText;
	private static String codeFileText;
	private static String javacPathText;
	private static final String exeCommandtext = "Befehl";
	private static final String exeNametext = "Name für Programm";
	private static String commandsFromFiletext;
	private static final String showColorChooserText = "Farbwähler";
	private static String helpText;
	private static String autoLayoutText;
	private static String replaceText = "ersetzen";
	private static String editNewSnippetText;
	private static String editNewCompleteSnippetText = "new c";
	private static String deleteSnippetText;
	private static String importSnippetText = "Import";
	private static String exportSnippetText = "Export";
	private static String snippetInfoText = "Info";
	private static String showGeneratedCodeText;
	private static String resetOnStartText;
	//private static final String lastEditedSnippetText = "<html><em>letztes Snippet</em></html>";

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
	// private JLabel snippetNameLabel = new JLabel(newSnippetText);
	// private JLabel statusLabel = new JLabel();
	private JLabel executionInfoLabel = new JLabel();
	private JLabel infoLabel = new JLabel();
	private String snippetName;
	private JColorChooser colorChooser = new JColorChooser();
	private JButton runButton = new JButton();
	private JButton stopButton = new JButton();
	private JButton saveButton;
	private JButton saveAsButton;
	private JButton showColorChooserButton = new JButton();
	private JButton deleteSnippetButton = new JButton();
	private JButton snippetInfoButton;
	private Box center = new Box(BoxLayout.Y_AXIS);
	private Box controllBox = new Box(BoxLayout.X_AXIS);
	private Board board;
	private CodeDB codeDB = new CodeDB();
	private JComboBox<String> snippetSelector = new JComboBox<String>();
	private JComboBox<String> colorSelector = new JComboBox<String>();
	private CodeExecutor codeExecutor;
	private String authorName = "nobody";
	private String fileOpenDirectory = null;
	private String XMLFileName = null;
	private boolean codeHasChanged = false;
	private String imageDir = "images/";
	private String stopImgLocation = imageDir + "Stop16.gif";
	private String playImgLocation = imageDir + "Play16.gif";
	private String colorImgLocation = imageDir + "color.gif";
	private String deleteImgLocation = imageDir + "delete.gif";

	private Document editorPaneDocument;
	protected UndoHandler undoHandler = new UndoHandler();
	protected UndoManager undoManager = new UndoManager();
	private UndoAction undoAction = null;
	private RedoAction redoAction = null;
	private CodeLayouter codeLayouter = new CodeLayouter();
	private ResourceBundle messages;
	private Properties properties;
	private boolean resetOnStart;
	private String lastSourceText = "";
	private String lastDestText = "";

	public CodeWindow(Board board) {
		this.board = board;
		properties = board.getProperties();
		messages = board.getMessages();
		Locale locale = messages.getLocale();
		System.out.println("CodeWindow: got " + locale.getLanguage());
		setStrings();

		board.setFilterMode(true);
		String a = properties.getProperty("author");
		if (a != null) {
			authorName = a;
		}
		resetOnStart = Boolean.parseBoolean(properties.getProperty("resetOnStart"));

		fileOpenDirectory = properties.getProperty("codeDir");
		XMLFileName = properties.getProperty("XMLFileName");
		String mode = properties.getProperty("compiler", CodeExecutor.javaText);
		codeExecutor = CodeExecutor.getExecutor(mode, board, this);
		CodeExecutorJava.setJavacPath(properties.getProperty("javacPath", ""));

		setup("CodeWindow " + version);
		updateInfoLabel();

	}

	public Font getNormalFont() {
		return normalFont;
	}

	public void setNormalFont(Font normalFont) {
		this.normalFont = normalFont;
	}

	public boolean isResetOnStart() {
		return resetOnStart;
	}

	public void setResetOnStart(boolean resetOnStart) {
		this.resetOnStart = resetOnStart;
	}

	/**
	 * Gets the texts from the resource bundle and copies them into variables.
	 */
	private void setStrings() {
		bigFontText = Utils.capitalize(messages.getString("big"));
		fontSizeText = Utils.capitalize(messages.getString("fontSize"));
		helpText = Utils.capitalize(messages.getString("help"));
		editNewSnippetText = Utils.capitalize(messages.getString("new"));
		editNewCompleteSnippetText = Utils.capitalize(messages.getString("new") + " complete");
		// newSnippetText = "<html><em>" + messages.getString("new") +
		// "</em></html>";
		version = messages.getString("codeWindowVersion");
		saveText = messages.getString("save");
		saveAsText = messages.getString("saveAs");
		autoLayoutText = messages.getString("format");
		authorText = messages.getString("author");
		codeFileText = messages.getString("codeFile");
		javacPathText = messages.getString("javacPath");
		runText = messages.getString("compileExecute");
		stopText = messages.getString("stopExecution");
		commandsFromFiletext = messages.getString("fromFile");
		showGeneratedCodeText = messages.getString("generatedCode");
		deleteSnippetText = messages.getString("deleteSnippet");
		resetOnStartText = messages.getString("resetOnStart");

	}

	private String readXMLFile(boolean askDefault) {
		String fileName;
		if (askDefault && Dialogs.useCodesXMLStandard(messages)) {
			fileName = "codes.xml";
		} else {
			fileName = askCodeFileName();
		}
		if (fileName == null)
			return null;
		File file = new File(fileName);
		codeDB.setXmlFile(file);
		if (!file.exists()) {
			JOptionPane.showMessageDialog(this, "Datei " + fileName + " neu anlegen ", messages.getString("readCodes"),
					JOptionPane.INFORMATION_MESSAGE);
			codeDB.createDocument();
			codeDB.writeXML();
		}
		properties.setProperty("XMLFileName", file.getName());
		board.saveProperties();
		try {
			codeDB.readXML();
		} catch (ParserConfigurationException | SAXException | IOException e2) {
			JOptionPane.showMessageDialog(this, "Fehler beim Lesen der Codes-Datei: " + e2.getMessage(), "Codes lesen",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return fileName;

	}

	private String askCodeFileName() {
		JFileChooser chooser = new JFileChooser();
		if (fileOpenDirectory != null) {
			chooser.setCurrentDirectory(new File(fileOpenDirectory));
		}
		chooser.setDialogTitle(messages.getString("codeFile"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		String fileSuffixes = "xml";
		FileNameExtensionFilter filter = new FileNameExtensionFilter(" XML", fileSuffixes);
		chooser.setFileFilter(filter);

		int retval = chooser.showDialog(null, null);
		if (retval == JFileChooser.APPROVE_OPTION) {
			fileOpenDirectory = chooser.getCurrentDirectory().getAbsolutePath();
			properties.setProperty("codeDir", fileOpenDirectory);
			board.saveProperties();
			String filename = chooser.getSelectedFile().getAbsolutePath();
			// System.out.println(filename);
			return filename;
		} else {
			return null;
		}

	}

	void savePosition() {
		properties.setProperty("codeWindowPosX", "" + getBounds().x);
		properties.setProperty("codeWindowPosY", "" + getBounds().y);
		properties.setProperty("codeWindowWidth", "" + getBounds().width);
		properties.setProperty("codeWindowHeight", "" + getBounds().height);
		board.saveProperties();
	}

	private void loadPosition() {
		String s;
		s = properties.getProperty("codeWindowPosX", "" + xpos);
		xpos = Integer.parseInt(s);
		s = properties.getProperty("codeWindowPosY", "" + ypos);
		ypos = Integer.parseInt(s);
		s = properties.getProperty("codeWindowWidth", "" + xsize);
		xsize = Integer.parseInt(s);
		s = properties.getProperty("codeWindowHeight", "" + ysize);
		ysize = Integer.parseInt(s);

	}

	private void setup(String string) {
		if (XMLFileName != null) {
			codeDB.setXmlFile(new File(fileOpenDirectory, XMLFileName));
		}
		try {
			codeDB.readXML();
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			JOptionPane.showMessageDialog(this, "Fehler beim Lesen der Codes-Datei: " + e1.getMessage(), "Codes lesen",
					JOptionPane.ERROR_MESSAGE);
			readXMLFile(true);
		}

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (codeHasChanged) {
					int reply = Dialogs.codeHasChangedDialog(messages);
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

		// Dimension minSize = new Dimension(5, 5);
		// Dimension prefSize = new Dimension(5, 5);
		// Dimension maxSize = new Dimension(Short.MAX_VALUE, 5);
		Dimension componentSize = new Dimension(componentXSize, componentYSize);

		List<String> snippetNames = codeDB.getSnippetNames();
		snippetSelector.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
		for (String name : snippetNames) {
			snippetSelector.addItem(name);
		}

		saveButton = new JButton(saveText);
		saveButton.addActionListener(this);
		saveButton.setEnabled(false);

		snippetSelector.setActionCommand(loadCommand);
		String lastSnippetName = properties.getProperty("snippetName");
		snippetSelector.setSelectedIndex(-1);
		if( lastSnippetName != null ) {
			System.out.println( "Try snippet " + lastSnippetName );
			if( codeDB.hasSnippet(lastSnippetName)  ) {
				codeInput.setText(codeDB.getSnippetCode(lastSnippetName) );
				for( int j=0; j<snippetSelector.getItemCount(); j++ ) {
					if( lastSnippetName.equals(snippetSelector.getItemAt(j))) {
						snippetName = lastSnippetName;
						snippetSelector.setSelectedIndex(j);
						saveButton.setEnabled(true);
						break;
					}
				}
			}
		}
				

		snippetSelector.addActionListener(this);

		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		saveAsButton = new JButton(saveAsText);
		saveAsButton.addActionListener(this);
		showColorChooserButton.addActionListener(this);

		snippetInfoButton = new JButton(snippetInfoText);
		snippetInfoButton.addActionListener(this);

		URL imageURL = CodeWindow.class.getResource(playImgLocation);
		runButton.setIcon(new ImageIcon(imageURL));
		runButton.setActionCommand(runText);
		runButton.setToolTipText(runText);

		imageURL = CodeWindow.class.getResource(stopImgLocation);
		stopButton.setIcon(new ImageIcon(imageURL));
		stopButton.setActionCommand(stopText);
		stopButton.setToolTipText(stopText);
		stopButton.setEnabled(false);

		imageURL = CodeWindow.class.getResource(deleteImgLocation);
		deleteSnippetButton.setIcon(new ImageIcon(imageURL));
		deleteSnippetButton.addActionListener(this);
		deleteSnippetButton.setActionCommand(deleteSnippetText);
		deleteSnippetButton.setToolTipText(deleteSnippetText);

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
			selectorInfo.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));

			colorSelector = new JComboBox<String>(colorNames);
			colorSelector.setMaximumSize(componentSize);
			colorSelector.setAlignmentX(Component.LEFT_ALIGNMENT);

			ComboBoxRenderer renderer = new ComboBoxRenderer(colorSelector);
			renderer.setColors(colors);
			renderer.setStrings(colorNames);

			colorSelector.setRenderer(renderer);

			colorSelector.setActionCommand(colorSelectorCommand);
			colorSelector.addActionListener(this);

		} else {
			JOptionPane.showMessageDialog(this,
					"Fehler beim Lesen der Farben-Datei: " + codeDB.getLastException().getMessage(), "Farben lesen",
					JOptionPane.ERROR_MESSAGE);
			colorSelector.addItem("-");

		}
		colorSelector.setMaximumSize(new Dimension(componentXSize, componentYSize));

		snippetSelector.setMaximumSize(componentSize);
		// snippetNameLabel.setMaximumSize(componentSize);
		// statusLabel.setMaximumSize(componentSize);
		snippetNameField.setMaximumSize(componentSize);
		// runButton.setMaximumSize(componentSize);
		saveButton.setMaximumSize(componentSize);
		saveAsButton.setMaximumSize(componentSize);

		snippetSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		snippetNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
		// snippetNameLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE,
		// 3));
		// statusLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));

		codeInput.setPreferredSize(new Dimension(500, 300));
		codeInput.setFont(normalFont);
		codeInput.getDocument().addDocumentListener(this);
		editorPaneDocument = codeInput.getDocument();
		editorPaneDocument.addUndoableEditListener(undoHandler);

		KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.META_MASK);
		KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.META_MASK);

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
		
//		if (codeDB.hasLastEditElement()) {
//			codeInput.setText(codeDB.getLastEditElement().getTextContent());
//			// snippetNameLabel.setText(lastEditedSnippetText);
//		}
		
		codeHasChanged = false;

		messageField.setColumns(60);
		messageField.setRows(10);
		messageField.setFont(normalFont);
		JScrollPane scrollPane2 = new JScrollPane(messageField);

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, scrollPane2);
		split.setResizeWeight(0.7);
		center.add(split);
		// center.add(scrollPane);
		// center.add(scrollPane2);

		JMenu menuProperties;
		JMenu menuCompile;
		JMenuBar menuBar = new JMenuBar();

		menuProperties = new JMenu(messages.getString("properties"));

		Utils.addMenuItem(this, menuProperties, fontIncText, messages.getString("tooltip.increaseFont"), "alt PLUS");
		Utils.addMenuItem(this, menuProperties, fontDecText, messages.getString("tooltip.decreaseFont"), "alt MINUS");
		Utils.addMenuItem(this, menuProperties, bigFontText, messages.getString("tooltip.bigFont"), "alt B");
		Utils.addMenuItem(this, menuProperties, fontSizeText, messages.getString("tooltip.fontSize"));

		Utils.addMenuItem(this, menuProperties, normalFontText, messages.getString("tooltip.normalFont"), "alt N");
		menuProperties.addSeparator();
		Utils.addMenuItem(this, menuProperties, authorText, messages.getString("tooltip.author"));
		menuProperties.addSeparator();
		Utils.addMenuItem(this, menuProperties, codeFileText, messages.getString("tooltip.codeFile"));
		Utils.addMenuItem(this, menuProperties, javacPathText, messages.getString("tooltip.javacPath"));

		menuCompile = new JMenu("Compiler");

		Utils.addMenuItem(this, menuCompile, CodeExecutor.boSLText, "BoS Language");
		Utils.addMenuItem(this, menuCompile, CodeExecutor.JSText, "JavaScript");
		Utils.addMenuItem(this, menuCompile, CodeExecutor.pythonText, "Python");

		JMenu cMenu = new JMenu("C");

		Utils.addMenuItem(this, cMenu, CodeExecutor.gccText, "GCC");
		Utils.addMenuItem(this, cMenu, CodeExecutor.devCText, "Dev C++");
		Utils.addMenuItem(this, cMenu, CodeExecutor.vsText, "MS Visual Studio");
		menuCompile.add(cMenu);
		Utils.addMenuItem(this, menuCompile, CodeExecutor.javaText, "Java");

		menuCompile.addSeparator();
		Utils.addMenuItem(this, menuCompile, editNewSnippetText, messages.getString("tooltip.newSnippet"), "control N");
		Utils.addMenuItem(this, menuCompile, editNewCompleteSnippetText, messages.getString("tooltip.newCompleteSnippet") );
		Utils.addMenuItem(this, menuCompile, importSnippetText);
		Utils.addMenuItem(this, menuCompile, exportSnippetText);
		Utils.addMenuItem(this, menuCompile, showGeneratedCodeText, messages.getString("tooltip.generatedCode"),
				"control G");
		menuCompile.addSeparator();
		Utils.addMenuItem(this, menuCompile, runText, messages.getString("tooltip.compileExecute"), "alt X");
		Utils.addMenuItem(this, menuCompile, stopText, messages.getString("tooltip.stopExecution"));
		JMenuItem mi = new JCheckBoxMenuItem(resetOnStartText, resetOnStart);
		mi.addActionListener(this);
		menuCompile.add(mi);
		Utils.addMenuItem(this, menuCompile, commandsFromFiletext, messages.getString("tooltip.fromFile"), "alt Y");

		// only usefull for testing / debugging
		// Utils.addMenuItem(this, menuCompile, exeNametext,
		// "Name für Programm (generierte C-Datei)");
		// Utils.addMenuItem(this, menuCompile, exeCommandtext,
		// "Befehl zum Ausführen");

		JMenu editMenu = new JMenu("Edit");
		JMenuItem undoMenuItem = new JMenuItem(undoAction);
		undoMenuItem.setAccelerator(KeyStroke.getKeyStroke("control Z"));
		JMenuItem redoMenuItem = new JMenuItem(redoAction);
		redoMenuItem.setAccelerator(KeyStroke.getKeyStroke("control Y"));
		editMenu.add(undoMenuItem);
		editMenu.add(redoMenuItem);
		Utils.addMenuItem(this, editMenu, autoLayoutText, "automatisch formatieren", "control F");
		Utils.addMenuItem(this, editMenu, replaceText, "ersetzen");

		editMenu.addSeparator();
		String className = "jserver.XSend" + messages.getLocale().getLanguage().toUpperCase();
		String[] methods = MethodExtractor.getMethods(className);
		for (String method : methods) {
			JMenuItem jmi = Utils.addMenuItem(this, editMenu, method);
			jmi.setActionCommand(METHOD_PREFIX + method);
		}

		JMenu menuHelp = new JMenu(messages.getString("help"));
		Utils.addMenuItem(this, menuHelp, helpText);

		GroupLayout layout = new GroupLayout(menuBar);
		layout.setAutoCreateGaps(true);
		menuBar.setLayout(layout);

		SequentialGroup verticalGroup = layout.createSequentialGroup();
		verticalGroup.addGroup(layout.createParallelGroup().addComponent(menuProperties).addComponent(menuCompile)
				.addComponent(editMenu).addComponent(menuHelp).addComponent(infoLabel));
		verticalGroup.addGroup(layout.createParallelGroup().addComponent(runButton).addComponent(stopButton)
				.addComponent(executionInfoLabel).addComponent(showColorChooserButton).addComponent(colorSelector));
		verticalGroup.addGroup(layout.createParallelGroup().addComponent(snippetInfoButton)
				.addComponent(snippetSelector).addComponent(saveButton).addComponent(saveAsButton)
				.addComponent(snippetNameField).addComponent(deleteSnippetButton));

		ParallelGroup horizontalGroup = layout.createParallelGroup();
		horizontalGroup.addGroup(layout.createSequentialGroup().addComponent(menuProperties).addComponent(menuCompile)
				.addComponent(editMenu).addComponent(menuHelp).addGap(0, 0, Short.MAX_VALUE).addComponent(infoLabel));
		horizontalGroup.addGroup(layout.createSequentialGroup().addComponent(runButton).addComponent(stopButton)
				.addComponent(executionInfoLabel).addComponent(showColorChooserButton).addComponent(colorSelector));
		horizontalGroup.addGroup(layout.createSequentialGroup().addComponent(snippetInfoButton)
				.addComponent(snippetSelector).addComponent(saveButton).addComponent(saveAsButton)
				.addComponent(snippetNameField).addComponent(deleteSnippetButton));

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
			if (resetOnStart) {
				board.reset();
			}
			SwingWorker<Boolean, Void> worker = new CodeRunner<Boolean, Void>();
			runButton.setEnabled(false);
			stopButton.setEnabled(true);
			worker.execute();

		} else if (cmd.equals(stopText)) {
			codeExecutor.stopExecution();

		} else if (cmd.equals(commandsFromFiletext)) {
			CodeExecutorBoSL fileExecuter = new CodeExecutorBoSL(board);
			String result = fileExecuter.compileAndExecute("commands.txt");
			messageField.setText(result);

		} else if (cmd.equals(editNewSnippetText) || cmd.equals(editNewCompleteSnippetText)) {
			if (codeHasChanged) {
				int reply = Dialogs.codeHasChangedDialog(messages);
				if (reply == JOptionPane.NO_OPTION) {
					return;
				}
			}
			if(cmd.equals(editNewCompleteSnippetText) ) {
				codeInput.setText( codeExecutor.getCompleteTemplate() );
			} else {
				codeInput.setText("");
			}
			snippetName = null;
			// remove action listen temporarily to avoid load action
			snippetSelector.removeActionListener(this);
			snippetSelector.setSelectedIndex(-1);
			snippetSelector.addActionListener(this);
			// snippetNameLabel.setText(newSnippetText);
			saveButton.setEnabled(false);
			codeHasChanged = false;
			updateInfoLabel();

		} else if (cmd.equals(loadCommand)) {
			if (codeHasChanged) {
				int reply = Dialogs.codeHasChangedDialog(messages, "changesLoadSnippet");
				if (reply == JOptionPane.NO_OPTION) {
					return;
				}
			}
			JComboBox<String> cb = (JComboBox<String>) event.getSource();
			String selectedSnippetName = (String) cb.getSelectedItem();
			//System.out.println("combo: " + selectedSnippetName);
			codeInput.setText(codeDB.getSnippetCode(selectedSnippetName));
			snippetName = selectedSnippetName;
			// snippetNameLabel.setText(clipText(snippetName, 15));
			saveButton.setEnabled(true);
			codeHasChanged = false;
			rememberSnippetName( );
			updateInfoLabel();

		} else if (cmd.equals(exportSnippetText)) {
			InfoBox info = new InfoBox(this, "", 500, 400);
			info.setTitle("Export Snippet");
			info.getTextArea().setFont( normalFont );
			String exportText = "";
			if (snippetName == null) {
				exportText = "No snippet loaded";
			} else {
				Element s = codeDB.getSnippetByName(snippetName);
				Snippet snippet = new Snippet(s);
				exportText  = snippet.write();
			}

			info.getTextArea().setText(exportText);
			info.setVisible(true);

		} else if (cmd.equals(importSnippetText)) {
			JTextArea ta = new JTextArea(20, 20);
			int ret = JOptionPane.showConfirmDialog(this, new JScrollPane(ta), "Import", JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				return;
			}
			String xmlString = ta.getText();
			try {
				Snippet importedSnippet = new Snippet(xmlString);
				String text = importedSnippet.getName();
				if (codeDB.hasSnippet(text)) {
					throw new SnippetException("duplicate snippet name");
				}
				// JOptionPane.showMessageDialog(this,
				// importedSnippet.getInfo(), "Imported: " + text,
				// JOptionPane.PLAIN_MESSAGE);
				codeDB.importSnippet(importedSnippet.getElement());
				codeHasChanged = false;
				snippetSelector.addItem(text);
				snippetSelector.setSelectedIndex(snippetSelector.getItemCount() - 1);
				snippetName = text;
				rememberSnippetName();
				saveButton.setEnabled(true);
				updateInfoLabel();

			} catch (ParserConfigurationException | SAXException | IOException | SnippetException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
			}

		} else if (cmd.equals(snippetInfoText)) {
			InfoBox info = new InfoBox(this, "", 600, 400);
			info.setTitle("Snippet Info");
			info.getTextArea().setFont(board.getCodeWindow().getNormalFont());
			String infoText = "";
			if (snippetName == null) {
				infoText = "No snippet loaded";
			} else {
				infoText += "Snippet: " + snippetName + "\n";
				Element s = codeDB.getSnippetByName(snippetName);
				Snippet snippet = new Snippet(s);
				infoText += snippet.getInfo();
			}
			info.getTextArea().setText(infoText);
			info.setVisible(true);

		} else if (cmd.equals(deleteSnippetText)) {
			if (snippetName == null) {
				JOptionPane.showMessageDialog(this, "No snippet to delete.", "No Snippet", JOptionPane.ERROR_MESSAGE);
				return;
			}
			int ret = JOptionPane.showConfirmDialog(this, "Really delete snippet <<" + snippetName + ">> ?");
			if (ret != JOptionPane.OK_OPTION) {
				return;
			}
			codeDB.deleteSnippet(snippetName);
			snippetSelector.removeItem(snippetName);

		} else if (cmd.equals(saveText)) {
			codeDB.overwriteSnippet(snippetName, codeInput.getText());
			codeHasChanged = false;
			updateInfoLabel();

		} else if (cmd.equals(saveAsText)) {
			String text = snippetNameField.getText();
			if (text.equals("")) {
				JOptionPane.showMessageDialog(this, "Bitte Name eintragen.", "No Name", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String regex = "[a-zA-Z0-9_.-]+";
			if (!text.matches(regex)) {
				JOptionPane.showMessageDialog(this, "Bitte keine Umlaute oder ähnliches im Namen..", "Wrong Name",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (codeDB.hasSnippet(text)) {
				JOptionPane.showMessageDialog(this, text + " bereits vorhanden.", "Overwrite",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			String language = properties.getProperty("language", null);
			String country = properties.getProperty("country", null);
			Locale locale = new Locale(language, country);

			codeDB.saveAsSnippet(text, codeInput.getText(), authorName, locale);
			codeHasChanged = false;
			snippetSelector.addItem(text);
			snippetSelector.setSelectedIndex(snippetSelector.getItemCount() - 1);
			snippetName = text;
			rememberSnippetName();
			// snippetNameLabel.setText(snippetName);
			snippetNameField.setText("");
			saveButton.setEnabled(true);
			updateInfoLabel();

		} else if (cmd.equals(showColorChooserText)) {
			JFrame colorChooserFrame = new JFrame();
			colorChooserFrame.setTitle(cmd);
			colorChooserFrame.setSize(450, 300);
			colorChooserFrame.getContentPane().add(colorChooser);
			colorChooserFrame.setVisible(true);

		} else if (cmd.equals(colorSelectorCommand)) {
			@SuppressWarnings("unchecked")
			JComboBox<String> cb = (JComboBox<String>) event.getSource();
			String color = (String) cb.getSelectedItem();
			Document doc = codeInput.getDocument();
			int pos = codeInput.getCaretPosition(); // get the cursor position
			try {
				doc.insertString(pos, color, null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

		} else if (cmd.startsWith(METHOD_PREFIX)) {
			int pos = codeInput.getCaretPosition(); // get the cursor position
			try {
				Document doc = codeInput.getDocument();
				doc.insertString(pos, cmd.substring(METHOD_PREFIX.length()), null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

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

		} else if (cmd.equals(fontSizeText)) {
			String a = JOptionPane.showInputDialog(null, messages.getString("tooltip.fontSize"), fontSize);
			if (a != null) {
				fontSize = Integer.parseInt(a);
				Font font = new Font("Consolas", Font.PLAIN, --fontSize);
				codeInput.setFont(font);
				messageField.setFont(font);
			}

		} else if (cmd.equals(normalFontText)) {
			fontSize = normalFontSize;
			codeInput.setFont(normalFont);
			messageField.setFont(normalFont);
			// updateInfoLabel();

		} else if (cmd.equals(authorText)) {
			String a = JOptionPane.showInputDialog(this, authorText, authorName);
			if (a != null && a.trim().length() > 0) {
				authorName = a;
				properties.setProperty("author", authorName);
				board.saveProperties();
			}

		} else if (cmd.equals(replaceText)) {
			JTextField source = new JTextField(lastSourceText);
			JTextField dest = new JTextField(lastDestText);
			int ret = Dialogs.replaceDialog(source, dest, messages);
			if (ret == JOptionPane.OK_OPTION) {
				String text = codeInput.getText();
				lastSourceText = source.getText();
				lastDestText = dest.getText();
				text = text.replaceAll(lastSourceText, lastDestText);
				codeInput.setText(text);
				codeHasChanged = true;
			}

		} else if (cmd.equals(autoLayoutText)) {
			codeInput.setText(codeLayouter.autoLayout(codeInput.getText()));

		} else if (cmd.equals(exeCommandtext)) {
			String a = JOptionPane.showInputDialog(this, "Befehl", codeExecutor.getExeCommand());
			if (a != null && a.trim().length() > 0) {
				codeExecutor.setExeCommand(a);
				properties.setProperty("exeCommand", a);
				board.saveProperties();
			}

		} else if (cmd.equals(exeNametext)) {
			String a = JOptionPane.showInputDialog(this, "Name für Programmdatei, bei VS noch in mscomp.bat eintragen",
					codeExecutor.getExeName());
			if (a != null && a.trim().length() > 0) {
				if (!a.endsWith(".c")) {
					a += ".c";
				}
				codeExecutor.setExeName(a);
				properties.setProperty("exeName", a);
				board.saveProperties();
			}
		} else if (CodeExecutor.isCodeExecuterSelection(cmd)) {
			codeExecutor = CodeExecutor.getExecutor(cmd, board, this);
			properties.setProperty("compiler", cmd);
			board.saveProperties();
			updateInfoLabel();

		} else if (cmd.equals(codeFileText)) {
			if (codeHasChanged) {
				int reply = Dialogs.codeHasChangedDialog(messages, "changesLoadFile");
				if (reply == JOptionPane.NO_OPTION) {
					return;
				}
				codeHasChanged = false;
			}
			String newFileName = readXMLFile(false);
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

		} else if (cmd.equals(javacPathText)) {
			String newJavacPath = Dialogs.askJavacPath(messages);
			if (newJavacPath != null) {
				newJavacPath += File.separator;
				CodeExecutorJava.setJavacPath(newJavacPath);
				properties.setProperty("javacPath", newJavacPath);
				board.saveProperties();
			}

		} else if (cmd.equals(helpText)) {
			JOptionPane.showMessageDialog(this, "Noch keine Hilfe vorhanden - sorry", messages.getString("help"),
					JOptionPane.INFORMATION_MESSAGE);

		} else if (cmd.equals(resetOnStartText)) {
			resetOnStart = !resetOnStart;
			properties.setProperty("resetOnStart", "" + resetOnStart);
			board.saveProperties();

		} else if (cmd.equals(showGeneratedCodeText)) {
			codeExecutor.showGeneratedCode(messages);
		}
	}

	private void rememberSnippetName() {
		properties.setProperty("snippetName", snippetName);
		board.saveProperties();
	}

	private String clipText(String text, int maxLength) {
		if (text.length() <= maxLength) {
			return text;
		} else {
			return text.substring(0, maxLength) + "...";
		}
	}

	private void updateInfoLabel() {
		String text = "";
		if (codeHasChanged) {
			text += "* ";
		}
		text += codeExecutor.getCompileMode();
		text += " / " + codeDB.getXMLFileName();
		infoLabel.setText(text);
		// infoLabel.setText( codeExecutor.getCompileMode() + " " + fontSize
		// +"pt");

	}

	class CodeRunner<T, V> extends SwingWorker {

		@Override
		public Boolean doInBackground() {
			String result = "";
			messageField.setText("compile ...\n");
			codeExecutor.setMessageField(messageField);
			String fileName = codeExecutor.createTmpSourceFile(codeInput.getText());
			System.out.println("CodeRunner fileName: " + fileName);
			if (fileName == null) {
				result += "ERROR " + board.getLastError();
				failedCompilation();
			} else {
				result += codeExecutor.compileAndExecute(fileName);
			}

			int maxLength = 5000;
			if (result.length() > maxLength) {
				System.out.println("Result too long: " + result.length());
				result = "... \n" + result.substring(result.length() - maxLength);
				// result = "zu viele Zeilen \n";
			}
			if (codeExecutor instanceof CodeExecutorJS) {
				messageField.append(result);
			} else if (!(codeExecutor instanceof CodeExecutorJava)) {
				messageField.append(result);
			}
			// if (board.getErrorCount() > 0) {
			// statusLabel.setText(board.getErrorCount() + " Fehler");
			// statusLabel.setForeground(Color.RED);
			// } else {
			// statusLabel.setText("keine Fehler");
			// statusLabel.setForeground(Color.GREEN);
			//
			// }

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
		executionInfoLabel.setText(messages.getString("startCompile"));
		executionInfoLabel.setBackground(Color.RED);
		executionInfoLabel.setOpaque(true);
		Sleep.sleep(100);

	}

	@Override
	public void failedCompilation() {
		executionInfoLabel.setText(messages.getString("failedCompile"));
		executionInfoLabel.setBackground(Color.RED);
		executionInfoLabel.setOpaque(true);
		runButton.setEnabled(true);
		stopButton.setEnabled(false);

	}

	@Override
	public void endCompilation() {
		executionInfoLabel.setText(messages.getString("compileEnded"));
		executionInfoLabel.setOpaque(false);
		Sleep.sleep(100);

	}

	@Override
	public void startExecution() {
		executionInfoLabel.setText(messages.getString("startExecution"));
		executionInfoLabel.setBackground(Color.GREEN);
		executionInfoLabel.setOpaque(true);
		Sleep.sleep(100);
	}

	@Override
	public void endExecution() {
		executionInfoLabel.setText(messages.getString("executionEnded"));
		executionInfoLabel.setOpaque(false);
		runButton.setEnabled(true);
		stopButton.setEnabled(false);

	}

	public String getCode() {
		return codeInput.getText();
	}

}
