#include "stdafx.h"

static char nachricht[500];

void groesse(int x, int y) {
    sprintf_s(nachricht, ">>r %d %d\n", x, y);
    sendMessage( nachricht );
}
void hintergrund(int i, int f) {
    if( f >= 0 ) {
        sprintf_s(nachricht, ">>b %d 0x%x\n", i, f);
    } else {
        sprintf_s(nachricht, ">>b %d %d\n", i, f);
    }
    sendMessage( nachricht );
}
void hintergrund2(int i, int j, int f) {
    if( f >= 0 ) {
        sprintf_s(nachricht, ">>#b %d %d 0x%x\n", i, j, f);
    } else {
        sprintf_s(nachricht, ">>#b %d %d %d\n", i, j, f);
    }
    sendMessage( nachricht );
}
void flaeche(int f) {
    sprintf_s(nachricht, ">>ba 0x%x\n", f);
    sendMessage( nachricht );
}
void rahmen(int f) {
    sprintf_s(nachricht, ">>bo 0x%x\n", f);
    sendMessage( nachricht );
}
void loeschen() {
    sprintf_s(nachricht, ">>c\n");
    sendMessage( nachricht );
}
void farben(int f) {
    sprintf_s(nachricht, ">>a 0x%x\n", f);
    sendMessage( nachricht );
}
void farbe(int i, int f) {
    sprintf_s(nachricht, ">>%d 0x%x\n", i, f);
    sendMessage( nachricht );
}
void grau(int i, int g) {
    farbe(i, g << 16 | g <<8 | g);
}
void farbe2(int i, int j, int f) {
    sprintf_s(nachricht, ">># %d %d 0x%x\n", i, j, f);
    sendMessage( nachricht );
}
void grau2(int i, int j, int g) {
    farbe2(i, j, g << 16 | g <<8 | g);
}
void formen(char* f) {
    sprintf_s(nachricht, ">>f %s\n", f);
    sendMessage( nachricht );
}
void form(int i, char* f) {
    sprintf_s(nachricht, ">>fi %d %s\n", i, f);
    sendMessage( nachricht );
}
void form2(int i, int j, char* f) {
    sprintf_s(nachricht, ">>#fi %d %d %s\n", i, j, f);
    sendMessage( nachricht );
}
void symbolGroesse(int i, double s) {
    sprintf_s(nachricht, ">>s %d %g\n", i, s);
    sendMessage( nachricht );
}
void symbolGroesse2(int i, int j, double s) {
    sprintf_s(nachricht, ">>#s %d %d %g\n", i, j, s);
    sendMessage( nachricht );
}
void text(int i, char* f) {
    sprintf_s(nachricht, ">>T %d %s\n", i, f);
    sendMessage( nachricht );
}
void text2(int i, int j, char* f) {
    sprintf_s(nachricht, ">>#T %d %d %s\n", i, j, f);
    sendMessage( nachricht );
}
void zeichen(int i, char c) {
    if( c >= 32 ) {
        sprintf_s(nachricht, ">>T %d %c\n", i, c);
        sendMessage( nachricht );
    }
}
void zeichen2(int i, int j, char c) {
    if( c >= 32 ) {
        sprintf_s(nachricht, ">>#T %d %d %c \n", i, j, c);
        sendMessage( nachricht );
    }
}

char  *abfragen() {
    sprintf_s(nachricht, "p\n" );
    sendMessage( nachricht );
    return getAnswer();
}

void statusText( char * s ) {
    sprintf_s(nachricht, ">>t %s\n", s );
    sendMessage( nachricht );
}
