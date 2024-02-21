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
package ch.wsl.sustfor.lang;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ch.wsl.sustfor.util.CsvReader;

/**
 * 
 * @author Stefan Holm
 *
 */
public class LanguageManager {
	
	protected String activeLanguageCode;
	
	// String1: language, String2: fieldName, String3: text
	protected HashMap<String, HashMap<String, String>> entries;
	
	private static HashMap<String, LanguageManager> instances = new HashMap<String, LanguageManager>();
	
	// tests
	public static void main(String[] args) {
//		LanguageManager lm = new LanguageManager();

		LanguageManager.createNewInstance("bla");
		LanguageManager.createNewInstance("blabla", "res/lang_BaumSchaftformFkt_Lemm1991.csv");
		LanguageManager lm = LanguageManager.getInstance("bla");
		
		lm.addText("de", "1234", "stefan");
		lm.addText("de", "1234", "stefan2");
		lm.addText("de", "5678", "HOLM");
		lm.addText("de", "890", "holm");;
		lm.addText("en", "5678", "HOLMe");
		lm.addText("en", "890", "holme");
		lm.addText("en", "1234", "steve");
		
		lm.setActiveLanguage("en");
		
		lm.printAllLanguages();
		lm.printAllEntries();
		
		lm = LanguageManager.getInstance("bla");

		System.out.println( lm.getText("1234") );
		
		LanguageManager.getInstance("blabla").setActiveLanguage("de");
		System.out.println( LanguageManager.getInstance("blabla").getText("szBaumartUebrNdhlz") );
	}
	
	public static LanguageManager getInstance(String instanceName) {
		if (instances != null) {
			return instances.get(instanceName);
		}
		return null;
	}
	
	public static LanguageManager createNewInstance(String instanceName) {
		if (instances.containsKey(instanceName) == true) {
			System.err.println("A LanguageManager instance with this name already exists, returning existing instance!");
			return instances.get(instanceName);
		}
		instances.put(instanceName, new LanguageManager());
		return instances.get(instanceName);
	}
	
	public static LanguageManager createNewInstance(String instanceName, String fileName) {
		if (instances.containsKey(instanceName) == true) {
			System.err.println("A LanguageManager instance with this name already exists, returning existing instance!");
			return instances.get(instanceName);
		}
		instances.put(instanceName, new LanguageManager(fileName));	
		return instances.get(instanceName);
	}

	protected LanguageManager() {
		activeLanguageCode = null;
		entries = new HashMap<String, HashMap<String, String>>();
	}
	
	/**
	 * Reads entries from the given csv-file.
	 */
	protected LanguageManager(String fileName) {
		this();
		
		//read csv file
		addEntriesFromFile(fileName);
	}
	
	/**
	 * Reads entries from the given csv-file.
	 */
	protected void addEntriesFromFile(String fileName) {
		//read csv file
		CsvReader csv = new CsvReader();
		List<String[]> csvEntries = csv.readFile(fileName);
		
		if (csvEntries != null) {
			
			//read all lines in file
			for (int lineNr=0; lineNr<csvEntries.size(); lineNr++) {
				String[] line = csvEntries.get(lineNr);
				
				//sanity check
				if (line.length < 2 || line.length > 5) { //eigentlich 5 Felder, aber hintere können leer sein
					System.err.println("Invalid language file (" + fileName + ")");
					return;
				}
				
				if (line[0].startsWith("'")) {
					continue;
				}
								
				//add entry
				this.addText("de", line[0], line[1]);
				if (line.length > 2) {
					this.addText("fr", line[0], line[2]);
				}
//				if (line.length > 3) {
//					this.addText("it", line[0], line[3]);
//				}
				if (line.length > 3) {
					this.addText("en", line[0], line[3]);
				}
			}
		}
	}
	
	/**
	 * Returns the text of the given field in the currently active language.
	 * 
	 * @param fieldName
	 * @return text or null, if field is not found or active language is null
	 */
	public String getText(String fieldName) {
		HashMap<String, String> languageEntries = entries.get(activeLanguageCode);
		if (languageEntries == null) {
			return null;
		}		
		return languageEntries.get(fieldName);		
	}
	
	/**
	 * Adds a text for a given field in a given language.
	 * 
	 * @param languageCode
	 * @param fieldName
	 * @param text
	 */
	public void addText(String languageCode, String fieldName, String text) {
		if (entries.get(languageCode) == null) {
			entries.put(languageCode, new HashMap<String, String>());
		}
		
		entries.get(languageCode).put(fieldName, text);		
	}
	
	/**
	 * Print all entries to System.out
	 */
	public void printAllEntries() {
		//iterate through languages
		Iterator<String> itLang = entries.keySet().iterator();
		while (itLang.hasNext()) {
			String language = itLang.next();
			
			//iterate through fieldNames
			Iterator<String> itField = entries.get(language).keySet().iterator();
			while (itField.hasNext()) {
				String fieldName = itField.next();
				
				//print
				System.out.println(language + " " + fieldName + " " + entries.get(language).get(fieldName) );
			}
		}
	}
	
	/**
	 * Print all languages to System.out
	 */
	public void printAllLanguages(){
		//iteratore through languages
		Iterator<String> it = entries.keySet().iterator();
		while (it.hasNext()) {
			//print
			System.out.println( it.next() );
		}
	}
	
	public void setActiveLanguage(String languageCode) {
		this.activeLanguageCode = languageCode;
	}
	
	public String getActiveLanguage() {
		return activeLanguageCode;
	}
}
