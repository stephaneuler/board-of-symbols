package plotter;

/** *******************************************************
  *  Vector Instruction Set 
  *  Collection of "vector" routines (im memorial HP1000) 
  *
  *  Naming convention:
  *     .[vm][spec][func]
  *       |     |     \------- Function (e.g. max, min)
  *       |     |
  *       |     \------------- specifier i: index        (imax find index of max)
  *       |                            abs: absolute values 
  *       |                              s: scalar value (smult is scalar x vector )
  *       |
  *       |------------------- v: vector
  *                            m: matrix
  *   version  who    when      what 
  *     1.0    se    27.10.98   this header
  *     1.1          10.11.98   more functions, more info
  *     
  */
import java.util.*;
import java.io.*;

public final class Vis 
{
  public static void main(String[] args) {
    double x[] = null;
    int i;

    //System.out.println( "max = " + vmax( x ) );
    x = new double[1000000];
    Random r = new Random( 123 );
    Date start;
    Date end;
    int iter = 10;
    int j;
    float delta, max;
 
    for( i=0; i<x.length; i++ ) x[i] = r.nextDouble();

    for( i=0; i<3; i++ ) {
      start = new Date();
      max = 0;
      for( j=0; j<iter; j++ ) max += vmax( x );
      System.out.print( "max1 = " + max );
      end = new Date();
      delta = (float) (end.getTime() - start.getTime() ) / (float) 1000;
      System.out.println( " time1    = " + delta + "s" );
      
      start = new Date();
      max = 0;
      for( j=0; j<iter; j++ ) max += vmax2( x );
      System.out.print( "max2 = " + max );
      end = new Date();
      delta = (float) (end.getTime() - start.getTime() ) / (float) 1000;
      System.out.println( " time2    = " + delta + "s" );
    }
 }


  /** ***************************************************************************
    */
  public static void vcopy( short in[], short out[] ) {
    if( in.length <= 0 ) return;
    for( int i=0; i<in.length; i++ ) out[i] = in[i];
  }

  /** ***************************************************************************
    */
  public static void vinvers( double vect[] ) {
    for( int i=0; i<vect.length; i++ ) vect[i] = 1. / vect[i];
  }


  /** ***************************************************************************
    */
  static double[] vclone( double vect[] ) {
    double[] out = new double[ vect.length ];
    for( int i=0; i<vect.length; i++ ) out[i] = vect[i];
    return out;
  }


  /** ***************************************************************************
    */
  static void vprint( double vect[] ) {
    
    for( int i=0; i<vect.length; i++ ) {
      System.out.println( i +": " + vect[i]);
    }
  }

  /** ***************************************************************************
   */
    public static void vprint( String fileName, double vect[] ) {
    
	try {
	    PrintWriter dout;
	    dout = new PrintWriter(new FileOutputStream(new File( fileName ) ) );
	    for( int i=0; i<vect.length; i++ ) {
		dout.println( vect[i] );
	    }
	    dout.close();
	} catch( Exception ex ) {
	    System.out.println( "Can not open file " + fileName );
	}
    }

  /** ***************************************************************************
    */
  static void vprint( int vect[] ) {
    
    for( int i=0; i<vect.length; i++ ) {
      System.out.println( i +": " + vect[i]);
    }
  }

   /** ***************************************************************************
    */
    public static void vadd( double vect1[], double vect2[], double result[] ) {
	for( int i=0; i<vect1.length; i++ ) result[i] = vect1[i] + vect2[i];
    }
 
   /** ***************************************************************************
    */
  public static int vimin( double vect[] ) {
    int index=0;
    
    for( int i=1; i<vect.length; i++ ) {
      if( vect[i] < vect[index] ) index = i;
    }
    return index;
  }

  /** ***************************************************************************
    */
  static void vfill( double x, double[] vect ) {
    
    for( int i=0; i<vect.length; i++ ) vect[i] = x;
  }

  /** ***************************************************************************
    */
  static double vsum( double vect[] ) {
    double wert = 0;
    
    for( int i=0; i<vect.length; i++ ) wert += vect[i];
    return wert;
  }

  /** ***************************************************************************
    */
  static int vsum( int vect[] ) {
    int wert = 0;
    
    for( int i=0; i<vect.length; i++ ) wert += vect[i];
    return wert;
  }

