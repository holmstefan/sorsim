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

import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition;
import ch.wsl.sustfor.baumschaft.lfispline.Rindenkoeffizienten.StammAbschnitt;
import ch.wsl.sustfor.baumschaft.lfispline.flanagan.CubicSpline;

/**
 * 
 * @author Stefan Holm
 *
 */
public class BaumschaftformFkt_Lfi_Spline extends BaumSchaftformFunktion {
	
	@Override
	public double getDurchmesser_iR_cm(BaumschaftDefinition bsDef, double messHoehe_m) {
		// 1. Berechne 9 Stützstellen am Baumschaft
		double[] stuetzStellen = DurchmesserKoeffizienten.getStuetzstellen(
				bsDef.getBaumart(),
				bsDef.getBhd_cm(),
				bsDef.getD7m_cm(),
				bsDef.getSchaftLaenge_m()
				);
		
		// 2. Berechne Krümmung in Höhe 1m
		double kruemmung1m = StammfussKoeffizienten.getCurvatureAt1m(
				bsDef.getBaumart(),
				stuetzStellen[DurchmesserKoeffizienten.StuetzstellenHoehe.H_1m.ordinal()],
				bsDef.getBhd_cm(),
				bsDef.getD7m_cm()
				);

//		for (int i=0; i<9; i++) {
//			System.out.println( "Stuetzstelle in Höhe " + DurchmesserKoeffizienten.StuetzstellenHoehe.values()[i] + " = " + stuetzStellen[DurchmesserKoeffizienten.StuetzstellenHoehe.values()[i].ordinal()] );
//		}
//		System.out.println( "Krümmung in Höhe 1m = " + kruemmung1m);
//
//		System.out.println("Hoehe der Stützstelle  1m = " + 1);
//		System.out.println("Hoehe der Stützstelle  5% = " + 0.05 * bsDef.getSchaftLaenge_m());
//		System.out.println("Hoehe der Stützstelle 10% = " + 0.10 * bsDef.getSchaftLaenge_m());
//		System.out.println("Hoehe der Stützstelle 20% = " + 0.20 * bsDef.getSchaftLaenge_m());
//		System.out.println("Hoehe der Stützstelle 30% = " + 0.30 * bsDef.getSchaftLaenge_m());
//		System.out.println("Hoehe der Stützstelle 50% = " + 0.50 * bsDef.getSchaftLaenge_m());
//		System.out.println("Hoehe der Stützstelle 70% = " + 0.70 * bsDef.getSchaftLaenge_m());
//		System.out.println("Hoehe der Stützstelle 80% = " + 0.80 * bsDef.getSchaftLaenge_m());
		
		// 3. Interpolation mit kubischen Splines
		double[] x = new double[9];
		x[0] = 1;
		x[1] = 0.05 * bsDef.getSchaftLaenge_m();
		x[2] = 0.10 * bsDef.getSchaftLaenge_m();
		x[3] = 0.20 * bsDef.getSchaftLaenge_m();
		x[4] = 0.30 * bsDef.getSchaftLaenge_m();
		x[5] = 0.50 * bsDef.getSchaftLaenge_m();
		x[6] = 0.70 * bsDef.getSchaftLaenge_m();
		x[7] = 0.80 * bsDef.getSchaftLaenge_m();
		x[8] = 1.00 * bsDef.getSchaftLaenge_m();
		
		if ( Math.abs(bsDef.getSchaftLaenge_m() - 20.0) <= 2 ) { //Korrektur bei Schaftlänge 20m: Auslassen der 1. Stützstelle
			x = new double[]{x[0],x[2],x[3],x[4],x[5],x[6],x[7],x[8]};
			stuetzStellen = new double[]{stuetzStellen[0],stuetzStellen[2],stuetzStellen[3],stuetzStellen[4],stuetzStellen[5],stuetzStellen[6],stuetzStellen[7],stuetzStellen[8]};
		} 
		else if ( Math.abs(bsDef.getSchaftLaenge_m() - 10.0) <= 2 ) { //Korrektur bei Schaftlänge 10m: Auslassen der 2. Stützstelle
			x = new double[]{x[0],x[1],x[3],x[4],x[5],x[6],x[7],x[8]};
			stuetzStellen = new double[]{stuetzStellen[0],stuetzStellen[1],stuetzStellen[3],stuetzStellen[4],stuetzStellen[5],stuetzStellen[6],stuetzStellen[7],stuetzStellen[8]};
		}
		else if ( Math.abs(bsDef.getSchaftLaenge_m() - 5.0) <= 2 ) { //Korrektur bei Schaftlänge 5: Auslassen der 3. Stützstelle
			x = new double[]{x[0],x[1],x[2],x[4],x[5],x[6],x[7],x[8]};
			stuetzStellen = new double[]{stuetzStellen[0],stuetzStellen[1],stuetzStellen[2],stuetzStellen[4],stuetzStellen[5],stuetzStellen[6],stuetzStellen[7],stuetzStellen[8]};
		}
		CubicSpline cs = new CubicSpline(x, stuetzStellen);	
		CubicSpline.supress();
		double[] derivs = cs.getDeriv();
		derivs[0] = kruemmung1m;
		cs.setDeriv(derivs);
		
		// Keine Berechnung unter 1m
		if (messHoehe_m < 1.0) {
			messHoehe_m = 1.0;
		}
		
		// 4. Korrekturfaktor		
		double d130diff = bsDef.getBhd_cm() - 0;
		if (bsDef.getSchaftLaenge_m() >= 1.3) {
			d130diff = bsDef.getBhd_cm() - cs.interpolate(1.30);		
		}
		
		double d700diff = bsDef.getD7m_cm() - 0;
		if (bsDef.getSchaftLaenge_m() >= 7) {
			d700diff = bsDef.getD7m_cm() - cs.interpolate(7.0);		
		}
		
		double interpolated = cs.interpolate(messHoehe_m);		
		double result = -1.0;		
		if (messHoehe_m <= 7.0) {			
			double term = (messHoehe_m - 1.3) / 5.7;			
			result = interpolated + term * d700diff + (1-term) * d130diff;			
		}
		else {
			double term = (messHoehe_m - 7.0) / (bsDef.getSchaftLaenge_m() - 7.0);			
			result = interpolated + (1-term) * d700diff;
		}
		
		return result;
	}
	
