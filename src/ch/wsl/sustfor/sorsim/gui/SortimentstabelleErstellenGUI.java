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
import ch.wsl.sustfor.baumschaft.base.Baumart;
import ch.wsl.sustfor.lang.SorSimLanguageManager;
import ch.wsl.sustfor.sorsim.StaticSettings;
import ch.wsl.sustfor.sorsim.controller.BaumlistenSortimentierer;

/**
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public class SortimentstabelleErstellenGUI extends JPanel implements ISorSimFrame {
	
	private final BaumlistenSortimentierer baumlistSort = new BaumlistenSortimentierer();
	
	private SorSimLanguageManager lang = SorSimLanguageManager.getInstance();	
	
	//Bäume
	private final PanelBaeume pnlBaeume = new PanelBaeume();
		
	//Allgemein
	private final PanelSortimentsVorgaben pnlSortimentsVorgaben = new PanelSortimentsVorgaben(baumlistSort);		
	
	//Darstellung der Ergebnisse
	private final PanelDarstellungErgebnisse pnlDarstellungErgebnisse = new PanelDarstellungErgebnisse();
		
	//Buttons
	private JButton btnSortimentieren;
		
	//Ergebnis
	private final PanelErgebnis pnlErgebnis = new PanelErgebnis();
	
	public SortimentstabelleErstellenGUI() {		
		//init fields etc
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
		
		//panel Baeume
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		pnlBaeume.addBaumartChangeListener(e -> {
			Baumart baumartSelected = pnlBaeume.getBaumart();	
			if (baumartSelected != null) {
				pnlSortimentsVorgaben.updateLaengenKlassenKuerzelCombo(baumartSelected);
			}	
		});
		this.add(pnlBaeume, c);
		
		//panel Sortimentsvorgaben
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		this.add(pnlSortimentsVorgaben, c);
		
		//panel DarstellungErgebnisse
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		this.add(pnlDarstellungErgebnisse, c);
		
		//panel Buttons
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		JPanel pnlButtons = initPanelButtons();
		this.add(pnlButtons, c);
		
		//panel Ergebnis
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 100;
		c.weighty = 100;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		this.add(pnlErgebnis, c);
	}
	
	private JPanel initPanelButtons() {
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
						sortimentieren();
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

	private void sortimentieren() {
		//Sortimentstückliste zurücksetzen
		baumlistSort.clearSortimentsStueckListe();
		
		//Baumliste erstellen
		pnlBaeume.baumschaefteEinfuellen(baumlistSort.getBsDefListe());		
		
		//Sortimentsvorgaben lesen
		try {
			pnlSortimentsVorgaben.readFileSortimentsVorgaben();
		} catch (Exception e) {
	        JOptionPane.showMessageDialog(this, e.getClass().getSimpleName() + "\n" + e.getMessage(), lang.getText(lang.errFehler), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}
		
		//Sortimentieren
		baumlistSort.sortimentieren(
				pnlSortimentsVorgaben.getSelectedLaengenKlasse(), 
				pnlBaeume.getSchaftanteil() / 100.0, 
				pnlSortimentsVorgaben.getMindestZopf(),
				pnlSortimentsVorgaben.getStockhoehe()
				);
		
		//Ausgabe Zusammenfassung
		pnlErgebnis.writeZusammenfassung(baumlistSort.getSsListe());
		
		//Warnung, falls keine Sortimente erstellt wurden
		if (baumlistSort.getSsListe().size() == 0) {			
			JOptionPane.showMessageDialog(null, lang.getText(lang.errKeineSortimenteErstellt), "SorSim", JOptionPane.WARNING_MESSAGE);			
			return; //keine Ausgabe auf Datei
		}
		
		//Ausgabe File
		pnlDarstellungErgebnisse.writeToFileIfSelected(baumlistSort.getSsListe());
	}
	
	private void initFields() {
		String folder = StaticSettings.BASE_DIR;
		pnlSortimentsVorgaben.setSvFile(folder + "SortimentsVorgabenListe.csv");
		pnlDarstellungErgebnisse.setSsFile(folder + "Sortimentstabelle.csv");	
		pnlDarstellungErgebnisse.setWriteToFileChecked(true);
	}

	@Override
	public void setExtendedMode(boolean flag) {
		pnlBaeume.setExtendedMode(flag);
		pnlSortimentsVorgaben.setExtendedMode(flag);
	}

	@Override
	public void setSchaftformFunktion(BaumSchaftformFunktion schaftformFkt) { 
		baumlistSort.selectSchaftformFunktion(schaftformFkt);
			
		//Baumarten
		pnlBaeume.setSchaftformFunktion( schaftformFkt );
	}

	@Override
	public void updateLabels() {
		//Bäume
		pnlBaeume.updateLabels();
		
		//Sortimentsvorgaben
		pnlSortimentsVorgaben.updateLabels();

		//Darstellung Ergebnisse
		pnlDarstellungErgebnisse.updateLabels();

		//Buttons
		btnSortimentieren.setText(lang.getText(lang.lblSortimentieren));
		
		//Ergebnis
		pnlErgebnis.updateLabels();	
	}

	@Override
	public void setAnzahlNachkommastellen(int anzahl) {
		pnlErgebnis.setAnzahlStellenNachKomma(anzahl);
	}
}
