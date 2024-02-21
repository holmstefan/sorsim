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
package ch.wsl.sustfor.sorsim.gui.util;

import javax.swing.text.PlainDocument;

/**
 * 
 * @author Stefan Holm
 *
 */
public class NumberDocument extends PlainDocument {
	
	private int maxLength = Integer.MAX_VALUE;
	private boolean intOnly = false;
	
	public NumberDocument() {
	}
	
	public NumberDocument(int maxLength) {
		this.maxLength = maxLength;
	}
	
	@Override
	public void insertString(int offset, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
		//Länderspezfische Separatoren abfragen
		char decimalSeparator = (new java.text.DecimalFormatSymbols()).getDecimalSeparator();
		char groupingSeparator = (new java.text.DecimalFormatSymbols()).getGroupingSeparator();
		char minusSign = (new java.text.DecimalFormatSymbols()).getMinusSign();
		
		//Ziffern und weitere gültige Zeichen hinzufügen
		String valid = "0123456789" + groupingSeparator + minusSign;
		if (intOnly == false) {
			valid += decimalSeparator;
		}
		
		for (int i=0; i<str.length();i++) {
			if (valid.indexOf(str.charAt(i)) == -1) {
				//Beep
				System.err.println("Falsches Zeichen");
				java.awt.Toolkit.getDefaultToolkit().beep();
				return;
			}
		}
		
		//check maximum length
		int lengthLeft = maxLength - super.getLength();
		
		//cut string if necessary
		if (lengthLeft < str.length()) {
			str = str.substring(0, lengthLeft);
		}
		
		//Wichtig: Aufruf der übergeordneten Methode
		super.insertString(offset, str, a);
	}
}
