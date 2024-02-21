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
package ch.wsl.sustfor.sorsim.gui;

import java.awt.FileDialog;
import java.awt.Frame;

import javax.swing.JPanel;

import ch.wsl.sustfor.lang.SorSimLanguageManager;
import ch.wsl.sustfor.sorsim.StaticSettings;
import ch.wsl.sustfor.sorsim.controller.Presenter;

/**
 * 
 * @author Stefan Holm
 *
 */
public class SorSimPanel extends JPanel {	
	
	protected SorSimLanguageManager lang = SorSimLanguageManager.getInstance();
    protected Presenter presenter = new Presenter();	
	
	protected String showFileDialog(boolean saveDialog) {
		FileDialog fd = new FileDialog((Frame) null, lang.getText(lang.lblDateiAuswaehlen), saveDialog ? FileDialog.SAVE : FileDialog.LOAD);
		fd.setDirectory(StaticSettings.BASE_DIR);
		fd.setVisible(true);
		
		if (fd.getFile() != null) {
			return fd.getDirectory() + fd.getFile();
		}
		
		return null;
	}
	
	public void setExtendedMode(@SuppressWarnings("unused") boolean flag) {
		//can be implemented in subclasses if necessary
	}
}
