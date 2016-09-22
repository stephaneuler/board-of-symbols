package jserver;

import java.util.Random;

public class PatternGenerator {

	public static void main(String[] args) {
		BoardSerializer bs = new BoardSerializer();
		Random random = new Random();
		int N = 10;
		int[] colors = { 0xFF, 0xFF00, 0xFF0000 };

		XSendAdapter xsa = new XSendAdapter();
		Board b = xsa.getBoard();
		xsa.groesse(N, N);

		for (int m = 0; m < 10; m++) {
			xsa.loeschen();
			bs = new BoardSerializer();
			int zstart = random.nextInt(3);
			int sstart = random.nextInt(3);
			int zinc, sinc;
			do {
				zinc = 1 + random.nextInt(3);
				sinc = 1 + random.nextInt(3);
			} while (zinc == 1 && sinc == 1);
			int color = colors[random.nextInt(colors.length)];
			xsa.statusText("Farbe: 0x" + Integer.toHexString(color).toUpperCase());
			for (int z = zstart; z < N; z += zinc) {
				for (int s = sstart; s < N; s += sinc) {
					xsa.farbe2(s, z, color);
				}
			}

			bs.serialize(b);
			String s = bs.write();
			// System.out.println( s );
			String filename = "pattern/p" + random.nextLong() + ".png";
			b.getGraphic().saveImageToFile(filename);
			System.out.println(filename + "  Hash: " + s.hashCode());
		}

	}

}
