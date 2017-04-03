package jserver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BoardTest {
	static Board board = new Board();

	@Test
	public void testLastError() {
		String message1 = "This is a test";
		String message2 = "This is another test";
		board.setLastError(message1);
		assertEquals("last error", message1, board.getLastError());
		board.setLastError(message2);
		assertEquals("last error", message2, board.getLastError());
	}

	@Test
	public void testAddSymbol() {
		int n = board.getSymbolCount();
		board.addSymbol(new Symbol(new Position(0, 0), 1.));
		assertEquals("#symbols increased by 1", n + 1, board.getSymbolCount());
	}

	@Test
	public void testContent() {
		BoardSerializer bs = new BoardSerializer();

		bs.buildDocument(board);
		String s = bs.write();
		assertEquals("#hash code content", s.hashCode(), 699796684);
	}

	@Test
	public void testClearSymbol() {
		board.clearSymbols();
		assertEquals("#symbols after clear", 0, board.getSymbolCount());
	}

}
