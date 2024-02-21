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
package ch.wsl.sustfor.sorsim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.baumschaft.lemm1991.BaumSchaftformFkt_Lemm1991;
import ch.wsl.sustfor.baumschaft.lfispline.BaumschaftformFkt_Lfi_Spline;
import ch.wsl.sustfor.sorsim.controller.BaumlistenSortimentierer;
import ch.wsl.sustfor.sorsim.controller.Presenter;
import ch.wsl.sustfor.sorsim.model.D7mBhdRelations.Standort;
import ch.wsl.sustfor.sorsim.model.SortimentVorgabenListe.SortierKriterium;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.CodeLaengenKlassen;
import ch.wsl.sustfor.util.SortedList.SortOrder;

/**
 * 
 * @author Stefan Holm
 *
 */
public class SortimentStueckListeTest {
	
	/**
	 * Test with standard configuration.
	 */
	@Test
	public void testCase01() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase01/Input-Baumliste.csv", "testdata/testcase01/Input-SortimentsVorgabenListe.csv");
		
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
		
		//save file
		String actualOutputFile = "testdata/testcase01/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase01/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with BaumschaftformFkt_Lfi_Spline.
	 */
	@Test
	public void testCase02() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumschaftformFkt_Lfi_Spline());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase02/Input-Baumliste.csv", "testdata/testcase02/Input-SortimentsVorgabenListe.csv");
		
		//D7m berechnen, falls nötig
		baumlistSort.getBsDefListe().forEach(bsDef -> bsDef.autoCalcD7mIfZero(Standort.Gut));
		
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
		
		//save file
		String actualOutputFile = "testdata/testcase02/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase02/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with SortimentsVorgabenListe_ZuOberst.
	 */
	@Test
	public void testCase03() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase03/Input-Baumliste.csv", "testdata/testcase03/Input-SortimentsVorgabenListe_ZuOberst.csv");
		
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
		
		//save file
		String actualOutputFile = "testdata/testcase03/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase03/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with Schaftanteil 60%
	 */
	@Test
	public void testCase04() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase04/Input-Baumliste.csv", "testdata/testcase04/Input-SortimentsVorgabenListe.csv");
		
		//Einstellungen
		CodeLaengenKlassen codeLaenge = CodeLaengenKlassen.L1;
		double dFuerSortimenteZuVerwendenderSchaftanteil = 0.6; 
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
		
		//save file
		String actualOutputFile = "testdata/testcase04/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase04/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with Mindestzopf 12 cm.
	 */
	@Test
	public void testCase05() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase05/Input-Baumliste.csv", "testdata/testcase05/Input-SortimentsVorgabenListe.csv");
		
		//Einstellungen
		CodeLaengenKlassen codeLaenge = CodeLaengenKlassen.L1;
		double dFuerSortimenteZuVerwendenderSchaftanteil = 1; 
		double dAbzopfDurchmesserOhneRinde_cm = 12;
		double dStockhoehe_cm = -1;
		
		//Sortimentieren
		baumlistSort.clearSortimentsStueckListe();
		baumlistSort.sortimentieren(
				codeLaenge,
				dFuerSortimenteZuVerwendenderSchaftanteil,
				dAbzopfDurchmesserOhneRinde_cm,
				dStockhoehe_cm
				);
		
		//save file
		String actualOutputFile = "testdata/testcase05/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase05/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with Stockhöhe 80 cm.
	 */
	@Test
	public void testCase06() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase06/Input-Baumliste.csv", "testdata/testcase06/Input-SortimentsVorgabenListe.csv");
		
		//Einstellungen
		CodeLaengenKlassen codeLaenge = CodeLaengenKlassen.L1;
		double dFuerSortimenteZuVerwendenderSchaftanteil = 1; 
		double dAbzopfDurchmesserOhneRinde_cm = -1;
		double dStockhoehe_cm = 80;
		
		//Sortimentieren
		baumlistSort.clearSortimentsStueckListe();
		baumlistSort.sortimentieren(
				codeLaenge,
				dFuerSortimenteZuVerwendenderSchaftanteil,
				dAbzopfDurchmesserOhneRinde_cm,
				dStockhoehe_cm
				);
		
		//save file
		String actualOutputFile = "testdata/testcase06/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase06/Output-Sortimentsliste.csv", actualOutputFile);

		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with CodeLaengenKlassen.Restholz und Mindestzopf 7 cm und SortimentsVorgabenListe_ZuOberst.
	 */
	@Test
	public void testCase07() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase07/Input-Baumliste.csv", "testdata/testcase07/Input-SortimentsVorgabenListe_ZuOberst.csv");
		
		//Einstellungen
		CodeLaengenKlassen codeLaenge = CodeLaengenKlassen.Restholz;
		double dFuerSortimenteZuVerwendenderSchaftanteil = 1; 
		double dAbzopfDurchmesserOhneRinde_cm = 7;
		double dStockhoehe_cm = -1;
		
		//Sortimentieren
		baumlistSort.clearSortimentsStueckListe();
		baumlistSort.sortimentieren(
				codeLaenge,
				dFuerSortimenteZuVerwendenderSchaftanteil,
				dAbzopfDurchmesserOhneRinde_cm,
				dStockhoehe_cm
				);
		
		//save file
		String actualOutputFile = "testdata/testcase07/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase07/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with Baumliste01015000_BC_Neu (large).
	 */
	@Test
	public void testCase08() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase08/Input-Baumliste01015000_BC_Neu.csv", "testdata/testcase08/Input-SortimentsVorgabenListe.csv");
		
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
		
		//save file
		String actualOutputFile = "testdata/testcase08/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase08/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with SortimentsVorgabenListeWertvolleFichte.
	 */
	@Test
	public void testCase09() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase09/Input-Baumliste.csv", "testdata/testcase09/Input-SortimentsVorgabenListeWertvolleFichte.csv");
		
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
		
		//save file
		String actualOutputFile = "testdata/testcase09/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase09/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with BaumlisteLaub.
	 */
	@Test
	public void testCase10() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase10/Input-BaumlisteLaub.csv", "testdata/testcase10/Input-SortimentsVorgabenListe.csv");
		
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
		
		//save file
		String actualOutputFile = "testdata/testcase10/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase10/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with BaumlisteLaubNadel.
	 */
	@Test
	public void testCase11() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase11/Input-BaumlisteLaubNadel.csv", "testdata/testcase11/Input-SortimentsVorgabenListe.csv");
		
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
		
		//save file
		String actualOutputFile = "testdata/testcase11/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase11/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with SortimentsVorgabenListeLaengenzugabePrz.
	 */
	@Test
	public void testCase12() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase12/Input-Baumliste.csv", "testdata/testcase12/Input-SortimentsVorgabenListeLaengenzugabePrz.csv");
		
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
		
		//save file
		String actualOutputFile = "testdata/testcase12/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase12/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	/**
	 * Test with SortimentsVorgabenListeMinimaleLaenge.
	 */
	@Test
	public void testCase13() {
		BaumlistenSortimentierer baumlistSort = createNewSortimentierer(new BaumSchaftformFkt_Lemm1991());
		Presenter presenter = new Presenter();
		
        //Dateien laden
		loadFiles(baumlistSort, presenter, "testdata/testcase13/Input-Baumliste.csv", "testdata/testcase13/Input-SortimentsVorgabenListeMinimaleLaenge.csv");
		
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
		
		//save file
		String actualOutputFile = "testdata/testcase13/Output-actual.csv";
		presenter.writeSortimentsStueckListeToFile(baumlistSort.getSsListe(), actualOutputFile);
		
		// compare expected with actual output file
		compareFiles("testdata/testcase13/Output-Sortimentsliste.csv", actualOutputFile);
		
		// delete actual output file
		deleteFile(actualOutputFile);
	}
	
	
	private BaumlistenSortimentierer createNewSortimentierer(BaumSchaftformFunktion schaftformFunktion) {
		BaumlistenSortimentierer baumlistSort = new BaumlistenSortimentierer();
		baumlistSort.selectSchaftformFunktion(schaftformFunktion);
		return baumlistSort;
	}
	
	private void loadFiles(BaumlistenSortimentierer baumlistSort, Presenter presenter, String fileBaumliste, String fileSortimentsVorgaben) {
		try {
			readFileBaumschaefte(baumlistSort, presenter, fileBaumliste);
			readFileSortimentsVorgaben(baumlistSort, presenter, fileSortimentsVorgaben);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Fehler beim Laden der Dateien");
		}
	}
	
	private void readFileBaumschaefte(BaumlistenSortimentierer baumlistSort, Presenter presenter, String fileBaumliste) throws NumberFormatException, IOException {
        // --> Baumdaten ab File lesen
        presenter.readBaumschaefteFromFile(baumlistSort.getBsDefListe(), fileBaumliste);
	}
	
	private void readFileSortimentsVorgaben(BaumlistenSortimentierer baumlistSort, Presenter presenter, String fileSortimentsVorgaben) throws IOException, NumberFormatException {
        //Sortimentvorgaben lesen
		baumlistSort.clearSortimentsStueckListe();
        presenter.readSortimentsVorgabenFromFile(baumlistSort.getSvListe(), fileSortimentsVorgaben);

		//Sortimentsvorgaben sortieren
		baumlistSort.getSvListe().setSortKriterium( SortierKriterium.WertProEinheit );
		baumlistSort.getSvListe().setSortOrder( SortOrder.Descending );
		baumlistSort.getSvListe().sort();
	}
	
	private void compareFiles(String fileExpected, String flieActual) {
		try {
			assertEquals(-1L, Files.mismatch(Path.of(fileExpected), Path.of(flieActual)));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Fehler beim Vergleichen der Dateien");
		}
	}
	
	private void deleteFile(String file) {
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}

		try {
			Files.delete(Path.of(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

//	@Test
	public void testAll() {
//		SortimentStueckListe list = new SortimentStueckListe();
//		assertTrue(list.size() == 0); //test size()
//		
//		SortimentsStueck ss0 = new SortimentsStueck();
//		list.add(ss0);
//		assertTrue(list.size() == 1); //test add() && size()
//
//		SortimentsStueck ss1 = new SortimentsStueck();
//		list.add(ss1);
//		assertTrue(list.size() == 2); //test add() && size()
//
//		SortimentsStueck ss2 = new SortimentsStueck();
//		list.add(ss2);
//		assertTrue(list.size() == 3); //test add() && size()
//
//		assertTrue(list.get(0) == ss0); //test get()
//		assertTrue(list.get(1) != ss0); //test get()
//		assertTrue(list.get(2) != ss0); //test get()
//
//		assertTrue(list.get(0) != ss1); //test get()
//		assertTrue(list.get(1) == ss1); //test get()
//		assertTrue(list.get(2) != ss1); //test get()
//
//		assertTrue(list.get(0) != ss2); //test get()
//		assertTrue(list.get(1) != ss2); //test get()
//		assertTrue(list.get(2) == ss2); //test get()
//		
//		list.removeAll();
//		assertTrue(list.size() == 0); //test removeAll() && size()
	}
}
