package bigBruch;

public class Beispiele {

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		System.out.println( Bruch.getAnzahlInstanzen() + " Bruch Instanzen" );
		Bruch t1 = new Bruch( 2, 3  );
		Bruch t2 = new Bruch( 2, 3  );
		Bruch t3 = new Bruch( 2, 7  );
		System.out.println( Bruch.getAnzahlInstanzen() + " Bruch Instanzen" );

		System.out.println( Bruch.euclid(12,  8) );
		System.out.println( Bruch.euclid(10,  10) );
		
		Bruch b = null;
		b = new Bruch( 5 );
		System.out.println( b );
		
		for( int n=0; n<10; n++ ) {
			System.out.println( Bruch.zufallsBruch(20) );
		}
		add();
		//harmonischeReihe();
		//baslerReihe();
		
	}

	@SuppressWarnings("unused")
	private static void harmonischeReihe() {
        Bruch summe = Bruch.NULL;
        for( int n=1; n<=100; n++ ) {
            summe = summe.add( new Bruch( 1, n ) );
            System.out.println( "H(" + n + ") = " + summe  
            		+ " =  " + summe.toDouble() );
        }

		
	}

	private static void baslerReihe() {
        Bruch summe = Bruch.NULL;
        double g = Math.PI * Math.PI / 6;
        for( int n=1; n<=30; n++ ) {
        	Bruch s =  new Bruch( 1, n );
        	s = s.mult( s );
            summe = summe.add( s );
            System.out.println( "B(" + n + ") = " + summe  
            		+ " =  " + summe.toDouble()  + " -> " + g);
        }

		
	}

	private static void add() {
		Bruch b = new Bruch(1, 2);
		Bruch c = new Bruch(2, 3);
		System.out.println(b.getZaehler() + " / " + b.getNenner());
		System.out.println(b );	
		Bruch s = b.add( c );
		System.out.println(s  );	
		
	}

}
