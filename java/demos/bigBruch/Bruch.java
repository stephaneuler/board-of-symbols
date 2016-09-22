package bigBruch;

import java.math.BigInteger;
import java.util.Random;

/**
 * @author Euler
 *
 */

public class Bruch {
	public static final Bruch NULL = new Bruch(0);
	public static final Bruch EINS = new Bruch(1);
	private static int anzahlInstanzen;
	private static int anzahlVergleiche;
	private static Random random = new Random();
	private BigInteger nenner;
	private BigInteger zaehler;

	private Bruch() {
		++anzahlInstanzen;
	}

	public Bruch(long zahl) {
		this(zahl, 1);
	}

	public Bruch(long zaehler, long nenner) {
		this();
		long teiler = euclid(zaehler, nenner);
		this.zaehler = BigInteger.valueOf(zaehler / teiler);
		this.nenner = BigInteger.valueOf(nenner / teiler);
	}

	public Bruch(BigInteger zneu, BigInteger nneu) {
		this();
		zaehler = zneu;
		nenner = nneu;

		BigInteger ggt = nenner.gcd(zaehler);
		zaehler = zaehler.divide(ggt);
		nenner = nenner.divide(ggt);

		if (nenner.equals(BigInteger.ZERO)) {
			System.out.println("Ungültig - bitte nicht verwenden!!");
		}
	}

	public BigInteger getZaehler() {
		return zaehler;
	}

	public BigInteger getNenner() {
		return nenner;
	}

	public static int getAnzahlInstanzen() {
		return anzahlInstanzen;
	}

	public static int getAnzahlVergleiche() {
		return anzahlVergleiche;
	}

	public static void resetAnzahlVergleiche() {
		Bruch.anzahlVergleiche = 0;
	}

	public String toString() {
		return zaehler + "/" + nenner;
	}

	public double toDouble() {
		return zaehler.doubleValue() / nenner.doubleValue();
	}

	public Bruch inv() {
		return new Bruch(nenner, zaehler);
	}

	public Bruch neg() {
		return new Bruch(zaehler.negate(), nenner);
	}

	public Bruch add(int i) {
		return add(new Bruch(i));
	}

	public Bruch add(Bruch b) {
		BigInteger z = this.zaehler.multiply(b.nenner).add(this.nenner.multiply(b.zaehler));
		BigInteger n = this.nenner.multiply(b.nenner);

		return new Bruch(z, n);
	}

	
	public Bruch fareyAdd(Bruch b) {
		BigInteger z = this.zaehler.add(b.zaehler);
		BigInteger n = this.nenner.add(b.nenner);

		return new Bruch(z, n);
	}

	
	public Bruch sub(Bruch b) {
		return add(b.neg());
	}

	public Bruch mult(Bruch b) {
		BigInteger z = this.zaehler.multiply(b.zaehler);
		BigInteger n = this.nenner.multiply(b.nenner);

		return new Bruch(z, n);
	}

	public Bruch div(Bruch b) {
		return mult(b.inv());
	}

	static public long euclid(long n, long m) {
		if (m == 0)
			return n;
		return euclid(m, n % m);
	}

	public boolean groesser(Bruch b) {
		++anzahlVergleiche;
		Bruch diff = this.sub(b);
		return diff.istPostiv();
	}

	public boolean kleiner(Bruch b) {
		++anzahlVergleiche;
		Bruch diff = this.sub(b);
		return diff.istNegativ();
	}

	public boolean gleich(int z, int n) {
		return zaehler.intValue() == z & nenner.intValue() == n;
	}

	public boolean gleich(Bruch b) {
		++anzahlVergleiche;
		Bruch diff = this.sub(b);
		return diff.zaehler.intValue() == 0;
	}

	private boolean istPostiv() {
		return nenner.multiply(zaehler).signum() > 0;
	}
	
	private boolean istNegativ() {
		return nenner.multiply(zaehler).signum() < 0;
	}
	
	static public Bruch zufallsBruch(int max) {
		return new Bruch(random.nextInt(max), 1 + random.nextInt(max - 1));
	}

	static public Bruch zufallsBruch01(int max) {
		int n = random.nextInt(max);
		if (n == 0) {
			return Bruch.NULL;
		}
		int z = random.nextInt(n);
		return new Bruch(z, n);
	}


}
