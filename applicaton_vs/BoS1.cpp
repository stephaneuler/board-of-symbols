// BoS1.cpp : Definiert den Einstiegspunkt für die Konsolenanwendung.
//

#include "stdafx.h"

void test1() {
		farbe( 12, RED );
}

void test2() {
		int i;
		for( i=0; i<20; i++ ) {
			farbe( i, BLUE );
			Sleep( 400 );
		}
}

void test3() {
	for(;;) {
		sendMessage("p\n");
		char *a = getAnswer();
		if( strlen( a ) > 0 ) {
			printf( "Nachricht: %s\n", a );
		} else {
			Sleep( 100 );
		}
	}
}

void test4() {
	int feld;
	for(;;) {
		sendMessage("p\n");
		char *a = getAnswer();
		if( strlen( a ) > 0 ) {
			printf( "Nachricht: %s\n", a );
			if( a[0] == '#' ) {
				sscanf_s( a, "# %d", &feld );
				farbe( feld, 0xff00 );
			}
		} else {
			Sleep( 100 );
		}
	}
}


int _tmain(int argc, _TCHAR* argv[])
{
	test4();

	return 0;
}

