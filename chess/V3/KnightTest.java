package chessv3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


class KnightTest {
	ChessBoard chessBoard = new ChessBoard();

	@BeforeEach
	void setUp() throws Exception {
		chessBoard.init();
		chessBoard.removeAllPieces();
	}

	@Test
	void testGetMoves() {
		Knight knight = new Knight(2, 2, ChessBoard.BLACK);
		List<Move> moves = knight.getMoves(chessBoard);
		assertEquals(2, moves.size(), "Corner");
	}

	
	@DisplayName("moves from CSV")
	@ParameterizedTest(name = "x ={0}, y ={1} ==> {2} moves")
	@CsvSource({ "2, 2, 2", "3, 2, 3", "5, 5, 8", "9, 9, 2"})
	void testToStringWithCsvSource(int x, int y, int moves) {
		chessBoard.removeAllPieces();
		Knight knight = new Knight(x, y, ChessBoard.BLACK);
		assertEquals(moves, knight.getMoves(chessBoard).size());
	}


}
