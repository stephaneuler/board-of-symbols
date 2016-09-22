package bigBruch;

import jserver.XSendAdapter;

public class Anzeiger {
	private XSendAdapter xsend = new XSendAdapter();
	private int pos = 0;

	public Anzeiger( int n, int m ) {
		xsend.groesse(n, m);
	}

	public void zeige( Bruch b  ) {
		xsend.text( pos++, " " + b.getZaehler());
		xsend.text( pos++, " / ");
		xsend.text( pos++, " " + b.getNenner());
	}
	public void zeige( String text ) {
		xsend.text( pos++, text );
	}
}
