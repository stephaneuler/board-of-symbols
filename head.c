#include <stdio.h>

#include "colors.h" 

void groesse(int x, int y) {
  printf(">>r %d %d\n", x, y);
}
void hintergrund(int i, int f) {
  printf(">>b %d 0x%x\n", i, f);
}
void hintergrund2(int i, int j, int f) {
  printf(">>#b %d %d 0x%x\n", i, j, f);
}
void flaeche(int f) {
   printf(">>ba 0x%x\n", f);
}
void rahmen(int f) {
   printf(">>bo 0x%x\n", f);
}
void clear() {
   printf(">>c\n");
}
void loeschen() {
   printf(">>c\n");
}
void farbe(int i, int f) {
   printf(">>%d 0x%x\n", i, f);
}
void grau(int i, int g) {
  farbe(i, g << 16 | g <<8 | g);
}
void farbe2(int i, int j, int f) {
  printf(">># %d %d 0x%x\n", i, j, f);
}
void grau2(int i, int j, int g) {
  farbe2(i, j, g << 16 | g <<8 | g);
}
void formen(char* f) {
   printf(">>f %s\n", f);
}
void form(int i, char* f) {
   printf(">>fi %d %s\n", i, f);
}
void form2(int i, int j, char* f) {
  printf(">>#fi %d %d %s\n", i, j, f);
}
void zusammen2( int i, int j, int f, char* form) {
  farbe2( i, j, f );
  form2( i, j, form );
}
void symbolGroesse(int i, double s) {
   printf(">>s %d %g\n", i, s);
}
void symbolGroesse2(int i, int j, double s) {
  printf(">>#s %d %d %g\n", i, j, s);
}
int main() {
