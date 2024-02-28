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
package ch.wsl.sustfor.sorsim.wrapper;

import java.io.IOException;
import java.util.List;

import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.baumschaft.lemm1991.BaumSchaftformFkt_Lemm1991;
import ch.wsl.sustfor.baumschaft.lfispline.BaumschaftformFkt_Lfi_Spline;
import ch.wsl.sustfor.sorsim.controller.BaumlistenSortimentierer;
import ch.wsl.sustfor.sorsim.controller.Presenter;
import ch.wsl.sustfor.sorsim.model.SortimentsStueck;
import ch.wsl.sustfor.sorsim.model.D7mBhdRelations.Standort;
import ch.wsl.sustfor.sorsim.model.SortimentVorgabenListe.SortierKriterium;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.CodeLaengenKlassen;
import ch.wsl.sustfor.util.SortedList.SortOrder;

/**
 * 
 * @author Stefan Holm
 *
 */
public class SimpleWrapper {

    private final BaumlistenSortimentierer baumlistSort = new BaumlistenSortimentierer();
    private final Presenter presenter = new Presenter();
    
    private String fileBaumliste = "Baumliste.csv";
    private String fileSortimentsVorgaben = "SortimentsVorgabenListe.csv";
    private String fileOutput = "Output.csv";
    
    private int zuVerwendenderschaftanteil = 100;
    private CodeLaengenKlassen codeLaengenKlassen = CodeLaengenKlassen.L1;
    private BaumSchaftformFunktion schaftformFunktion = new BaumSchaftformFkt_Lemm1991();
    
	
	public String[][] makeAssortments() {
		baumlistSort.selectSchaftformFunktion(schaftformFunktion);
		
        // Dateien laden
		try {
			readFileBaumschaefte();
			readFileSortimentsVorgaben();
		} catch (Exception e) {
			e.printStackTrace();
			return new String[][]{{"Fehler beim Laden der Dateien"}};
		}
		
		// D7m berechnen, falls nötig
		if (schaftformFunktion instanceof BaumschaftformFkt_Lfi_Spline) {
			baumlistSort.getBsDefListe().forEach(bsDef -> bsDef.autoCalcD7mIfZero(Standort.Gut));
		}
		
		// Einstellungen
		double dFuerSortimenteZuVerwendenderSchaftanteil = zuVerwendenderschaftanteil / 100d; 
		double dAbzopfDurchmesserOhneRinde_cm = -1;
		double dStockhoehe_cm = -1;
		
		// Sortimentieren
		baumlistSort.clearSortimentsStueckListe();
		baumlistSort.sortimentieren(
				codeLaengenKlassen,
				dFuerSortimenteZuVerwendenderSchaftanteil,
				dAbzopfDurchmesserOhneRinde_cm,
				dStockhoehe_cm
				);
		
		// Output-Datei speichern
		if (fileOutput != null) {
			presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), fileOutput);
		}
		
		// Darstellung
		String[][] result = getZusammenfassungAs2dStringArray(baumlistSort.getSsListe());
		
		return result;
	}
	
	public int getNumberOfTrees() {
		try {
			readFileBaumschaefte();
		} catch (Exception e) {
			return -1;
		}
		return baumlistSort.getBsDefListe().size();
	}
	
	public int getNumberOfAssortmentSpecifications() {
		try {
			readFileSortimentsVorgaben();
		} catch (Exception e) {
			return -1;
		}
		return baumlistSort.getSvListe().size();
	}
	
	private void readFileBaumschaefte() throws NumberFormatException, IOException {
        presenter.readBaumschaefteFromFile(baumlistSort.getBsDefListe(), fileBaumliste);
	}
	
	private void readFileSortimentsVorgaben() throws IOException, NumberFormatException {
        //Sortimentvorgaben lesen
		baumlistSort.clearSortimentsStueckListe();
        presenter.readSortimentsVorgabenFromFile(baumlistSort.getSvListe(), fileSortimentsVorgaben);

		//Sortimentsvorgaben sortieren
		baumlistSort.getSvListe().setSortKriterium(SortierKriterium.WertProEinheit);
		baumlistSort.getSvListe().setSortOrder(SortOrder.Descending);
		baumlistSort.getSvListe().sort();
	}
	
	private String[][] getZusammenfassungAs2dStringArray(List<SortimentsStueck> ssListe) {
       	//get summary and split into lines
		String zusFas = presenter.printSortimentsStueckListeZusammenfassung(ssListe);
		String[] allLines = zusFas.split("\n");
		
		//create result
		String[][] result = new String[allLines.length][];
		for (int i=0; i<allLines.length; i++) {
			result[i] = allLines[i].split("\t");
		}
		return result;
	}

	public void setFileTreeList(String path) {
		this.fileBaumliste = path;
	}

	public void setFileAssortmentSpecifications(String path) {
		this.fileSortimentsVorgaben = path;
	}

	public void setFileOutput(String path) {
		this.fileOutput = path;
	}
	
	
	/**
	 * 
	 * @param value Value is rounded to the closest <code>int</code>.
	 */
	public void setBolePercentage_pct(double value) {
		int intValue = round(value);
		this.zuVerwendenderschaftanteil = intValue;
	}
	
	/**
	 * 
	 * @param value L1=1, L2=2, L3=3, L1+L2=4, L2+L3=5, L1+L2+L3=6, residual timber=7 
	 */
	public void setCombinationOfLengthClasses_category(double value) {
		int intValue = round(value);
		
		this.codeLaengenKlassen = switch(intValue) {
			case 1 -> CodeLaengenKlassen.L1;
			case 2 -> CodeLaengenKlassen.L2;
			case 3 -> CodeLaengenKlassen.L3;
			case 4 -> CodeLaengenKlassen.L1L2;
			case 5 -> CodeLaengenKlassen.L2L3;
			case 6 -> CodeLaengenKlassen.L1L2L3;
			case 7 -> CodeLaengenKlassen.Restholz;
			default -> throw new IllegalArgumentException("value must be in [1-7]");
		};
	}
	
	/**
	 * 
	 * @param value Lemm1991=1, LFI-Splines=2
	 */
	public void setStemFormFunction_category(double value) {
		int intValue = round(value);
		
		this.schaftformFunktion = switch(intValue) {
			case 1 -> new BaumSchaftformFkt_Lemm1991();
			case 2 -> new BaumschaftformFkt_Lfi_Spline();
			default -> throw new IllegalArgumentException("value must be in [1,2]");
		};
	}
	
	private static int round(double value) {
		long longValue = Math.round(value);
	    
        if ((int)longValue != longValue) {
            throw new ArithmeticException("integer overflow: " + longValue);
        }
        return (int)longValue;
	}
}
