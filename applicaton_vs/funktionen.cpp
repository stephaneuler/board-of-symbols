#include "stdafx.h"

static char nachricht[500];

void groesse(int x, int y) {
#if __WIN32__
    sprintf_s(nachricht, ">>r %d %d\n", x, y);
#else
    sprintf(nachricht, ">>r %d %d\n", x, y);
#endif
    sendMessage( nachricht );
}
void hintergrund(int i, int f) {
    if( f >= 0 ) {
#if __WIN32__
        sprintf_s(nachricht, ">>b %d 0x%x\n", i, f);
#else
        sprintf(nachricht, ">>b %d 0x%x\n", i, f);
#endif
    } else {
#if __WIN32__
        sprintf_s(nachricht, ">>b %d %d\n", i, f);
#else
        sprintf(nachricht, ">>b %d %d\n", i, f);
#endif
    }
    sendMessage( nachricht );
}
void hintergrund2(int i, int j, int f) {
    if( f >= 0 ) {
#if __WIN32__
        sprintf_s(nachricht, ">>#b %d %d 0x%x\n", i, j, f);
#else
        sprintf(nachricht, ">>#b %d %d 0x%x\n", i, j, f);
#endif
    } else {
#if __WIN32__
        sprintf_s(nachricht, ">>#b %d %d %d\n", i, j, f);
#else
        sprintf(nachricht, ">>#b %d %d %d\n", i, j, f);
#endif
    }
    sendMessage( nachricht );
}
void flaeche(int f) {
#if __WIN32__
    sprintf_s(nachricht, ">>ba 0x%x\n", f);
#else
    sprintf(nachricht, ">>ba 0x%x\n", f);
#endif
    sendMessage( nachricht );
}
void rahmen(int f) {
#if __WIN32__
    sprintf_s(nachricht, ">>bo 0x%x\n", f);
#else
    sprintf(nachricht, ">>bo 0x%x\n", f);
#endif
    sendMessage( nachricht );
}
void loeschen() {
#if __WIN32__
    sprintf_s(nachricht, ">>c\n");
#else
    sprintf(nachricht, ">>c\n");
#endif
    sendMessage( nachricht );
}
void farben(int f) {
    sprintf_s(nachricht, ">>a 0x%x\n", i, f);
    sendMessage( nachricht );
}
void farbe(int i, int f) {
#if __WIN32__
    sprintf_s(nachricht, ">>%d 0x%x\n", i, f);
#else
    sprintf(nachricht, ">>%d 0x%x\n", i, f);
#endif
    sendMessage( nachricht );
}
void grau(int i, int g) {
    farbe(i, g << 16 | g <<8 | g);
}
void farbe2(int i, int j, int f) {
#if __WIN32__
    sprintf_s(nachricht, ">># %d %d 0x%x\n", i, j, f);
#else
    sprintf(nachricht, ">># %d %d 0x%x\n", i, j, f);
#endif
    sendMessage( nachricht );
}
void grau2(int i, int j, int g) {
    farbe2(i, j, g << 16 | g <<8 | g);
}
void formen(char* f) {
#if __WIN32__
    sprintf_s(nachricht, ">>f %s\n", f);
#else
    sprintf(nachricht, ">>f %s\n", f);
#endif
    sendMessage( nachricht );
}
void form(int i, char* f) {
#if __WIN32__
    sprintf_s(nachricht, ">>fi %d %s\n", i, f);
#else
    sprintf(nachricht, ">>fi %d %s\n", i, f);
#endif
    sendMessage( nachricht );
}
void form2(int i, int j, char* f) {
#if __WIN32__
    sprintf_s(nachricht, ">>#fi %d %d %s\n", i, j, f);
#else
    sprintf(nachricht, ">>#fi %d %d %s\n", i, j, f);
#endif
    sendMessage( nachricht );
}
void symbolGroesse(int i, double s) {
#if __WIN32__
    sprintf_s(nachricht, ">>s %d %g\n", i, s);
#else
    sprintf(nachricht, ">>s %d %g\n", i, s);
#endif
    sendMessage( nachricht );
}
void symbolGroesse2(int i, int j, double s) {
#if __WIN32__
    sprintf_s(nachricht, ">>#s %d %d %g\n", i, j, s);
#else
    sprintf(nachricht, ">>#s %d %d %g\n", i, j, s);
#endif
    sendMessage( nachricht );
}
void text(int i, char* f) {
#if __WIN32__
    sprintf_s(nachricht, ">>T %d %s\n", i, f);
#else
    sprintf(nachricht, ">>T %d %s\n", i, f);
#endif
    sendMessage( nachricht );
}
void text2(int i, int j, char* f) {
#if __WIN32__
    sprintf_s(nachricht, ">>#T %d %d %s\n", i, j, f);
#else
    sprintf(nachricht, ">>#T %d %d %s\n", i, j, f);
#endif
    sendMessage( nachricht );
}
void zeichen(int i, char c) {
    if( c >= 32 ) {
#if __WIN32__
        sprintf_s(nachricht, ">>T %d %c\n", i, c);
#else
        sprintf(nachricht, ">>T %d %c\n", i, c);
#endif
        sendMessage( nachricht );
    }
}
void zeichen2(int i, int j, char c) {
    if( c >= 32 ) {
#if __WIN32__
        sprintf_s(nachricht, ">>#T %d %d %c \n", i, j, c);
#else
        sprintf(nachricht, ">>#T %d %d %c \n", i, j, c);
#endif
        sendMessage( nachricht );
    }
}

char  *abfragen() {
#if __WIN32__
    sprintf_s(nachricht, "p\n" );
#else
    sprintf(nachricht, "p\n" );
#endif
    sendMessage( nachricht );
    return getAnswer();
    
}

void statusText( char * s ) {
#if __WIN32__
    sprintf_s(nachricht, ">>t %s\n", s );
#else
    sprintf(nachricht, ">>t %s\n", s );
#endif
    sendMessage( nachricht );
    
}
