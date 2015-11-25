#include "stdafx.h" 
#define N 8


void dame() {
	int i, j;

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
				form2( i, j, "none" );
			}
			if( (i + j ) % 2    == 0 ) {
				hintergrund2( i, j, 0xafafaf );
			} else { 
				hintergrund2( i, j , WHITE );
			}
			Sleep( 100 );
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
					form2( ix, iy, "none" );
					brett[ix][iy] = 0;
				} else {
					brett[ix][iy] = spieler;
					farbe2( ix, iy, BLUE );
					symbolGroesse2( ix, iy, 0.4 );
					form2( ix, iy , "c" );
				}
				
			}
		} else {
			Sleep( 100 );
		}
	}

}