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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition;
import ch.wsl.sustfor.util.SortedList;

/**
 * 
 * @author Stefan Holm
 *
 */
public class H0Calculator {

	/**
	 * @return HashMap<AufnahmeDatum, H0-Wert>
	 */
	public static HashMap<String, Double> calcH0(List<BaumschaftDefinition> bsDefListe) {
		//HashMap<Aufnahmejahr, SortedBsDefListe>
		HashMap<String, SortedBsDefListe> sortedLists = new HashMap<String, SortedBsDefListe>();
		
		//geordnete Liste für jedes Aufnahmejahr erstellen
		for (int i=0; i<bsDefListe.size(); i++) {
			BaumschaftDefinition bsDef = bsDefListe.get(i);
			
			String aufnahmeJahr = bsDef.getDatum();
			SortedBsDefListe listeAktuellesJahr = sortedLists.get(aufnahmeJahr);
			if (listeAktuellesJahr == null) {
				listeAktuellesJahr = new SortedBsDefListe();
				sortedLists.put(aufnahmeJahr, listeAktuellesJahr);
			}
			listeAktuellesJahr.add(bsDef);
		}
		
		//Mittelwerte berechnen und zurückgeben
		HashMap<String, Double> result = new HashMap<String, Double>();
		Iterator<String> it = (sortedLists.keySet().iterator());
		while (it.hasNext()) {
			String key = it.next();
			result.put(key, sortedLists.get(key).getH0() );
		}
				
		return result;		
	}
	
	//Sortierte eine BsDefListe nach BHD (grösster zuerst)
	private static class SortedBsDefListe extends SortedList<BaumschaftDefinition, Double> {
		{
			super.sortOrder = SortOrder.Descending;
		}
		@Override
		protected Double getElementWert(BaumschaftDefinition bsDef) {
			return bsDef.getBhd_cm();
		}
		
		public double getH0() {
			//Sortieren
			super.sort();
			
			//Berechne Mittelwert der 10% dicksten Bäumen (min. 1)
			int count = super.size() / 10;
			if (count == 0) {
				count = 1;
			}
			double sum = 0;
			for (int i=0; i<count; i++) {
				sum += super.get(i).getSchaftLaenge_m();
			}
			double avg = sum / count;
			return avg;
		}
	}
}
