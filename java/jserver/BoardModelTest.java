package jserver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BoardModelTest {
	BoardModel boardModel = new BoardModel();
	
	@Test
	public void testAddRow() {
		boardModel.setRows(10);
		boardModel.incRows(2);
		assertEquals("#rows increased by 2", 12, boardModel.getRows() );
		boardModel.incRows(-3);
		assertEquals("#rows decreased by 3", 9, boardModel.getRows() );
	}
}
