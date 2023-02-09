package simulationen;

import java.util.Random;

import jserver.Board;
import jserver.XSendAdapter;
import plotter.Sleep;

public class Hanoi {
	private XSendAdapter xsend = new XSendAdapter();
	Board board = xsend.getBoard();
	int sleepTime = 100;
	int N = 64;
	int[][] tuerme = new int[3][N];
	private int anzahlSchritte = 0;
	private int[] colors = new int[N];

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		(new Hanoi()).demo();

	}

	public Hanoi() {
		
		xsend.groesse(3, N+1);
		xsend.formen( "none" );
		xsend.text2(0, N, "A");
		xsend.text2(1, N, "Z");
		xsend.text2(2, N, "B");
		
		Random r = new Random();
		for (int i = 0; i < N; i++) {
			tuerme[0][i] = N - i;
			colors[i] =  r.nextInt(255) *256*256 + r.nextInt(255) *256 + r.nextInt(155);
		}
		

	}

	void zeichnen() {
		xsend.formen( "none" );
		for (int n = 0; n < 3; n++) {
			for (int s = 0; s < N; s++) {
				if (tuerme[n][s] != 0) {
					int scheibe = tuerme[n][s];
					xsend.form2(n, s, "-");
					xsend.symbolGroesse2(n, s, (double) scheibe / N / 2);
					xsend.farbe2(n, s, colors[scheibe-1]);
				} 
			}
		}
	}

	void lege(int n, int von, int nach, int zwischen) {
		if (n > 0) {
			lege(n - 1, von, zwischen, nach);
			//System.out.println(n + ". Scheibe von " + von + " nach " + nach);
			ziehe( von, nach );
			zeichnen();
			xsend.statusText("N="+N + " Zug Nr. " + anzahlSchritte);
			Sleep.sleep( sleepTime);
			lege(n - 1, zwischen, nach, von);
			anzahlSchritte++;
		}
	}

	private void ziehe(int von, int nach) {
		for( int i=N-1; i>=0; i-- ) {
			if( tuerme[von][i] != 0 ) {
				int scheibe = tuerme[von][i];
				tuerme[von][i] = 0;
				for( int j=0; j<N; j++ ) {
					if( tuerme[nach][j] == 0) {
						tuerme[nach][j] = scheibe;
						return;
					}
				}
			}
		}
		
	}

	private void demo() {
		zeichnen();
		Sleep.sleep( 2* sleepTime);

		lege(N, 0, 2, 1);
	}
}
