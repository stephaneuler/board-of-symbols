package jserver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import plotter.Graphic;

public class Trainer extends JFrame implements ActionListener {
	private static final String PATTERN_URL = "https://hosting.iem.thm.de/user/euler/gallery2/index.php?inhalt=pattern";
	private static final String beforeCheck = " ------------ ";
	private static final String LEVEL_TEXT = "Level";
	private static final String PROTOCOL_TEXT = "Protocol";
	private static final String LEVEL_DOWN = "L -";
	private static final String LEVEL_UP = "L +";
	private static final long serialVersionUID = 1L;

	Board board;
	JLabel goal = new JLabel();
	JLabel result = new JLabel(beforeCheck);
	private JLabel statusLabel;
	private JLabel levelLabel;
	private int hashCode;
	private JButton levelButtonUp;
	private JButton levelButtonDown;
	private JButton nextButton;
	private JButton checkButton;
	private int patterns = 1;
	private int attempts = 0;
	private int hits = 0;
	private int level = 1;
	private XMLProtocol protocol = new XMLProtocol();
	private Random random = new Random();
	private ResourceBundleWrapper messages;
	private String checkText;
	private String nextText;
	private String patternInfoText;
	private int numMessagesGenerator;
	private int numMessagesUser;

	// private String startTime = time();
	private TrainerLevel[] levels = { 
			new TrainerLevel(TrainerMode.SINGLE, false, false, "basic"),
			new TrainerLevel(TrainerMode.SINGLE, true, false, "basic"), 
			new TrainerLevel(TrainerMode.SINGLE, true, true, "basic"),
			new TrainerLevel(TrainerMode.MULTI, false, false, "basic"), 
			new TrainerLevel(TrainerMode.MULTI, true, false, "basic"),
			new TrainerLevel(TrainerMode.ALL_SYMBOLS, false, false, "basic"), 
			new TrainerLevel(TrainerMode.BACKGROUND, false, false, "basic"),
			new TrainerLevel(TrainerMode.STRIPES, false, false, "pattern"),
			new TrainerLevel(TrainerMode.STAIRWAY, false, false, "pattern"), 
			new TrainerLevel(TrainerMode.TRIANGLE, false, false, "pattern"),
			new TrainerLevel(TrainerMode.FRAME, false, false, "pattern"), 
			new TrainerLevel(TrainerMode.X, true, false, "pattern"),
			new TrainerLevel(TrainerMode.Y, true, false, "pattern"), 
			new TrainerLevel(TrainerMode.Z, true, false, "pattern"), 
			new TrainerLevel(TrainerMode.ARROW, false, false, "pattern"),
			new TrainerLevel(TrainerMode.ARROW, false, true, "pattern"), 
			new TrainerLevel(TrainerMode.TREE, false, false, "pattern"),
			new TrainerLevel(TrainerMode.DICE, false, true, "pattern"), 
			new TrainerLevel(TrainerMode.SIZES, true, false, "pattern"), 
			new TrainerLevel(TrainerMode.THM, false, false, "text"), 
			new TrainerLevel(TrainerMode.ABC, false, false, "text"),
			new TrainerLevel(TrainerMode.MODULO, false, false, "text"),
			new TrainerLevel(TrainerMode.COORD, false, false, "text"),
			new TrainerLevel(TrainerMode.ABCOORD, false, false, "text"),
			new TrainerLevel(TrainerMode.LETTERTREE, false, false, "text"),
			new TrainerLevel(TrainerMode.LETTERTREE, false, true, "text"),
			};
	private int[] hitCount = new int[levels.length + 1];

	public Trainer(Board board) {
		super("Trainer");
		this.board = board;

		messages = board.getMessageWrapper();

		setTitle("Trainer " + messages.getString("trainerVersion"));
		setStrings();

		setLocation(600, 5);
		Component contents = svCreateComponents();
		getContentPane().add(contents, BorderLayout.CENTER);

	}

	private void setStrings() {
		checkText = messages.getString("check");
		nextText = messages.getString("next");
		patternInfoText = messages.getString("patternInfo");

	}

