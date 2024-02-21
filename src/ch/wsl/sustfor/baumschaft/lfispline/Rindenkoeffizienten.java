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
public class Rindenkoeffizienten {
	
	public static final double[][][] allCoefficients = new double[Baumart.values().length][4][3];
	
	public enum StammAbschnitt{
		Erdstamm,
		Mittelstammstück,
		Gipfelstammstück,
		Durchschnitt;
	}
	
	static {
		//Fichte
		double[][] fichte = {//       b0        b1        b2     
				/* Erde    */	{  1.55540,  0.55475, -0.00225 },
				/* Mitte   */	{  0.82652,  0.59424, -0.00212 },
				/* Gipfel  */	{  0.17440,  0.67905, -0.00247 },
				/* Schnitt */	{  0.85149,  0.60934,  0.00228 },
				};
		allCoefficients[Baumart.Fichte.ordinal()] = fichte;

		//Tanne
		double[][] tanne = {//       b0        b1        b2     
				/* Erde    */	{  1.67703,  0.56074,  0.00000 },
				/* Mitte   */	{  0.82802,  0.62504,  0.00000 },
				/* Gipfel  */	{  0.67058,  0.68492,  0.00000 },
				/* Schnitt */	{  1.76896,  0.59175,  0.00000 },
				};
		allCoefficients[Baumart.Tanne.ordinal()] = tanne;

		//Föhre
		double[][] foehre = {//       b0        b1        b2     
				/* Erde    */	{  5.43367,  0.62571,  0.00000 },
				/* Mitte   */	{  0.05652,  0.56149,  0.00000 },
				/* Gipfel  */	{  4.17891,  0.22292,  0.00000 },
				/* Schnitt */	{  1.59099,  0.51146,  0.00000 },
				};
		allCoefficients[Baumart.Foehre.ordinal()] = foehre;

		//Lärche
		double[][] laerche = {//       b0        b1        b2     
				/* Erde    */	{ -6.46451,  1.73845, -0.00943 },
				/* Mitte   */	{ -6.45758,  1.82516, -0.01176 },
				/* Gipfel  */	{ -9.74591,  2.31981, -0.02250 },
				/* Schnitt */	{ -2.82354,  1.65101, -0.00951 },
				};
		allCoefficients[Baumart.Laerche.ordinal()] = laerche;

		//Buche
		double[][] buche = {//       b0        b1        b2     
				/* Erde    */	{  1.97733,  0.28119,  0.00000 },
				/* Mitte   */	{  2.25734,  0.29724,  0.00000 },
				/* Gipfel  */	{  2.69794,  0.31096,  0.00000 },
				/* Schnitt */	{  2.61029,  0.28522,  0.00000 },
				};
		allCoefficients[Baumart.Buche.ordinal()] = buche;

		//Eiche
		double[][] eiche = {//       b0        b1        b2     
				/* Erde    */	{  9.10974,  0.66266,  0.00000 },
				/* Mitte   */	{  8.94454,  0.71505,  0.00000 },
				/* Gipfel  */	{  9.88377,  0.75877,  0.00000 },
				/* Schnitt */	{ 10.18342,  0.68977,  0.00000 },
				};
		allCoefficients[Baumart.Eiche.ordinal()] = eiche;

		allCoefficients[Baumart.UebrigesNadelholz.ordinal()] = fichte;
		allCoefficients[Baumart.Ahorn.ordinal()] = buche;
		allCoefficients[Baumart.Esche.ordinal()] = buche;
		allCoefficients[Baumart.UebrigesLaubholz.ordinal()] = buche;
	}
	
}
