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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import ch.wsl.sustfor.baumschaft.base.Baumart;
import ch.wsl.sustfor.sorsim.StaticSettings;
import ch.wsl.sustfor.sorsim.controller.BaumlistenSortimentierer;
import ch.wsl.sustfor.sorsim.model.SortimentVorgabenListe;
import ch.wsl.sustfor.sorsim.model.SortimentVorgabenListe.SortierKriterium;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.CodeLaengenKlassen;
import ch.wsl.sustfor.util.SortedList.SortOrder;

/**
 * 
 * @author Stefan Holm
 *
 */
public class PanelSortimentsVorgaben extends SorSimPanel {	

    private BaumlistenSortimentierer baumlistSort;
	
	//Sortimentsvorgaben
    private JLabel lblFileSortimentsVorgaben;
	private JTextField txtFileSortimentsVorgaben;
	private JLabel lblLaengenklassenKuerzel;
	private JComboBox<CodeLaengenKlassen> cmbLaengenklassenKuerzel;
	private JCheckBox chkMindestZopf;
	private JSpinner txtMindestZopf;
	private JCheckBox chkStockhoehe;
	private JSpinner txtStockhoehe;	
	private JLabel lblSortierKriterium;
	private JComboBox<SortierKriterium> cmbSortierKriterium;
	
