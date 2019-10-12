package jserver;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import evaluator.EvaluatorConfig;
import plotter.Sleep;

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
	private JTextArea messageArea = new JTextArea();

	private boolean doPatternAnalysis = true;
	private boolean doPMD = false;
	private boolean doCheckStyle = true;
	private String XMLFileName = "letters-ws18.xml";
	private String HTMLFileName = "analyserProtocol.html";
	private String filter = ".*";
	private String contentFilter = null;
	private String imagePreFix = "";
	private String snippetPreFix = "";
	private String imageDirName = "analyserImages";
	private String relativeImageDirName = "analyserImages";
	private String lastError = "";
	private int maxCodeLength = 120;
	private int drawingTimeout = 3;

	public static void main(String[] args) {
		Analyser analyser = new Analyser();
		analyser.start();
	}

	public void setMessageArea(JTextArea messageArea) {
		this.messageArea = messageArea;
	}

	public Analyser(String fileOpenDirectory, String xMLFileName, String filter) {
		super();
		this.fileOpenDirectory = fileOpenDirectory;
		XMLFileName = xMLFileName;
		this.filter = filter;

	}

	public Analyser() {
		checkImageDir();
	}

	public String getLastError() {
		return lastError;
	}

	private void checkImageDir() {
		// if necessary create directory for images
		File dir = new File(imageDirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public Analyser(EvaluatorConfig config) {
		setFilter(config.getFilter());
		setContentFilter(config.getContentFilter());
		setHTMLFileName(config.gethTMLFileName());
		setImageDirName(config.getImageDirName());
		setRelativeImageDirName(config.getImageDirName(), config.getAnalyserFileName());
		checkImageDir();
		protocol = new XMLProtocol(config.getAnalyserFileName(), "analyses", "analysis");
		protocol.writeInfo("snippet-filter", filter);
		protocol.writeInfo("content-filter", contentFilter);
	}

	private void setRelativeImageDirName(String imageDirName, String base) {
		FileSystem fs = FileSystems.getDefault();
		Path p1 = fs.getPath(imageDirName).toAbsolutePath();
		if (fs.getPath(base).getParent() == null) {
			base = "./" + base;
		}
		Path p2 = fs.getPath(base).getParent().toAbsolutePath();
		Path p3 = p2.relativize(p1);
		System.out.println("Resolve: " + p3);
		relativeImageDirName = p3.toString();
	}

	public String getImageDirName() {
		return imageDirName;
	}

	public void setImageDirName(String imageDirName) {
		this.imageDirName = imageDirName;
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
			analyseSnippets();
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
			String line = "";
			for (String c2 : codes) {
				int d = Utils.calculateLevenshtein(c1, c2);
				System.out.printf("%5d ", d);
				line += d + " ";
			}
			result += line.trim() + "\n";
			System.out.println();
		}
		try {
			Files.write(Paths.get("./checker.txt"), result.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void analyseSnippets() {
		codeExecutor.setMessageField(messageArea);
		codeExecutor.setVerbose(false);
		System.out.println("Start Analyse");

		List<String> snippetNames = codeDB.getSnippetNames();
		int numSnippets = 0;
		for (String snippetName : snippetNames) {
			System.out.println(ASTERIX + " " + snippetName + " " + ASTERIX);
			if (!snippetName.matches(filter)) {
				System.out.println("wrong snippet name, ignoring ...");
				continue;
			}
			String code = codeDB.getSnippetCode(snippetName);
			if (contentFilter != null) {
				if (code.indexOf(contentFilter) < 0) {
					System.out.println("wrong content, ignoring ...");
					continue;
				}
			}

			messageArea.append("Snippet: "+ snippetName + " ");
			String language = codeDB.getSnippetLocale(snippetName);

			++numSnippets;
			protocol.nextChild("analysis");
			protocol.writeInfo("snippet-name", snippetPreFix + snippetName);
			protocol.writeCData("code", code.trim());

			String purCode = code.replaceAll("\\s", "");
			codes.add(purCode.substring(0, Integer.min(maxCodeLength, purCode.length())));

			CodeAnalyser codeAnalyser = new CodeAnalyser(code);

			Map<String, String> result = codeAnalyser.getResultMap();
			System.out.println("code analyser:");
			for (String key : result.keySet()) {
				protocol.writeInfo(key, result.get(key));
				System.out.println(key + ":" + result.get(key));
			}
			System.out.println();

			if (doPatternAnalysis) {
				board.receiveMessage(">>t " + snippetPreFix + snippetName);

				if (!drawPattern(code, language)) {
					continue;
				}

				// String filename = imageDirName + System.getProperty("file.separator") +
				// imagePreFix + snippetName + ".png";
				// String filename = relativeImageDirName + System.getProperty("file.separator")
				// + imagePreFix + snippetName + ".png";
				String filename = imagePreFix + snippetName + ".png";
				protocol.writeInfo("imageFile", "" + relativeImageDirName + Board.FS + filename);
				saveImage(imageDirName + Board.FS + filename);
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

	class Task implements Callable<String> {
			@Override
			public String call() throws Exception {
				codeExecutor.compileAndExecute("");
				// Interrupts funktionieren nicht mehr 
	//			try {
	//				SwingUtilities.invokeAndWait(new Runnable() {
	//					public void run() {
	//						codeExecutor.compileAndExecute("");
	//					}
	//				});
	//			} catch (InvocationTargetException | InterruptedException e) {
	//				// e.printStackTrace();
	//				return "Interrupted !!!";
	//			}
				return "Ready!";
			}
		}

	private boolean drawPattern(final String code, String language) {
		board.reset();
		board.resetMessageHistory();
		board.resetMaxOutOfRange();
		String fileName = codeExecutor.createTmpSourceFile(code, language);
		if (fileName == null) {
			System.out.println("createTmpSourceFile failed *************");
			return false;
		}

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(new Task());

		try {
			System.out.println("Future: " + future.get(drawingTimeout, TimeUnit.SECONDS));
		} catch (TimeoutException | InterruptedException | ExecutionException e) {
			codeExecutor.stopExecution();
			board.receiveMessage("t timout!!!");
			int c = board.getColumns();
			int r = board.getRows();
			board.receiveMessage("#T " + c/2 + " " + r/2 + " timout!!!");
			future.cancel(true);
			System.out.println("timout - terminated!");
			protocol.writeInfo("error", "drawing timeout");
		}
		 executor.shutdownNow();

		System.out.println("# BoSL commands: " + board.getMessageCount());
		protocol.writeInfo("BoSL-commands", "" + board.getMessageCount());
		protocol.writeInfo("out-of-ranges", "" + board.getOutOfRangeCount());
		return true;
	}

	private boolean loadSnippets() {
		codeDB.setXmlFile(new File(XMLFileName));
		try {
			codeDB.readXML();
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			String message = "Fehler beim Lesen der Code-Datei: \n" + e1.getLocalizedMessage();
			if (e1 instanceof SAXParseException) {
				SAXParseException se = (SAXParseException) e1;
				message += "\nZeile:" + se.getLineNumber() + " Spalte:" + se.getColumnNumber();
			}
			protocol.writeInfo("XML-error", message);
			lastError = message;
			return false;
		}
		return true;
	}


	public String getRelativeImageDirName() {
		return relativeImageDirName;
	}

	public void setRelativeImageDirName(String relativeImageDirName) {
		this.relativeImageDirName = relativeImageDirName;
	}

}