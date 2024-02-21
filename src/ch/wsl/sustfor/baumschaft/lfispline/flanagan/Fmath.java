/*
*   Class   Fmath
*
*   USAGE:  Mathematical class that supplements java.lang.Math and contains:
*               the main physical constants
*               trigonemetric functions absent from java.lang.Math
*               some useful additional mathematical functions
*               some conversion functions
*
*   WRITTEN BY: Dr Michael Thomas Flanagan
*
*   DATE:    June 2002
*   AMENDED: 6 January 2006, 12 April 2006, 5 May 2006, 28 July 2006, 27 December 2006,
*            29 March 2007, 29 April 2007, 2,9,15 & 26 June 2007, 20 October 2007, 4-6 December 2007
*            27 February 2008, 25 April 2008, 26 April 2008, 13 May 2008, 25/26 May 2008, 3-7 July 2008
*            11 November 2010, 9-18 January 2011, 13 August 2011
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's Java library on-line web pages:
*   http://www.ee.ucl.ac.uk/~mflanaga/java/
*   http://www.ee.ucl.ac.uk/~mflanaga/java/Fmath.html
*
*   Copyright (c) 2002 - 2011
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Fmath{

	// HashMap for 'arithmetic integer' recognition nmethod
	private static final Map<Object,Object> integers = new HashMap<Object,Object>();
	static{
		integers.put(Integer.class, BigDecimal.valueOf(Integer.MAX_VALUE));
		integers.put(Long.class, BigDecimal.valueOf(Long.MAX_VALUE));
		integers.put(Byte.class, BigDecimal.valueOf(Byte.MAX_VALUE));
		integers.put(Short.class, BigDecimal.valueOf(Short.MAX_VALUE));
		integers.put(BigInteger.class, BigDecimal.valueOf(-1));
	}

	// ARRAY MAXIMUM  (deprecated - see ArryMaths class)
	// Maximum of a 1D array of doubles, aa
	public static double maximum(double[] aa){
		int n = aa.length;
		double aamax=aa[0];
		for(int i=1; i<n; i++){
			if(aa[i]>aamax)aamax=aa[i];
		}
		return aamax;
	}

	// Minimum of a 1D array of doubles, aa
	public static double minimum(double[] aa){
		int n = aa.length;
		double aamin=aa[0];
		for(int i=1; i<n; i++){
			if(aa[i]<aamin)aamin=aa[i];
		}
		return aamin;
	}
	// sort elements in an array of doubles into ascending order
	// using selection sort method
	// aa - the original array - not altered
	// bb - the sorted array
	// indices - an array of the original indices of the sorted array
	public static void selectionSort(double[] aa, double[] bb, int[] indices){
		int index = 0;
		int lastIndex = -1;
		int n = aa.length;
		double holdb = 0.0D;
		int holdi = 0;
		for(int i=0; i<n; i++){
			bb[i]=aa[i];
			indices[i]=i;
		}

		while(lastIndex != n-1){
			index = lastIndex+1;
			for(int i=lastIndex+2; i<n; i++){
				if(bb[i]<bb[index]){
					index=i;
				}
			}
			lastIndex++;
			holdb=bb[index];
			bb[index]=bb[lastIndex];
			bb[lastIndex]=holdb;
			holdi=indices[index];
			indices[index]=indices[lastIndex];
			indices[lastIndex]=holdi;
		}
	}

	// sort the elements of an array of doubles into ascending order with matching switches in an array of the length
	// using selection sort method
	// array determining the order is the first argument
	// matching array  is the second argument
	// sorted arrays returned as third and fourth arguments respectively
	public static void selectionSort(double[] aa, double[] bb, double[] cc, double[] dd){
		int index = 0;
		int lastIndex = -1;
		int n = aa.length;
		int m = bb.length;
		if(n!=m)throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
		int nn = cc.length;
		if(nn<n)throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
		int mm = dd.length;
		if(mm<m)throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

		double holdx = 0.0D;
		double holdy = 0.0D;

		for(int i=0; i<n; i++){
			cc[i]=aa[i];
			dd[i]=bb[i];
		}

		while(lastIndex != n-1){
			index = lastIndex+1;
			for(int i=lastIndex+2; i<n; i++){
				if(cc[i]<cc[index]){
					index=i;
				}
			}
			lastIndex++;
			holdx=cc[index];
			cc[index]=cc[lastIndex];
			cc[lastIndex]=holdx;
			holdy=dd[index];
			dd[index]=dd[lastIndex];
			dd[lastIndex]=holdy;
		}
	}
}

