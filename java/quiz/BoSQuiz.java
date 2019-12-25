package quiz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import jserver.Board;
import jserver.BoardClickEvent;
import jserver.BoardClickListener;
import jserver.Utils;
import jserver.XSendAdapter;
import plotter.Graphic;

/**
 * BoS Quiz
 * 
 * @author Stephan Euler
 *
 */
public class BoSQuiz implements BoardClickListener {
	private static final String VERSION = "BoS Quiz V0.3 Dezember 2019";
	private XSendAdapter xsend = new XSendAdapter();
	private int size = 8;
	private Questioner questioner = new Questioner();
	private JLabel rundenLabel = new JLabel("Versuche:");
	private Graphic graphic = xsend.getBoard().getGraphic();
	private Random random = new Random();
	private Properties properties = new Properties();
	private String propertiesFile = "quiz.properties";
	private QuizField[][] quizField;
	private int[] colors = { XSendAdapter.GREEN, XSendAdapter.YELLOW, XSendAdapter.RED, 0xbb0000, 0x660000 };
	private int versuche = 0;
	private JTextPane lastQuestionField = new JTextPane();
	private JTextField counterField = new JTextField();
	private int numCorrect;
	private int numFalse;
	private boolean showNumbering = false;

	public static void main(String[] args) {
		BoSQuiz simu = new BoSQuiz();
		simu.start();
	}

	public int getSize() {
		return size;
	}

	private void start() {
		System.out.println("BoS Quiz V0.2 Dezember 2019");
		readConfig();
//		saveConfig();

		quizField = new QuizField[size][size];
		getTopics();
		setupGUI();
		fillQuestions();

	}

	private List<String> getTopics() {
		List<String> files = new ArrayList<>();
		Path path = FileSystems.getDefault().getPath("ressources");
		System.out.println("path: " + path.toAbsolutePath());

		if (path.toFile().exists() && path.toFile().isDirectory()) {
			Set<String> fileSet = Stream.of(new File("ressources").listFiles())
					.filter(file -> file.getName().endsWith(".top")).map(File::getName).collect(Collectors.toSet());
			files.addAll(fileSet);
			Collections.sort(files);
		} else {
			JOptionPane.showMessageDialog(graphic, "Verzeichnis <<" + path + ">> nicht gefunden", "Fragen-Verzeichnis",
					JOptionPane.ERROR_MESSAGE);
		}
		return files;
	}

	private void fillQuestions() {
		int n = 0;
		for (Question question : questioner.getQuestions()) {
			int x;
			int y;
			do {
				x = random.nextInt(size);
				y = random.nextInt(size);
			} while (quizField[x][y] != null);
			quizField[x][y] = new QuizField();
			quizField[x][y].setQuestion(question);
			xsend.farbe2(x, y, XSendAdapter.YELLOW);
			if( showNumbering ) {
				xsend.text2(x, y, "" + n);				
			}
			++n;
		}
	}

	private void saveConfig() {
		properties.setProperty("size", "" + size);
		properties.setProperty("showNumbering", "" + showNumbering);
		try {
			properties.store(new FileWriter(propertiesFile), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readConfig() {
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(propertiesFile));
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			System.out.println("properties file " + propertiesFile + " not found");
		} catch (IOException e) {
			e.printStackTrace();
		}
		size = Integer.parseInt(properties.getProperty("size", "" + size));
		showNumbering = Boolean.parseBoolean(properties.getProperty("showNumbering", "" + showNumbering));
	}

	private void setupGUI() {
		xsend.groesse(size, size);
		xsend.getBoard().addClickListener(this);
		xsend.getBoard().receiveMessage("statusfontsize 14");

		graphic.setSize(600, 600);
		graphic.setLocation(100, 60);
		graphic.setTitle(VERSION);
		xsend.statusText("Bitte Thema aussuchen und dann gelbe Fragepunkte anklicken");

		lastQuestionField.setContentType("text/html");
		lastQuestionField.setEditable(false);
//		lastQuestionField.setText("<html>erste Zeile <p/> zweite Zeile</html>");
		counterField.setEditable(false);

		graphic.addSouthComponent(new JLabel("Letzte Frage"));
		graphic.addSouthComponent(lastQuestionField);
		graphic.addSouthComponent(rundenLabel);
		graphic.addSouthComponent(counterField);

		graphic.removeMenu("Formen");
		graphic.removeMenu("Brett");
		graphic.removeMenu("Optionen");
		graphic.removeMenu("Hilfe");

		JMenu topicsMenu = new JMenu("Themen");
		List<String> files = getTopics();
		for (String file : files) {
			addQuestionsMenue(topicsMenu, file);
		}
		graphic.addExternMenu(topicsMenu);

		JMenu statistikMenu = new JMenu("Statistik");
		graphic.addExternMenu(statistikMenu);

		JMenuItem fragen = new JMenuItem("Versuche");
		fragen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double pCorrect = 0;
				int sum = numCorrect + numFalse;
				if (sum != 0) {
					pCorrect = 100. * numCorrect / sum;
				}
				String text = String.format("Richtig: %d (%.1f%%), Falsch: %d", numCorrect, pCorrect, numFalse);
				JOptionPane.showMessageDialog(graphic, text, "Versuche", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		statistikMenu.add(fragen);

		JMenu hilfeMenu = new JMenu("Hilfe");
		graphic.addExternMenu(hilfeMenu);
		JMenuItem anleitung = new JMenuItem("Anleitung");
		anleitung.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showHelp("BoSQuiz Hilfe", "help.html");
			}
		});
		hilfeMenu.add(anleitung);

