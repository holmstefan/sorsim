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

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import ch.wsl.sustfor.sorsim.StaticSettings;
import ch.wsl.sustfor.sorsim.model.SortimentsStueck;

/**
 * 
 * @author Stefan Holm
 *
 */
public class PanelDarstellungErgebnisse extends SorSimPanel {
	
	//Darstellung der Ergebnisse
	private JTextField txtFileSortimentsStueckListen;
	private JCheckBox chkSortimenteInAusgabeDateiSchreiben;
	private JButton btnSortimentsStueckListenFileSelect;
	
	//Standard-Konstruktor
	public PanelDarstellungErgebnisse() {
		if (StaticSettings.USE_COLORS) {
			this.setOpaque(false);
			this.setBackground(StaticSettings.COLOR_DARSTELLUNG_ERGEBNIS);
		}
		initContent();
		initFields();
	}
	
	@Override //necessary to avoid artefacts when painting backgrounds with an alpha value < 1
    protected void paintComponent(Graphics g) {
        g.setColor( getBackground() );
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

	private void initContent(){
		//init panel
		this.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblDarstellungErgebnisse)));

		//set layout
		this.setLayout( new GridBagLayout() );
		GridBagConstraints c;

		//check box
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		chkSortimenteInAusgabeDateiSchreiben = new JCheckBox(lang.getText(lang.lblZieldatei));
		chkSortimenteInAusgabeDateiSchreiben.addChangeListener(e -> {
			boolean selected = ((JCheckBox) e.getSource()).isSelected();
			txtFileSortimentsStueckListen.setEnabled(selected);
			btnSortimentsStueckListenFileSelect.setEnabled(selected);
		});
		if (StaticSettings.USE_COLORS) {
			chkSortimenteInAusgabeDateiSchreiben.setOpaque(false);
		}
		this.add(chkSortimenteInAusgabeDateiSchreiben, c);

		//text field
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,35,5,5);
		txtFileSortimentsStueckListen = new JTextField();
		txtFileSortimentsStueckListen.setEditable(false);
		txtFileSortimentsStueckListen.setToolTipText(lang.getText(lang.lblDoppelKlickZumOeffnen));
		txtFileSortimentsStueckListen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtFileSortimentsStueckListen.isEnabled() == false) {
					return;
				}
				if (e.getClickCount() == 2) {
					try {
						Desktop.getDesktop().open(new File(txtFileSortimentsStueckListen.getText()));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		if (StaticSettings.USE_COLORS) {
			txtFileSortimentsStueckListen.setOpaque(false);
		}
		this.add(txtFileSortimentsStueckListen, c);		

		//button
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,0,5,5);
		c.anchor = GridBagConstraints.WEST;
		btnSortimentsStueckListenFileSelect = new JButton();
		btnSortimentsStueckListenFileSelect.setMinimumSize( new Dimension(20, 18) );
		btnSortimentsStueckListenFileSelect.setPreferredSize( new Dimension(20, 18) );
		btnSortimentsStueckListenFileSelect.setMaximumSize( new Dimension(20, 18) );
		btnSortimentsStueckListenFileSelect.setAction(new AbstractAction("..."){
			@Override
			public void actionPerformed(ActionEvent e) {
				String filename = showFileDialog(true);
				if (filename != null)  {
					txtFileSortimentsStueckListen.setText(filename);
				}
			}        	
		});
		if (StaticSettings.USE_COLORS) {
			btnSortimentsStueckListenFileSelect.setOpaque(false);
		}
		this.add(btnSortimentsStueckListenFileSelect, c);
	}
	
	private void initFields() {		
		String folder = "data/";
		txtFileSortimentsStueckListen.setText(folder + "Sortimentsliste.csv");
		chkSortimenteInAusgabeDateiSchreiben.setSelected(true);
	}

	public void setSsFile(String fileNameAndPath) {
		txtFileSortimentsStueckListen.setText(fileNameAndPath);
	}
	
	private File getSsFile() {
		return new File(txtFileSortimentsStueckListen.getText());
	}
	
	public void setWriteToFileChecked(boolean checked) {
		chkSortimenteInAusgabeDateiSchreiben.setSelected(checked);
	}
	
	private boolean isWriteToFileChecked() {
		return chkSortimenteInAusgabeDateiSchreiben.isSelected();
	}
	
	public void writeToFileIfSelected(List<SortimentsStueck> ssList) {		
		if ( this.isWriteToFileChecked() ) {
			File file = this.getSsFile();
			
			//save file
			presenter.writeSortimentsStueckListeToFile(ssList, file.getAbsolutePath());
			
			//try to open file
			try {
				Desktop.getDesktop().open(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateLabels() {		
		((TitledBorder) this.getBorder()).setTitle(lang.getText(lang.lblDarstellungErgebnisse));
		chkSortimenteInAusgabeDateiSchreiben.setText(lang.getText(lang.lblZieldatei));		
	}
}
