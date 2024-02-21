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
package ch.wsl.sustfor.sorsim.controller;

import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition;
import ch.wsl.sustfor.sorsim.model.SortimentsStueck;
import ch.wsl.sustfor.sorsim.model.SortimentsStueck.ReststueckKategorie;
import ch.wsl.sustfor.sorsim.model.SortimentsStueck.SortimentsStueckBuilder;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsAushalteStrategie;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsStueckPositionierung;

/**
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public class SortimentStueckInBaumschaftEinpassen {

    private final BaumschaftDefinition bsDef;
    private SortimentsVorgabe sv;
    private SortimentsStueck ss;
    private static final int nxDrmDigits = 2; // Anzahl Nachkommastellen, auf die Durchmesserermittlungen_cm gerundet werden.    
    
    // Für Einpassen benötigte Variabeln:
    private static final double dHoehenToleranzDefault_m = 0.01;
    private static final double dHoehenToleranzMinimal_m = 0.0001;
    private double dMiDrmMinPos_m;
    private double dMiDrmMaxPos_m;
    private double dZopfDrmMinPos_m;
    private double dZopfDrmMaxPos_m;
    private double dMiDrmAktPos_m, dZopfDrmAktPos_m, dMiDrmAkt_cm, dZopfDrmAkt_cm;
    private double dLaengeAkt_m, dLaengeMaxMoeglich_m;
    private double dBasisAktPos_m, dSortimentsEndeMaxAmStamm_m;
    private boolean isMiDrmOk, isMiDrmZuGross, isMiDrmZuKlein;
    private boolean isZopfDrmOk, isZopfZuKlein, isZopfZuGross;
    private boolean isLaengeOk, isBasisOk;
    
    public SortimentStueckInBaumschaftEinpassen(BaumschaftDefinition bsDef, BaumSchaftformFunktion schaftformFunktion) {
		this.bsDef = bsDef;
		this.bsDef.selectSchaftfunktion(schaftformFunktion);
	}
    
    /**
     * @return error message or null, iff everything ok
     */
    public String sortimentEinpassen(Integer nextSortimentStueckId, ReststueckKategorie reststueckKategorie) {  
    	String result = null; //FIXME: achtung: rückgabewert null heisst alles ok!
    	
    	// Passt eine Sortimentsstück, das den Vorgaben sv entspricht 
        // in den definierten Schaft hinein und hält das Resultat in ss fest!

        // ACHTUNG:
        // Funktioniert nur, falls der Schaftdurchmesser von unten nach oben stetig abnimmt!
        // -> Ist das problematisch? -> Noch diskutieren! 
        //    Aufgrund von Erfahrungen mit Berechnungen für WEK ist diese Annahme leider z. T. problematisch, allerdings nur bei extremen Höhen, resp. H/D-Verhältnissen! 

    	//Korrektheit der Eingabedaten überprüfen
    	String fehlerMeldung = checkOK_Daten(); 
        if (fehlerMeldung != null) {
            return fehlerMeldung;
        }
    	
        calcMinMaxPositionsFromMinMaxDiameters();
        if ( ! checkOK_PositionenVonGrenzDurchmessern()) {
            return getGrenzDurchmesserListe();
        }
        dSortimentsEndeMaxAmStamm_m = Math.min(sv.getPositionAmStammOben_m(), dZopfDrmMinPos_m);
        
        // Nach welcher Strategie soll das Stück eingepasst werden?
        // Einpassen (s. auch frühere Lösungen!)
        switch (sv.getAushalteStrategie()) {
        case MaximalLaenge:
        	String einpassenMaxLaengeResult = einpassenMaximaleLaenge();
        	if (einpassenMaxLaengeResult != null) {
        		return einpassenMaxLaengeResult;
        	}
        	break;
        	
        case MinimalLaenge:
        	String einpassenMinLaengeResult = einpassenMinimaleLaenge();
        	if (einpassenMinLaengeResult != null) {
        		return einpassenMinLaengeResult;
        	}
        	break;
        	
        default:
        	throw new RuntimeException("This should never happen");
        }
        
        // Abschlussarbeiten (Sortiments- und Reststücke speichern, etc.)
        ermittelteWerteRegistrieren(dBasisAktPos_m, dLaengeAkt_m, nextSortimentStueckId, reststueckKategorie);
        
        return result;
    }

    /**
     * Überprüft Korrektheit der Eingabedaten
     * 
     * @return String mit Fehlermeldung, oder null, falls alles ok
     */
    private String checkOK_Daten() {
        // Prüfen ob alle Infos vorhanden sind
        boolean isMissingInformation = bsDef == null || sv == null;
        if (isMissingInformation) {
            return "szMissingInformation"; // Fehlermeldung
        }

        // Überprüfen min / max etc
        boolean isPositionenAmStammUngueltig = sv.getPositionAmStammOben_m() > bsDef.getSchaftLaenge_m();
        double dStammAbschnittLaenge_m = sv.getPositionAmStammOben_m() - sv.getPositionAmStammUnten_m();
        boolean isLaengeUngueltig = sv.getLaengeMin_m() > dStammAbschnittLaenge_m || sv.getLaengeMin_m() > sv.getLaengeMax_m();
        boolean isMittenDrmUngueltig = sv.getMittenDurchmMin_cm() > sv.getMittenDurchmMax_cm();
        boolean isZopfDrmUngueltig = sv.getZopfDurchmMin_cm() > sv.getZopfDurchmMax_cm();

        boolean isDatenUngueltig = isPositionenAmStammUngueltig || isLaengeUngueltig || isMittenDrmUngueltig || isZopfDrmUngueltig; // or ...
        if (isDatenUngueltig) {        	
        	String sProblemMeldung = ("szDatenUngueltig") + ": ";
        	if (isPositionenAmStammUngueltig) {
        		sProblemMeldung = sProblemMeldung + ("szPositionenAmStammUngueltig") + ", ";
        	}
        	if (isLaengeUngueltig) {
        		sProblemMeldung = sProblemMeldung + ("szLaengeUngueltig") + ", ";
        	}
        	if (isMittenDrmUngueltig) { 
        		sProblemMeldung = sProblemMeldung + ("szMittenDrmUngueltig") + ", ";
        	}
        	if (isZopfDrmUngueltig) { 
        		sProblemMeldung = sProblemMeldung + ("szZopfDrmUngueltig");
        	}
        	return sProblemMeldung;
        }
        
        //everything ok
        return null;
    }
    
    private void calcMinMaxPositionsFromMinMaxDiameters() {
        double dHoehenToleranz_m = Math.min(dHoehenToleranzDefault_m, sv.getLaengenIntervall_m());
        if (dHoehenToleranz_m < dHoehenToleranzMinimal_m) {
        	dHoehenToleranz_m = dHoehenToleranzMinimal_m;
        }

        dMiDrmMinPos_m = bsDef.getHoehe_m(sv.getMittenDurchmMin_cm(), false, dHoehenToleranz_m);
        dMiDrmMaxPos_m = bsDef.getHoehe_m(sv.getMittenDurchmMax_cm(), false, dHoehenToleranz_m);
        dZopfDrmMinPos_m = bsDef.getHoehe_m(sv.getZopfDurchmMin_cm(), false, dHoehenToleranz_m);
        dZopfDrmMaxPos_m = bsDef.getHoehe_m(sv.getZopfDurchmMax_cm(), false, dHoehenToleranz_m);

        // 23012006,ve Nachfolgende LaengenIntervallBereinigungen kommentarisiert, 
        // da sonst die Fixmasse für das Sortiment sich auch auf die Positionen am Stamm auswirken, 
        // was m. E. nicht nötig scheint (s. "Laufende Notizen zur Arbeit.doc" vom 23.01.2006).

        //dxMiDrmMinPos_m = XX_LaengenIntervallBereinigung(dvInput:=dxMiDrmMinPos_m, _
        //                                                 bvSchrittHalbieren:=True, _
        //                                                 bvZielwertMussKleinerSeinAlsInputwert:=True)
        //dxMiDrmMaxPos_m = XX_LaengenIntervallBereinigung(dvInput:=dxMiDrmMaxPos_m, _
        //                                                    bvSchrittHalbieren:=True)
        //dxZopfDrmMinPos_m = XX_LaengenIntervallBereinigung(dvInput:=dxZopfDrmMinPos_m, _
        //                                                   bvZielwertMussKleinerSeinAlsInputwert:=True)
        //dxZopfDrmMaxPos_m = XX_LaengenIntervallBereinigung(dvInput:=dxZopfDrmMaxPos_m)  
    }
    
    private boolean checkOK_PositionenVonGrenzDurchmessern() {
    	boolean isZopfDurchmesserBereichInnerhalbStammabschnitt = (dZopfDrmMaxPos_m <= sv.getPositionAmStammOben_m()) && (dZopfDrmMinPos_m >= sv.getPositionAmStammUnten_m());
    	boolean isMittenDurchmesserBereichInnerhalbStammabschnitt = (dMiDrmMaxPos_m <= sv.getPositionAmStammOben_m()) && (dMiDrmMinPos_m >= sv.getPositionAmStammUnten_m());

    	return (isZopfDurchmesserBereichInnerhalbStammabschnitt && isMittenDurchmesserBereichInnerhalbStammabschnitt);
    }
    
    private String getGrenzDurchmesserListe() {
    	return "szXXFehlerBeiPositionenVonGrenzDurchmessern" + "\n" + "\n" + 
    			"positionAmStammUnten_m = " + sv.getPositionAmStammUnten_m() + "\n" + 
    			"positionAmStammOben_m = " + sv.getPositionAmStammOben_m() + "\n" + 
    			"dZopfDrmMinPos_m = " + dZopfDrmMinPos_m + "\n" + 
    			"dZopfDrmMaxPos_m = " + dZopfDrmMaxPos_m + "\n" + 
    			"dMiDrmMinPos_m = " + dMiDrmMinPos_m + "\n" + 
    			"dMiDrmMaxPos_m = " + dMiDrmMaxPos_m;
    }
    
    /**
     * @return error message or null, iff everything ok
     */
    private String einpassenMaximaleLaenge() {
    	// Sortimentsdaten für grösstmögliche Länge von unten beginnend abgreifen ... und prüfen ...
    	ermittleGroesstMoeglicheLange();
    	if (laengeUndBasisPositionZuweisen(dLaengeMaxMoeglich_m) == false) {
    		return getProblemMeldung("Sortiment kann nicht erstellt werden! Sortimentsmasse können für verlangte Intervalle nicht gesetzt werden."); 
    	}
    	positionUndWertVonMiDrmUndZopfErmitteln();

    	if (isMiDrmOk && isZopfDrmOk) { // Länge muss Ok sein! Sortiment ist bereits wie gewünscht! -> abfertigen!
    		return null;
    		
    	} else if (isZopfZuKlein) {
    		if (sv.getPositionierung() == SortimentsStueckPositionierung.ZuOberst) {
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! ZopfDrm ist zu klein! SortimentsStück kann nicht nach oben verlängert oder geschoben werden.");
    		}
    		if (sortStueckBeiGegebenerBasisPosAufBestimmtenZopfDrmAusrichten(dZopfDrmMinPos_m, true)) {
    			return null; // Sortiment ist ok! -> abfertigen!
    		}

    		return getProblemMeldung("Sortiment kann nicht erstellt werden! ZopfDrm ist zu klein! SortimentsEnde oben kann nicht auf minimalen Zopf ausgerichtet werden. Prüfe, ob Kürzung oder weitere Verschiebung nach oben etwas bringen kann.");

    	} else if (isZopfZuGross) { //=> ZopfDrm muss zu gross sein, da dxSortimentsEndeMaxAmStamm_m so gesetzt, dass ZopfPos <= ZopfDrmMinPos_m!
    		if (dZopfDrmAktPos_m == dSortimentsEndeMaxAmStamm_m || sv.getPositionierung() == SortimentsStueckPositionierung.ZuUnterst) {
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! ZopfDrm ist zu gross! SortimentsStück kann nicht verlängert und nicht nach oben geschoben werden.");
    		}
    		if (sortStueckBeiBleibenderLaengeAufMaximalenZopfDrmAusrichten()) { 
    			return null; // Sortiment ist ok! -> abfertigen!
    		}
    		//  prüfen, weitere Verschiebung nach oben etwas bringen kann ...
    		//  Länge ist geblieben, Zopf sollte auf max. Zopf gesetzt worden sein, wobei das Stück nach oben verschoben wurde.
    		//  also kann nur noch der MiDrm nicht stimmen!
    		//  => Auf min. MiDrm ausrichten versuchen!
    		if (isMiDrmZuGross) {
    			if (sortStueckBeiBleibenderLaengeAufMaximalenMiDrmRespEndeMaxAmStammAusrichten()) { 
    				return null; // Sortiment ist ok! -> abfertigen!
    			}
    		}
    		return getProblemMeldung("Sortiment kann nicht erstellt werden! Länge muss zu kurz sein, reicht aber bereits bis ZopfDrmMinPos! Stück kann nicht verlängert und nicht nach oben geschoben werden!"); // Tritt diese Variante überhaupt auf?

    	} else if (isMiDrmZuKlein) {
    		if (sv.getPositionierung() == SortimentsStueckPositionierung.ZuOberst) {
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! MiDrm ist zu klein! SortimentsStück kann nicht nach unten verlängert oder geschoben werden.");
    		}

    		// -> Von oben kürzen ... bis MiDrm stimmt (=> minimal, oberste Position!)
    		if ( sortStueckBeiGegebenerBasisPosAufBestimmtenMiDrmAusrichten(dMiDrmMinPos_m, true) == false) {
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! SortimentsStück kann nicht auf minimalen MiDrm ausgerichtet werden. Länge wäre zu kurz oder Zopf zu gross!");
    		}
    		return null; // Sortiment ist wie gewünscht! -> abfertigen!

    	} else if (isMiDrmZuGross) {
    		if (sv.getPositionierung() == SortimentsStueckPositionierung.ZuUnterst) {
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! MiDrm ist zu gross, Länge max. und esspZuUnterst gewünscht! Stück kann nicht verlängert und nicht nach oben geschoben werden.");
    		}

    		// -> Wenn möglich Sortiment hinaufschieben; sonst von unten kürzen! 
    		// Ist Sortimentsende bei dxSortimentsEndeMaxAmStamm_m?
    		if (dZopfDrmAktPos_m == dSortimentsEndeMaxAmStamm_m) { // Wenn ja, von unten kürzen ...
    			if (sortStueckBeiGegebenerZopfPosAufBestimmtenMiDrmAusrichten(dMiDrmMaxPos_m, false)) { 
    				return null; // Sortiment ist ok! -> abfertigen!
    			}
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! SortimentsStück reicht bereits bis ZopfDrmMinPos! Die BasisPos ist ungültig oder die Länge zu kurz. Das Stück kann nicht weiter gekürzt werden!");

    		} else { // Wenn nein, Sortiment nach oben schieben ...
    			if (sortStueckBeiBleibenderLaengeAufMaximalenMiDrmRespEndeMaxAmStammAusrichten()) { 
    				return null; // Sortiment ist ok! -> abfertigen!
    			}
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! Der MiDrm ist zu gross und das SortimentsStück kann nicht auf den maximalen MiDrm ausgerichtet werden! (Länge zu kurz, reicht aber bereits bis ZopfDrmMinPos?)");
    		}
    	}
    	throw new RuntimeException("This should never happen");
    }
    
    /**
     * @return error message or null, iff everything ok
     */
    private String einpassenMinimaleLaenge() {
    	laengeUndBasisPositionZuweisen(sv.getLaengeMin_m());
    	positionUndWertVonMiDrmUndZopfErmitteln();

    	if (isMiDrmOk && isZopfDrmOk) { // Länge muss Ok sein!
    		return null; // Sortiment ist bereits wie gewünscht! -> abfertigen!

    	} else if ( ! isZopfDrmOk) { //=> ZopfDrm muss zu gross sein, da dxSortimentsEndeMaxAmStamm_m so gesetzt, dass ZopfPos <= ZopfDrmMinPos_m!
    		if (sv.getPositionierung() == SortimentsStueckPositionierung.ZuOberst) {
    			// Sortiment ist schon zu oberst angesetzt. ZopfPos kann nicht geändert werden!
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! Verlangt ist esspZuOberst! Die ZopfPos kann folglich nicht verändert werden!");

    		}
    		if (sv.getPositionierung() == SortimentsStueckPositionierung.ZuUnterst) {
    			// Versuchen Sortiment zu verlängern, bis Zopf stimmt!
    			if (sortStueckBeiGegebenerBasisPosAufBestimmtenZopfDrmAusrichten(dZopfDrmMaxPos_m, false)) { 
    				return null; // Sortiment ist ok! -> abfertigen!
    			}
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! Verlangt sind esasMinimaleLaenge und esspZuUnterst! Das SortimentsStck ist möglichst kurz gewählt, der ZopfDrm ist aber nicht Ok. Falls zu klein ist nichts zu machen (min Länge ist in diesem Fall bereits zu lang!). Andernfalls müsste der ZopfDrm auf den Maximalwert gesetzt werden. Dies ist aber fehlgeschlagen!");
    		}

    		// esspIrgendwo ...
    		// Versuchen Sortiment nach oben zu schieben, bis Zopf stimmt (= maximal, da bisher zu gross)!
    		if (sortStueckBeiBleibenderLaengeAufMaximalenZopfDrmAusrichten()) { 
    			return null; // Sortiment ist ok! -> abfertigen!
    		}

    		if (isMiDrmZuKlein) { // ! bxDone und bxMiDrmZuKlein => Sortiment verlängern!
    			if (sortStueckBeiGegebenerZopfPosAufBestimmtenMiDrmAusrichten(dMiDrmMinPos_m, false)) {
    				return null; // Sortiment ist ok! -> abfertigen!
    			}
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! Verlangt sind esasMinimaleLaenge und esspIrgendwo. Gleichzeitige Ausrichtung auf gültigen Zopf (zumindest maximal) und gültigen MiDrm (zumindest minimal) sind nicht möglich.");
    		}

    	} else if (isMiDrmZuKlein) {
    		if (sv.getPositionierung() != SortimentsStueckPositionierung.ZuOberst) {
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! Verlangt sind esasMinimaleLaenge und esspZuUnterst oder esspIrgendwo. Trotzdem ist  bxMiDrmZuKlein! Das Stück kann nicht mehr nach unten geschoben oder von oben gekürzt werden!");
    		}

    		// -> Nach unten verlängern ... bis min. MiDrm erreicht ist!
    		if (sortStueckBeiGegebenerZopfPosAufBestimmtenMiDrmAusrichten(dMiDrmMinPos_m, false)) { 
    			return null; // Sortiment ist ok! -> abfertigen!
    		}
    		return getProblemMeldung("Sortiment kann nicht erstellt werden! Verlangt sind esasMinimaleLaenge und esspZuOberst. Es ist bxMiDrmZuKlein und  XX_SortStueckBeiGegebenerZopfPosAufMinimalenMiDrmAusrichten hat fehlgeschlagen.");

    	} else if (isMiDrmZuGross) {
    		if (sv.getPositionierung() == SortimentsStueckPositionierung.ZuOberst) {
    			return getProblemMeldung("Sortiment kann nicht erstellt werden!  Verlangt sind esasMinimaleLaenge und esspZuOberst. Es ist bxMiDrmZuGross! Stück kann nicht gekürzt oder nach oben geschoben werden!");
    		}
    		// -> Wenn möglich Sortiment hinaufschieben; sonst nach oben verlängern! 
    		// Ist Sortimentsende bei dxSortimentsEndeMaxAmStamm_m?
    		if (sv.getPositionierung() == SortimentsStueckPositionierung.ZuUnterst) {
    			// -> Sortiment nach oben verlängern! 
    			if (sortStueckBeiGegebenerBasisPosAufBestimmtenMiDrmAusrichten(dMiDrmMaxPos_m, false)) { 
    				return null; // Sortiment ist ok! -> abfertigen!
    			}
    			return getProblemMeldung("Sortiment kann nicht erstellt werden! Verlangt sind esasMinimaleLaenge und esspZuUnterst. Es ist bxMiDrmZuGross! XX_SortStueckBeiGegebenerBasisPosAufMaximalenMiDrmAusrichten (=verlängern) hat fehlgeschlagen.");
    		}

    		// esspIrgendwo -> Wenn möglich Sortiment hinaufschieben
    		if (sortStueckBeiBleibenderLaengeAufMaximalenMiDrmRespEndeMaxAmStammAusrichten()) { 
    			return null; // Sortiment ist ok! -> abfertigen!
    		}
    		return getProblemMeldung("Sortiment kann nicht erstellt werden! Verlangt sind esasMinimaleLaenge und esspIrgendwo. Es ist bxMiDrmZuGross! XX_SortStueckBeiBleibenderLaengeAufMaximalenMiDrmRespEndeMaxAmStammAusrichten (=verschieben) hat fehlgeschlagen.");

    	} else {
    		return getProblemMeldung("Sortiment kann nicht erstellt werden! Verlangt sind esasMinimaleLaenge und esspIrgendwo. - bxMiDrmZuGross! XX_SortStueckBeiBleibenderLaengeAufMaximalenMiDrmRespEndeMaxAmStammAusrichten hat fehlgeschlagen.");
    	}
    	return null;
    }
    
    private void ermittleGroesstMoeglicheLange() {    	
    	double dStammAbschnittLaenge_m = sv.getPositionAmStammOben_m() - sv.getPositionAmStammUnten_m();    	
    	double dLaengeMaxWennZopfMinimal_m = this.dZopfDrmMinPos_m - sv.getPositionAmStammUnten_m();
    	
    	this.dLaengeMaxMoeglich_m = Math.min(sv.getLaengeMax_m(), Math.min(dStammAbschnittLaenge_m, dLaengeMaxWennZopfMinimal_m));
    	this.dLaengeMaxMoeglich_m = this.laengenIntervallBereinigung(dLaengeMaxMoeglich_m, false, true, 0.0);    	
    	
//    	boolean bLaengeMaxMoeglichIstZuKurz = this.dxLaengeMaxMoeglich_m < svx.getLaengeMin_m();
    }
    
    /**
     * @param dLaenge
     * @return true wenn erfolgreich, false bei fehler
     */
    private boolean laengeUndBasisPositionZuweisen(double dLaenge) {
    	if (sv.getAushalteStrategie() == SortimentsAushalteStrategie.MaximalLaenge) {
    		this.dLaengeAkt_m = this.laengenIntervallBereinigung(dLaenge, false, true, 0.0);
    	}
    	else {
    		this.dLaengeAkt_m = this.laengenIntervallBereinigung(dLaenge, false, false, 0.0);
    	}

    	if (sv.getPositionierung() == SortimentsStueckPositionierung.ZuOberst) {
    		this.dBasisAktPos_m = this.dSortimentsEndeMaxAmStamm_m - this.dLaengeAkt_m;
    	}
    	else { // .ZuUnterst oder .Irgendwo
    		this.dBasisAktPos_m = sv.getPositionAmStammUnten_m();
    	} 

    	checkLaengeOk();            
    	isBasisOk = (dBasisAktPos_m >= sv.getPositionAmStammUnten_m()) && (dBasisAktPos_m <= sv.getPositionAmStammOben_m());            
    	return isLaengeOk && isBasisOk;
    }
    
    /**
     * @return true wenn erfolgreich, false bei fehler
     */
    private boolean positionUndWertVonMiDrmUndZopfErmitteln() {    	
        dMiDrmAktPos_m = dBasisAktPos_m + dLaengeAkt_m / 2;
        dMiDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dMiDrmAktPos_m), nxDrmDigits);
        checkMiDrmOk();

        dZopfDrmAktPos_m = dBasisAktPos_m + dLaengeAkt_m;
        dZopfDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dZopfDrmAktPos_m), nxDrmDigits);
        checkZopfDrmOk();

        return isMiDrmOk && isZopfDrmOk;
    }
    
    /**
     * @return true wenn erfolgreich, false bei fehler
     */
    private boolean sortStueckBeiGegebenerBasisPosAufBestimmtenZopfDrmAusrichten(double dZopfDrmPos_m, boolean isZopfDrmMin) {
        dZopfDrmAktPos_m = laengenIntervallBereinigung(dZopfDrmPos_m, false, isZopfDrmMin, sv.getPositionAmStammUnten_m());
        dZopfDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dZopfDrmAktPos_m), nxDrmDigits);
        checkZopfDrmOk();

        dLaengeAkt_m = dZopfDrmAktPos_m - dBasisAktPos_m;
        dLaengeAkt_m = round(dLaengeAkt_m, 8);
    	checkLaengeOk();

        dMiDrmAktPos_m = dBasisAktPos_m + dLaengeAkt_m / 2;
        dMiDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dMiDrmAktPos_m), nxDrmDigits);
        checkMiDrmOk();

        return isMiDrmOk && isZopfDrmOk && isLaengeOk;
    }
    
    /**
     * @return true wenn erfolgreich, false bei fehler
     */
    private boolean sortStueckBeiBleibenderLaengeAufMaximalenZopfDrmAusrichten() {
        dZopfDrmAktPos_m = dZopfDrmMaxPos_m;
        dZopfDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dZopfDrmAktPos_m), nxDrmDigits);

        isZopfDrmOk = dZopfDrmAktPos_m <= dSortimentsEndeMaxAmStamm_m;
        dMiDrmAktPos_m = dZopfDrmAktPos_m - dLaengeAkt_m / 2;
        dMiDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dMiDrmAktPos_m), nxDrmDigits);
        dBasisAktPos_m = dZopfDrmAktPos_m - dLaengeAkt_m;
    	checkLaengeOk();
        checkMiDrmOk();
        return isMiDrmOk && isZopfDrmOk && isLaengeOk;
    }
    
    /**
     * @return true wenn erfolgreich, false bei fehler
     */
    private boolean sortStueckBeiBleibenderLaengeAufMaximalenMiDrmRespEndeMaxAmStammAusrichten() {
    	boolean isSortimentsEndeZuWeitOben;
    	
        dMiDrmAktPos_m = dMiDrmMaxPos_m;
        dMiDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dMiDrmAktPos_m), nxDrmDigits);

        checkMiDrmOk();

        // Ist bei neuer Lage mit LaengeAkt immer noch dxSortimentsEndeMaxAmStamm_m erfüllt?
        dZopfDrmAktPos_m = dMiDrmAktPos_m + dLaengeAkt_m / 2;

        // ZopfDrmPos auf PositionsGrenzen am Schaft prüfen
        isSortimentsEndeZuWeitOben = dZopfDrmAktPos_m > dSortimentsEndeMaxAmStamm_m;
        if (isSortimentsEndeZuWeitOben) {
            dZopfDrmAktPos_m = laengenIntervallBereinigung(dSortimentsEndeMaxAmStamm_m, false, true, sv.getPositionAmStammUnten_m());
        }

        dZopfDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dZopfDrmAktPos_m), nxDrmDigits);
        checkZopfDrmOk();
        dLaengeAkt_m = 2 * (dZopfDrmAktPos_m - dMiDrmAktPos_m);
        dLaengeAkt_m = round(dLaengeAkt_m, 8);
        dBasisAktPos_m = dZopfDrmAktPos_m - dLaengeAkt_m;
        isBasisOk = true; // Hinweis: dxBasisAktPos_m muss gültig sein, da Sortiment nur noch gekürzt 
        //                           und so die Basis nach oben geschoben wurde, also das Minimum sicher erfüllt bleibt!
    	checkLaengeOk(); // eigentlich nur zu kurz möglich!

    	return isMiDrmOk && isZopfDrmOk && isLaengeOk && isBasisOk;
    }
    
    /**
     * @return true wenn erfolgreich, false bei fehler
     */
    private boolean sortStueckBeiGegebenerBasisPosAufBestimmtenMiDrmAusrichten(double dMiDrmPos_m, boolean isMiDrmMin) {    	
    	dMiDrmAktPos_m = laengenIntervallBereinigung(dMiDrmPos_m, true, isMiDrmMin, sv.getPositionAmStammUnten_m());    	
    	dMiDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dMiDrmAktPos_m), nxDrmDigits);    	
    	dLaengeAkt_m = 2 * (dMiDrmAktPos_m - dBasisAktPos_m);    	
    	dLaengeAkt_m = round(dLaengeAkt_m, 8);    	
    	// ACHTUNG: Korrektur eingefügt 03022006,ve    	
    	if ((dBasisAktPos_m + dLaengeAkt_m) > bsDef.getSchaftLaenge_m()) {
        	dLaengeAkt_m = bsDef.getSchaftLaenge_m() - dBasisAktPos_m;
        	dMiDrmAktPos_m = dBasisAktPos_m + 0.5 * dLaengeAkt_m;
        	dMiDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dMiDrmAktPos_m), nxDrmDigits);	
    	}

    	checkLaengeOk();
        checkMiDrmOk();

    	dZopfDrmAktPos_m = dBasisAktPos_m + dLaengeAkt_m;
    	dZopfDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dZopfDrmAktPos_m), nxDrmDigits);
        checkZopfDrmOk();

    	return isMiDrmOk && isZopfDrmOk && isLaengeOk;
    }
    
    /**
     * @return true wenn erfolgreich, false bei fehler
     */
    private boolean sortStueckBeiGegebenerZopfPosAufBestimmtenMiDrmAusrichten(double dMiDrmPos_m, boolean isMiDrmMin) {
    	if (isMiDrmMin) {  //-> Korrektur Richtung Schaftbasis!
    		dMiDrmAktPos_m = laengenIntervallBereinigung(dMiDrmPos_m, true, true, sv.getPositionAmStammUnten_m());
    	}
    	else {
    		dMiDrmAktPos_m = laengenIntervallBereinigung(dMiDrmPos_m, true, false, sv.getPositionAmStammUnten_m());
    	}
    	dMiDrmAkt_cm = round(bsDef.getDurchmesser_oR_cm(dMiDrmAktPos_m), nxDrmDigits);
        checkMiDrmOk();
    	dLaengeAkt_m = 2 * (dZopfDrmAktPos_m - dMiDrmAktPos_m);
    	dLaengeAkt_m = round(dLaengeAkt_m, 8);
    	checkLaengeOk();
    	dBasisAktPos_m = dZopfDrmAktPos_m - dLaengeAkt_m;
    	isBasisOk = dBasisAktPos_m >= sv.getPositionAmStammUnten_m();
    	return isMiDrmOk && isZopfDrmOk && isLaengeOk && isBasisOk;
    }
    
    private double laengenIntervallBereinigung(double dInput, boolean isSchrittHalbieren, boolean isZielwertMussKleinerSeinAlsInputwert, double basis) {
    	// Gibt das nächste Vielfache von svx.LaengenIntervall_m zurück, 
    	// das gleich oder grösser, resp. falls bvZielwertMussKleinerSeinAlsInputwert, kleiner ist als dvInput (Math.Ceiling)! 

    	if (sv.getLaengenIntervall_m() >= dHoehenToleranzMinimal_m) { // ACHTUNG: Das Längenintervall wird nur beachtet, falls es >= 0.0001m ist!		
    		// Basis (z.B. PosAmStammUnten) muss erst abgezogen, dann (am Schluss) wieder hinzugefügt werden,
    		//  damit die Längenintervall die PosAmStammUnten als Basis haben, und nicht Baumhöhe 0!
    		dInput -= basis;
    		
    		double dFaktor = 1;
    		if (isSchrittHalbieren) {
    			dFaktor = 0.5;
    		}
    		double dSchrittLaenge_m = sv.getLaengenIntervall_m() * dFaktor;
    		double d = Math.ceil(dInput / dSchrittLaenge_m);
    		double dResult = d * dSchrittLaenge_m;

    		if (isZielwertMussKleinerSeinAlsInputwert && dResult > dInput) {
    			dResult = dResult - dSchrittLaenge_m;
    		}
    		
    		// Basis (z.B. PosAmStammUnten) muss erst abgezogen, dann (am Schluss) wieder hinzugefügt werden,
    		//  damit die Längenintervall die PosAmStammUnten als Basis haben, und nicht Baumhöhe 0!
    		dResult += basis;
    		
    		return dResult;
    	}
    	return dInput;
    }
    
    private void ermittelteWerteRegistrieren(double dBasisPos_m, double dLaenge_m, Integer nextSortimentStueckId, ReststueckKategorie reststueckKategorie) {
        double dMiDrmPos_m = dBasisPos_m + 0.5 * dLaenge_m;
        double dZopfDrmPos_m = dBasisPos_m + dLaenge_m;

        SortimentsStueckBuilder builder = new SortimentsStueckBuilder();
        if (nextSortimentStueckId != null) {
        	builder.setId(nextSortimentStueckId);
        }
        builder.setBsDef(bsDef);

        builder.setPositionDerBasisAmStamm_m(dBasisPos_m);
        builder.setLaenge_m(dLaenge_m);
              
        builder.setBasisDrmIR_cm(bsDef.getDurchmesser_iR_cm(dBasisPos_m));
        builder.setBasisDrmOR_cm(bsDef.getDurchmesser_oR_cm(dBasisPos_m));
        
        builder.setMittenDrmIR_cm(bsDef.getDurchmesser_iR_cm(dMiDrmPos_m));
        builder.setMittenDrmOR_cm(bsDef.getDurchmesser_oR_cm(dMiDrmPos_m));
        
        builder.setZopfDrmIR_cm(bsDef.getDurchmesser_iR_cm(dZopfDrmPos_m));
        builder.setZopfDrmOR_cm(bsDef.getDurchmesser_oR_cm(dZopfDrmPos_m));
        
        builder.setVolumenRestStueckUntenOR_m3(bsDef.getStammStueckVolumen_m3(sv.getPositionAmStammUnten_m(), dBasisPos_m, 0.1, true));
        
        boolean isRestStueckObenBisGanzeSchaftlaengeErmitteln = true;
        if (isRestStueckObenBisGanzeSchaftlaengeErmitteln) {
        	builder.setVolumenRestStueckObenOR_m3(bsDef.getStammStueckVolumen_m3(dZopfDrmPos_m, bsDef.getSchaftLaenge_m(), 0.1, true));
        }
        else { //RestStueckOben nur bis svx.PositionAmStammOben_m ermitteln
        	builder.setVolumenRestStueckObenOR_m3(bsDef.getStammStueckVolumen_m3(dZopfDrmPos_m, sv.getPositionAmStammOben_m(), 0.1, true));
        }

        builder.setSortimentsVorgabe(sv);
        
        if (reststueckKategorie != null) {
        	builder.setReststueckKategorie(reststueckKategorie);
        }
        
    	ss = builder.build();
    }
    
    private void checkMiDrmOk() {
        isMiDrmZuGross = dMiDrmAkt_cm >= sv.getMittenDurchmMax_cm();
        isMiDrmZuKlein = dMiDrmAkt_cm < sv.getMittenDurchmMin_cm();
        isMiDrmOk = ( ! isMiDrmZuGross) && ( ! isMiDrmZuKlein);    	
    }
    
    private void checkZopfDrmOk() {
        isZopfZuKlein = dZopfDrmAkt_cm < sv.getZopfDurchmMin_cm();
        isZopfZuGross = dZopfDrmAkt_cm > sv.getZopfDurchmMax_cm();
        isZopfDrmOk = ( ! isZopfZuKlein) &&  ( ! isZopfZuGross);    	
    }
    
    private void checkLaengeOk() {
        boolean bLaengeZuLang = dLaengeAkt_m > sv.getLaengeMax_m();
        boolean bLaengeZuKurz = dLaengeAkt_m < sv.getLaengeMin_m();
        isLaengeOk = ( ! bLaengeZuKurz) && ( ! bLaengeZuLang);    	
    }
    
    private static double round(double value, int digits) {
    	double factor = Math.pow(10, digits);    	
    	return Math.round(value * factor) / factor;  
    }
    
    private String getProblemMeldung(String szMeldungsString) {
    	StringBuilder sb = new StringBuilder();

        if (isMiDrmZuGross) {
        	sb.append("szMiDrmZuGross, ");
        }
        if (isMiDrmZuKlein) {
        	sb.append("szMiDrmZuKlein, ");
        	}
        if (isZopfZuGross) {
        	sb.append("szZopfDrmZuGross, ");
        	}
        if (isZopfZuKlein) {
        	sb.append("szZopfDrmZuKlein, ");
        	}
        if ( ! isLaengeOk) {
        	sb.append("szLaengeNichtOk");
        }

        return szMeldungsString + "\n" + "(" + sb.toString() + ")";
    }
    
    public SortimentsStueck getSortimentStueck() {
    	return this.ss;
    }
    
    public BaumschaftDefinition getSchaftDefinition() {
    	return this.bsDef;
    }
    
    public SortimentsVorgabe getSortimentsVorgaben() {
    	return this.sv;
    }
    
    public void setSortimentsVorgaben(SortimentsVorgabe sortimentsVorgaben) {
    	this.sv = sortimentsVorgaben;
    }
}
