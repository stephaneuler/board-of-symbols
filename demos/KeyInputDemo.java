package demos;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import jserver.Board;
import jserver.XSendAdapter;

/**
 * Demo of a KeyListener: move a blue circle. No boundary check!
 * 
 * @author Stephan Euler
 * @version May 2019
 *
 */
public class KeyInputDemo implements KeyListener  {
	int x = 4;
	int y = 4;
	int color = XSendAdapter.DARKBLUE;
	int bgColor = XSendAdapter.LIGHTGRAY;
	Board board = new Board();
	XSendAdapter xsend = new XSendAdapter(board);
	
	public static void main(String[] args) {
		(new KeyInputDemo()).start();
	}
	
	private void start() {
		board.getPlotter().addKeyListener( this );
		board.getPlotter().setFocusable(true);
		board.getPlotter().requestFocusInWindow();
		xsend.farben( bgColor );
		xsend.farbe2(x, y, color);		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println( e );
	}

	@Override
	public void keyReleased(KeyEvent e) {
		System.out.println( e );
		if( e.getKeyCode() == KeyEvent.VK_RIGHT)  {
			xsend.farbe2( x, y, bgColor);
			xsend.farbe2(++x, y, color);
		} else if( e.getKeyCode() == KeyEvent.VK_LEFT) {
			xsend.farbe2( x, y, bgColor);
			xsend.farbe2(--x, y, color);
		} if( e.getKeyCode() == KeyEvent.VK_DOWN)  {
			xsend.farbe2( x, y, bgColor);
			xsend.farbe2( x, --y, color);
		} if(e.getKeyCode() == KeyEvent.VK_UP)  {
			xsend.farbe2( x, y, bgColor);
			xsend.farbe2( x, ++y, color);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println( e );
		
	}

}

