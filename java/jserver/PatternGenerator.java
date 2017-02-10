package jserver;

import java.util.Random;

enum Mode {
	SINGLE, MULTI, TRIANGLE, FRAME, ARROW
}

public class PatternGenerator {
	Random random = new Random();
	int[] colors = { XSendAdapter.RED, XSendAdapter.GREEN, XSendAdapter.BLUE, XSendAdapter.YELLOW };
	String[] forms = { "s", "c", "d", "*", "+" };
	int N = 10;
	int hashCode;
	XSendAdapter xsa;
	Board b;

	public int getHashCode() {
		return hashCode;
	}

	public PatternGenerator(XSendAdapter xsa) {
		super();
		this.xsa = xsa;
		b = xsa.getBoard();
		xsa.groesse(N, N);
	}

	public PatternGenerator(int n) {
		super();
		N = n;
		xsa = new XSendAdapter();
		b = xsa.getBoard();
		xsa.groesse(N, N);
	}

	public static void main(String[] args) {
		PatternGenerator pg = new PatternGenerator(10);
		// pg.generateAll();

		pg.generate(Mode.ARROW, true);
	}

	public void generateAll() {
		for (int m = 0; m < 10; m++) {
			generate();
			saveToFile();
		}
	}

	private void saveToFile() {
		// System.out.println( s );
		String filename = "pattern/p" + random.nextLong() + ".png";
		b.getGraphic().saveImageToFile(filename);
		System.out.println(filename + "  Hash: " + hashCode);
	}

	public void generate() {
		generate(Mode.MULTI);
	}

	public void generate(Mode mode) {
		generate(mode, false);
	}

	public void generate(Mode mode, boolean randomForm) {
		BoardSerializer bs = new BoardSerializer();
		b.receiveMessage("statusfontsize 20");
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
		xsa.statusText("Mode: " + mode.toString() + " Farbe: 0x" + Integer.toHexString(color).toUpperCase() );
		String form = forms[random.nextInt(forms.length)];

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

		} else if (mode == Mode.ARROW) {
			zstart = 1 + random.nextInt(2);
			sstart = 1 + random.nextInt(2);
			int max = (zstart > sstart) ? zstart : sstart;
			int size = N - 2* max;
			int tip = random.nextInt(4);
			switch (tip) {
			case 0:
				Painter.waagrecht(xsa, sstart, zstart, size, color, "c");
				Painter.senkrecht(xsa, sstart, zstart, size, color, "c");
				Painter.linie(xsa, sstart, zstart, 1, 1, size, color, "c");
				break;
			case 1:
				Painter.waagrecht(xsa, sstart, zstart+size-1, size, color, "c");
				Painter.senkrecht(xsa, sstart, zstart, size, color, "c");
				Painter.linie(xsa, sstart, zstart+size-1, 1, -1, size, color, "c");
				break;
			case 2:
				Painter.waagrecht(xsa, sstart, zstart+size-1, size, color, "c");
				Painter.senkrecht(xsa, sstart+size-1, zstart, size, color, "c");
				Painter.linie(xsa, sstart, zstart, 1, 1, size, color, "c");
				break;
			case 3:
				Painter.waagrecht(xsa, sstart, zstart, size, color, "c");
				Painter.senkrecht(xsa, sstart+size-1, zstart, size, color, "c");
				Painter.linie(xsa, sstart+size-1, zstart, -1, 1, size, color, "c");
				break;
			}
		}

		bs.serialize(b);
		String s = bs.write();
		hashCode = s.hashCode();

	}

}