	public PanelSortimentsVorgaben(BaumlistenSortimentierer baumlistSort) {
		if (StaticSettings.USE_COLORS) {
			this.setOpaque(false);
			this.setBackground(StaticSettings.COLOR_SORTIMENTSVORGABEN);
		}
		this.baumlistSort = baumlistSort;
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
		this.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblSortimentsVorgaben)));
		
		//set layout
		this.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblFileSortimentsVorgaben = new JLabel(lang.getText(lang.lblSortimentsVorgabenFile));
		this.add(lblFileSortimentsVorgaben, c);		
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtFileSortimentsVorgaben = new JTextField();
		txtFileSortimentsVorgaben.setEditable(false);
		txtFileSortimentsVorgaben.setToolTipText(lang.getText(lang.lblDoppelKlickZumOeffnen));
		txtFileSortimentsVorgaben.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					new SortimentsVorgabenListeBearbeiten(txtFileSortimentsVorgaben.getText());
				}
			}
		});	
		if (StaticSettings.USE_COLORS) {
			txtFileSortimentsVorgaben.setOpaque(false);
		}
		this.add(txtFileSortimentsVorgaben, c);	

		//button
        c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 1;
		c.insets = new Insets(0,0,5,5);
		c.anchor = GridBagConstraints.WEST;
		JButton btn1 = new JButton();
		btn1.setMinimumSize( new Dimension(20, 18) );
		btn1.setPreferredSize( new Dimension(20, 18) );
		btn1.setMaximumSize( new Dimension(20, 18) );
		btn1.setAction(new AbstractAction("..."){
			@Override
			public void actionPerformed(ActionEvent e) {
				String filename = showFileDialog(false);
				if (filename != null)  {
					txtFileSortimentsVorgaben.setText(filename);
				}
			}        	
        });	
		if (StaticSettings.USE_COLORS) {
			btn1.setOpaque(false);
		}
		this.add(btn1, c);	
		
		//label
        c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 2;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,5,0,5);
		lblLaengenklassenKuerzel = new JLabel(lang.getText(lang.lblLaengenklasenKuerzel));
		this.add(lblLaengenklassenKuerzel, c);		
		
		//combo box
        c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 2;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10,5,5,5);
		cmbLaengenklassenKuerzel = new JComboBox<>();
		cmbLaengenklassenKuerzel.setMinimumSize( new Dimension(80, 20) );
		cmbLaengenklassenKuerzel.setPreferredSize( new Dimension(80, 20) );
		this.add(cmbLaengenklassenKuerzel, c);

		//checkbox
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,5,0,5);
		chkMindestZopf = new JCheckBox( lang.getText(lang.lblImmerFolgendenMindestzopfVerwenden_cm) );
		chkMindestZopf.addChangeListener(e -> txtMindestZopf.setEnabled(chkMindestZopf.isSelected()));
		if (StaticSettings.USE_COLORS) {
			chkMindestZopf.setOpaque(false);
		}
		this.add(chkMindestZopf, c);	
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.insets = new Insets(5,5,5,5);
		txtMindestZopf = new JSpinner(new SpinnerNumberModel(7, 0, 100, 1));
		txtMindestZopf.setMinimumSize( new Dimension(50, 20) );
		txtMindestZopf.setPreferredSize( new Dimension(50, 20) );
		txtMindestZopf.setEnabled(false);
		this.add(txtMindestZopf, c);
		
		//checkbox
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		chkStockhoehe = new JCheckBox( lang.getText(lang.lblImmerFolgendeStockhoeheVerwenden_cm) );
		chkStockhoehe.addChangeListener(e -> txtStockhoehe.setEnabled(chkStockhoehe.isSelected()));
		if (StaticSettings.USE_COLORS) {
			chkStockhoehe.setOpaque(false);
		}
		this.add(chkStockhoehe, c);	
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 4;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.insets = new Insets(5,5,5,5);
		txtStockhoehe = new JSpinner(new SpinnerNumberModel(30, 0, 200, 1));
		txtStockhoehe.setMinimumSize( new Dimension(50, 20) );
		txtStockhoehe.setPreferredSize( new Dimension(50, 20) );
		txtStockhoehe.setEnabled(false);
		this.add(txtStockhoehe, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblSortierKriterium = new JLabel(lang.getText(lang.lblKriterium));
		this.add(lblSortierKriterium, c);
		
		//combo box
        c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		cmbSortierKriterium = new JComboBox<>();
		cmbSortierKriterium.addActionListener(e -> {
			svListeSortimentieren(); // Neu sortieren nötig
		});
		this.add(cmbSortierKriterium, c);
	}
	
	private void svListeSortimentieren() {
			if (cmbSortierKriterium.getSelectedIndex() < 0 /*|| cmbSortierReihenfolge.getSelectedIndex() < 0*/) {
				return;
			}
			
			baumlistSort.getSvListe().setSortKriterium( SortierKriterium.values()[cmbSortierKriterium.getSelectedIndex()] );
			baumlistSort.getSvListe().setSortOrder( SortOrder.Descending );
			baumlistSort.getSvListe().sort();
			printSVListenSortierReihenfolge();
	}
	
	public void readFileSortimentsVorgaben() throws IOException, NumberFormatException {
		readFileSortimentsVorgaben(txtFileSortimentsVorgaben.getText());
	}
	
	private void readFileSortimentsVorgaben(String filePath) throws IOException, NumberFormatException {
		baumlistSort.clearSortimentsStueckListe();
        presenter.readSortimentsVorgabenFromFile(baumlistSort.getSvListe(), filePath);

        svListeSortimentieren();
        System.out.println(presenter.printSortimentsVorgabenListe(baumlistSort.getSvListe()));		
	}
	
	private void printSVListenSortierReihenfolge() {		
		StringBuilder sb = new StringBuilder();
		SortimentVorgabenListe svListe = baumlistSort.getSvListe();;

		sb.append("Sortier-Reihenfolge: \n");
		if (svListe.size() > 0) {
			sb.append("FirstItem = " + svListe.get(0).getBeschrieb() + "\n");
			sb.append("LastItem = " + svListe.get(svListe.size()-1).getBeschrieb() + "\n");
		}
		else {
			sb.append("<empty list>");
		}
		
		System.out.println(sb.toString());
	}
	
	private void initFields() {
        cmbSortierKriterium.removeAllItems();
        for (int i=0; i<SortierKriterium.values().length; i++) {
        	cmbSortierKriterium.addItem(SortierKriterium.values()[i]);
        }
        cmbSortierKriterium.setSelectedItem(SortierKriterium.WertProEinheit);
        
        cmbLaengenklassenKuerzel.removeAllItems();
        for (int i=0; i<CodeLaengenKlassen.values().length; i++) {
        	cmbLaengenklassenKuerzel.addItem(CodeLaengenKlassen.values()[i]);
        }
        cmbLaengenklassenKuerzel.setSelectedIndex(0);
	}

	/**
	 * Stellt sicher, dass bei Laubholz nur die Längenklasse L1 selektiert werden kann
	 * @param baumart
	 */
	public void updateLaengenKlassenKuerzelCombo(Baumart baumart) {
		cmbLaengenklassenKuerzel.removeAllItems();
		if (baumart.isLaubholz()) {
			cmbLaengenklassenKuerzel.addItem(CodeLaengenKlassen.L1);
		}
		else {
	        for (int i=0; i<CodeLaengenKlassen.values().length; i++) {
	        	cmbLaengenklassenKuerzel.addItem(CodeLaengenKlassen.values()[i]);
	        }
		}
		cmbLaengenklassenKuerzel.setSelectedIndex(0);
	}
	
	public void setSvFile(String fileNameAndPath) {
		txtFileSortimentsVorgaben.setText(fileNameAndPath);
	}
	
	public CodeLaengenKlassen getSelectedLaengenKlasse() {
		return (CodeLaengenKlassen) cmbLaengenklassenKuerzel.getSelectedItem();
	}
	
	/**
	 * Gibt den eingestellten Mindestzopf zurück. Falls (-1) zurückgegeben wird,
	 *  soll der eingestellte Mindestzopf nicht berücksichtigt werden!
	 * @return
	 */
	public float getMindestZopf() {
		if (chkMindestZopf.isSelected() == false) {
			return -1;
		}
		return (Integer) txtMindestZopf.getValue();
	}
	
	/**
	 * Gibt die eingestellte Stockhoehe zurück. Falls (-1) zurückgegeben wird,
	 *  soll die eingestellte Stockhoehe nicht berücksichtigt werden!
	 * @return
	 */
	public float getStockhoehe() {
		if (chkStockhoehe.isSelected() == false) {
			return -1;
		}
		return (Integer) txtStockhoehe.getValue();
	}
	
	public void updateLabels() {		
		((TitledBorder) this.getBorder()).setTitle(lang.getText(lang.lblSortimentsVorgaben));		
		lblFileSortimentsVorgaben	.setText( lang.getText(lang.lblSortimentsVorgabenFile) );
		lblLaengenklassenKuerzel	.setText( lang.getText(lang.lblLaengenklasenKuerzel) );
		chkMindestZopf				.setText( lang.getText(lang.lblImmerFolgendenMindestzopfVerwenden_cm) );
		chkStockhoehe				.setText( lang.getText(lang.lblImmerFolgendeStockhoeheVerwenden_cm) );
		lblSortierKriterium			.setText( lang.getText(lang.lblKriterium) );
	}
	
	@Override
	public void setExtendedMode(boolean flag) {
		lblSortierKriterium.setVisible(flag);
		cmbSortierKriterium.setVisible(flag);
		chkMindestZopf.setVisible(flag);
		txtMindestZopf.setVisible(flag);
		chkStockhoehe.setVisible(flag);
		txtStockhoehe.setVisible(flag);
	}
}