	@Override
	public double getRindenDickeSummeBeidseitig_cm(BaumschaftDefinition bsDef, double messHoehe_m) {
		double[][] koeffBaum = Rindenkoeffizienten.allCoefficients[bsDef.getBaumart().ordinal()];
		double[] koeff;
		
		if (messHoehe_m <= 0.33 * bsDef.getSchaftLaenge_m()) {
			koeff = koeffBaum[StammAbschnitt.Erdstamm.ordinal()];
		}
		else if (messHoehe_m <= 0.66 * bsDef.getSchaftLaenge_m()) {
			koeff = koeffBaum[StammAbschnitt.Mittelstammstück.ordinal()];
		}
		else if (messHoehe_m <= bsDef.getSchaftLaenge_m()) {
			koeff = koeffBaum[StammAbschnitt.Gipfelstammstück.ordinal()];
		}
		else {
			throw new RuntimeException("Invalid height");
		}
		
		double drm = this.getDurchmesser_iR_cm(bsDef, messHoehe_m);	
		double result = 
				koeff[0] + 
				koeff[1] * drm +
				koeff[2] * drm * drm;
		result /= 10; //mm -> cm
		
//		System.out.println(messHoehe_m + ": " + result + " (" + drm + ")");
		return result;
	}
	
	@Override
	public String getInfo() {
		return "LFI-Splines";
	}

	@Override
	public String toString(){
		return "LFI-Splines";			
	}
}
