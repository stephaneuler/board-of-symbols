package bigBruch;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

public class BruchTest {

	@Test
	public void testAdd() {
		assertEquals("#Bruch:", 2, Bruch.getAnzahlInstanzen());

		Bruch b1 = new Bruch(1, 2);
		Bruch b2 = new Bruch(2, 3);

		assertEquals("#Bruch:", 4, Bruch.getAnzahlInstanzen());

		Bruch b3 = b1.add(b2);
		assertEquals("Zähler:", BigInteger.valueOf(7), b3.getZaehler());
		assertEquals("Nenner:", BigInteger.valueOf(6), b3.getNenner());

		b3 = b1.sub(b2);
		assertEquals("Zähler:", BigInteger.valueOf(-1), b3.getZaehler());
		assertEquals("Nenner:", BigInteger.valueOf(6), b3.getNenner());

		b3 = b1.div(b2);
		assertEquals("Zähler:", BigInteger.valueOf(3), b3.getZaehler());
		assertEquals("Nenner:", BigInteger.valueOf(4), b3.getNenner());

		b3 = b1.mult(b2);
		assertEquals("Zähler:", BigInteger.valueOf(1), b3.getZaehler());
		assertEquals("Nenner:", BigInteger.valueOf(3), b3.getNenner());

		assertEquals("Vegleich = :", false, b1.gleich(b2));
		assertEquals("Vegleich = :", true, b1.gleich(b1));
		assertEquals("Vegleich > :", false, b1.groesser(b1));
		assertEquals("Vegleich > :", true, b2.groesser(b1));
		assertEquals("Vegleich > :", false, b1.groesser(b2));
	}

}
