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
package ch.wsl.sustfor.baumschaft.lemm1991;

import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition;
import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.baumschaft.lemm1991.BaumartListe.SchaftformInRindeParameterSatz;
import ch.wsl.sustfor.lang.SorSimLanguageManager;


/**
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public class BaumSchaftformFkt_Lemm1991 extends BaumSchaftformFunktion{
	
	private static final double BRUST_HOEHE_m = 1.3;	
	private BaumartListe baListe = new BaumartListe();	
	
	@Override
	public String getInfo() {
		SorSimLanguageManager lang = SorSimLanguageManager.getInstance();
		
		String infoText = this.getClass().getSimpleName() + "\n\n" +
							lang.getText("szInfoText") + "\n\n" +
							lang.getText("szInfoTextStandardSprache") + "\n" +
							lang.getText("szInfoTextStandardBaumart");		
		return infoText;
	}
	
	/**
	 * Ermittelt den Durchmesser in Rinde in cm auf messHoehe_m.
	 */
	@Override
	public double getDurchmesser_iR_cm(BaumschaftDefinition bsDef, double messHoehe_m) {		
		double dBHD = bsDef.getBhd_cm();
		double dSH = bsDef.getSchaftLaenge_m();		

		boolean done = this.isInputOk(bsDef, messHoehe_m);		
		if (done == false) {
			return -1;
		}

        double dXi = (dSH - messHoehe_m) / dSH;
        double dXiBH = (dSH - BRUST_HOEHE_m) / dSH;	
		
        SchaftformInRindeParameterSatz param = baListe.getSchaftformParameter(bsDef.getBaumart());
        double dF = ((1 / dXiBH) 
        		- (param.k21 + param.k22 * dSH) * dXiBH 
        		- (param.k31 + param.k32 * dSH) * Math.pow(dXiBH, 2)
        		- (param.k41 + param.k42 * dSH) * Math.pow(dXiBH, 3) 
        		- (param.k51 + param.k52 * dSH) * Math.pow(dXiBH, 4) ) * dXi 
        		+ (param.k21 + param.k22 * dSH) * Math.pow(dXi, 2) 
        		+ (param.k31 + param.k32 * dSH) * Math.pow(dXi, 3)
        		+ (param.k41 + param.k42 * dSH) * Math.pow(dXi, 4) 
        		+ (param.k51 + param.k52 * dSH) * Math.pow(dXi, 5);
        

		double durchmesser_iR_cm = dF * dBHD;
		
		return durchmesser_iR_cm;
	}
	
	/**
	 * Ermittelt die Summe der beidseitigen Rindendicke in cm.
	 */
	@Override
	public double getRindenDickeSummeBeidseitig_cm(BaumschaftDefinition bsDef, double messHoehe_m) {
		boolean done = isInputOk(bsDef, messHoehe_m);
        if (done == false) {
        	return -1;
        }
        
        double dDrmIR = getDurchmesser_iR_cm(bsDef, messHoehe_m);
        double dRiAnteilPrz = getRindenAnteilAmDurchmesserInRinde_Prz(bsDef, messHoehe_m);
        double rindenDickeSummeBeidseitig_cm = dRiAnteilPrz / 100 * dDrmIR;
		
		return rindenDickeSummeBeidseitig_cm;
	}
	
	private double getRindenAnteilAmDurchmesserInRinde_Prz(BaumschaftDefinition bsDef, double messHoehe_m) {
		double bsLaenge_m = bsDef.getSchaftLaenge_m();
		
		boolean done = isInputOk(bsDef, messHoehe_m);
		if ( done == false ) {
			return -1;
		}		

		double dXi = (bsLaenge_m - messHoehe_m) / bsLaenge_m;	

		double r0 = this.baListe.getRindenParameter(bsDef.getBaumart()).r0;
		double r1 = this.baListe.getRindenParameter(bsDef.getBaumart()).r1;
		double r2 = this.baListe.getRindenParameter(bsDef.getBaumart()).r2;
		double r3 = this.baListe.getRindenParameter(bsDef.getBaumart()).r3;
		
		double dRiAnteil = r0 * Math.pow(dXi,0) +
				    r1 * Math.pow(dXi,1) +
				    r2 * Math.pow(dXi,2) +
				    r3 * Math.pow(dXi,3);				
		
		return dRiAnteil;
	}
	
	/**
	 * Prüft die übergebenen Grössen auf ihre Gültigkeit
	 * 
	 * @param bsDef
	 * @param messHoehe_m
	 * 
	 * @return false, wenn mindestens ein Wert ungültig ist
	 */
	private boolean isInputOk(BaumschaftDefinition bsDef, double messHoehe_m) {
		double bhd_iR_cm = bsDef.getBhd_cm();
		double scheitelHoehe_m = bsDef.getSchaftLaenge_m();
		
		if (scheitelHoehe_m <= BRUST_HOEHE_m) {
			return false;
		}
		
		if (scheitelHoehe_m > 100) {
			return false;
		}
		
		if (bhd_iR_cm <= 0) {
			return false;
		}
		
		if (bhd_iR_cm > 200) {
			return false;
		}
		
		if (messHoehe_m < 0) {
			return false;
		}
		
		if (messHoehe_m > scheitelHoehe_m) {
			return false;
		}
		
		//all checks passed
		return true;
	}
	
	@Override
	public String toString(){
		return "Lemm 1991";				
	}
}
