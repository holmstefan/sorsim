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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.lang.SorSimLanguageManager;
import ch.wsl.sustfor.sorsim.StaticSettings;
import ch.wsl.sustfor.sorsim.controller.BaumlistenSortimentierer;
import ch.wsl.sustfor.sorsim.controller.Presenter;


/**
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public class BaumListeSortimentierenGUI extends JPanel implements ISorSimFrame {
	
    private final BaumlistenSortimentierer baumlistSort = new BaumlistenSortimentierer();
    private Presenter presenter = new Presenter();
	private SorSimLanguageManager lang = SorSimLanguageManager.getInstance();
	
	//Baumliste
	private final PanelBaumliste pnlBaumListe = new PanelBaumliste(baumlistSort);
	
	//Sortimentsvorgaben
	private final PanelSortimentsVorgaben pnlSortimentsVorgaben = new PanelSortimentsVorgaben(baumlistSort);
	
	//Darstellung der Ergebnisse
	private final PanelDarstellungErgebnisse pnlDarstellungErgebnisse = new PanelDarstellungErgebnisse();
	
	private JButton btnSortimentieren;
	
	//Ergebnis
	private final PanelErgebnis pnlErgebnis = new PanelErgebnis();

	public BaumListeSortimentierenGUI() {
		//init text fields, checkboxes etc
		initContent();
		initFields();

		//show window
		this.setVisible(true);
	}
	
	private void initContent() {
		//remove all
		this.removeAll();

		//set layout
		this.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//panel BaumListe
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		this.add(pnlBaumListe, c);
		
		//panel SortimentsVorgaben
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
//		pnlSortimentsVorgaben = new PanelSortimentsVorgaben(blsx);
		this.add(pnlSortimentsVorgaben, c);
		
		//panel DarstellungErgebnisse
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
//		JPanel pnlDarstellungErgebnisse = initPanelDarstellungErgebnisse();
		this.add(pnlDarstellungErgebnisse, c);
		
		//panel Buttons
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		JPanel pnlButtons = initPanelButtons();
		this.add(pnlButtons, c);
		
		//panel Ergebnis
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 5;
		c.weightx = 100;
		c.weighty = 100;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		this.add(pnlErgebnis, c);
	}

	private JPanel initPanelButtons(){
		//init panel
		JPanel pnlButtons = new JPanel();
		
		//set layout
		pnlButtons.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//button
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0,5,5,5);
		c.weightx = 2;
		btnSortimentieren = new JButton();
		btnSortimentieren.setAction(new AbstractAction(lang.getText(lang.lblSortimentieren)){
			@Override
			public void actionPerformed(ActionEvent e) {				
				Thread t = new Thread(){
					@Override
					public void run() {
						btnSortimentieren.setEnabled(false);
						btnSortimentieren.setText(lang.getText(lang.lblBitteWarten));
						btnSortimentierenClick();
						btnSortimentieren.setEnabled(true);	
						btnSortimentieren.setText(lang.getText(lang.lblSortimentieren));					
					}
				};
				t.start();
			}        	
        });
		pnlButtons.add(btnSortimentieren, c);
		
		return pnlButtons;
	}
	
	private void btnSortimentierenClick(){
        //load files
		try {
			pnlBaumListe.readFileBaumschaefte();
			pnlSortimentsVorgaben.readFileSortimentsVorgaben();
		} catch (Exception e) {
	        JOptionPane.showMessageDialog(this, e.getClass().getSimpleName() + "\n" + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}
		
		//sortimentieren
		baumlistSort.clearSortimentsStueckListe();
		baumlistSort.sortimentieren(
				pnlSortimentsVorgaben.getSelectedLaengenKlasse(),
				pnlBaumListe.getSchaftanteil(),
				pnlSortimentsVorgaben.getMindestZopf(),
				pnlSortimentsVorgaben.getStockhoehe()
				);
		
		//Ausgabe Zusammenfassung
		pnlErgebnis.writeZusammenfassung(baumlistSort.getSsListe());

		//Ausgabe File
		pnlDarstellungErgebnisse.writeToFileIfSelected(baumlistSort.getSsListe());
	}
	
	private void initFields() {
		String folder = StaticSettings.BASE_DIR;
		pnlBaumListe.setFilename(folder + "Baumliste.csv");
		pnlSortimentsVorgaben.setSvFile(folder + "SortimentsVorgabenListe.csv");
		pnlDarstellungErgebnisse.setSsFile(folder + "Sortimentsliste.csv");	
		pnlDarstellungErgebnisse.setWriteToFileChecked(true);
	}

	@Override
	public void setExtendedMode(boolean flag) {
		pnlSortimentsVorgaben.setExtendedMode(flag);
	}

	@Override
	public void setSchaftformFunktion(BaumSchaftformFunktion schaftformFkt) {        
        baumlistSort.selectSchaftformFunktion(schaftformFkt);
		pnlBaumListe.setSchaftformFunktion(schaftformFkt);
	}

	@Override
	public void updateLabels() {		
		//Buttons
		btnSortimentieren.setText(lang.getText(lang.lblSortimentieren));
		
		//Panels
		pnlBaumListe.updateLabels();
		pnlSortimentsVorgaben.updateLabels();
		pnlDarstellungErgebnisse.updateLabels();
		pnlErgebnis.updateLabels();
	}

	@Override
	public void setAnzahlNachkommastellen(int anzahl) {
		presenter.setAnzahlStellenNachKomma(anzahl);
		pnlErgebnis.setAnzahlStellenNachKomma(anzahl);
	}
}
