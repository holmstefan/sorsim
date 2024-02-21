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

import ch.wsl.sustfor.baumschaft.base.Baumart;

/**
 * 
 * @author Stefan Holm
 *
 */
public class D7mBhdRelations {

	private static HashMap<Baumart, HashMap<Standort, Double>> values = new HashMap<>();
	
	public enum Standort{
		Gut,
		Mager;
	}
	
	static {
		for (Baumart baumart : Baumart.values()) {
			values.put(baumart, getTableForBaumart(baumart));
		}	
	}
	
	private static HashMap<Standort, Double> getTableForBaumart(Baumart baumart) {
		HashMap<Standort, Double> result = new HashMap<Standort, Double>();
		
		switch (baumart) {
		case Ahorn:
			result.put(Standort.Gut, 0.79);
			result.put(Standort.Mager, 0.73);
			break;
		case Buche:
			result.put(Standort.Gut, 0.82);
			result.put(Standort.Mager, 0.78);
			break;
		case Eiche:
			result.put(Standort.Gut, 0.82);
			break;
		case Esche:
			result.put(Standort.Gut, 0.82);
			result.put(Standort.Mager, 0.77);
			break;
		case Fichte:
			result.put(Standort.Gut, 0.81);
			result.put(Standort.Mager, 0.77);
			break;
		case Foehre:
			result.put(Standort.Gut, 0.81);
			result.put(Standort.Mager, 0.70);
			break;
		case Laerche:
			result.put(Standort.Gut, 0.80);
			result.put(Standort.Mager, 0.74);
			break;
		case Tanne:
			result.put(Standort.Gut, 0.81);
			result.put(Standort.Mager, 0.80);
			break;
		case UebrigesLaubholz:
			result.put(Standort.Gut, 0.74);
			result.put(Standort.Mager, 0.66);
			break;
		case UebrigesNadelholz:
			result.put(Standort.Gut, 0.81);
			break;
		default:
			throw new RuntimeException("Unbekannte Baumart");
		}
		return result;
	}
	
	// Gibt das Verhältnis d7m zu Bhd zurück, oder NULL, falls für
	//  die entsprechende Baumart für diesen Standort kein Wert existiert
	public static Double getRelation(Baumart baumart, Standort standort) {		
		return values.get(baumart).get(standort);
	}
	
	// Gibt das Verhältnis d7m zu Bhd zurück. 
	//  Falls für die entsprechende Baumart für diesen Standort 
	//  kein Wert existiert, wird der Wert des anderen Standorts zurückgegeben
	public static Double getNonNullRelation(Baumart baumart, Standort standort) {
		Double result = getRelation(baumart, standort);
		if (result == null) {
			switch (standort) {
			case Gut:
				standort = Standort.Mager;
				break;
			case Mager:
				standort = Standort.Gut;
				break;
			}
			result = getRelation(baumart, standort);
		}
		if (result == null) {
			throw new RuntimeException("Kein Wert für diese Baumart gefunden");
		}
		
		return result;
	}
}
