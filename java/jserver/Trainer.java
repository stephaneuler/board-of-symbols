package jserver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Trainer extends JFrame implements ActionListener {
	private static final String PROTOCOL_TEXT= "Protocol";
	private static final String LEVEL_DOWN = "L -";
	private static final String LEVEL_UP = "L +";
	private static final long serialVersionUID = 1L;
	
	Board board;
	JLabel goal = new JLabel();
	JLabel result = new JLabel(" ------------ ");
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
	private int maxLevel = 7;
	private TrainerProtocol protocol = new TrainerProtocol();
	private Random random = new Random();
	private int[] hitCount = new int[maxLevel+1];
	private ResourceBundle messages;
	private String checkText;
	//private String startTime = time();


	public Trainer(Board board) {
		super("Trainer");
		this.board = board;

		messages = board.getMessages();
		
		setTitle( "Trainer "  + messages.getString("trainerVersion"));
		setStrings();

		setLocation(600, 5);
		Component contents = svCreateComponents();
		getContentPane().add(contents, BorderLayout.CENTER);

	}

	private void setStrings() {
		checkText = messages.getString("check");
		
	}

	private String statusText() {
		return patterns + " " + messages.getString("pattern") + ", " + 
				attempts + " " + messages.getString("attempts") + " , " + 
				hits + " " + messages.getString("hits");
	}

	public static void main(String[] args) {
		Trainer t = new Trainer(new Board());
		t.loadImage();
		t.setVisible(true);

	}

	/**
	 * create all components (buttons, sliders, views, etc) and arrange them
	 */
	public Component svCreateComponents() {

		JMenu menuInfos;
		JMenuBar menuBar = new JMenuBar();

		menuInfos = new JMenu("Infos")
				;
		Utils.addMenuItem(this, menuInfos, PROTOCOL_TEXT);
		menuBar.add(menuInfos);
		
		setJMenuBar(menuBar);

		JPanel basePane = new JPanel();
		basePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		basePane.setLayout(new BorderLayout());

		Box controllBox = new Box(BoxLayout.X_AXIS);
		levelButtonUp = new JButton(LEVEL_UP);
		levelButtonUp.addActionListener(this);
		controllBox.add(levelButtonUp);
		
		levelLabel = new JLabel( levelText() );
		controllBox.add(levelLabel);

		levelButtonDown = new JButton(LEVEL_DOWN);
		levelButtonDown.addActionListener(this);
		controllBox.add(levelButtonDown);

		nextButton = new JButton("next");
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
		if (level == 1) {
			pg.generate(Mode.SINGLE);
		} else if (level == 2) {
			pg.generate(Mode.SINGLE, true);
		} else if (level == 3) {
			pg.generate(Mode.MULTI);
		} else if (level == 4) {
			pg.generate(Mode.MULTI, true);
		} else if (level == 5) {
			pg.generate(Mode.TRIANGLE);
		} else if (level == 6) {
			pg.generate(Mode.FRAME);
		} else if (level == 7) {
			pg.generate(Mode.ARROW);
		}
		hashCode = pg.getHashCode();
		BufferedImage image = board.getGraphic().getImage();
		goal.setIcon(new ImageIcon(image));
		pack();
		xsa.loeschen();
		xsa.formen("c");
		
		String dirName = "pattern";
		String filename = dirName+ System.getProperty("file.separator") +"p" + random.nextLong() + ".png";
		File dir = new File( dirName);
		if( ! dir.exists() ) {
			dir.mkdirs();
		}
		try {
			ImageIO.write(image, "png", new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}

		protocol.nextImage();
		protocol.writeInfo( "time", time() );
		protocol.writeInfo( "level", ""+level);
		protocol.writeInfo( "file", ""+ filename);
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
				protocol.writeInfo("hit", time() );
				protocol.writeCData("code",board.getCodeWindow().getCode() );
			} else {
				result.setText("Leider nicht!");
				protocol.writeInfo("fail", time());
			}

		} else if (cmd.equals(LEVEL_UP)) {
			if (level < maxLevel) {
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

		} else if (cmd.equals("next")) {
			nextImage();

		} else if (cmd.equals(PROTOCOL_TEXT)) {
//			String results = "Results:" + System.lineSeparator();
//			results += "Start: " + startTime + System.lineSeparator();
//			results += "aktuell: " + time() + System.lineSeparator();
//			for( int l=1; l<=maxLevel; l++ ) {
//				results += "Level: " + l + "   " + hitCount[l] + " hits" + System.lineSeparator();
//			}
//			JLabel test = new JLabel( protocol.getText() );
//			JOptionPane.showMessageDialog(this, test,
//					"Protocol", JOptionPane.INFORMATION_MESSAGE);
			
			File file = new File(protocol.getHTMLFileName());
			try {
			    Desktop.getDesktop().browse(file.toURI());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(),
						"Protocol", JOptionPane.ERROR_MESSAGE);
			}

		}
		statusLabel.setText(statusText());

	}

	private void nextImage() {
		++patterns;
		loadImage();
		checkButton.setEnabled(true);
	}

	private String time() {
		return Calendar.getInstance().getTime().toString();
	}

}
