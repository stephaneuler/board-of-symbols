#include <stdio.h>

#include "colors.h" 

void hintergrund(int i, int f) {
  printf(">>b %d 0x%x\n", i, f);
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
void farbe2(int i, int j, int f) {
  printf(">># %d %d 0x%x\n", i, j, f);
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
void groesse(int x, int y) {
  printf(">>r %d %d\n", x, y);
}
int main() { 
