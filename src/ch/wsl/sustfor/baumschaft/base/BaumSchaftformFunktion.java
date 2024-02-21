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
package ch.wsl.sustfor.baumschaft.base;

/**
 * Schnittstelle für die Einbindung beliebiger Schaftformfunktionen in BaumSchaftForm!
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public abstract class BaumSchaftformFunktion {

    /**
     * Liefert Informationen über die jeweilige SchaftFormFunktion, vergleichbar einem "About".
     */
    public abstract String getInfo();

    /**
     * Ermittelt den Durchmesser in Rinde in cm auf dvoMessHoehe_m.
     * 
     * @param messHoehe_m
     */
    public abstract double getDurchmesser_iR_cm(BaumschaftDefinition bsDef, double messHoehe_m);

    /**
     *  Ermittelt die Summe der beidseitigen Rindendicke in cm.
     * 
     * @param messHoehe_m
     */
    public abstract double getRindenDickeSummeBeidseitig_cm(BaumschaftDefinition bsDef, double messHoehe_m);

    /**
     * Ermittelt den Durchmesser ohne Rinde in cm, indem zuerst jener in Rinde, dann der Rindenabzug 
     * und schliesslich aus der Differenz der beiden der Durchmesser ohne Rinde ermittelt wird.
     * 
     * @param messHoehe_m
     */
	public double getDurchmesser_oR_cm(BaumschaftDefinition bsDef, double messHoehe_m) {
		return getDurchmesser_iR_cm(bsDef, messHoehe_m) - getRindenDickeSummeBeidseitig_cm(bsDef, messHoehe_m);
	}

    /**
     * Ermittelt die Höhe in Meter am Stamm, in der der angegebne Durchmesser in, resp. ohne Rinde auftritt.
     * Wird eine Toleranz > dem Zieldurchmesser angegeben, wird die Toleranz automatisch 
     * auf 1/100 des Zieldurchmessers gesetzt.
     * Die Höhe wird durch ... gesucht.
     * 
     * @param durchmesser_cm
     * @param durchmesserAngabeInRinde
     * @param toleranzHoehe_m
     */
	public double getHoehe_m(BaumschaftDefinition bsDef, double durchmesser_cm,	boolean bDurchmesserAngabeInRinde, double dToleranzHoehe_m) {			
		 // Zweck:
        // Ermittelt die Höhe in Meter am Stamm, in der der angegebne Durchmesser in, resp. ohne Rinde auftritt.
        // Wird eine Toleranz > dem Zieldurchmesser angegeben, wird die Toleranz automatisch 
        // auf 1/100 des Zieldurchmessers gesetzt.
        // Die Höhe wird durch ... gesucht.
        double dZielDrm = durchmesser_cm;
        double dHoehe_m = bsDef.getSchaftLaenge_m() / 2;
        double dHoeheAltOben_m = bsDef.getSchaftLaenge_m();
        double dHoeheAltUnten_m = 0;

        // Falls zur Messhöhe kein Wert übergeben wird, 
        // so wird nachfolgend ein bereits früher eingegebener Wert, allenfalls halt der Default zugewiesen.
        if (dToleranzHoehe_m == 0 || dToleranzHoehe_m == Double.MAX_VALUE) {
        	dToleranzHoehe_m = bsDef.getSchaftLaenge_m() / 3000;
        }

        double dDrm = bDurchmesserAngabeInRinde ? getDurchmesser_iR_cm(bsDef, 0) : getDurchmesser_oR_cm(bsDef, 0);
        boolean done = dZielDrm <= dDrm;
        if (done == false) {
        	return -1;
        }

        while(dHoeheAltOben_m - dHoeheAltUnten_m > dToleranzHoehe_m) {
        	dDrm = bDurchmesserAngabeInRinde ? getDurchmesser_iR_cm(bsDef, dHoehe_m) : getDurchmesser_oR_cm(bsDef, dHoehe_m);
        	double dAbweichung = dDrm - dZielDrm;
        	if (dAbweichung < 0) {
        		dHoeheAltOben_m = dHoehe_m;
        		dHoehe_m = (dHoehe_m + dHoeheAltUnten_m) / 2;
        	} else {
        		dHoeheAltUnten_m = dHoehe_m;
        		dHoehe_m = (dHoehe_m + dHoeheAltOben_m) / 2;
        	}
        }
		
		return dHoehe_m;
	}

    /**
     * Ermittelt das Volumen in m3 von einem Teil des Stammes oder des ganzen Stammes.
     * Mit bvoOHneRinde wird definiert, ob das Volumen in oder ohne Rinde ermittelt werden soll.
     * 
     * @param stueckBasisHoeheAmStamm_m
     * @param stueckEndHoeheAmStamm_m
     * @param messIntervall_m
     * @param ohneRinde
     */
	public double getStammStueckVolumen_m3(BaumschaftDefinition bsDef, double stueckBasisHoeheAmStamm_m, double stueckEndHoeheAmStamm_m, double messIntervall_m, boolean ohneRinde) {
		double result = -1;

        double dSumDrmInRindeQuadr = 0;
        double dSumDrmOhneRindeQuadr = 0;
        double dMIvall_m = 0.1;
        if (messIntervall_m > 0) {
        	dMIvall_m = messIntervall_m;
        }
        if (dMIvall_m < 0.001) {
        	dMIvall_m = 0.001;
        }
        double dStammStueckLaenge_m = stueckEndHoeheAmStamm_m - stueckBasisHoeheAmStamm_m;
        
        int lAnzMessSchritte = (int) (dStammStueckLaenge_m / dMIvall_m);  // gerundetes Ergebnis!
        int lObereForSchlaufenGrenze;
        if (lAnzMessSchritte%2 == 0) {
            lObereForSchlaufenGrenze = lAnzMessSchritte;
        } else {
            lObereForSchlaufenGrenze = lAnzMessSchritte + 1;
        }
        
        // Division durch 0 verhindern
        if (lObereForSchlaufenGrenze == 0) {
        	lObereForSchlaufenGrenze = 2;
        }
        
        // DurchmesserQuadratSummen berechnen
        for (int n=1; n<(lObereForSchlaufenGrenze/2); n++) {
            double dMesshoehe_m = stueckBasisHoeheAmStamm_m 
                         + (dStammStueckLaenge_m / lObereForSchlaufenGrenze) 
                         * ((2 * n) - 1);
            
            double dDrmInRi = getDurchmesser_iR_cm(bsDef, dMesshoehe_m);
            double dDrmOhRi = getDurchmesser_oR_cm(bsDef, dMesshoehe_m);

            dSumDrmInRindeQuadr = dSumDrmInRindeQuadr + dDrmInRi * dDrmInRi;
            dSumDrmOhneRindeQuadr = dSumDrmOhneRindeQuadr + dDrmOhRi * dDrmOhRi;
        }

        if (ohneRinde) {
            result = Math.PI / 4 * 2 
            		* dStammStueckLaenge_m / lObereForSchlaufenGrenze
            		* dSumDrmOhneRindeQuadr / 10000;
        } else {
            result = Math.PI / 4 * 2
            		* dStammStueckLaenge_m / lObereForSchlaufenGrenze 
            		* dSumDrmInRindeQuadr / 10000;
        }
                
        return result;
	}

    /**
     * Liefert eine Lister aller Baumartnamen, die in der jeweiligen Funktion verfügbar sind.
     * Die Baumartnamen werden in der aktuell selektierten Landessprache geliefert.
     */
    public Baumart[] getBaumartListe(){
    	return Baumart.values();    	
    }
    
    @Override
    public boolean equals(Object other) {
    	if (other == null) {
    		return false;
    	}
    	return (this.hashCode() == other.hashCode());
    }
    
    @Override
    public int hashCode() {
    	return this.getClass().getName().hashCode();
    }
}
