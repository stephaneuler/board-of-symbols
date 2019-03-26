package jserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

enum Mode {
	SINGLE, MULTI, ALL_SYMBOLS, TRIANGLE, FRAME, ARROW, TREE, STAIRWAY, THM, ABC, 
	MODULO, DICE, STRIPES, X, Y, Z, SIZES, BACKGROUND, LETTERTREE, COORD, ABCOORD,
}

/**
 * Generator for BoS-patterns. The method generate() draws a pattern on a board
 * using the given mode. Parameters such as position, size and color vary
 * randomly.
 * 
 * @author Euler
 *
 */
public class PatternGenerator {
	public static final int HEADCOUNT = 7;    // number of board commands for setup
	
	// parameters for the random variations
	Random random = new Random();
	int[] colors = { XSendAdapter.RED, XSendAdapter.GREEN, XSendAdapter.BLUE, XSendAdapter.YELLOW };
	String[] forms = { "s", "c", "d", "*", "+" };
	String[] bgForms = { "+", "/", "\\", "none" };

	int N = 10;
	int hashCode;
	Board board;
	XSendAdapter xsa;

	public PatternGenerator(XSendAdapter xsa) {
		super();
		this.xsa = xsa;
		board = xsa.getBoard();
		xsa.groesse(N, N);
	}

