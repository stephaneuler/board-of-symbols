// BoS1.cpp : Definiert den Einstiegspunkt fuer die Konsolenanwendung.
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
#if WINDOWS
        Sleep( 100 );
#else
		sleep(1);
#endif
    }
}

void test2a() {
    int i, anzahl;
    loeschen();
    printf("Wie viele Felder? ");
    scanf_s("%d", &anzahl );
    for( i=0; i<anzahl; i++ ) {
        farbe( i, BLUE );
#if WINDOWS
        Sleep( 100 );
#else
		sleep(1);
#endif
    }
}

void test3() {
    for(;;) {
        char *a = abfragen();
        if( strlen( a ) > 0 ) {
            printf( "Nachricht: %s\n", a );
        } else {
#if WINDOWS
            Sleep( 100 );
#else
			sleep(1);
#endif
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
#if WINDOWS
            Sleep( 100 );
#else
			sleep(1);
#endif
        }
    }
}

/** 
 * When using MINGW, please link the following files in this given order in your linker settings:
 * "C:\MinGW\lib\libws2_32.a"
 * "C:\MinGW\lib\libmswsock.a"
 * "C:\MinGW\lib\libadvapi32.a"
 * 
 * For Eclipse C++: Right click on your project -> Properties -> C/C++ Build -> Settings -> "Tool Settings" -> MinGW C++ Linker -> Miscellaneous -> "Other objects" (lower part) -> Add object
 */

#if WINDOWS
int _tmain(int argc, _TCHAR* argv[])
{
    //	test2a();
    dame();
    
    return 0;
}
#else
int main(int argc, char* argv[])
{
    //test1();
    //test2();
    //test2a();
    //test3();
    //test4();
    
    dame();
    
    return 0;
}
#endif