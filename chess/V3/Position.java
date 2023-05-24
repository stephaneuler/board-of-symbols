package chessv3;

import static chessv3.ChessBoard.BLACK;
import static chessv3.ChessBoard.WHITE;

import java.util.ArrayList;
import java.util.List;

// Eine Position mit Name und Figurenliste
//
//Version 
//3.0  Mai 2023 SE Erste Version
public class Position {
	String name;
	Piece[] pieces;

	public Position(String name, Piece[] pieces) {
		super();
		this.name = name;
		this.pieces = pieces;
	}

	public static List<Position> getPositions() {
		List<Position> positions = new ArrayList<>();

		Piece[] pieces = { new King(3, 3, WHITE), new King(5, 7, BLACK), new Pawn(2, 3, WHITE), new Pawn(6, 4, WHITE),
				new Pawn(7, 5, BLACK), new Pawn(6, 7, BLACK) };
		positions.add(new Position("Bauern-Test", pieces));

		pieces = new Piece[] { new King(6, 2, WHITE), new King(6, 9, BLACK), new Rook(9, 2, WHITE),
				new Rook(2, 9, BLACK), new Pawn(9, 8, BLACK), };
		positions.add(new Position("Rochade-Test", pieces));

		pieces = new Piece[] { new King(3, 3, WHITE), new King(5, 7, BLACK), new Pawn(2, 8, WHITE),
				new Pawn(6, 3, BLACK) };
		positions.add(new Position("Umwandlung", pieces));

		pieces = new Piece[] { new King(3, 3, WHITE), new King(5, 7, BLACK), new Queen(2, 8, WHITE) };
		positions.add(new Position("Damen-Matt", pieces));

		pieces = new Piece[] { new King(7, 3, WHITE), new King(5, 6, BLACK), new Rook(2, 8, WHITE) };
		positions.add(new Position("Turm-Matt", pieces));

		pieces = new Piece[] { new King(7, 3, WHITE), new King(6, 6, BLACK), new Bishop(3, 8, WHITE),
				new Bishop(3, 7, WHITE) };
		positions.add(new Position("Laeufer-Matt", pieces));

		List<Piece> all = new ArrayList<>();
		for (int x = 2; x < 10; x++) {
			all.add(new Pawn(x, 3, WHITE));
			all.add(new Pawn(x, 8, BLACK));
		}
		all.add(new Rook(2, 2, WHITE));
		all.add(new Rook(9, 2, WHITE));
		all.add(new Rook(2, 9, BLACK));
		all.add(new Rook(9, 9, BLACK));

		all.add(new Knight(3, 2, WHITE));
		all.add(new Knight(8, 2, WHITE));
		all.add(new Knight(3, 9, BLACK));
		all.add(new Knight(8, 9, BLACK));

		all.add(new Bishop(4, 2, WHITE));
		all.add(new Bishop(7, 2, WHITE));
		all.add(new Bishop(4, 9, BLACK));
		all.add(new Bishop(7, 9, BLACK));

		all.add(new Queen(5, 2, WHITE));
		all.add(new King(6, 2, WHITE));
		all.add(new Queen(5, 9, BLACK));
		all.add(new King(6, 9, BLACK));

		positions.add(new Position("Anfang", all.toArray(pieces)));

		return positions;
	}

	public String getName() {
		return name;
	}

	public Piece[] getPieces() {
		return pieces;
	}
}
