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
package ch.wsl.sustfor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Stefan Holm
 *
 */
public class CsvReader {

	private final String delimiter;	
	private final String commentCode;
	private final boolean skipComments;
	private final boolean TRIM_FIELDS = true;
	
	public CsvReader(String delimiter, String commentCode, boolean skipComments) {
		this.delimiter = delimiter;
		this.commentCode = commentCode;
		this.skipComments = skipComments;
	}
	
	public CsvReader() {
		this(";", "", false);
	}
	
	public List<String[]> readFile(String filename) {
		List<String[]> result = new ArrayList<>();
		BufferedReader in = openFile(filename);
		
		if (in == null) {
			System.err.println("Error opening file '" + filename + "'");
			return result;
		}
			
		try {
			//read line, split fields, and add to result
			String line;
			while ((line = in.readLine()) != null) {
				
				if (skipComments && line.startsWith(commentCode)) {
					continue;
				}
				
				String[] fields = line.split(delimiter);
				
				if (TRIM_FIELDS) {
					for (int i=0; i<fields.length; i++) {
						fields[i] = fields[i].trim();
					}
				}
				result.add( fields );				
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}
	
	public List<String[]> readFileThrowException(String filename) throws IOException {
		List<String[]> result = new ArrayList<>();
		BufferedReader in = openFileThrowException(filename);
		
		//read line, split fields, and add to result
		String line;
		while ((line = in.readLine()) != null) {

			if (skipComments && line.startsWith(commentCode)) {
				continue;
			}

			String[] fields = line.split(delimiter);

			if (TRIM_FIELDS) {
				for (int i=0; i<fields.length; i++) {
					fields[i] = fields[i].trim();
				}
			}				

			result.add( fields );				
		}
		in.close();
		return result;
	}
	
	private BufferedReader openFile(String filename) {
		//try to open from jar
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);	
		if (is != null) {
			return new BufferedReader(new InputStreamReader(is));
		}
		
		//otherwise, try to open from file system
		File file = new File(filename);
		try {
			//open file
			return new BufferedReader( new FileReader(file));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private BufferedReader openFileThrowException(String filename) throws FileNotFoundException {
		//try to open from jar
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);	
		if (is != null) {
			return new BufferedReader(new InputStreamReader(is));
		}
		
		//otherwise, try to open from file system
		File file = new File(filename);
		return new BufferedReader( new FileReader(file));
	}
}
