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

import ch.wsl.sustfor.baumschaft.base.Baumart;;

/**
 * 
 * @author Stefan Holm
 *
 */
public class DurchmesserKoeffizienten {
	
	private static final double[][][] allCoefficients = new double[Baumart.values().length][8][5];
	
	static {
		//Fichte (Spruce)
//		int[][] fichte = new int[8][5];
		double[][] fichte = {//    b0           b1           b2         b3            b4
				/* 1m  */	{ -0.0322735 ,  0         ,  1.01822722,  0         ,  0.00070241 },
				/*  5% */	{  0.6694153 , -0.03912357,  1.10731936,  0.00183994, -0.00460934 },
				/* 10% */	{  1.98212707, -0.08931625,  1.01658607,  0.01297247, -0.01315235 },
				/* 20% */	{  2.32979631, -0.08534539,  0.90240645,  0.02113534, -0.01876776 },
				/* 30% */	{  1.94286623, -0.05605474,  0.83535552,  0.02046769, -0.01817887 },
				/* 50% */	{  1.2168963 ,  0.00958755,  0.65084147,  0.01887258, -0.0163289  },
				/* 70% */	{  0.1701299 ,  0.11005913,  0.37702981,  0.01457552, -0.01197956 },
				/* 80% */	{ -0.35434648,  0.14221273,  0.23483065,  0.01028162, -0.00846602 }
				};
		allCoefficients[Baumart.Fichte.ordinal()] = fichte;
		
		//Tanne (Fir)
//		int[][] tanne = new int[8][5];
		double[][] tanne = {//    b0           b1           b2         b3            b4
				/* 1m  */	{ -0.61671376,  0.01348875,  1.05035114, -0.0020169 ,  0.00156697 },
				/*  5% */	{  0.38173759, -0.01419189,  1.08620965,  0.00221964, -0.00474542 },
				/* 10% */	{  2.26735806, -0.07434391,  0.95987737,  0.0144543 , -0.01352572 },
				/* 20% */	{  2.96815848, -0.09566062,  0.87055081,  0.02176944, -0.0186884  },
				/* 30% */	{  2.73444057, -0.08702372,  0.81440997,  0.02087228, -0.01768526 },
				/* 50% */	{  1.1004138 ,  0.01780012,  0.66899645,  0.01685419, -0.01450547 },
				/* 70% */	{ -1.5623776 ,  0.22157046,  0.42897272,  0.00991785, -0.00938154 },
				/* 80% */	{ -2.1747944 ,  0.27151403,  0.26857728,  0.00707074, -0.00680849 }
				};
		allCoefficients[Baumart.Tanne.ordinal()] = tanne;
		
		//Föhre (Scotch Pine)
//		int[][] foehre = new int[8][5];
		double[][] foehre = {//    b0           b1           b2         b3            b4
				/* 1m  */	{  0.33030733,  0         ,  1.02457213,  0.00379692, -0.00300232 },
				/*  5% */	{  0.63361645, -0.02878408,  1.11662853, -0.00140075, -0.00226452 },
				/* 10% */	{  1.64052474, -0.07434391,  1.02397656,  0.00732716, -0.00865593 },
				/* 20% */	{  2.52197576, -0.10863366,  0.89019656,  0.02174408, -0.01854897 },
				/* 30% */	{  2.25426126, -0.0958468 ,  0.83243483,  0.02315113, -0.01978661 },
				/* 50% */	{  1.77930748, -0.07293812,  0.7132659 ,  0.02046024, -0.01785395 },
				/* 70% */	{  0.59456933, -0.04502022,  0.64238423,  0.01239497, -0.01308449 },
				/* 80% */	{ -0.21832582,  0.02805848,  0.49054864,  0.00834929, -0.01011157 }
				};
		allCoefficients[Baumart.Foehre.ordinal()] = foehre;
		
		//Lärche (Larch)
//		int[][] laerche = new int[8][5];
		double[][] laerche = {//    b0           b1           b2         b3            b4
				/* 1m  */	{ -0.66468376,  0.05510109,  1.02942657, -0.00196514,  0.00125772 },
				/*  5% */	{  1.2846204 , -0.05446032,  1.09672856,  0.00096711, -0.00355392 },
				/* 10% */	{  2.63373613, -0.10524832,  0.97468442,  0.01231533, -0.01152565 },
				/* 20% */	{  2.06671786, -0.07194527,  0.89571863,  0.01959724, -0.01754426 },
				/* 30% */	{  1.47027445, -0.0459033 ,  0.83956546,  0.01894159, -0.01717868 },
				/* 50% */	{  0.71770215, -0.02104711,  0.6494292 ,  0.01692222, -0.01490832 },
				/* 70% */	{ -0.29833773,  0.12124127,  0.40120178,  0.01198215, -0.01057149 },
				/* 80% */	{ -0.61502206,  0.15138558,  0.25490171,  0.00875077, -0.00784795 }
				};
		allCoefficients[Baumart.Laerche.ordinal()] = laerche;
		
		//Buche (Beech)
//		int[][] buche = new int[8][5];
		double[][] buche = {//    b0           b1           b2         b3            b4
				/* 1m  */	{ -0.10826337,  0         ,  1.03912008, -0.00089265,  0.0004035  },
				/*  5% */	{  0.59974957, -0.01922579,  1.04605079,  0.00017884, -0.00176683 },
				/* 10% */	{  1.30630982, -0.03507829,  0.98725438,  0.00997312, -0.01022303 },
				/* 20% */	{  1.69247139, -0.05899471,  0.9332602 ,  0.01975506, -0.0184743  },
				/* 30% */	{  1.686064  , -0.06477597,  0.86990666,  0.02075929, -0.01893105 },
				/* 50% */	{  0.6492604 , -0.00148954,  0.6994524 ,  0.01686741, -0.01543733 },
				/* 70% */	{ -1.2748044 ,  0.13125174,  0.43584272,  0.00958976, -0.00995488 },
				/* 80% */	{ -0.72813153,  0.117301  ,  0.22970511,  0.00731678, -0.00673901 }
				};
		allCoefficients[Baumart.Buche.ordinal()] = buche;
		
		//Eiche (Oak)
//		int[][] eiche = new int[8][5];
		double[][] eiche = {//    b0           b1           b2         b3            b4
				/* 1m  */	{ -0.39968038,  0         ,  1.08216906, -0.0013725 ,  0          },
				/*  5% */	{  0.28164521, -0.01204597,  1.09435189, -0.00087433, -0.00222605 },
				/* 10% */	{  1.49493408, -0.05652448,  1.0137068 ,  0.01020512, -0.01101364 },
				/* 20% */	{  1.48586392, -0.06749376,  0.98738503,  0.0208354 , -0.02066637 },
				/* 30% */	{  1.75309861, -0.07523583,  0.89548081,  0.02410906, -0.02224103 },
				/* 50% */	{  0.50951594, -0.00726158,  0.74923503,  0.01699877, -0.0161029  },
				/* 70% */	{ -0.80354124,  0.11033778,  0.48804128,  0.01151507, -0.01212845 },
				/* 80% */	{ -0.62629098,  0.11335155,  0.29778415,  0.00792077, -0.00831111 }
				};
		allCoefficients[Baumart.Eiche.ordinal()] = eiche;

		allCoefficients[Baumart.UebrigesNadelholz.ordinal()] = fichte;
		allCoefficients[Baumart.Ahorn.ordinal()] = buche;
		allCoefficients[Baumart.Esche.ordinal()] = buche;
		allCoefficients[Baumart.UebrigesLaubholz.ordinal()] = buche;
	}
	
