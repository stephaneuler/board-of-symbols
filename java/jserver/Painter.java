package jserver;

import jserver.XSendAdapter;

/**
 * Hilfsklasse mit Methoden zum vereinfachten Zeichnen auf BoS.
 * 
 * @author Stephan Euler
 * @version 0.3 Juni 2016
 *
 */
public class Painter {

	/**
	 * Haupt-Methode mit beispielhaftem Aufruf der Methoden
	 * 
	 * @param args Argumente beim Aufruf - werden ignoriert
	 * 
	 */
	public static void main(String[] args) {
		XSendAdapter sender = new XSendAdapter();
		waagrecht(sender, 1, 2, 4, 0xff);
		waagrecht(sender, 1, 2, 4, "*");
		waagrecht(sender, 2, 5, 5, 0xff00, "d");
	}

	/**
	 * Methode zum F&auml;rben einer waagrechten Linie
	 * 
	 * @param sender XSendAdapter
	 * @param s Spalte des Startfeldes
	 * @param z Zeile des Startfeldes
	 * @param n Anzahl der Felder
	 * @param farbe zu setzende Farbe
	 */
	static void waagrecht(XSendAdapter sender, int s, int z, int n, int farbe) {
		for (int i=0; i<n; i++) {
			sender.farbe2(s+i, z, farbe);
		}
	}

	/**
	 * Methode zum Setzen des Symboltyps einer waagrechten Linie
	 * 
	 * @param sender XSendAdapter
	 * @param s Spalte des Startfeldes
	 * @param z Zeile des Startfeldes
	 * @param n Anzahl der Felder
	 * @param typ neuer Symboltyp
	 */
	static void waagrecht(XSendAdapter sender, int s, int z, int n, String typ) {
		for (int i=0; i<n; i++) {
			sender.form2(s+i, z, typ);
		}
	}

	/**
	 * Methode zum gleichzeitigen F&auml;rben und Setzen des Symboltyps einer waagrechten Linie
	 * 
	 * @param sender XSendAdapter
	 * @param s Spalte des Startfeldes
	 * @param z Zeile des Startfeldes
	 * @param n Anzahl der Felder
	 * @param farbe zu setzende Farbe
	 * @param typ neuer Symboltyp
	 */
	static void waagrecht(XSendAdapter sender, int s, int z, int n, int farbe,
			String typ) {
		waagrecht(sender, s, z, n, farbe);
		waagrecht(sender, s, z, n, typ);
	}

	/**
	 * Methode zum F&auml;rben einer senkrechten Linie
	 * 
	 * @param sender XSendAdapter
	 * @param s Spalte des Startfeldes
	 * @param z Zeile des Startfeldes
	 * @param n Anzahl der Felder
	 * @param farbe zu setzende Farbe
	 */
	static void senkrecht(XSendAdapter sender, int s, int z, int n, int farbe) {
		for (int i=0; i<n; i++) {
			sender.farbe2(s, z+i, farbe);
		}
	}

	/**
	 * Methode zum Setzen des Symboltyps einer senkrechten Linie
	 * 
	 * @param sender XSendAdapter
	 * @param s Spalte des Startfeldes
	 * @param z Zeile des Startfeldes
	 * @param n Anzahl der Felder
	 * @param typ neuer Symboltyp
	 */
	static void senkrecht(XSendAdapter sender, int s, int z, int n, String typ) {
		for (int i=0; i<n; i++) {
			sender.form2(s, z+i, typ);
		}
	}

	/**
	 * Methode zum gleichzeitigen F&auml;rben und Setzen des Symboltyps einer senkrechten Linie
	 * 
	 * @param sender XSendAdapter
	 * @param s Spalte des Startfeldes
	 * @param z Zeile des Startfeldes
	 * @param n Anzahl der Felder
	 * @param farbe zu setzende Farbe
	 * @param typ neuer Symboltyp
	 */
	static void senkrecht(XSendAdapter sender, int s, int z, int n, int farbe,
			String typ) {
		senkrecht(sender, s, z, n, farbe);
		senkrecht(sender, s, z, n, typ);
	}

	public static void linie(XSendAdapter sender, int s, int z, int ds, int dz, int n, int farbe,
			String typ) {
		for (int i=0; i<n; i++) {
			sender.farbe2(s, z, farbe);
			sender.form2(s, z, typ);
			s += ds;
			z += dz;
		}
		
	}

}