  /** ***************************************************************************
    */
  static int vimax( double vect[] ) {
    int index=0;
    
    for( int i=1; i<vect.length; i++ ) {
      if( vect[i] > vect[index] ) index = i;
    }
    return index;
  }

    /** ***************************************************************************
    */
    public static double vmin( double vect[] ) {
	double wert = vect[0];
	
	if( vect.length >= 1 ) {
	    for( int i=1; i<vect.length; i++ ) {
		if( vect[i] < wert ) wert = vect[i];
	    }
	}
	return wert;
    }
    
    /** ***************************************************************************
    */
    public static short vmin( short vect[] ) {
	short wert = vect[0];
	
	if( vect.length >= 1 ) {
	    for( int i=1; i<vect.length; i++ ) {
		if( vect[i] < wert ) wert = vect[i];
	    }
	}
	return wert;
    }
    
    /** ***************************************************************************
     * Minimum EXCLUDING one value
    */
    static double vminx( double vect[], double ex ) {
	double wert = Double.MAX_VALUE;
	
	for( int i=0; i<vect.length; i++ ) {
	    if( vect[i] < wert & vect[i] != ex ) {
		//System.out.print( we
		wert = vect[i];
	    }
	}
	
	return wert;
    }
    
    /** ***************************************************************************
     * Maximum EXCLUDING one value
    */
    static double vmaxx( double vect[], double ex ) {
	double wert = Double.MIN_VALUE;
	
	for( int i=0; i<vect.length; i++ ) {
	    if( vect[i] > wert & vect[i] != ex ) wert = vect[i];
	}
	
	return wert;
    }
    
    /** ***************************************************************************
     * cumulative minimum search
     */
    static double vmin( double wert, double vect[] ) {
	
	if( vect == null ) return wert;
	for( int i=0; i<vect.length; i++ ) {
	    if( vect[i] < wert ) wert = vect[i];
	}
	return wert;
    }

    /** ***************************************************************************
     * cumulative minimum search
     */
    public static short vmin( short wert, short vect[] ) {
	
	if( vect == null ) return wert;
	for( int i=0; i<vect.length; i++ ) {
	    if( vect[i] < wert ) wert = vect[i];
	}
	return wert;
    }

    /** ***************************************************************************
     */
    public static double vmax( double vect[] ) {
	double wert = vect[0];
	
	if( vect.length >= 1 ) {
	    for( int i=1; i<vect.length; i++ ) {
		if( vect[i] > wert ) wert = vect[i];
	    }
	}
	return wert;
    }

    /** ***************************************************************************
     */
    public static short vmax( short vect[] ) {
	short wert = vect[0];
	
	if( vect.length >= 1 ) {
	    for( int i=1; i<vect.length; i++ ) {
		if( vect[i] > wert ) wert = vect[i];
	    }
	}
	return wert;
    }

    /** ***************************************************************************
     * cumulative maximum search
     */
    static double vmax( double wert, double vect[] ) {
	
	for( int i=0; i<vect.length; i++ ) {
	    if( vect[i] > wert ) wert = vect[i];
	}
	return wert;
    }

    /** ***************************************************************************
     * cumulative maximum search
     */
    public static short vmax( short wert, short vect[] ) {
	
	if( vect == null ) return wert;
	if( vect.length >= 1 ) {
	    for( int i=0; i<vect.length; i++ ) {
		if( vect[i] > wert ) wert = vect[i];
	    }
	}
	return wert;
    }

   /** ***************************************************************************
    */
  static double vabsmax( double vect[] ) {
    double wert = Math.abs( vect[0] );
    
    if( vect.length >= 1 ) {
      for( int i=1; i<vect.length; i++ ) {
	if( Math.abs( vect[i] ) > wert ) wert = Math.abs( vect[i] );
      }
    }
    return wert;
  }

  /** ***************************************************************************
    */
  static double mmax( double vect[][] ) {
    double wert = java.lang.Double.MIN_VALUE;
    
    for( int i=0; i<vect.length; i++ ) {
      for( int j=0; j<vect[i].length; j++ ) {
	if( vect[i][j] > wert ) wert = vect[i][j];
      }
    }
    return wert;
  }

