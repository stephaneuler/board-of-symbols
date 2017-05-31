package jserver;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class InfoBox extends JFrame implements ActionListener {
	JEditorPane textArea;

	public JEditorPane getTextArea() {
		return textArea;
	}

	public InfoBox(Frame parent, String msg, int xsize, int ysize) {
		super();
		setBackground(Color.lightGray);
		setLayout(new BorderLayout());
		Point parloc = parent.getLocation();
		setLocation(parloc.x + 50, parloc.y + 50);
		setSize(xsize, ysize);
		textArea = new JEditorPane(  );
		textArea.setEditable(false);
		//textArea.setBackground(Color.lightGray);
		JScrollPane scrollPane = new JScrollPane(textArea);
		add("Center", scrollPane);
		Button button = new Button("OK");
		button.addActionListener(this);
		add("South", button);
	}

	public void actionPerformed(ActionEvent event) {
		setVisible(false);
		dispose();
	}

	public void setText(List<String> lines) {
		String code = "";
		for( int l=0; l<lines.size(); l++ ) {
			code += String.format("%3d", l + 1) + " " + lines.get(l) + "\n";
		}
		textArea.setText(code);		
	}
}