	public static enum StuetzstellenHoehe {
		H_1m,
		H_5Prz,
		H_10Prz,
		H_20Prz,
		H_30Prz,
		H_50Prz,
		H_70Prz,
		H_80Prz,
		H_100Prz;
	}
	
	private static double getDrmAtHeight(Baumart baumart, StuetzstellenHoehe targetHeight, double d130, double d700, double treeHeight) {	
		if (targetHeight == StuetzstellenHoehe.H_100Prz) {
			return 0;
		}
		
		double[][] coefficientsBaumart = allCoefficients[baumart.ordinal()];		
		double[] b = coefficientsBaumart[targetHeight.ordinal()];		
		double result = 
				b[0] + 
				b[1] * treeHeight + 
				b[2] * d700 + 
				b[3] * d700 * treeHeight + 
				b[4] * d130 * treeHeight;

//		System.out.println("Drm at height " + targetHeight + " for " + baumart + " is " + result);		
		return result;
	}
	
	public static double[] getStuetzstellen(Baumart baumart, double d130, double d700, double treeHeight) {		
		double[] result = new double[9];		
		for (int i=0; i<9; i++) {
			result[i] = getDrmAtHeight(baumart, StuetzstellenHoehe.values()[i], d130, d700, treeHeight);
		}		
		return result;		
	}
}
