#include "stdafx.h"
#define N 8


void dame() {
    int i, j;
    
    char symbol[5];
    
    groesse(N, N);
    
    int brett[N][N] = {
        {1, 0, 1, 0, 0, 0, 2, 0 },
        {0, 1, 0, 0, 0, 2, 0, 2 },
        {1, 0, 1, 0, 0, 0, 2, 0 },
        {0, 1, 0, 0, 0, 2, 0, 2 },
        {1, 0, 1, 0, 0, 0, 2, 0 },
        {0, 1, 0, 0, 0, 2, 0, 2 },
        {1, 0, 1, 0, 0, 0, 2, 0 },
        {0, 1, 0, 0, 0, 2, 0, 2 }
    };
    
    for( i=0; i<N; i++ ) {
        for( j=0; j<N; j++ ) {
            if( brett[i][j] == 1 ) {
                farbe2( i, j, BLUE );
                symbolGroesse2( i, j, 0.4 );
            } else if( brett[i][j] == 2 ) {
                symbolGroesse2( i, j, 0.4 );
                farbe2( i, j, YELLOW );
            } else {
                sprintf_s(symbol, "%s", "none");
                form2( i, j, symbol );
            }
            if( (i + j ) % 2    == 0 ) {
                hintergrund2( i, j, 0xafafaf );
            } else {
                hintergrund2( i, j , WHITE );
            }
#if WINDOWS
            Sleep( 100 );
#else
			sleep(1);
#endif
            printf(".");
            fflush( stdout );
        }
    }
    printf("\n");
    
    int feld, ix, iy;
    int spieler = 1;
    for(;;) {
        char *a = abfragen();
        if( strlen( a ) > 0 ) {
            printf( "Nachricht: %s\n", a );
            if( a[0] == '#' ) {
                sscanf_s( a, "# %d %d %d", &feld, &ix, &iy );
                if( brett[ix][iy] != 0 ) {
                    sprintf_s(symbol, "%s", "none");
                    form2( ix, iy, symbol );
                    brett[ix][iy] = 0;
                } else {
                    brett[ix][iy] = spieler;
                    farbe2( ix, iy, BLUE );
                    symbolGroesse2( ix, iy, 0.4 );
                    sprintf_s(symbol, "%s", "c");
                    form2( ix, iy , symbol );
                }
                
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