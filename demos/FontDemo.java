package demos;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Random;

import jserver.Board;
import jserver.XSendAdapter;
import plotter.Sleep;

public class FontDemo {

	public static void main(String[] args) {
		Board board = new Board();
		board.setSize(900, 260);
		XSendAdapter xsend = new XSendAdapter(board);
		xsend.groesse(1, 2);
		xsend.formen("none");
		board.receiveMessage("fontsize 32");
		board.receiveMessage("statusfontsize 32");
		xsend.text2(0, 1, "abcdefghijklmnopqrstuvwxyz");
		xsend.text2(0, 0, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fonts = ge.getAllFonts();

		Random r = new Random();
		for (;;) {
			int i = r.nextInt(fonts.length);
			String family = fonts[i].getFamily();
			xsend.statusText(family);
			board.receiveMessage("fonttype " + family);
			Sleep.sleep(1000);
		}

	}
}
