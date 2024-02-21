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
package ch.wsl.sustfor.sorsim.model;

import ch.wsl.sustfor.lang.SorSimLanguageManager;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.CodeLaengenKlassen;
import ch.wsl.sustfor.util.SortedList;

/**
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public class SortimentVorgabenListe extends SortedList<SortimentsVorgabe, Double> {
	
    private SortierKriterium sortKriterium = SortierKriterium.MittenDurchmesserMax;
    
    public enum SortierKriterium {
    	MittenDurchmesserMax,
//    	MittenDurchmesserMin,
    	LaengeMax,
//    	LaengeMin,
    	WertProEinheit;
		
		@Override
		public String toString() {
			String text = SorSimLanguageManager.getInstance().getText("SortierKriterium." + this.name());
			
			if (text == null) {
				return this.name();
			}
			return text;
		}
    }  
    
    @Override
    public void sort(){
    	super.sort();
    	
    	//Alle SV für Restholz ans Ende der Liste schieben
    	int countRestholzElements = 0;
    	for (int i=0; i<listElements.size()-countRestholzElements; i++) {
    		if (listElements.get(i).getLaengenKlassenCode() == CodeLaengenKlassen.Restholz) {
    			SortimentsVorgabe sv = listElements.remove(i);
    			listElements.add(sv);
    			countRestholzElements++;
    			i--;
    		}
    	}
    	
//    	//Ausgabe der sortierten SV-Liste
//    	for (int i=0; i<vElements.size(); i++) {
//    		System.out.println(vElements.elementAt(i).getBaumart() + ": " + vElements.elementAt(i).getBeschrieb());
//    	}
    }
    
    @Override
    protected Double getElementWert(SortimentsVorgabe sv) {
    	
    	switch (sortKriterium) {

    	case LaengeMax:
    		return sv.getLaengeMax_m();
    		
//    	case LaengeMin:
//    		return sv.getLaengeMin_m();
    		
    	case MittenDurchmesserMax:
    		return sv.getMittenDurchmMax_cm();
    		
//    	case MittenDurchmesserMin:
//    		return sv.getMittenDurchmMin_cm();
    		
    	case WertProEinheit:
    		return sv.getWertProEinheit();
    		
    	}    	
    	
    	return -1.0;
    }
    
	public SortierKriterium getSortKriterium() {
		return sortKriterium;
	}

	public void setSortKriterium(SortierKriterium sortKriterium) {
		this.sortKriterium = sortKriterium;
	}
}