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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import ch.wsl.sustfor.baumschaft.base.Baumart;
import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition;
import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition.BaumschaftDefinitionBuilder;
import ch.wsl.sustfor.lang.SorSimLanguageManager;
import ch.wsl.sustfor.sorsim.model.SortimentVorgabenListe;
import ch.wsl.sustfor.sorsim.model.SortimentsStueck;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.CodeDrmKlassen;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.CodeLaengenKlassen;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsAushalteStrategie;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsStueckPositionierung;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsVorgabeBuilder;
import ch.wsl.sustfor.util.CsvReader;

/**
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public class Presenter {

    private final int nxANZAHL_IN_OUTPUT_AUFZULISTENDE_SORTIMENTE = 50;
    private final String trennZeichenInFiles = ";";
	private static int anzahlStellenNachKomma = 2;
	private SorSimLanguageManager lang = SorSimLanguageManager.getInstance();
	
	public void readBaumschaefteFromFile(List<BaumschaftDefinition> bsList, String filename) throws IOException, NumberFormatException {
		//clear old list
		bsList.clear();
		
		//read file
		CsvReader csv = new CsvReader(";", "#", true);
		List<String[]> lines = csv.readFileThrowException(filename);
		
		//fill trees into list
		for (int i=0; i<lines.size(); i++) {
			String[] line = lines.get(i);
			
			//sanity check
			if (line.length < 8) {
				System.err.println("Baumschäfte einlesen: Zeile übersprungen ('" + getString(line) + "')");
				continue;
			}
			
			//neue definition
			BaumschaftDefinitionBuilder bsDefBuilder = new BaumschaftDefinitionBuilder();
			
			bsDefBuilder.setId( Integer.valueOf( line[0] ));
			//baumartName
			bsDefBuilder.setBaumart(Baumart.getBaumart(Integer.valueOf( line[2] )));
			bsDefBuilder.setBeschrieb(line[3]);
			bsDefBuilder.setDatum(line[4]);
			bsDefBuilder.setBhd_cm(Double.valueOf( line[5] ));
			bsDefBuilder.setD7m_cm(Double.valueOf( line[6] ));
			bsDefBuilder.setSchaftLaenge_m(Math.round(Double.valueOf( line[7] ))); //FIXME: Für nicht ganzzahlige Schaftlängen werden in ca. 50% der Fälle keine Sortimente gefunden, warum?
			
			bsList.add(bsDefBuilder.build());			
		}		
	}
	
	private String getString(String[] str) {
		String result = "";
		for (int i=0; i<str.length; i++) {
			result += str[i] + ";";
		}
		return result;
	}

	public void readSortimentsVorgabenFromFile(SortimentVorgabenListe svList, String filename) throws IOException, NumberFormatException {
		// Sortimentsvorgaben einfüllen
		svList.removeAll();

		//read file
		CsvReader csv = new CsvReader(";", "#", true);
		List<String[]> lines = csv.readFileThrowException(filename);

		// Read the lines from the file until the end of the file is reached.
		for (int i=0; i<lines.size(); i++) {
			String[] line = lines.get(i);

	    	SortimentsVorgabeBuilder builder = new SortimentsVorgabeBuilder();
	    	builder.setId(Integer.valueOf(line[0]));
//	    	builder.setBeschrieb(line[1].trim());
	    	builder.setBaumart(Baumart.getBaumart(Integer.valueOf(line[2].trim())));
	    	builder.setLaengenKlassenCode(CodeLaengenKlassen.valueOf( line[3].trim() ));
	    	builder.setDurchmesserKlassenCode(CodeDrmKlassen.valueOfText( line[4].trim()) );
	    	builder.setAnzahlStueck(Integer.valueOf(line[5]));
	    	builder.setLaengeMin_m(			Double.valueOf(line[6]));
	    	builder.setLaengeMax_m(			Double.valueOf(line[7]));
	    	builder.setMittenDurchmMin_cm(	Double.valueOf(line[8]));
	    	builder.setMittenDurchmMax_cm(	Double.valueOf(line[9]));
	    	builder.setZopfDurchmMin_cm(	Double.valueOf(line[10]));
	    	builder.setZopfDurchmMax_cm(	Double.valueOf(line[11]));
	    	builder.setLaengenZugabe_cm(	Double.valueOf(line[12]));
	    	builder.setLaengenZugabe_Prozent(Double.valueOf(line[13]));
	    	builder.setPositionAmStammUnten_m(Double.valueOf(line[14]));
	    	builder.setPositionAmStammOben_m(Double.valueOf(line[15]));
	    	builder.setLaengenIntervall_m(	Double.valueOf(line[16]));
	    	builder.setAushalteStrategie(	SortimentsAushalteStrategie.valueOf(line[17]) );
	    	builder.setPositionierung( 		SortimentsStueckPositionierung.valueOf(line[18]) );
	    	builder.setWertA(				Double.valueOf(line[19]));
	    	builder.setWertB(				Double.valueOf(line[20]));
	    	builder.setWertC(				Double.valueOf(line[21]));
	    	builder.setWertD(				Double.valueOf(line[22]));
	    	builder.setAnteilA(				Double.valueOf(line[23]));
	    	builder.setAnteilB(				Double.valueOf(line[24]));
	    	builder.setAnteilC(				Double.valueOf(line[25]));
	    	builder.setAnteilD(				Double.valueOf(line[26]));
			
	    	SortimentsVorgabe sv = builder.build();
			svList.add(sv);
		}
	}
    
	public String printSortimentsVorgabenListe(SortimentVorgabenListe svList) {
		StringBuilder sb = new StringBuilder();
		
		if (svList == null) {
			return "";
		}
		sb.append("Reihenfolge der Sortimentsvorgaben in der Liste:\n");		
		
		sb.append("\t" + "Beschrieb" + "\t" + "Lae_min" + "\t" + "Lae_max" + "\t" + "MiDrm_min" + "\t" + "MiDrm_max" + "\n");
		
		for (int i=0; i<svList.size(); i++) {
			SortimentsVorgabe sv = svList.get(i);
			sb.append("\t" + sv.getBeschrieb() + "\t" +
					rnd(sv.getLaengeMin_m()) + "\t" +
					rnd(sv.getLaengeMax_m()) + "\t" +
					rnd(sv.getMittenDurchmMin_cm()) + "\t" +
					rnd(sv.getMittenDurchmMax_cm()) + "\t" +
					rnd(sv.getWertProEinheit()) + "\n");
		}
		
		return sb.toString();
	}
	
	public String printSortimentStueck(SortimentStueckInBaumschaftEinpassen ssibsex) {
		StringBuilder sb = new StringBuilder();

		sb.append("SortimentStueck" + "\n");
		sb.append("AushalteStrategie" + " = " + ssibsex.getSortimentsVorgaben().getAushalteStrategie() + "\n");
		sb.append("Positionierung" + " = " + ssibsex.getSortimentsVorgaben().getPositionierung() + "\n");
		sb.append("PositionDerBasisAmStamm_m" + " = " + rnd(ssibsex.getSortimentStueck().getPositionDerBasisAmStamm_m()) + "\n");
		sb.append("Laenge_m" + " = " + rnd(ssibsex.getSortimentStueck().getLaenge_m()) + "\n");
		sb.append("PosMiDrm_m" + " = " + rnd(ssibsex.getSortimentStueck().getPositionDerBasisAmStamm_m() + ssibsex.getSortimentStueck().getLaenge_m() / 2) + "\n");
		sb.append("PosZopf_m" + " = " + rnd(ssibsex.getSortimentStueck().getPositionDerBasisAmStamm_m() + ssibsex.getSortimentStueck().getLaenge_m()) + "\n");
		sb.append("MittenDrmOR_cm" + " = " + rnd(ssibsex.getSortimentStueck().getMittenDrmOR_cm()) + "\n");
		sb.append("ZopfDrmOR_cm" + " = " + rnd(ssibsex.getSortimentStueck().getZopfDrmOR_cm()) + "\n");
		sb.append("VolumenOR_m3" + " = " + rnd(ssibsex.getSortimentStueck().getVolumenOR_m3()) + "\n");
		sb.append("VolumenRestStueckUntenOR_m3" + " = " + rnd(ssibsex.getSortimentStueck().getVolumenRestStueckUntenOR_m3()) + "\n");
		sb.append("VolumenRestStueckObenOR_m3" + " = " + rnd(ssibsex.getSortimentStueck().getVolumenRestStueckObenOR_m3()));

		return sb.toString();
	}
    
	@Deprecated
    public String printSortimentsStueckListe(List<SortimentsStueck> ssList) {    	
    	String result = "";
    	
    	double dWertGesamt = 0;
    	double dVolumenORGesamt_m3 = 0;

    	for (int i=0; i<ssList.size(); i++) {
    		SortimentsStueck ss = ssList.get(i);
    		
    		dWertGesamt += ss.getWert();
    		dVolumenORGesamt_m3 += ss.getVolumenOR_m3();
        	StringBuilder sb = new StringBuilder();
    		sb.append("Sortiment " + ss.getId() + ":" + "\n");
    		sb.append("Beschrieb" + " = " +  ss.getBsDef().getBeschrieb() + "\t" + ss.getSortimentsVorgabe().getBeschrieb() + "\n");
    		sb.append("PositionDerBasisAmStamm_m" + " = " + rnd(ss.getPositionDerBasisAmStamm_m()) + "\n");
    		sb.append("Laenge_m" + " = " + rnd(ss.getLaenge_m()) + "\n");
    		sb.append("MittenDrmOR_cm" + " = " + rnd(ss.getMittenDrmOR_cm()) + "\n");
    		sb.append("ZopfDrmOR_cm" + " = " + rnd(ss.getZopfDrmOR_cm()) + "\n");
    		sb.append("VolumenOR_m3" + " = " + rnd(ss.getVolumenOR_m3()) + "\n");
    		sb.append("VolumenRestStueckUntenOR_m3" + " = " + rnd(ss.getVolumenRestStueckUntenOR_m3()) + "\n");
    		sb.append("VolumenRestStueckObenOR_m3" + " = " + rnd(ss.getVolumenRestStueckObenOR_m3()) + "\n");
    		sb.append("Wert" + " = " + rnd(ss.getWert()) + "\n");
    		sb.append("\n");

    		if (i <= nxANZAHL_IN_OUTPUT_AUFZULISTENDE_SORTIMENTE) {
        		result = sb.toString() + "\n" + result;    			
    		}
    		
    		if (i == nxANZAHL_IN_OUTPUT_AUFZULISTENDE_SORTIMENTE) {

    			result = "... (über " +
    					(nxANZAHL_IN_OUTPUT_AUFZULISTENDE_SORTIMENTE) +
    					" Sortimente, nicht mehr aufgelistet!) ..." +
    					"\n" + "\n" + result;
//    			break; //Kein break, da Summe der Volumen fertig berechnet werden muss!
    		} 
    	}
    	
    	result = "Gesamtwert: " + rnd(dWertGesamt) + "\n" + 
   			 	 "Gesamtvolumen OR m3: " + rnd(dVolumenORGesamt_m3) + "\n" +
   			 	 "Anzahl Sortimente: " + ssList.size() + "\n" +
   			 	 "\n" + result;

    	return result;
    }
    
    /**
     * Erstellt eine Zusammenfassung der SortimentsStueck-Liste
     * 
     * @param ssList Sortimentsstück-Liste
     * @param trennZeichen Trennzeichen zwischen Feldern (z.B Semikolon für csv-Files, Tab für Textfeld)
     * @param kommentarZeichen Zeichen am Anfang jeder neuen Linie
     * @return
     */
    private String getSortimentStueckListeZusammenfassung(List<SortimentsStueck> ssList, String trennZeichen, String kommentarZeichen, boolean extended) {
    	HashTree.Node rootNode = new HashTree().new Node();
    	double dWertGesamt = 0;
    	double dVolumenORGesamt_m3 = 0;
    	
       	for (int i=0; i<ssList.size(); i++) {
    		SortimentsStueck ss = ssList.get(i);
    		dWertGesamt += ss.getWert();
    		dVolumenORGesamt_m3 += ss.getVolumenOR_m3();
    		
    		//get values
    		String codeDatum = ss.getBsDef().getDatum();
    		String codeBaumart = ss.getBsDef().getBaumart().toString();
    		String codeLaengenKlasse = ss.getSortimentsVorgabe().getLaengenKlassenCode().name();
    		String codeDrmKlasse = ss.getSortimentsVorgabe().getDurchmesserKlassenCode().toString();

    		//get node for aufnahmeDatum
    		HashTree.Node nDatum = rootNode.getChild(codeDatum);

    		//get node for baumart
    		HashTree.Node nBaumart = nDatum.getChild(codeBaumart);

    		//get node for laengenklasse
    		HashTree.Node nLaengenKlasse = nBaumart.getChild(codeLaengenKlasse);    		
    		
    		//get leaf for staerkenklasse
    		HashTree.Leaf nStaerkenKlasse = nLaengenKlasse.getLeaf(codeDrmKlasse);

    		//add values to leaf
    		nStaerkenKlasse.add("volOR", ss.getVolumenOR_m3() );
    		nStaerkenKlasse.add("volIR", ss.getVolumenIR_m3() );
    		nStaerkenKlasse.add("wert" , ss.getWert()  );
    		nStaerkenKlasse.add("count", 1.0   ); 		
    	}
       	
       	//Ausgabe der kalkulierten Daten
       	StringBuilder sb = new StringBuilder();
       	
       	if (extended == true) {
       		sb.append( kommentarZeichen + lang.getText(lang.lblGesamtwert) 		  + " " + trennZeichen + trennZeichen + rnd(dWertGesamt) + "\n" );
       		sb.append( kommentarZeichen + lang.getText(lang.lblGesamtvolumenORm3) + " " + trennZeichen + trennZeichen + rnd(dVolumenORGesamt_m3) + "\n" );
       		sb.append( kommentarZeichen + lang.getText(lang.lblAnzahlSortimente)  + " " + trennZeichen + trennZeichen + ssList.size() + "\n" ); 
       		sb.append( kommentarZeichen + lang.getText(lang.lblStueckvolumen)	  + " " + trennZeichen + trennZeichen + rnd(dVolumenORGesamt_m3 / ssList.size()) + "\n" + kommentarZeichen + "\n" ); 
       	}
    	
       	String title =
       				kommentarZeichen + 
					lang.getAutoText(lang.titleStratifizierungsMerkmal) + trennZeichen + 
					lang.getAutoText(lang.titleBaumart) + trennZeichen + 
					lang.getAutoText(lang.titleLaengenklasse) + trennZeichen + 
					lang.getAutoText(lang.titleStaerkenklasse) + trennZeichen + 
					lang.getAutoText(lang.titleVolumenOR_m3) + trennZeichen +
					lang.getAutoText(lang.titleVolumenIR_m3) + trennZeichen +
					lang.getAutoText(lang.titleWert_CHF) + trennZeichen +
					lang.getAutoText(lang.titleAnzahl) + "\n";		
       	sb.append(title);
       	String [] datumKeySet = rootNode.getKeySet(true); // datum = strat-merkmal
       	for (int iDatum=0; iDatum<datumKeySet.length; iDatum++) {
       		String datum = datumKeySet[iDatum];

       		String [] baumartKeySet = rootNode.getChild(datum).getKeySet(false);
       		for (int iBaumart=0; iBaumart<baumartKeySet.length; iBaumart++) {
       			String baumart = baumartKeySet[iBaumart];

       			String[] lkKeySet = rootNode.getChild(datum).getChild(baumart).getKeySet(false);
       			for (int iLK=0; iLK<lkKeySet.length; iLK++) {
       				String lk = lkKeySet[iLK];

       				String[] skKeySet = rootNode.getChild(datum).getChild(baumart).getChild(lk).getKeySet(true);
       				for (int iSK=0; iSK<skKeySet.length; iSK++) {
       					String sk = skKeySet[iSK];

       					HashTree.Leaf values = rootNode.getChild(datum).getChild(baumart).getChild(lk).getLeaf(sk);
     				
       					sb.append(
       							kommentarZeichen + 
       							datum + trennZeichen + 
       							baumart + trennZeichen + 
       							lk + trennZeichen + 
       							sk + trennZeichen + 
       							rnd(values.getSum("volOR")) + trennZeichen +
       							rnd(values.getSum("volIR")) + trennZeichen +
       							rnd(values.getSum("wert")) + trennZeichen +
       							rndInt(values.getSum("count")) + "\n" );
       				}
       			}	
   				sb.append(kommentarZeichen + "\n");	
       		}
       	}    	
    	return sb.toString(); // + "\n" + printSortimentsStueckListeZusammenfassungBaum(ssList);    	
    }
    
    public String printSortimentsStueckListeZusammenfassung(List<SortimentsStueck> ssList) {   
    	return getSortimentStueckListeZusammenfassung(ssList, "\t", "", false);
    }
    
    @Deprecated
    public String printSortimentsStueckListeZusammenfassungBaum(List<SortimentsStueck> ssList) {
    	//get tree structure
    	HashTree.Node rootNode = getSortimentStueckListeZusammenfassungBaum(ssList);
       	
       	//Ausgabe der kalkulierten Daten
       	StringBuilder sb = new StringBuilder();
       	String[] treeKeySet = rootNode.getKeySet(false);
       	for (int iTree=0; iTree<treeKeySet.length; iTree++) {
       		String treeKey = treeKeySet[iTree];

       		String[] ssKeySet = rootNode.getChild(treeKey).getKeySet(false);
       		for (int iSS=0; iSS<ssKeySet.length; iSS++) {
       			String ssKey = ssKeySet[iSS];

       			HashTree.Leaf values = rootNode.getChild(treeKey).getLeaf(ssKey);

       			String trennZeichen = "\t";       				
       			sb.append(
       					treeKey + trennZeichen + 
       					ssKey + trennZeichen +
       					rnd(values.getSum("volOR")) + trennZeichen +
       					rnd(values.getSum("volIR")) + trennZeichen +
       					rnd(values.getSum("wert")) + trennZeichen +
       					rnd(values.getSum("count")) + "\n" );
       		}
       	}		
    	
    	return sb.toString();
    }
    
    private HashTree.Node getSortimentStueckListeZusammenfassungBaum(List<SortimentsStueck> ssList) {    	
    	HashTree.Node rootNode = new HashTree().new Node();
    	
       	for (int i=0; i<ssList.size(); i++) {
    		SortimentsStueck ss = ssList.get(i);
    		
    		//get values
    		String codeTree = String.valueOf( ss.getBsDef().getId() );
    		String codeSortStueck = String.valueOf( ss.getId() );    		
    		
    		//get node for single tree
    		HashTree.Node nTree = rootNode.getChild(codeTree);

    		//get leaf-node for sortimentStueck
    		HashTree.Leaf nSortStk = nTree.getLeaf(codeSortStueck);
    		
    		//add values to leaf
    		nSortStk.add("volOR", ss.getVolumenOR_m3() );
    		nSortStk.add("volIR", ss.getVolumenIR_m3() );
    		nSortStk.add("wert" , ss.getWert()  );
    		nSortStk.add("count", 1.0   ); 		
    	}
       	
       	return rootNode;    	
    }
    
    private static class HashTree {
    	private abstract class AbstractNode {
    		/**
    		 * Returns the sum of all values of this node's children
    		 * 
    		 * @param sumKey
    		 */
    		public abstract Double getSum(String sumKey);
    	}

    	private class Node extends AbstractNode{
//    		private HashMap<String, AbstractNode> children = new HashMap<String, AbstractNode>(); //unordered
    		private Comparator<String> myComparator = new Comparator<String>() {
				@Override
				public int compare(String arg0, String arg1) {
					try {
						Double d0 = Double.parseDouble(arg0);
						Double d1 = Double.parseDouble(arg1);
						return d0.compareTo(d1);
					} catch (NumberFormatException e) {
						return arg0.compareTo(arg1);
					}
				}
    			
    		};
    		private TreeMap<String, AbstractNode> children = new TreeMap<String, AbstractNode>(myComparator); //ordered
    		
    		private Node getChild(String key) {
    			if (children.get(key) == null) {
    				children.put(key, new Node());
    			}
    			return (Node) children.get(key);
    		}
    		
    		
    		private Leaf getLeaf(String key) {
    			if (children.get(key) == null) {
    				children.put(key, new Leaf());
    			}
    			return (Leaf) children.get(key);
    		}

    		@Override
    		public Double getSum(String sumKey) {
    			Double sum = 0.0;
    			Iterator<AbstractNode> it = children.values().iterator();
    			while (it.hasNext()) {
    				sum += it.next().getSum(sumKey);
    			}
    			return sum;				
    		}
    		
    		private String[] getKeySet(boolean reverseOrder) {
    			Object[] keySet = reverseOrder ? children.descendingKeySet().toArray() : children.keySet().toArray();
    			String[] result = new String[keySet.length];
    			
    			for (int i=0; i<keySet.length; i++) {
    				result[i] = (String) keySet[i];
    			}    			
    			
    			return result;
    		}
    	}

    	/**
    	 * Maintains a HashMap of Strings, where each String is the key for a certain sum.
    	 * 
    	 * @author Stefan Holm
    	 *
    	 */
    	private class Leaf extends AbstractNode {
    		private HashMap<String, Double> values = new HashMap<String, Double>();

    		private void add(String key, Double value) {
    			double oldSum = 0.0;
    			if (values.get(key) != null) {
    				oldSum = values.get(key);
    			}
    			values.put(key, (oldSum + value) );
    		}

    		@Override
    		public Double getSum(String sumKey) {
    			return values.get(sumKey);
    		}			
    	}
    }
	
    public void writeSortimentsStueckListeToFile(List<SortimentsStueck> ssList, String filename) {
    	try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
    		
    		// Write titles
    		String titles = "#" + 
    				lang.getText(lang.titleId) 			    + trennZeichenInFiles +
    				lang.getText(lang.titleBaumId)			+ trennZeichenInFiles +
    				lang.getText(lang.titleBaumart) 		+ trennZeichenInFiles +
    				lang.getText(lang.titleBaumartCode) 	+ trennZeichenInFiles +
    				lang.getText(lang.titleBeschriebBaumNr) + trennZeichenInFiles +
    				lang.getText(lang.titleStratifizierungsMerkmal)	+ trennZeichenInFiles +
    				lang.getText(lang.titleBHD) 			+ trennZeichenInFiles +
    				lang.getText(lang.titleSchaftlaenge) 	+ trennZeichenInFiles +
    				lang.getText(lang.titleLaengenklasse) 	+ trennZeichenInFiles +
    				lang.getText(lang.titleStaerkenklasse) 	+ trennZeichenInFiles +
    				lang.getText(lang.titlePosBasisAmStamm_m) + trennZeichenInFiles +
    				lang.getText(lang.titleLaenge_m) 		+ trennZeichenInFiles +
    				lang.getText(lang.titleBasisDrmOR) 		+ trennZeichenInFiles +
    				lang.getText(lang.titleMiDrmOR_cm) 		+ trennZeichenInFiles +
    				lang.getText(lang.titleZopfDrmOR_cm) 	+ trennZeichenInFiles +
    				lang.getText(lang.titleVolumenOR_m3) 	+ trennZeichenInFiles +
    				lang.getText(lang.titleVolumenIR_m3) 	+ trennZeichenInFiles +
    				lang.getText(lang.titleVolRestStkUntenOR_m3) + trennZeichenInFiles +
    				lang.getText(lang.titleVolRestStkObenOR_m3) + trennZeichenInFiles +
//    				lang.getText(lang.titleQualitaet) + trennZeichenInFiles +
    				lang.getText(lang.titleWert_CHF) 		+ trennZeichenInFiles;
    		out.write(titles + "\n");
           	
           	//Ausgabe der Sortimentstuecke mit Zusammenfassungen pro Baum
        	HashTree.Node rootNode = getSortimentStueckListeZusammenfassungBaum(ssList);         	
           	//Iteration über alle Bäume
           	String[] treeKeySet = rootNode.getKeySet(false);
           	for (int iTree=0; iTree<treeKeySet.length; iTree++) {
           		String treeKey = treeKeySet[iTree];

           		//Iteration über alle SortimentStuecke von diesem Baum
           		String[] ssKeySet = rootNode.getChild(treeKey).getKeySet(false);
           		SortimentsStueck ss = null;
           		for (int iSS=0; iSS<ssKeySet.length; iSS++) {
           			String ssKey = ssKeySet[iSS];
           			int ssId = Integer.valueOf(ssKey);
           			
           			ss = ssList.get(ssId-1); //Annahme: ssList ist geordner nach den ss-id's
           			if (ss.getId() != ssId) {
           				throw new RuntimeException("das sollte nicht passieren...");
           			}

           			//SortimentStueck schreiben
            		out.write( getOutputLineForSortimentStueck(ss) );
           		}
           		
//           		//Reststück ausgeben
//           		if (true) { 
//    				boolean bvoRestStueckObenBisGanzeSchaftlaengeErmitteln = false;    				
//    				double dVolumenRestORAlt_m3 = 0;
//    				double dVolumenRestIRAlt_m3 = 0;
//    				double dBasisPosRest_m = 0;
////    				double dBasisDrmRestIR_cm = 0;
//    				double dBasisDrmRestOR_cm = 0;
//    				double dLaengeRest_m = 0;
//    				
//    				if (ss.getSortimentsVorgabe().getPositionierung().equals( ESortimentsStueckPositionierung.ZuUnterst )) {
//    					dVolumenRestORAlt_m3 = ss.getVolumenRestStueckObenOR_m3();
//    					dVolumenRestIRAlt_m3 = dVolumenRestORAlt_m3 * ss.getVolumenIR_m3() / ss.getVolumenOR_m3();
//    					dBasisPosRest_m = ss.getPositionDerBasisAmStamm_m() + ss.getLaenge_m();
////    					dBasisDrmRestIR_cm = ss.getZopfDrmIR_cm();
//    					dBasisDrmRestOR_cm = ss.getZopfDrmOR_cm();
//    					if (bvoRestStueckObenBisGanzeSchaftlaengeErmitteln) {
//    						dLaengeRest_m = ss.getBsDef().getSchaftLaenge_m() * ss.getBsDef().getFuerSortimenteZuVerwendenderSchaftanteil() - dBasisPosRest_m;
//    					}else{
//    						dLaengeRest_m = ss.getBsDef().getSchaftLaenge_m() * 1 - dBasisPosRest_m;
//    					}
//    				} 
//    				else {
//    					dVolumenRestORAlt_m3 = ss.getVolumenRestStueckUntenOR_m3();
//    					dVolumenRestIRAlt_m3 = dVolumenRestORAlt_m3 * ss.getVolumenIR_m3() / ss.getVolumenOR_m3();
//    					dBasisPosRest_m = 0; //unbekannt
////    					dBasisDrmRestIR_cm = 0; //unbekannt
//    					dBasisDrmRestOR_cm = 0; //unbekannt
//    					dLaengeRest_m = ss.getPositionDerBasisAmStamm_m();
//    				}
//    				
//    				out.write("#" + //Kommentarzeile 
//            				"Rest" + trennZeichenInFiles +
//    						ss.getBsDef().getId() + trennZeichenInFiles + 
//    						ss.getBsDef().getBaumart() + trennZeichenInFiles + 
//    						ss.getBsDef().getBaumart().getExternalCode() + trennZeichenInFiles + 
//            				ss.getBsDef().getBeschrieb() + trennZeichenInFiles +
//            				ss.getBsDef().getDatum() + trennZeichenInFiles +
//    						ss.getBsDef().getUntererDurchmesser_iR_cm() + trennZeichenInFiles + 
//    						ss.getBsDef().getSchaftLaenge_m() + trennZeichenInFiles + 
//    						"" + trennZeichenInFiles + 
//    						"Rest:" + trennZeichenInFiles + 
//    						rnd(dBasisPosRest_m) + trennZeichenInFiles + 
//    						rnd(dLaengeRest_m) + trennZeichenInFiles + 
//    						rnd(dBasisDrmRestOR_cm) + trennZeichenInFiles + 
//    						"" + trennZeichenInFiles + 
//    						"" + trennZeichenInFiles +
//    						rnd(dVolumenRestORAlt_m3) + trennZeichenInFiles + 
//    						rnd(dVolumenRestIRAlt_m3) + trennZeichenInFiles + 
//    						"" + trennZeichenInFiles + 
//    						"" + trennZeichenInFiles + 
//    						"" + trennZeichenInFiles + 
//    						"" + trennZeichenInFiles + 
//    						"\n");
//           		}

           		//Zusammenfassung pro Baum
           		HashTree.Node values = rootNode.getChild(treeKey);           		
           		out.write( "#" + //Kommentarzeile 
//        				"Summe (ohne Rest)" + trennZeichenInFiles +
           				lang.getText(lang.titleSumme) + trennZeichenInFiles +
        				ss.getBsDef().getId() + trennZeichenInFiles +
        				ss.getBsDef().getBaumart() + trennZeichenInFiles +
        				ss.getBsDef().getBaumart().getExternalCode() + trennZeichenInFiles +
        				ss.getBsDef().getBeschrieb() + trennZeichenInFiles +
        				ss.getBsDef().getDatum() + trennZeichenInFiles +
        				ss.getBsDef().getBhd_cm() + trennZeichenInFiles +
        				ss.getBsDef().getSchaftLaenge_m() + trennZeichenInFiles +
        				/*ss.getSortimentsVorgabe().getLaengenKlassenCode() +*/ trennZeichenInFiles +
        				/*ss.getSortimentsVorgabe().getDurchmesserKlassenCode() +*/ trennZeichenInFiles +
        				/*rnd(ss.getPositionDerBasisAmStamm_m()) +*/ trennZeichenInFiles +
        				/*rnd(ss.getLaenge_m()) +*/ trennZeichenInFiles +
        				/*rnd(ss.getBasisDrmOR_cm()) +*/ trennZeichenInFiles +
        				/*rnd(ss.getMittenDrmOR_cm()) +*/ trennZeichenInFiles +
        				/*rnd(ss.getZopfDrmOR_cm()) +*/ trennZeichenInFiles +
        				rnd(values.getSum("volOR")) + trennZeichenInFiles +
        				rnd(values.getSum("volIR")) + trennZeichenInFiles +
        				/*rnd(ss.getVolumenRestStueckUntenOR_m3()) +*/ trennZeichenInFiles +
        				/*rnd(ss.getVolumenRestStueckObenOR_m3()) +*/ trennZeichenInFiles +
        				/*ss.getSortimentsVorgabe().getQualitaetsKlassenCode() +*/// trennZeichenInFiles +
        				rnd(values.getSum("wert")) + trennZeichenInFiles +
        				"\n");           		
           		
           		//Leerzeile (mit Kommentarzeichen beginnend)
           		out.write("#\n");
           	}            	

//        	double dWertGesamt = rootNode.getSum("wert");
//        	double dVolumenORGesamt_m3 = rootNode.getSum("volOR");
    		
//    		//Ausgabe der SortimentStueck ohne Zusammenfassung pro Baum
//        	double dWertGesamt = 0;
//        	double dVolumenORGesamt_m3 = 0;
//    		for (int i=0; i< ssList.size(); i++) {
//    			SortimentsStueck ss = ssList.get(i);
//    			dWertGesamt = dWertGesamt + ss.getWert();
//    			dVolumenORGesamt_m3 = dVolumenORGesamt_m3 + ss.getVolumenOR_m3();
//    			
//        		out.write( getOutputLineForSortimentStueck(ss) );
//    		}
    		
//    		out.write("' WertGesamt" + " = " + rnd(dWertGesamt) + "\n");
//    		out.write("' dVolumenORGesamt_m3" + " = " + rnd(dVolumenORGesamt_m3) + "\n");   
    		
    		out.write( getSortimentStueckListeZusammenfassung(ssList, ";", "#", true) );
    		
    		out.close();
    		
    	} catch (IOException e) {
    		System.err.println(lang.getText(lang.errDateiSchreibFehler) + "\n");
    		e.printStackTrace();
    	}
    }
    
    public void writeSortimentsVorgabenListeToFile(SortimentVorgabenListe svl, String filename) {
    	try {
    		BufferedWriter out = new BufferedWriter( new FileWriter(filename) );
    		
    		// Write titles
    		String titles = "#" + 
    				lang.getText(lang.titleId) 				+ trennZeichenInFiles + 
    				lang.getText(lang.titleBeschriebBaumNr) + trennZeichenInFiles + 
    				lang.getText(lang.titleBaumartCode) 	+ trennZeichenInFiles + 
    				lang.getText(lang.titleLaengenklasse) 	+ trennZeichenInFiles +
    				lang.getText(lang.titleStaerkenklasse) 	+ trennZeichenInFiles +
//    				"QualitaetsKlasse" 						+ trennZeichenInFiles + 
    				lang.getText(lang.titleAnzahlStueck)	+ trennZeichenInFiles + 
    				lang.getText(lang.titleLaengeMin_m) 	+ trennZeichenInFiles + 
    				lang.getText(lang.titleLaengeMax_m) 	+ trennZeichenInFiles + 
    				lang.getText(lang.titleMittenDrmMin_cm) + trennZeichenInFiles + 
    				lang.getText(lang.titleMittenDrmMax_cm) + trennZeichenInFiles + 
    				lang.getText(lang.titleZopfDrmMin_cm) 	+ trennZeichenInFiles + 
    				lang.getText(lang.titleZopfDrmMax_cm) 	+ trennZeichenInFiles + 
    				lang.getText(lang.titleLaengenZugabe_cm) + trennZeichenInFiles + 
    				lang.getText(lang.titleLaengenZugabe_Prozent) + trennZeichenInFiles + 
//    				"WertProEinheit" 						+ trennZeichenInFiles + 
    				lang.getText(lang.titlePositionAmStammUnten_m) + trennZeichenInFiles + 
    				lang.getText(lang.titlePositionAmStaummOben_m) + trennZeichenInFiles + 
    				lang.getText(lang.titleLaengenIntervall_m) 	+ trennZeichenInFiles + 
    				lang.getText(lang.titleAushaltestrategie) 	+ trennZeichenInFiles + 
    				lang.getText(lang.titlePositionierung) 	+ trennZeichenInFiles + 
    				lang.getText(lang.titleWertA) 			+ trennZeichenInFiles + 
    				lang.getText(lang.titleWertB) 			+ trennZeichenInFiles + 
    				lang.getText(lang.titleWertC) 			+ trennZeichenInFiles + 
    				lang.getText(lang.titleWertD) 			+ trennZeichenInFiles + 
    				lang.getText(lang.titleAnteilA) 		+ trennZeichenInFiles + 
    				lang.getText(lang.titleAnteilB) 		+ trennZeichenInFiles + 
    				lang.getText(lang.titleAnteilC) 		+ trennZeichenInFiles + 
    				lang.getText(lang.titleAnteilD) 		+ trennZeichenInFiles;     		
    		out.write(titles + "\n");        	
    		
    		// Write content
    		for (int i=0; i<svl.size(); i++) {
    			SortimentsVorgabe sv = svl.get(i);    			
    			out.write( sv.getId()						+ trennZeichenInFiles );
    			out.write( sv.getBeschrieb() 				+ trennZeichenInFiles );
    			out.write( sv.getBaumart().getExternalCode()+ trennZeichenInFiles );
    			out.write( sv.getLaengenKlassenCode() 		+ trennZeichenInFiles );
    			out.write( sv.getDurchmesserKlassenCode() 	+ trennZeichenInFiles );
    			out.write( sv.getAnzahlStueck() 			+ trennZeichenInFiles );
    			out.write( sv.getLaengeMin_m() 				+ trennZeichenInFiles );
    			out.write( sv.getLaengeMax_m() 				+ trennZeichenInFiles );
    			out.write( sv.getMittenDurchmMin_cm() 		+ trennZeichenInFiles );
    			out.write( sv.getMittenDurchmMax_cm() 		+ trennZeichenInFiles );
    			out.write( sv.getZopfDurchmMin_cm() 		+ trennZeichenInFiles );
    			out.write( sv.getZopfDurchmMax_cm() 		+ trennZeichenInFiles );
    			out.write( sv.getLaengenZugabe_cm()		 	+ trennZeichenInFiles );
    			out.write( sv.getLaengenZugabe_Prozent() 	+ trennZeichenInFiles );
    			out.write( sv.getPositionAmStammUnten_m() 	+ trennZeichenInFiles );
    			out.write( sv.getPositionAmStammOben_m() 	+ trennZeichenInFiles );
    			out.write( sv.getLaengenIntervall_m() 		+ trennZeichenInFiles );
    			out.write( sv.getAushalteStrategie().name()	+ trennZeichenInFiles );
    			out.write( sv.getPositionierung().name() 	+ trennZeichenInFiles );
    			out.write( sv.getWertA() 					+ trennZeichenInFiles );
    			out.write( sv.getWertB() 					+ trennZeichenInFiles );
    			out.write( sv.getWertC() 					+ trennZeichenInFiles );
    			out.write( sv.getWertD() 					+ trennZeichenInFiles );
    			out.write( sv.getAnteilA() 					+ trennZeichenInFiles );
    			out.write( sv.getAnteilB() 					+ trennZeichenInFiles );
    			out.write( sv.getAnteilC() 					+ trennZeichenInFiles );
    			out.write( sv.getAnteilD() 					+ trennZeichenInFiles );
    			out.write("\n");
    		}    		
    		out.close();

    	} catch (IOException e) {
    		System.err.println(lang.getText(lang.errDateiSchreibFehler) + "\n");
    		e.printStackTrace();
    	}
    }
    
    private String getOutputLineForSortimentStueck(SortimentsStueck ss) {
		String result =
				ss.getId() + trennZeichenInFiles +
				ss.getBsDef().getId() + trennZeichenInFiles +
				ss.getBsDef().getBaumart() + trennZeichenInFiles +
				ss.getBsDef().getBaumart().getExternalCode() + trennZeichenInFiles +
				ss.getBsDef().getBeschrieb() + trennZeichenInFiles +
				ss.getBsDef().getDatum() + trennZeichenInFiles +
				ss.getBsDef().getBhd_cm() + trennZeichenInFiles +
				ss.getBsDef().getSchaftLaenge_m() + trennZeichenInFiles +
				ss.getSortimentsVorgabe().getLaengenKlassenCode() + (ss.getSortimentsVorgabe().getLaengenKlassenCode()==CodeLaengenKlassen.Restholz ? " " + ss.getReststueckKategorie() : "") + trennZeichenInFiles +
				ss.getSortimentsVorgabe().getDurchmesserKlassenCode() + (ss.getSortimentsVorgabe().getDurchmesserKlassenCode()==CodeDrmKlassen.Restholz ? " " + ss.getReststueckKategorie() : "") + trennZeichenInFiles +
				rnd(ss.getPositionDerBasisAmStamm_m()) + trennZeichenInFiles +
				rnd(ss.getLaenge_m()) + trennZeichenInFiles +
				rnd(ss.getBasisDrmOR_cm()) + trennZeichenInFiles +
				rnd(ss.getMittenDrmOR_cm()) + trennZeichenInFiles +
				rnd(ss.getZopfDrmOR_cm()) + trennZeichenInFiles +
				rnd(ss.getVolumenOR_m3()) + trennZeichenInFiles +
				rnd(ss.getVolumenIR_m3()) + trennZeichenInFiles +
				rnd(ss.getVolumenRestStueckUntenOR_m3()) + trennZeichenInFiles +
				rnd(ss.getVolumenRestStueckObenOR_m3()) + trennZeichenInFiles +
//				ss.getSortimentsVorgabe().getQualitaetsKlassenCode() + trennZeichenInFiles +
				rnd(ss.getWert()) + trennZeichenInFiles;
    	
    	return result + "\n";    	
    }
	
	private String rnd(double d) {
		double factor = Math.pow(10, anzahlStellenNachKomma);
		double dResult = Math.round(d * factor) / factor;
		
		//safety check
		if (Math.abs(dResult - d) > 0.5) { //can happen if precision is to high
			throw new RuntimeException("Rounding Error (input=" + d + ", output=" +dResult + ")");
		}
		
		return String.valueOf(dResult);
	}
	
	private String rndInt(double d) {
		double dResult = Math.round(d);
		
		String tempResult = String.valueOf(dResult);		
		return tempResult.replace(".0", "");
	}
	
	public void setAnzahlStellenNachKomma(int anzahl) {
		anzahlStellenNachKomma = anzahl;
	}
}
