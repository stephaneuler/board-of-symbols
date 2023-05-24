package chessv3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PieceTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIsColor() {
		Piece piece = new King(2, 3, ChessBoard.BLACK);
		assertTrue(piece.isColor(ChessBoard.BLACK));
		assertFalse(piece.isColor(ChessBoard.WHITE));
	}

}
