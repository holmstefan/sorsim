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

import java.awt.Color;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import ch.wsl.sustfor.lang.SorSimLanguageManager;

/**
 * 
 * @author Stefan Holm
 *
 */
public class NumberInputVerifier extends InputVerifier {

	private double low = Double.MIN_VALUE;;
	private double high = Double.MAX_VALUE;	
	private SorSimLanguageManager lang = SorSimLanguageManager.getInstance();
	
	public NumberInputVerifier(){
	}
	
	public NumberInputVerifier(double low, double high) {
		this.low = low;
		this.high = high;
	}

	@Override
	public boolean verify(JComponent input) {
		JTextField textField = ((JTextField) input);
		String text = textField.getText();
		boolean error = false;
		try {
			double value = Double.parseDouble(text);
			if (value < low) {
				error = true;
			}
			if (value > high) {
				error = true;
			}			
		} catch (NumberFormatException e) {
			error = true;
		}

		if (error == false) {			
			//all checks ok
			textField.setForeground(Color.BLACK);
			return true;
		}
		
		//error
		textField.setForeground(Color.RED);
		JOptionPane.showOptionDialog(null, lang.getText(lang.errNumberInputVerifier1) + " " + getLow() + " " + lang.getText(lang.errNumberInputVerifier2) + " " + getHigh() + " " + lang.getText(lang.errNumberInputVerifier3), "SorSim", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);	
		return false;
	}
	
	private String getLow() {
		String result = String.valueOf(low);
		while (result.endsWith("0")) {
			result = result.substring(0, result.length()-1); //remove 0 at the end
		}
		if (result.endsWith(".")) {
			result = result.substring(0, result.length()-1); //remove . at the end
		}
		return result;
	}
	
	private String getHigh() {
		String result = String.valueOf(high);
		while (result.endsWith("0")) {
			result = result.substring(0, result.length()-1); //remove 0 at the end
		}
		if (result.endsWith(".")) {
			result = result.substring(0, result.length()-1); //remove . at the end
		}
		return result;
	}
}
