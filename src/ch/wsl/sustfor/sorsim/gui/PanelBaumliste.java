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
import java.util.HashMap;

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

import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition;
import ch.wsl.sustfor.baumschaft.lfispline.BaumschaftformFkt_Lfi_Spline;
import ch.wsl.sustfor.lang.SorSimLanguageManager.AutoText;
import ch.wsl.sustfor.sorsim.StaticSettings;
import ch.wsl.sustfor.sorsim.controller.BaumlistenSortimentierer;
import ch.wsl.sustfor.sorsim.model.D7mBhdRelations.Standort;
import ch.wsl.sustfor.sorsim.model.H0Calculator;

/**
 * 
 * @author Stefan Holm
 *
 */
public class PanelBaumliste extends SorSimPanel {
	
	private JLabel lblFileBaumliste;
	private JLabel lblZuVerwendenderSchaftanteil;
    private JTextField txtFileBaumliste;
	private JSpinner txtZuVerwendenderSchaftanteil;
	private JCheckBox chkSchaftanteilBerechnen;
	private JLabel lblD7mBhD;
	private JComboBox<AutoText> cmbD7mBhD;
	private JCheckBox chkHoeheBerechnen;
	private JLabel lblGrundflaechenmittelstamm;
	private JSpinner txtGrundflaechenmittelstamm;
	private JLabel lblHoeheGrundflaechenmittelstamm;
	private JSpinner txtHoeheGrundflaechenmittelstamm;

    private BaumlistenSortimentierer baumlistSort;
    private boolean lfiSplineSelected;

	public PanelBaumliste(BaumlistenSortimentierer baumlistSort) {
		if (StaticSettings.USE_COLORS) {
			this.setOpaque(false);
			this.setBackground(StaticSettings.COLOR_BAEUME);
		}
		this.baumlistSort = baumlistSort;
		initContent();		

		//trigger change listener
		chkHoeheBerechnen.setSelected(true);
		chkHoeheBerechnen.doClick(); //sets chk selection to false
	}
	