	private String statusText() {
		return patterns + " " + messages.getString("pattern") + ", " + attempts + " " + messages.getString("attempts")
				+ " , " + hits + " " + messages.getString("hits");
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Trainer t = new Trainer(new Board());
					t.loadImage();
					t.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * create all components (buttons, sliders, views, etc) and arrange them
	 * 
	 * @param image
	 */
	public Component svCreateComponents() {

		JMenu menuInfos;
		JMenuBar menuBar = new JMenuBar();
		URL imageURL = Board.class.getResource("images/trainer.jpg");
		setIconImage(new ImageIcon(imageURL).getImage());

		menuInfos = new JMenu("Infos");
		Utils.addMenuItem(this, menuInfos, PROTOCOL_TEXT);
		Utils.addMenuItem(this, menuInfos, patternInfoText);
		menuBar.add(menuInfos);

		JMenu menuLevels = new JMenu("Levels");
		for (int level = 0; level < levels.length; level++) {
			if( level > 1 && ! levels[level].hasSameTopic( levels[level-1]) ) {
				menuLevels.addSeparator();
			}
			JMenuItem li = Utils.addMenuItem(this, menuLevels, levels[level].toString());
			li.setActionCommand(LEVEL_TEXT + " " + (level + 1));
			li.setToolTipText( levels[level].getTopic() );
		}
		menuBar.add(menuLevels);

		setJMenuBar(menuBar);

		JPanel basePane = new JPanel();
		basePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		basePane.setLayout(new BorderLayout());

		Box controllBox = new Box(BoxLayout.X_AXIS);
		levelButtonUp = new JButton(LEVEL_UP);
		levelButtonUp.addActionListener(this);
		controllBox.add(levelButtonUp);

		levelLabel = new JLabel(levelText());
		controllBox.add(levelLabel);

		levelButtonDown = new JButton(LEVEL_DOWN);
		levelButtonDown.addActionListener(this);
		controllBox.add(levelButtonDown);

		nextButton = new JButton(nextText);
		nextButton.addActionListener(this);
		controllBox.add(nextButton);

		checkButton = new JButton(checkText);
		checkButton.addActionListener(this);
		controllBox.add(checkButton);
		controllBox.add(result);

		statusLabel = new JLabel(statusText());

		basePane.add("Center", goal);
		basePane.add("North", statusLabel);
		basePane.add("South", controllBox);
		return basePane;
	}

	private String levelText() {
		return level + ". Level";
	}

	public void loadImage() {

		final XSendAdapter xsa = new XSendAdapter(board);
		PatternGenerator pg = new PatternGenerator(xsa);
		int ncstart = board.getMessageCount();
		pg.generate(levels[level - 1]);
		numMessagesGenerator = board.getMessageCount() - ncstart + 1;
		numMessagesGenerator -= PatternGenerator.HEADCOUNT;
		hashCode = pg.hashCode();
		BufferedImage image = board.getGraphic().getImage();
		goal.setIcon(new ImageIcon(image));
		pack();
		xsa.loeschen();
		xsa.formen("c");
		System.out.println("Trainer: " + board);
		board.receiveMessage(Board.FILTER_PREFIX + "clearAllText");
		numMessagesUser = board.getMessageCount();

		String dirName = "pattern";
		String filename = dirName + System.getProperty("file.separator") + "p" + random.nextLong() + ".png";
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			ImageIO.write(image, "png", new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}

		protocol.nextTopChild();
		protocol.writeInfo("time", time());
		protocol.writeInfo("level", "" + level);
		protocol.writeInfo("mode", "" + levels[level - 1].mode);
		protocol.writeInfo("file", "" + filename);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		System.out.println("CodeWindow cmd: " + cmd);

		if (cmd.equals(checkText)) {
			int dest = board.getHashCode();
			System.out.println(hashCode + " " + dest);
			++attempts;
			if (hashCode == dest) {
				++hits;
				++hitCount[level];
				result.setText("SUPER!");
				checkButton.setEnabled(false);
				protocol.writeInfo("hit", time());
				if (board.getCodeWindow() == null) {
					protocol.writeCData("code", "???");
				} else {
					protocol.writeCData("code", board.getCodeWindow().getCode());
				}

				String message = " ** Super! **" + System.lineSeparator();
				message += " generator: " + numMessagesGenerator + " BoS commands" + System.lineSeparator();
				int userMessages = board.getMessageCount() - numMessagesUser;
				message += " user: " + userMessages + " BoS commands" + System.lineSeparator();
				URL imageURL = this.getClass().getResource("images/pattern_okay.jpg");
				Icon icon = new ImageIcon(imageURL);
				JOptionPane.showMessageDialog(null, message, checkText, JOptionPane.OK_OPTION, icon);
			} else {
				result.setText(messages.getString("sorryFail"));
				protocol.writeInfo("fail", time());
			}

		} else if (cmd.equals(LEVEL_UP)) {
			if (level < levels.length) {
				++level;
				levelLabel.setText(levelText());
				nextImage();
			}

		} else if (cmd.equals(LEVEL_DOWN)) {
			if (level > 1) {
				--level;
				levelLabel.setText(levelText());
				nextImage();
			}

		} else if (cmd.equals(nextText)) {
			nextImage();

		} else if (cmd.startsWith(LEVEL_TEXT)) {
			String ls = cmd.substring(LEVEL_TEXT.length()).trim();
			level = Integer.parseInt(ls);
			levelLabel.setText(levelText());
			nextImage();

		} else if (cmd.equals(PROTOCOL_TEXT)) {
			File file = new File(protocol.getHTMLFileName());
			try {
				Desktop.getDesktop().browse(file.toURI());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), "Protocol", JOptionPane.ERROR_MESSAGE);
			}

		} else if (cmd.equals(patternInfoText)) {
			try {
				Desktop.getDesktop().browse(new URI(PATTERN_URL));
			} catch (IOException | URISyntaxException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), "Protocol", JOptionPane.ERROR_MESSAGE);
			}

		}
		statusLabel.setText(statusText());

	}

	private void nextImage() {
		++patterns;
		loadImage();
		checkButton.setEnabled(true);
		result.setText(beforeCheck);
	}

	private String time() {
		return Calendar.getInstance().getTime().toString();
	}

}
