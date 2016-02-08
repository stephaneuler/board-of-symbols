package jserver;

import static org.junit.Assert.*;

import org.junit.Test;

public class DiceTest {

	@Test
	public void testIsDiceType() {
		assertTrue("isDice", Dice.isDiceType(SymbolType.DICE_3));
		assertFalse("isDice", Dice.isDiceType(SymbolType.CIRCLE));
		for (int n = 0; n < 5; n++) {
			assertTrue("random dice", Dice.isDiceType(Dice.getRandom()));
		}
	}

	@Test
	public void testGetValue() {
		assertEquals("dice value", 1, Dice.getValue(SymbolType.DICE_1));
		assertEquals("dice value", 5, Dice.getValue(SymbolType.DICE_5));
		assertEquals("dice value", 0, Dice.getValue(SymbolType.STAR));
	}

}