		JMenuItem faq = new JMenuItem("FAQ");
		faq.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showHelp("BoSQuiz FAQ", "faq.html");
			}
		});
		hilfeMenu.add(faq);
		
		graphic.pack();
		graphic.repaint();

		// questioner.loadQuestions(startTopic + ".txt");
		graphic.setTitle(VERSION);

	}

	protected void showHelp(String title, String filename ) {
		
		JFrame frame = new JFrame(title);
		frame.setSize(560, 450);
		frame.setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		LayoutManager layout = new FlowLayout();
		panel.setLayout(layout);

		JEditorPane jEditorPane = new JEditorPane();
		jEditorPane.setEditable(false);

		Path path = FileSystems.getDefault().getPath("ressources" + Board.FS + filename);
		jEditorPane.setContentType("text/html");
		try {
			List<String> lines = Files.readAllLines(path);
			String all = "";
			for (String line : lines) {
				all += line;
			}
			jEditorPane.setText(all);
		} catch (IOException e) {
			jEditorPane.setText("<html>Page "+ filename + " not found.</html>");
		}

		JScrollPane jScrollPane = new JScrollPane(jEditorPane);
		jScrollPane.setPreferredSize(new Dimension(540, 400));

		panel.add(jScrollPane);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setVisible(true);
	}

	private void addQuestionsMenue(JMenu topicsMenu, String file) {
		JMenu menu = new JMenu(file.split("\\.")[0]);
		Path path = FileSystems.getDefault().getPath("ressources" + Board.FS + file);
		List<String> questionFiles;
		try {
			questionFiles = Files.readAllLines(path);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println(questionFiles);

		for (String name : questionFiles) {
			JMenuItem menuItem = new JMenuItem(name);
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					reset();
					questioner.removeAllQuestions();
					questioner.loadQuestions(name + ".txt");
					fillQuestions();
					String t = e.getActionCommand();
					JOptionPane.showMessageDialog(graphic, questioner.getInfo(), "Fragen-Set " + t,
							JOptionPane.INFORMATION_MESSAGE);
					graphic.setTitle(VERSION + " - " + t);
				}

			});
			menu.add(menuItem);
		}
		topicsMenu.add(menu);
	}

	protected void reset() {
		quizField = new QuizField[size][size];
		xsend.getBoard().reset();
	}

	@Override
	public void boardClick(BoardClickEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (x >= quizField.length | y >= quizField[0].length) {
			return;
		}
		if (quizField[x][y] != null) {
			boolean correct = quizField[x][y].getQuestion().ask();
			if (correct) {
				quizField[x][y].decCount();
				xsend.statusText("super");
				++numCorrect;
			} else {
				quizField[x][y].incCount();
				xsend.statusText("sorry");
				++numFalse;
			}
			if (quizField[x][y].getCount() == 0) {
				// Path path = FileSystems.getDefault().getPath("ressources",
				// "pattern_okay.jpg");
				// System.out.println(path.toAbsolutePath());
				// xsend.getBoard().receiveMessage("image " + x + " " + y + " " +
				// path.toAbsolutePath());
				xsend.getBoard().receiveMessage("image " + x + " " + y + " ressources/pattern_okay.jpg");
				xsend.form2(x, y, "none");
			} else {
				xsend.getBoard().receiveMessage("image " + x + " " + y + " -");
				xsend.form2(x, y, "c");
				xsend.farbe2(x, y, colors[quizField[x][y].getCount()]);
			}
			++versuche;
			String text = "";
			text = quizField[x][y].getQuestion().getQuestion();
			lastQuestionField.setText(Utils.clipText(text, 30));
			text = "" + versuche;
			counterField.setText(text);
		}
	}

}
