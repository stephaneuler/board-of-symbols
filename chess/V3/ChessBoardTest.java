package chessv3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChessBoardTest {
	ChessBoard chessBoard = new ChessBoard();

	@BeforeEach
	void setUp() throws Exception {
		chessBoard.init();
		chessBoard.removeAllPieces();
	}

	@Test
	void testIsInCheck() {
		chessBoard.addPiece(new King(2, 2, ChessBoard.BLACK));
		chessBoard.addPiece(new King(9, 9, ChessBoard.WHITE));
		assertFalse(chessBoard.isInCheck());

		chessBoard.addPiece(new Rook(2, 9, ChessBoard.WHITE));
		assertTrue(chessBoard.isInCheck());

		chessBoard.addPiece(new Rook(2, 8, ChessBoard.BLACK));
		assertFalse(chessBoard.isInCheck());
		
		chessBoard.addPiece(new Queen(5, 2, ChessBoard.WHITE));
		assertTrue(chessBoard.isInCheck());
	}

}
