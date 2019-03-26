package jserver;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Analyser {
	private static final String ASTERIX = "*******************************************";
	private CodeDB codeDB = new CodeDB();
	private XMLProtocol protocol = new XMLProtocol("analyse.xml", "analyses", "analysis");
	private String fileOpenDirectory = ".";
	private Board board;
	private CodeExecutorJava codeExecutor;
	private List<String> codes = new ArrayList<>();
	// private Map<String, Integer> warningCount = new TreeMap<>();
	private CounterMap<String> warningCount = new CounterMap<>();

	private String XMLFileName = "letters-ws18.xml";
	private String HTMLFileName = "analyserProtocol.html";
	private boolean doPatternAnalysis = true;
	private boolean doPMD = false;
	private boolean doCheckStyle = true;
	private String filter = ".*";
	private String contentFilter = null;
	private String imagePreFix = "";
	private String snippetPreFix = "";
	private String dirName = "analyserImages";
	private int maxCodeLength = 120;

	public static void main(String[] args) {
		Analyser analyser = new Analyser();
		// Analyser analyser = new
		// Analyser("muster-testat/Demir_cueneytdeniz","uebung3.xml", ".*");
		analyser.start();

	}

	public Analyser(String fileOpenDirectory, String xMLFileName, String filter) {
		super();
		this.fileOpenDirectory = fileOpenDirectory;
		XMLFileName = xMLFileName;
		this.filter = filter;

		// if necessary create directory for images
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}

	}

	public Analyser() {
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setContentFilter(String contentFilter) {
		this.contentFilter = contentFilter;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public String getFileOpenDirectory() {
		return fileOpenDirectory;
	}

	public void setFileOpenDirectory(String fileOpenDirectory) {
		this.fileOpenDirectory = fileOpenDirectory;
	}

	public String getXMLFileName() {
		return XMLFileName;
	}

	public void setXMLFileName(String xMLFileName) {
		XMLFileName = xMLFileName;
	}

	public String getHTMLFileName() {
		return HTMLFileName;
	}

	public void setHTMLFileName(String hTMLFileName) {
		HTMLFileName = hTMLFileName;
	}

	public void setImagePreFix(String imagePreFix) {
		this.imagePreFix = imagePreFix;
	}

	public void setSnippetPreFix(String snippetPreFix) {
		this.snippetPreFix = snippetPreFix;
	}

	public void start() {
		if (board == null) {
			board = new Board();
		}
		codeExecutor = new CodeExecutorJava(board);
		protocol.setAutoSave(false);
		protocol.nextTopChild("file");
		protocol.writeInfo("directory", fileOpenDirectory);
		protocol.writeInfo("fileName", XMLFileName);

		if (!loadSnippets()) {
			protocol.writeInfo("Warning", "konnte keine Snippets laden");
		} else {
			analyse();
			System.out.println(ASTERIX + " Summary " + ASTERIX);
			// warningCount.printSorted();
		}

		protocol.setHTMLFileName(HTMLFileName);
		protocol.save();
	}

	public void saveWarningSummary() {
		protocol.nextTopChild("warnings");
		Set<Entry<String, Integer>> warnings = warningCount.getSet();
		for (Entry<String, Integer> e : warnings) {
			protocol.writeInfo("warning", e.getKey() + ": " + e.getValue());
		}
		protocol.save();
	}

	public void checkSimilarity() {
		System.out.println("Starting checker");
		String result = "";
		for (String c1 : codes) {
			System.out.println(c1);
			String line =  "";
			for (String c2 : codes) {
				int d = calculate(c1, c2);
				System.out.printf("%5d ", d);
				line += d +" ";
			}
			result += line.trim() + "\n";
			System.out.println();
		}
//		System.out.print( result );
		try {
			Files.write(Paths.get("./checker.txt"), result.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void analyse() {
		JTextArea output = new JTextArea();
		codeExecutor.setMessageField(output);
		codeExecutor.setVerbose(false);
		System.out.println( "Start Analyse");

		List<String> snippetNames = codeDB.getSnippetNames();
		int numSnippets = 0;
		for (String snippetName : snippetNames) {
			System.out.println(ASTERIX + " " + snippetName + " " + ASTERIX);
			if (!snippetName.matches(filter)) {
				System.out.println("wrong snippet name, ignoring ...");
				continue;
			}
			String code = codeDB.getSnippetCode(snippetName);
			if( contentFilter != null ) {
				if (code.indexOf(contentFilter) < 0 ) {
					System.out.println("wrong content, ignoring ...");
					continue;
				}			
			}
			
			String language = codeDB.getSnippetLocale(snippetName);

			++numSnippets;
			protocol.nextChild("analysis");
			protocol.writeInfo("snippet-name", snippetPreFix + snippetName);
			protocol.writeCData("code", code.trim());

			String purCode = code.replaceAll("\\s", "");
			codes.add(purCode.substring(0, Integer.min(maxCodeLength , purCode.length())));

			CodeAnalyser codeAnalyser = new CodeAnalyser(code);

			Map<String, String> result = codeAnalyser.getResultMap();
			System.out.println("code analyser:");
			for (String key : result.keySet()) {
				protocol.writeInfo(key, result.get(key));
				System.out.println(key + ":" + result.get(key));
			}
			System.out.println();

			if (doPatternAnalysis) {
				if (!drawPattern(code, language)) {
					continue;
				}

				String filename = dirName + System.getProperty("file.separator") + imagePreFix + snippetName + ".png";
				protocol.writeInfo("imageFile", "" + filename);
				saveImage(filename);
				analysePattern();
			}

			checkStyle(codeAnalyser);
			protocol.up();
		}
		protocol.writeInfo("NumSnippets", "" + numSnippets);

	}

	private void checkStyle(CodeAnalyser codeAnalyser) {
		if (doCheckStyle) {
			int numberWarnings = codeAnalyser.checkstyle(protocol, warningCount);
			System.out.println("# checkStyle warnings: " + numberWarnings);
		}

		// zeigt keine Fehler
		// codeAnalyser.errorProne( protocol );
		if (doPMD) {
			codeAnalyser.runPMD(protocol);
		}
	}

	private void saveImage(final String fileName) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				BufferedImage image = board.getGraphic().getImage();
				try {
					ImageIO.write(image, "png", new File(fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private void analysePattern() {
		BoardAnalyser boardAnalyser = new BoardAnalyser(board);
		boardAnalyser.getKC();
		int size = boardAnalyser.getSerialSize();
		int comp = boardAnalyser.getCompressedSize();
		String text = "";
		text += board.getBoardModel().getNumberSymbols() + " Symbols" + Board.LS;
		text += "Serialised form, size: " + size + " Byte" + Board.LS;
		text += "Serialised form, zip-size: " + comp + " Byte" + Board.LS;
		text += "Ratio: " + String.format("%.4f", 1000. * comp / size) + " mBit" + Board.LS;
		System.out.println(text);

		protocol.writeInfo("symbols", "" + board.getBoardModel().getNumberSymbols());
		protocol.writeInfo("size", "" + size);
		protocol.writeInfo("zip-size", "" + comp);
		protocol.writeInfo("Ratio", "" + String.format("%.4f", 1000. * comp / size));

		Map<String, Integer> doubles = boardAnalyser.getDuplicateMessages();
		for (String message : doubles.keySet()) {
			protocol.writeInfo("multiple-command", message + " : " + doubles.get(message));
		}

		protocol.writeInfo("required-bosl-commands", "" + boardAnalyser.calcNumBoSLCommands());
	}

	private boolean drawPattern(final String code, String language) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					board.reset();
					board.resetMessageHistory();
					board.resetMaxOutOfRange();
					String fileName = codeExecutor.createTmpSourceFile(code, language);
					if (fileName == null) {
						System.out.println("compile failed *************");
						return;
					}
					codeExecutor.compileAndExecute("");
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("# BoSL commands: " + board.getMessageCount());
		protocol.writeInfo("BoSL-commands", "" + board.getMessageCount());
		protocol.writeInfo("out-of-ranges", "" + board.getOutOfRangeCount());
		return true;
	}

	private boolean loadSnippets() {
		codeDB.setXmlFile(new File(fileOpenDirectory, XMLFileName));
		try {
			codeDB.readXML();
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			String message = "Fehler beim Lesen der Code-Datei: \n" + e1.getLocalizedMessage();
			if (e1 instanceof SAXParseException) {
				SAXParseException se = (SAXParseException) e1;
				message += "\nZeile:" + se.getLineNumber() + " Spalte:" + se.getColumnNumber();
			}
			JOptionPane.showMessageDialog(null, message, "Codes lesen", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	// http://www.baeldung.com/java-levenshtein-distance
	static int calculate(String x, String y) {
		int[][] dp = new int[x.length() + 1][y.length() + 1];

		for (int i = 0; i <= x.length(); i++) {
			for (int j = 0; j <= y.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
							dp[i - 1][j] + 1, dp[i][j - 1] + 1);
				}
			}
		}

		return dp[x.length()][y.length()];
	}

	public static int min(int... numbers) {
		return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
	}

	public static int costOfSubstitution(char a, char b) {
		return a == b ? 0 : 1;
	}
}
