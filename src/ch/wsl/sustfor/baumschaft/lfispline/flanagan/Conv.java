/*
*   Class   Conv
*
*   USAGE:  Methods for:
*       Recasting variable type with exception throwing not present in standard java recasts
*       Conversion of physical entities from one set of units to another
*       For copy methods now see Copy.java - those already in Conv will be retained for compatibility purposes (10 April 2012)
*
*   WRITTEN BY: Dr Michael Thomas Flanagan
*
*   DATE:    April 2008
*   AMENDED: September 2009, 9-20 January 2011, 6-11 April 2012
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's Java library on-line web pages:
*   http://www.ee.ucl.ac.uk/~mflanaga/java/
*   http://www.ee.ucl.ac.uk/~mflanaga/java/Conv.html
*
*   Copyright (c) 2008 - 2012
*
*   PERMISSION TO COPY:
*   Permission to use, copy and modify this software and its documentation for
*   NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement
*   to the author, Michael Thomas Flanagan at www.ee.ucl.ac.uk/~mflanaga, appears in all copies.
*
*   Dr Michael Thomas Flanagan makes no representations about the suitability
*   or fitness of the software for any or for a particular purpose.
*   Michael Thomas Flanagan shall not be liable for any damages suffered
*   as a result of using, modifying or distributing this software or its derivatives.
*
***************************************************************************************/

package ch.wsl.sustfor.baumschaft.lfispline.flanagan;

public class Conv{
	
    // COPY A ONE DIMENSIONAL ARRAY OF double
    public static double[] copy(double[] array){
    	if(array==null)return null;
    	int n = array.length;
    	double[] copy = new double[n];
    	for(int i=0; i<n; i++){
    		copy[i] = array[i];
    	}
    	return copy;
    }

    // COPY A ONE DIMENSIONAL ARRAY OF int
    public static int[] copy(int[] array){
    	if(array==null)return null;
    	int n = array.length;
    	int[] copy = new int[n];
    	for(int i=0; i<n; i++){
    		copy[i] = array[i];
    	}
    	return copy;
    }
}
    