	@Override //necessary to avoid artefacts when painting backgrounds with an alpha value < 1
    protected void paintComponent(Graphics g) {
        g.setColor( getBackground() );
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

	private void initContent() {
		//init panel
		this.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblBaumliste)));

		//set layout
		this.setLayout( new GridBagLayout() );
		GridBagConstraints c;	

		//label
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblFileBaumliste = new JLabel(lang.getText(lang.lblBaumlistenFile));
		this.add(lblFileBaumliste, c);		

		//text field
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtFileBaumliste = new JTextField();
		txtFileBaumliste.setEditable(false);
		txtFileBaumliste.setToolTipText(lang.getText(lang.lblDoppelKlickZumOeffnen));
		txtFileBaumliste.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					try {
						Desktop.getDesktop().open(new File(txtFileBaumliste.getText()));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});		
		if (StaticSettings.USE_COLORS) {
			txtFileBaumliste.setOpaque(false);
		}
		this.add(txtFileBaumliste, c);		

		//button
		c = new GridBagConstraints();
		c.gridx = 2;
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
					txtFileBaumliste.setText(filename);
				}
			}        	
		});	
		if (StaticSettings.USE_COLORS) {
			btn1.setOpaque(false);
		}
		this.add(btn1, c);	

		//label
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblZuVerwendenderSchaftanteil = new JLabel(lang.getText(lang.lblZuVerwendenderSchaftanteil));
		this.add(lblZuVerwendenderSchaftanteil, c);		

		//text field
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 1;
		c.insets = new Insets(0,5,5,5);
		c.anchor = GridBagConstraints.WEST;
		txtZuVerwendenderSchaftanteil = new JSpinner( new SpinnerNumberModel(100, 0, 100, 1) );
		//			txtZuVerwendenderSchaftanteil.setDocument(new NumberDocument(5));
		//			txtZuVerwendenderSchaftanteil.setInputVerifier(new NumberInputVerifier(0,1));
		txtZuVerwendenderSchaftanteil.setMinimumSize( new Dimension(50, 20) );
		txtZuVerwendenderSchaftanteil.setPreferredSize( new Dimension(50, 20) );
		this.add(txtZuVerwendenderSchaftanteil, c);

		//checkbox
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		c.gridheight = 2;
		c.anchor = GridBagConstraints.NORTH;
		chkSchaftanteilBerechnen = new JCheckBox(lang.getText(lang.lblSchaftanteilBerechnen));
		if (StaticSettings.USE_COLORS) {
			chkSchaftanteilBerechnen.setOpaque(false);
		}
		this.add(chkSchaftanteilBerechnen, c);	

		//label
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,5,0,5);
		lblD7mBhD = new JLabel(lang.getText(lang.lblD7mBHDRelationExt));
		this.add(lblD7mBhD, c);		

		//combo box
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,5,5,5);
		cmbD7mBhD = new JComboBox<>();;
		cmbD7mBhD.addItem(lang.getAutoText(lang.lblStandortGut));
		cmbD7mBhD.addItem(lang.getAutoText(lang.lblStandortMager));
		this.add(cmbD7mBhD, c);
		
		//checkbox
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,5,0,5);
		c.gridwidth = 2;
		chkHoeheBerechnen = new JCheckBox(lang.getText(lang.lblHoeheAutomatischBerechnenExt));
		chkHoeheBerechnen.addChangeListener(e -> {
			lblGrundflaechenmittelstamm.setEnabled(chkHoeheBerechnen.isSelected());
			txtHoeheGrundflaechenmittelstamm.setEnabled(chkHoeheBerechnen.isSelected());
			lblHoeheGrundflaechenmittelstamm.setEnabled(chkHoeheBerechnen.isSelected());
			txtGrundflaechenmittelstamm.setEnabled(chkHoeheBerechnen.isSelected());
		});
		if (StaticSettings.USE_COLORS) {
			chkHoeheBerechnen.setOpaque(false);
		}
		this.add(chkHoeheBerechnen, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,35,0,5);
		lblGrundflaechenmittelstamm = new JLabel(lang.getText(lang.lblGrundflächenmittelstamm));
		this.add(lblGrundflaechenmittelstamm, c);
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 4;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0,5,5,5);
		txtGrundflaechenmittelstamm = new JSpinner( new SpinnerNumberModel(35, 0, 100, 1) );
		txtGrundflaechenmittelstamm.setMinimumSize( new Dimension(50, 20) );
		txtGrundflaechenmittelstamm.setPreferredSize( new Dimension(50, 20) );
		this.add(txtGrundflaechenmittelstamm, c);	
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,35,0,5);
		lblHoeheGrundflaechenmittelstamm = new JLabel(lang.getText(lang.lblHoeheGrundflaechenmittelstamm));
		this.add(lblHoeheGrundflaechenmittelstamm, c);	
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 5;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0,5,5,5);
		txtHoeheGrundflaechenmittelstamm = new JSpinner( new SpinnerNumberModel(30, 0, 100, 1) );
		txtHoeheGrundflaechenmittelstamm.setMinimumSize( new Dimension(50, 20) );
		txtHoeheGrundflaechenmittelstamm.setPreferredSize( new Dimension(50, 20) );
		this.add(txtHoeheGrundflaechenmittelstamm, c);
	}
	
	public double getSchaftanteil() {
		return ((Integer) this.txtZuVerwendenderSchaftanteil.getValue()) / 100.0;
	}
	
	public void updateLabels() {
		((TitledBorder)this.getBorder()).setTitle(lang.getText(lang.lblBaumliste));
		lblFileBaumliste.setText(lang.getText(lang.lblBaumlistenFile));
		lblZuVerwendenderSchaftanteil.setText(lang.getText(lang.lblZuVerwendenderSchaftanteil));
		chkSchaftanteilBerechnen.setText(lang.getText(lang.lblSchaftanteilBerechnen));
		lblD7mBhD		.setText(lang.getText(lang.lblD7mBHDRelationExt));
		chkHoeheBerechnen.setText(lang.getText(lang.lblHoeheAutomatischBerechnenExt));
		lblGrundflaechenmittelstamm.setText(lang.getText(lang.lblGrundflächenmittelstamm));
		lblHoeheGrundflaechenmittelstamm.setText(lang.getText(lang.lblHoeheGrundflaechenmittelstamm));
	}
	
	public void setFilename(String filename) {
		txtFileBaumliste.setText(filename);
	}
	
	public void readFileBaumschaefte() throws NumberFormatException, IOException {
        // Baumdaten ab File lesen		
		String filePath = txtFileBaumliste.getText();		
        presenter.readBaumschaefteFromFile(baumlistSort.getBsDefListe(), filePath);	
        
        //Höhen berechnen
        if (chkHoeheBerechnen.isSelected() == true) {
        	double grundflaechenmittelstamm = (Integer) txtGrundflaechenmittelstamm.getValue();
        	double hoeheGrundflaechenmittelstamm = (Integer) txtHoeheGrundflaechenmittelstamm.getValue();
        	baumlistSort.getBsDefListe().forEach(bsDef -> bsDef.autoCalcSchaftLaengeIfZero(grundflaechenmittelstamm, hoeheGrundflaechenmittelstamm));
        }
        
        //Schaftanteile berechnen
        if (chkSchaftanteilBerechnen.isSelected() == true) {
        	//H0 für alle Aufnahmejahre berechnen
        	HashMap<String, Double> h0values = H0Calculator.calcH0(baumlistSort.getBsDefListe());    
    		for (String key : h0values.keySet()) {
    			double h0 = h0values.get(key);
    			System.out.println(key + " -> " + h0);
    		}
    		
    		//Bäume mit KronenansatzHöhe versehen
    		for (BaumschaftDefinition bsDef : baumlistSort.getBsDefListe()) {
    			double h0 = h0values.get(bsDef.getDatum());
    			bsDef.calcKronenansatzHoeheAnteil(h0);
    		}
        }
        
        //D7m berechnen, falls nötig
        if (lfiSplineSelected) {
        	Standort standort = cmbD7mBhD.getSelectedItem().equals(lang.getAutoText(lang.lblStandortGut)) ? Standort.Gut : Standort.Mager;
        	baumlistSort.getBsDefListe().forEach(bsDef -> bsDef.autoCalcD7mIfZero(standort));
        }
	}
	
	private void setD7mVisible(boolean flag) {
		lblD7mBhD.setVisible(flag);
		cmbD7mBhD.setVisible(flag);
	}
	
	public void setSchaftformFunktion(BaumSchaftformFunktion schaftformFkt) {		
		lfiSplineSelected = (schaftformFkt instanceof BaumschaftformFkt_Lfi_Spline);
		setD7mVisible(lfiSplineSelected);
	}	
}
