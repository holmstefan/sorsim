/*******************************************************************************
 * Copyright 2024 Stefan Holm
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ch.wsl.sustfor.baumschaft.lfispline;

import ch.wsl.sustfor.baumschaft.base.Baumart;

/**
 * 
 * @author Stefan Holm
 *
 */
public class StammfussKoeffizienten {	
	
	private static final double[][] allCoefficients = new double[Baumart.values().length][5];
	
	static {
		//Fichte
		double[] fichte = {
				/* b0 */  0.668756,
				/* b1 */ -0.000563,
				/* b2 */  0.000090993,
				/* b3 */ -0.679303,
				/* b4 */  0.000487
		};		
		allCoefficients[Baumart.Fichte.ordinal()] = fichte;
		
		//Tanne
		double[] tanne = {
				/* b0 */  0.825355,
				/* b1 */ -0.000472,
				/* b2 */  0.000064768,
				/* b3 */ -0.836510,
				/* b4 */  0.000415
		};		
		allCoefficients[Baumart.Tanne.ordinal()] = tanne;
		
		//Föhre
		double[] foehre = {
				/* b0 */  0.595389,
				/* b1 */ -0.000761,
				/* b2 */  0.000098059,
				/* b3 */ -0.612328,
				/* b4 */  0.000682
		};		
		allCoefficients[Baumart.Foehre.ordinal()] = foehre;
		
		//Lärche
		double[] laerche = {
				/* b0 */  0.894742,
				/* b1 */ -0.000435,
				/* b2 */  0.000065724,
				/* b3 */ -0.920327,
				/* b4 */  0.000383
		};		
		allCoefficients[Baumart.Laerche.ordinal()] = laerche;
		
		//Buche
		double[] buche = {
				/* b0 */  0.643240,
				/* b1 */ -0.000654,
				/* b2 */  0.000076046,
				/* b3 */ -0.651962,
				/* b4 */  0.000587
		};		
		allCoefficients[Baumart.Buche.ordinal()] = buche;
		
		//Eiche
		double[] eiche = {
				/* b0 */  0.691773,
				/* b1 */ -0.000646,
				/* b2 */  0.000079513,
				/* b3 */ -0.701271,
				/* b4 */  0.000576
		};		
		allCoefficients[Baumart.Eiche.ordinal()] = eiche;

		allCoefficients[Baumart.UebrigesNadelholz.ordinal()] = fichte;
		allCoefficients[Baumart.Ahorn.ordinal()] = buche;
		allCoefficients[Baumart.Esche.ordinal()] = buche;
		allCoefficients[Baumart.UebrigesLaubholz.ordinal()] = buche;
	}
	
	public static double getCurvatureAt1m(Baumart baumart, double d100, double d130, double d700) {
		double[] b = allCoefficients[baumart.ordinal()];
		
		double result = 
				b[0] +
				b[1] * Math.pow(d130, 2) +
				b[2] * Math.pow(d700, 2) +
				b[3] * (d130 / d100) + 
				b[4] * d130 * d100;
		
//		System.out.println("Curvature at 1m for " + baumart + " is " + result);		
		return result;
	}
}
