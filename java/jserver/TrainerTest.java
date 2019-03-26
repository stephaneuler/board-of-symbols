package jserver;

import static org.junit.Assert.*;

import org.junit.Test;
//import sun.swing.plaf.synth.Paint9Painter.PaintType;

public class TrainerTest {

	@Test
	public void testLevel() {
		TrainerLevel tl = new TrainerLevel(Mode.STAIRWAY, true, true);
		assertEquals("Trainer Level", "STAIRWAY F? BG", tl.toString());
		tl = new TrainerLevel(Mode.STAIRWAY, true, false);
		assertEquals("Trainer Level", "STAIRWAY F?", tl.toString());
	}
}