  /** ***************************************************************************
    */
  static short mmax( short vect[][] ) {
    short wert = java.lang.Short.MIN_VALUE;
    
    for( int i=0; i<vect.length; i++ ) {
      for( int j=0; j<vect[i].length; j++ ) {
	if( vect[i][j] > wert ) wert = vect[i][j];
      }
    }
    return wert;
  }

  /** ***************************************************************************
    */
  static double mmax( double wert, double vect[][] ) {
    
    for( int i=0; i<vect.length; i++ ) {
      for( int j=0; j<vect[i].length; j++ ) {
	if( vect[i][j] > wert ) wert = vect[i][j];
      }
    }
    return wert;
  }

  /** ***************************************************************************
    */
  static double mabsmax( double vect[][] ) {
    double wert = 0;
    
    for( int i=0; i<vect.length; i++ ) {
      for( int j=0; j<vect[i].length; j++ ) {
	if( Math.abs( vect[i][j] ) > wert ) wert = Math.abs( vect[i][j] );
      }
    }
    return wert;
  }

  /** ***************************************************************************
    */
  static double mabsmax( double wert, double vect[][] ) {
    
    for( int i=0; i<vect.length; i++ ) {
      for( int j=0; j<vect[i].length; j++ ) {
	if( Math.abs( vect[i][j] ) > wert ) wert = Math.abs( vect[i][j] );
      }
    }
    return wert;
  }

  /** ***************************************************************************
    */
  static double mmin( double vect[][] ) {
    double wert = java.lang.Double.MAX_VALUE;
    
    for( int i=0; i<vect.length; i++ ) {
      for( int j=0; j<vect[i].length; j++ ) {
	if( vect[i][j] < wert ) wert = vect[i][j];
      }
    }
    return wert;
  }

  /** ***************************************************************************
    */
  static short mmin( short vect[][] ) {
    short wert = java.lang.Short.MAX_VALUE;
    
    for( int i=0; i<vect.length; i++ ) {
      for( int j=0; j<vect[i].length; j++ ) {
	if( vect[i][j] < wert ) wert = vect[i][j];
      }
    }
    return wert;
  }

  /** ***************************************************************************
   * cumulative minimum search
   */
  static double mmin( double wert, double vect[][] ) {
    
    for( int i=0; i<vect.length; i++ ) {
      for( int j=0; j<vect[i].length; j++ ) {
	if( vect[i][j] < wert ) wert = vect[i][j];
      }
    }
    return wert;
  }

  /** ***************************************************************************
    */
  static void msmult( double wert, double vect[][] ) {
    
    for( int i=0; i<vect.length; i++ ) {
      for( int j=0; j<vect[i].length; j++ ) {
	vect[i][j] *= wert;
      }
    }
  }

  /** ***************************************************************************
    */
  public static void vsmult( double wert, double vect[] ) {
    
      for( int j=0; j<vect.length; j++ ) vect[j] *= wert;
  }

  /** ***************************************************************************
    * count down version, tests i!=0. Runs faster on SUN, no differences
    * on Win with jit but slower without jit?!
    */
  static double vmax2( double vect[] ) {
    double wert = vect[0];
    
    if( vect.length >= 1 ) {
      for( int i=vect.length-1; i != 0; i-- ) {
	if( vect[i] > wert ) wert = vect[i];
      }
    }
    return wert;
  }

    /* ===================================================================
     * sort input array, return max ... min
     */
    static int[] vsort(  int[] vect ) {
	int      i;
	boolean  change = false;
	int      tmp;
	int[]    out = new int[vect.length];
	
	for( i=0; i<vect.length; i++ ) out[i] = vect[i];

	do {
	    change = false;
	    for( i=1; i<vect.length; i++ ) {
		if( out[i] > out[i-1] ) {
		    change = true;
		    tmp      = out[i];
		    out[i]   = out[i-1];
		    out[i-1] = tmp;
		}
	    }
	} while( change );

	return out;
    }


	public static double vmax(double wert, double[] vect, int start, int inc) {
		for( int i=start; i<vect.length; i+=inc ) {
		    if( vect[i] > wert ) wert = vect[i];
		}
		return wert;
	}


	public static double vmin(double wert, double[] vect, int start, int inc) {
		for( int i=start; i<vect.length; i+=inc ) {
		    if( vect[i] < wert ) wert = vect[i];
		}
		return wert;
	}
}
