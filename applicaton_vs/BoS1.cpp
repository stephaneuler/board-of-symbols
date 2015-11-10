// BoS1.cpp : Definiert den Einstiegspunkt für die Konsolenanwendung.
//

#include "stdafx.h"

void dame();

void test1() {
		farbe( 12, RED );
}

void test2() {
		int i;
		loeschen();
		for( i=0; i<60; i++ ) {
			farbe( i, BLUE );
			Sleep( 200 );
		}
}

void test2a() {
		int i, anzahl;
		loeschen();
		printf("Wie viele Felder? ");
		scanf_s("%d", &anzahl );
		for( i=0; i<anzahl; i++ ) {
			farbe( i, BLUE );
			Sleep( 200 );
		}
}

void test3() {
	for(;;) {		
		char *a = abfragen();
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
		char *a = abfragen();
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
//	test2a();
	dame();

	return 0;
}