	public PatternGenerator(int n) {
		super();
		N = n;
		xsa = new XSendAdapter();
		board = xsa.getBoard();
		xsa.groesse(N, N);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public static void main(String[] args) {
		PatternGenerator pg = new PatternGenerator(10);
		// pg.generateAll();

		pg.generate(Mode.ABCOORD, true, true);
	}

	public void generateAll() {
		for (int m = 0; m < 10; m++) {
			generate();
			saveToFile();
		}
	}

	private void saveToFile() {
		String filename = "pattern/p" + random.nextLong() + ".png";
		board.getGraphic().saveImageToFile(filename);
		System.out.println(filename + "  Hash: " + hashCode);
	}

	public void generate(TrainerLevel trainerLevel) {
		generate(trainerLevel.mode, trainerLevel.randomForm, trainerLevel.useBackGroundForm);
	}

	public void generate() {
		generate(Mode.MULTI);
	}

	public void generate(Mode mode) {
		generate(mode, false);
	}

	void generate(Mode mode, boolean randomForm) {
		generate(mode, randomForm, false);
	}

	/**
	 * @param mode
	 *            the pattern mode
	 * @param randomForm
	 *            if true use a random form
	 * @param useBG
	 *            if true fill the board with one of the background forms
	 */
	public void generate(Mode mode, boolean randomForm, boolean useBG) {
		BoardSerializer bs = new BoardSerializer();
		board.receiveMessage(Board.FILTER_PREFIX + "statusfontsize 18");
		board.receiveMessage(Board.FILTER_PREFIX + "fontsize 32");
		board.receiveMessage(Board.FILTER_PREFIX + "clearAllText");
		xsa.loeschen();
		xsa.formen("c");

		
		int zstart = random.nextInt(3);
		int sstart = random.nextInt(3);
		int zinc, sinc;
		do {
			zinc = 1 + random.nextInt(3);
			sinc = 1 + random.nextInt(3);
		} while (zinc == 1 && sinc == 1);

		int color = colors[random.nextInt(colors.length)];
		String status = "Mode: " + mode.toString() + " Farbe: ";
		String colorName = ColorNames.getName(color);
		if (colorName == null) {
			status += "0x" + Integer.toHexString(color).toUpperCase();
		} else {
			status += colorName;
		}
		xsa.statusText(status);
		String form = forms[random.nextInt(forms.length)];

		if (useBG) {
			String bgform = bgForms[random.nextInt(bgForms.length)];
			while (form.equals(bgform)) {
				bgform = forms[random.nextInt(forms.length)];
			}
			xsa.formen(bgform);
		}

		if (mode == Mode.SINGLE) {
			xsa.farbe2(sstart, zstart, color);
			if (randomForm) {
				xsa.form2(sstart, zstart, form);
			}

		} else if (mode == Mode.MULTI) {
			for (int z = zstart; z < N; z += zinc) {
				for (int s = sstart; s < N; s += sinc) {
					xsa.farbe2(s, z, color);
					if (randomForm) {
						xsa.form2(s, z, form);
					}
				}
			}

		} else if (mode == Mode.ALL_SYMBOLS) {
			SymbolType[] types =  SymbolType.values();
			List<Integer> ind = new ArrayList<>();
			for( int i=0; i<N*N; i++ ) {
				ind.add( i );
			}
			Collections.shuffle(ind);
			for( int i=0; i<types.length-2; i++ ) {
				xsa.form(ind.get(i), SymbolType.getShort( types[i] ) );
				xsa.farbe(ind.get(i), color);
			}


		} else if (mode == Mode.TRIANGLE) {
			sinc = 1 + random.nextInt(2);
			for (int s = sstart; s < N - sstart; s += sinc) {
				for (int z = sstart; z <= s; ++z) {
					xsa.farbe2(s, z, color);
					if (z == s) {
						xsa.form2(s, z, "trd");
					} else {
						xsa.form2(s, z, "s");
					}
				}
			}

		} else if (mode == Mode.FRAME) {
			int size = 4 + random.nextInt(3);
			Painter.waagrecht(xsa, sstart, zstart, size, color, "s");
			Painter.waagrecht(xsa, sstart, zstart + size - 1, size, color, "s");
			Painter.senkrecht(xsa, sstart, zstart, size, color, "s");
			Painter.senkrecht(xsa, sstart + size - 1, zstart, size, color, "s");
			xsa.form2(sstart, zstart, "tru");
			xsa.form2(sstart, zstart + size - 1, "trd");
			xsa.form2(sstart + size - 1, zstart, "tlu");
			xsa.form2(sstart + size - 1, zstart + size - 1, "tld");

		} else if (mode == Mode.X) {
			int x = N / 2 + random.nextInt(3) - 1;
			int y = N / 2 + random.nextInt(3) - 1;
			int size = 2;
			xsa.formen("d1");
			xsa.farben(XSendAdapter.BLUE);
			Painter.linie(xsa, x - size, y - size, 1, 1, 2 * size + 1, color, form);
			Painter.linie(xsa, x - size, y + size, 1, -1, 2 * size + 1, color, form);

		} else if (mode == Mode.Y) {
			int x = N / 2 + random.nextInt(3) - 1;
			int y = N / 2 + random.nextInt(3) - 1;
			int size = 3;
			xsa.formen("d1");
			xsa.farben(XSendAdapter.BLUE);
			Painter.linie(xsa, x, y, 1, 1, size, color, "/");
			Painter.linie(xsa, x, y, -1, 1, size, color, "\\");
			Painter.linie(xsa, x, y, 0, -1, y, color, "|");

		} else if (mode == Mode.Z) {
			int x = N / 2 + random.nextInt(3) - 1;
			int y = N / 2 + random.nextInt(3) - 1;
			int size = 2;
			xsa.formen("d1");
			xsa.farben(XSendAdapter.BLUE);
			Painter.linie(xsa, x - size, y - size, 1, 1, 2 * size + 1, color, form);
			Painter.linie(xsa, x - size, y + size, 1, 0, 2 * size + 1, color, form);
			Painter.linie(xsa, x - size, y - size, 1, 0, 2 * size + 1, color, form);

		} else if (mode == Mode.ARROW) {
			zstart = 1 + random.nextInt(2);
			sstart = 1 + random.nextInt(2);
			int max = (zstart > sstart) ? zstart : sstart;
			int size = N - 2 * max;
			int tip = random.nextInt(4);
			switch (tip) {
			case 0:
				Painter.waagrecht(xsa, sstart, zstart, size, color, "c");
				Painter.senkrecht(xsa, sstart, zstart, size, color, "c");
				Painter.linie(xsa, sstart, zstart, 1, 1, size, color, "c");
				break;
			case 1:
				Painter.waagrecht(xsa, sstart, zstart + size - 1, size, color, "c");
				Painter.senkrecht(xsa, sstart, zstart, size, color, "c");
				Painter.linie(xsa, sstart, zstart + size - 1, 1, -1, size, color, "c");
				break;
			case 2:
				Painter.waagrecht(xsa, sstart, zstart + size - 1, size, color, "c");
				Painter.senkrecht(xsa, sstart + size - 1, zstart, size, color, "c");
				Painter.linie(xsa, sstart, zstart, 1, 1, size, color, "c");
				break;
			case 3:
				Painter.waagrecht(xsa, sstart, zstart, size, color, "c");
				Painter.senkrecht(xsa, sstart + size - 1, zstart, size, color, "c");
				Painter.linie(xsa, sstart + size - 1, zstart, -1, 1, size, color, "c");
				break;
			}

		} else if (mode == Mode.STAIRWAY) {
			int width = 3 + random.nextInt(2);
			for (int s = sstart; s < N - width; s++) {
				Painter.waagrecht(xsa, s, zstart + (s - sstart), width, color, "s");
			}

		} else if (mode == Mode.SIZES) {
			xsa.formen( "none");
			for( int x=0; x<N; x++ ) {
				xsa.symbolGroesse2(x, x, 0.5*x/(N-1));
				xsa.farbe2(x, x, color);
				xsa.form2(x, x, form);
			}
			
		} else if (mode == Mode.BACKGROUND) {
			List<Integer> bgColors = new ArrayList<>();
			for( int c : colors ) {
				if( c != color ) {
					bgColors.add(c);
				}
			}
			bgColors.add( XSendAdapter.WHITE);
			bgColors.add( XSendAdapter.BLACK);
			Collections.shuffle(bgColors);
			
			++zstart; 
			++sstart;
			for( int x=0; x<colors.length; x++ ) {
				xsa.farbe2(sstart + x, zstart, color);
				xsa.form2(sstart + x, zstart, "+");
				xsa.hintergrund2(sstart + x, zstart, bgColors.get(x));
			}
			
		} else if (mode == Mode.THM) {
			xsa.text2(sstart, zstart, "T");
			xsa.text2(sstart + 1, zstart, "H");
			xsa.text2(sstart + 2, zstart, "M");

		} else if (mode == Mode.ABC) {
			for (int i = zstart; i < 26; i += zinc) {
				char c = (char) ('A' + i);
				xsa.farbe(sstart, color);
				xsa.text(sstart, "" + c);
				++sstart;
			}

		} else if (mode == Mode.MODULO) {
			int m = 5 + random.nextInt(4);
			for (int i = 0; i < N * N; i++) {
				xsa.text(i, "" + i % m);
			}

		} else if (mode == Mode.DICE) {
			int bgColor = colors[random.nextInt(colors.length)];
			xsa.farben(bgColor);
			for (int i = 1; i <= 6; i++) {
				for (int j = 1; j <= 6; j++) {
					xsa.form2(i + 1, 1 + j, "d" + i);
					xsa.farbe2(i + 1, 1 + j, color);
				}
			}

		} else if (mode == Mode.STRIPES) {
			xsa.formen("s");
			int M = N / 2 + +random.nextInt(5) - 2;
			for (int y = 0; y < N; y += 2) {
				for (int x = 0; x < M; x++) {
					xsa.farbe2(x, y, color);
				}
				for (int x = M; x < N; x++) {
					xsa.farbe2(x, y + 1, color);
				}
			}

		} else if (mode == Mode.TREE) {
			xsa.formen("none");
			int r = 10;
			for (int y = 1; y < 6; y++) {
				for (int x = y; x < r; x++) {
					if (x == 5) {
						xsa.farbe2(x, y + zstart, XSendAdapter.BROWN);
						xsa.form2(x, y + zstart, "s");
					} else {
						xsa.farbe2(x, y + zstart, color);
						xsa.form2(x, y + zstart, "B");
					}
				}
				--r;
			}

		} else if (mode == Mode.LETTERTREE) {
			board.receiveMessage(Board.FILTER_PREFIX + "fonttype Courier New");
			xsa.formen("none");
			String innen = randomElement( "*+#xo");
			String p = innen;
			String pg = "";
			for (int y = N-2; y >0; y--) {
				pg = p;
				if( useBG ) {
					while( pg.length() < N + 4 ) {
						pg = "." + pg + ".";
					}
				} 
				xsa.text2( N/2, y, pg);
				p = innen  + p + innen;
			}
			xsa.statusText("LETTERTREE, Textbreite: " + pg.length() + " Zeichen");

		} else if (mode == Mode.COORD) {
			xsa.formen( "c");
			xsa.farben( color );
			board.receiveMessage(Board.FILTER_PREFIX + "fontsize 18");
			for( int x=0; x<N; x++  ) {
				for( int y=0; y<N; y++ ) {
					xsa.text2( x, y, x + "," + y);
				}
			}
		} else if (mode == Mode.ABCOORD ) {
			xsa.formen( "c");
			xsa.farben( color );
			board.receiveMessage(Board.FILTER_PREFIX + "fontsize 18");
			for( int x=0; x<N; x++  ) {
				for( int y=0; y<N; y++ ) {
					xsa.text2( x, y, "" + (char)('A'+x)+ y);
				}
			}
		}
		
		bs.buildDocument(board);
		String s = bs.write();
		hashCode = s.hashCode();

	}

	private String randomElement(String string) {
		int r = random.nextInt( string.length());
		return string.substring(r, r+1);
	}

}
