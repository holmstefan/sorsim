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

import ch.wsl.sustfor.baumschaft.lemm1991.BaumSchaftformFkt_Lemm1991;
import ch.wsl.sustfor.sorsim.controller.BaumlistenSortimentierer;
import ch.wsl.sustfor.sorsim.controller.Presenter;
import ch.wsl.sustfor.sorsim.model.SortimentsStueck;
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
	
	public SimpleWrapper() {
		//init default values
		baumlistSort.selectSchaftformFunktion(new BaumSchaftformFkt_Lemm1991());
	}
	
	public String[][] sortimentieren() {
        //Dateien laden
		try {
			readFileBaumschaefte();
			readFileSortimentsVorgaben();
		} catch (Exception e) {
			e.printStackTrace();
			return new String[][]{{"Fehler beim Laden der Dateien"}};
		}
		
		//Einstellungen
		CodeLaengenKlassen codeLaenge = CodeLaengenKlassen.L1;
		double dFuerSortimenteZuVerwendenderSchaftanteil = 1.0; 
		double dAbzopfDurchmesserOhneRinde_cm = -1;
		double dStockhoehe_cm = -1;
		
		//Sortimentieren
		baumlistSort.clearSortimentsStueckListe();
		baumlistSort.sortimentieren(
				codeLaenge,
				dFuerSortimenteZuVerwendenderSchaftanteil,
				dAbzopfDurchmesserOhneRinde_cm,
				dStockhoehe_cm
				);
		
		//Darstellung
		String[][] result = getZusammenfassungAs2dStringArray(baumlistSort.getSsListe());
		
		return result;
	}
	
	public double getAnzahlBaumschaefte() {
		try {
			readFileBaumschaefte();
		} catch (Exception e) {
			return -1;
		}
		return baumlistSort.getBsDefListe().size();
	}
	
	public double getAnzahlSortimentsvorgaben() {
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
		baumlistSort.getSvListe().setSortKriterium( SortierKriterium.WertProEinheit );
		baumlistSort.getSvListe().setSortOrder( SortOrder.Descending );
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

	public void setFileBaumliste(String fileBaumliste) {
		this.fileBaumliste = fileBaumliste;
	}

	public void setFileSortimentsVorgaben(String fileSortimentsVorgaben) {
		this.fileSortimentsVorgaben = fileSortimentsVorgaben;
	}
}
