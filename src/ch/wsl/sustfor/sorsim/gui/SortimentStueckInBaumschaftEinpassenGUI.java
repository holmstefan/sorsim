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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.baumschaft.base.Baumart;
import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition;
import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition.BaumschaftDefinitionBuilder;
import ch.wsl.sustfor.baumschaft.base.ScheitelHoehe;
import ch.wsl.sustfor.baumschaft.lemm1991.BaumSchaftformFkt_Lemm1991;
import ch.wsl.sustfor.lang.SorSimLanguageManager;
import ch.wsl.sustfor.sorsim.StaticSettings;
import ch.wsl.sustfor.sorsim.controller.Presenter;
import ch.wsl.sustfor.sorsim.controller.SortimentStueckInBaumschaftEinpassen;
import ch.wsl.sustfor.sorsim.gui.util.NumberDocument;
import ch.wsl.sustfor.sorsim.gui.util.NumberInputVerifier;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsAushalteStrategie;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsStueckPositionierung;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsVorgabeBuilder;

/**
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public class SortimentStueckInBaumschaftEinpassenGUI extends JPanel implements ISorSimFrame {
	
	//Schaftdefinition
	private JPanel pnlSchaftDefinition;
	private JLabel lblBaumart;
	private JComboBox<Baumart> cmbBaumart;
	private JLabel lblObererDurchmesser_iR_cm;
	private JTextField txtObererDurchmesser_iR_cm;
	private JLabel lblUntererDurchmesser_iR_cm;
	private JTextField txtUntererDurchmesser_iR_cm;
	private JLabel lblSchaftLaenge_m;
	private JTextField txtSchaftLaenge_m;	
	private JCheckBox chkHoeheBerechnen;
	private JLabel lblGrundflaechenmittelstamm;
	private JTextField txtGrundflaechenmittelstamm;
	private JLabel lblHoeheGrundflaechenmittelstamm;
	private JTextField txtHoeheGrundflaechenmittelstamm;
	
	//Sortimentsvorgaben
	private JPanel pnlSortimentsVorgaben;
	private JLabel lblAushalteStrategie;
	private JComboBox<SortimentsAushalteStrategie> cmbAushalteStrategie;
	private JLabel lblPositionierung;
	private JComboBox<SortimentsStueckPositionierung> cmbPositionierung;
	private JLabel lblLaengenIntervall_m;
	private JTextField txtLaengenIntervall_m;
	private JLabel lblMin;
	private JLabel lblMax;
	private JLabel lblPosAmStamm;
	private JTextField txtPositionAmStammUnten_m;
	private JTextField txtPositionAmStammOben_m;
	private JLabel lblLaenge_m;
	private JTextField txtLaengeMin_m;
	private JTextField txtLaengeMax_m;
	private JLabel lblMiDrm_cm;
	private JTextField txtMittenDurchmMin_cm;
	private JTextField txtMittenDurchmMax_cm;
	private JLabel lblZopfDrm_cm;
	private JTextField txtZopfDurchmMin_cm;
	private JTextField txtZopfDurchmMax_cm;		
	
	//Ergebnis
	private JPanel pnlErgebnis;
	private JTextArea txtOutput;
	
	//Baumschaft
	private JPanel pnlTree;	
	
	//Buttons
	private JPanel pnlButtons;
	private JButton btnStart;
//	private JButton btnInfo;

    private Presenter presenter = new Presenter();    
	private SortimentStueckInBaumschaftEinpassen ssibsex;
	private SorSimLanguageManager lang = SorSimLanguageManager.getInstance();
	
	private boolean paintSortStueck = false;
	
	private MainWindow parent;
	
	private boolean isInitialized = false;
	
	public SortimentStueckInBaumschaftEinpassenGUI(MainWindow parent) {
		//init fields etc
		this.parent = parent;
		this.initContent();
		this.xx_initComboBoxes();
		this.xx_initialisation();
		
		//show window
		this.setVisible(true);
	}
	
	private void initContent() {
		//remove all
		this.removeAll();
		
		//set layout
		this.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//panel SchaftDefinition
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		initPanelSchaftDefinition();
		this.add(pnlSchaftDefinition, c);
		
		//panel SortimentsVorgaben
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		initPanelSortimentsVorgaben();
		this.add(pnlSortimentsVorgaben, c);
		
		//panel Ergebnis
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 100;
		c.weighty = 100;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		initPanelErgebnis();
		this.add(pnlErgebnis, c);
		
		//panel Tree
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
//		c.gridwidth = 1;
		c.weightx = 100;
		c.weighty = 100;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		initPanelTree();
		this.add(pnlTree, c);
		
		//panel Buttons
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		initPanelButtons();
		this.add(pnlButtons, c);
	}

	private void initPanelSchaftDefinition() {
		//init panel
		pnlSchaftDefinition = new JPanel(){
			@Override //necessary to avoid artefacts when painting backgrounds with an alpha value < 1
		    protected void paintComponent(Graphics g) {
		        g.setColor( getBackground() );
		        g.fillRect(0, 0, getWidth(), getHeight());
		        super.paintComponent(g);
		    }
		};
		if (StaticSettings.USE_COLORS) {
			pnlSchaftDefinition.setOpaque(false);
			pnlSchaftDefinition.setBackground(StaticSettings.COLOR_BAEUME);
		}
		pnlSchaftDefinition.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblSchaftdefinition)));
		
		//set layout
		pnlSchaftDefinition.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblBaumart = new JLabel(lang.getText(lang.lblBaumartCode));
		pnlSchaftDefinition.add(lblBaumart, c);
		
		//combo box
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		cmbBaumart = new JComboBox<>();
		cmbBaumart.setMaximumRowCount(12);
		cmbBaumart.setAction(new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {
				autoCalcSchaftLaengeAndPaintTree();
			}        	
        });
		pnlSchaftDefinition.add(cmbBaumart, c);
		
		if (parent.getBaumSchaftformFunktion() instanceof BaumSchaftformFkt_Lemm1991) {
			//kein oberer Durchmesser bei Lemm1991	
			lblObererDurchmesser_iR_cm = new JLabel();
			txtObererDurchmesser_iR_cm = new JTextField();		
		} 
		else {
			//label
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 4;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0,5,0,5);
			lblObererDurchmesser_iR_cm = new JLabel(lang.getText(lang.lblObererDrmIR_cm));
			pnlSchaftDefinition.add(lblObererDurchmesser_iR_cm, c);
			
			//text field
	        c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 4;
			c.weightx = 100;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0,5,5,5);
			txtObererDurchmesser_iR_cm = new JTextField();
			txtObererDurchmesser_iR_cm.setDocument(new NumberDocument(5));
			txtObererDurchmesser_iR_cm.setInputVerifier(new NumberInputVerifier(0,200));
			txtObererDurchmesser_iR_cm.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent arg0) {
					txtObererDurchmesser_iR_cm.selectAll();
				}
				@Override
				public void focusLost(FocusEvent arg0) {
					autoCalcSchaftLaengeAndPaintTree();
				}				
			});
			pnlSchaftDefinition.add(txtObererDurchmesser_iR_cm, c);
		}
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblUntererDurchmesser_iR_cm = new JLabel(lang.getText(lang.lblUntererDrm_IR_cm));
		pnlSchaftDefinition.add(lblUntererDurchmesser_iR_cm, c);		
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtUntererDurchmesser_iR_cm = new JTextField();
		txtUntererDurchmesser_iR_cm.setDocument(new NumberDocument(5));
		txtUntererDurchmesser_iR_cm.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtUntererDurchmesser_iR_cm.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				autoCalcSchaftLaengeAndPaintTree();
			}				
		});
		txtUntererDurchmesser_iR_cm.setInputVerifier(new NumberInputVerifier(0,200));
		pnlSchaftDefinition.add(txtUntererDurchmesser_iR_cm, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 6;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblSchaftLaenge_m = new JLabel(lang.getText(lang.lblSchaftlaenge_m));
		pnlSchaftDefinition.add(lblSchaftLaenge_m, c);		
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 6;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtSchaftLaenge_m = new JTextField();
		txtSchaftLaenge_m.setDocument(new NumberDocument(5));
		txtSchaftLaenge_m.setInputVerifier(new NumberInputVerifier(0,100));
		txtSchaftLaenge_m.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtSchaftLaenge_m.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				autoCalcSchaftLaengeAndPaintTree();
			}				
		});
		pnlSchaftDefinition.add(txtSchaftLaenge_m, c);
		
		//checkbox
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 7;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,5,0,5);
		c.gridwidth = 2;
		chkHoeheBerechnen = new JCheckBox(lang.getText(lang.lblHoeheAutomatischBerechnen));
		chkHoeheBerechnen.addChangeListener(e -> {
			txtSchaftLaenge_m.setEnabled( ! chkHoeheBerechnen.isSelected());
			lblGrundflaechenmittelstamm.setEnabled(chkHoeheBerechnen.isSelected());
			txtHoeheGrundflaechenmittelstamm.setEnabled(chkHoeheBerechnen.isSelected());
			lblHoeheGrundflaechenmittelstamm.setEnabled(chkHoeheBerechnen.isSelected());
			txtGrundflaechenmittelstamm.setEnabled(chkHoeheBerechnen.isSelected());
			autoCalcSchaftLaengeAndPaintTree();
		});
		if (StaticSettings.USE_COLORS) {
			chkHoeheBerechnen.setOpaque(false);
		}
		pnlSchaftDefinition.add(chkHoeheBerechnen, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,35,0,5);
		lblGrundflaechenmittelstamm = new JLabel(lang.getText(lang.lblGrundflächenmittelstamm));
		pnlSchaftDefinition.add(lblGrundflaechenmittelstamm, c);
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 8;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtGrundflaechenmittelstamm = new JTextField();
		txtGrundflaechenmittelstamm.setDocument(new NumberDocument(5));
		txtGrundflaechenmittelstamm.setInputVerifier(new NumberInputVerifier(0,100));
		txtGrundflaechenmittelstamm.setText("35");
		txtGrundflaechenmittelstamm.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtGrundflaechenmittelstamm.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				autoCalcSchaftLaengeAndPaintTree();
			}				
		});
		pnlSchaftDefinition.add(txtGrundflaechenmittelstamm, c);	
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 9;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,35,0,5);
		lblHoeheGrundflaechenmittelstamm = new JLabel(lang.getText(lang.lblHoeheGrundflaechenmittelstamm));
		pnlSchaftDefinition.add(lblHoeheGrundflaechenmittelstamm, c);	
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 9;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtHoeheGrundflaechenmittelstamm = new JTextField();
		txtHoeheGrundflaechenmittelstamm.setDocument(new NumberDocument(5));
		txtHoeheGrundflaechenmittelstamm.setInputVerifier(new NumberInputVerifier(0,100));
		txtHoeheGrundflaechenmittelstamm.setText("32");
		txtHoeheGrundflaechenmittelstamm.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtHoeheGrundflaechenmittelstamm.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				autoCalcSchaftLaengeAndPaintTree();
			}				
		});
		pnlSchaftDefinition.add(txtHoeheGrundflaechenmittelstamm, c);
	}
	
	private void autoCalcSchaftLaengeAndPaintTree() {
		if (isInitialized == false) {
			return;
		}
		if (chkHoeheBerechnen.isSelected() == true) {
			try {
				double schaftLaenge = ScheitelHoehe.getScheitelHoehe(
						(Baumart) cmbBaumart.getSelectedItem(), 
						Double.valueOf( txtUntererDurchmesser_iR_cm.getText() ),
						Double.valueOf( txtGrundflaechenmittelstamm.getText() ),
						Double.valueOf( txtHoeheGrundflaechenmittelstamm.getText() )
						);
				txtSchaftLaenge_m.setText(String.valueOf(schaftLaenge));
			} catch (NumberFormatException e) {
			}
		}
        xx_datenAusFormUebernehmen();
        pnlTree.repaint();
	}

	private void initPanelSortimentsVorgaben() {
		//init panel
		pnlSortimentsVorgaben = new JPanel(){
			@Override //necessary to avoid artefacts when painting backgrounds with an alpha value < 1
		    protected void paintComponent(Graphics g) {
		        g.setColor( getBackground() );
		        g.fillRect(0, 0, getWidth(), getHeight());
		        super.paintComponent(g);
		    }
		};
		pnlSortimentsVorgaben.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblSortimentsVorgaben)));
		if (StaticSettings.USE_COLORS) {
			pnlSortimentsVorgaben.setOpaque(false);
			pnlSortimentsVorgaben.setBackground(StaticSettings.COLOR_SORTIMENTSVORGABEN);
		}
		
		//set layout
		pnlSortimentsVorgaben.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblAushalteStrategie = new JLabel(lang.getText(lang.lblAushalteStrategie));
		pnlSortimentsVorgaben.add(lblAushalteStrategie, c);
		
		//combo box
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		cmbAushalteStrategie = new JComboBox<>();
		pnlSortimentsVorgaben.add(cmbAushalteStrategie, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblPositionierung = new JLabel(lang.getText(lang.lblPositionierung));
		pnlSortimentsVorgaben.add(lblPositionierung, c);
		
		//combo box
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		cmbPositionierung = new JComboBox<>();
		pnlSortimentsVorgaben.add(cmbPositionierung, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblLaengenIntervall_m = new JLabel(lang.getText(lang.lblIntervall_m));
		pnlSortimentsVorgaben.add(lblLaengenIntervall_m, c);		
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtLaengenIntervall_m = new JTextField();
		txtLaengenIntervall_m.setDocument(new NumberDocument(5));
		txtLaengenIntervall_m.setInputVerifier(new NumberInputVerifier(0,10));
		txtLaengenIntervall_m.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtLaengenIntervall_m.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {				
			}				
		});
		pnlSortimentsVorgaben.add(txtLaengenIntervall_m, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblMin = new JLabel(lang.getText(lang.lblMin));
		lblMin.setHorizontalAlignment(SwingConstants.CENTER);
		pnlSortimentsVorgaben.add(lblMin, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblMax = new JLabel(lang.getText(lang.lblMax));
		lblMax.setHorizontalAlignment(SwingConstants.CENTER);
		pnlSortimentsVorgaben.add(lblMax, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblPosAmStamm = new JLabel(lang.getText(lang.lblPosAmStamm));
		pnlSortimentsVorgaben.add(lblPosAmStamm, c);		
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtPositionAmStammUnten_m = new JTextField();
		txtPositionAmStammUnten_m.setDocument(new NumberDocument(5));
		txtPositionAmStammUnten_m.setInputVerifier(new NumberInputVerifier(0,100));
		txtPositionAmStammUnten_m.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtPositionAmStammUnten_m.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {				
			}				
		});
		pnlSortimentsVorgaben.add(txtPositionAmStammUnten_m, c);	
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 4;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtPositionAmStammOben_m = new JTextField();
		txtPositionAmStammOben_m.setDocument(new NumberDocument(5));
		txtPositionAmStammOben_m.setInputVerifier(new NumberInputVerifier(0,100));
		txtPositionAmStammOben_m.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtPositionAmStammOben_m.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {				
			}				
		});
		pnlSortimentsVorgaben.add(txtPositionAmStammOben_m, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblLaenge_m = new JLabel(lang.getText(lang.lblLaenge_m));
		pnlSortimentsVorgaben.add(lblLaenge_m, c);		
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 5;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtLaengeMin_m = new JTextField();
		txtLaengeMin_m.setDocument(new NumberDocument(5));
		txtLaengeMin_m.setInputVerifier(new NumberInputVerifier(0,100));
		txtLaengeMin_m.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtLaengeMin_m.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {				
			}				
		});
		pnlSortimentsVorgaben.add(txtLaengeMin_m, c);	
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 5;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtLaengeMax_m = new JTextField();
		txtLaengeMax_m.setDocument(new NumberDocument(5));
		txtLaengeMax_m.setInputVerifier(new NumberInputVerifier(0,100));
		txtLaengeMax_m.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtLaengeMax_m.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {				
			}				
		});
		pnlSortimentsVorgaben.add(txtLaengeMax_m, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 6;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblMiDrm_cm = new JLabel(lang.getText(lang.lblMiDrm_cm));
		pnlSortimentsVorgaben.add(lblMiDrm_cm, c);		
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 6;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtMittenDurchmMin_cm = new JTextField();
		txtMittenDurchmMin_cm.setDocument(new NumberDocument(5));
		txtMittenDurchmMin_cm.setInputVerifier(new NumberInputVerifier(0,200));
		txtMittenDurchmMin_cm.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtMittenDurchmMin_cm.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {				
			}				
		});
		pnlSortimentsVorgaben.add(txtMittenDurchmMin_cm, c);	
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 6;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtMittenDurchmMax_cm = new JTextField();
		txtMittenDurchmMax_cm.setDocument(new NumberDocument(5));
		txtMittenDurchmMax_cm.setInputVerifier(new NumberInputVerifier(0,200));
		txtMittenDurchmMax_cm.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtMittenDurchmMax_cm.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {				
			}				
		});
		pnlSortimentsVorgaben.add(txtMittenDurchmMax_cm, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 7;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblZopfDrm_cm = new JLabel(lang.getText(lang.lblZopfDrm_cm));
		pnlSortimentsVorgaben.add(lblZopfDrm_cm, c);		
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 7;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtZopfDurchmMin_cm = new JTextField();
		txtZopfDurchmMin_cm.setDocument(new NumberDocument(5));
		txtZopfDurchmMin_cm.setInputVerifier(new NumberInputVerifier(0,100));
		txtZopfDurchmMin_cm.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtZopfDurchmMin_cm.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {				
			}				
		});
		pnlSortimentsVorgaben.add(txtZopfDurchmMin_cm, c);	
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 7;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtZopfDurchmMax_cm = new JTextField();
		txtZopfDurchmMax_cm.setDocument(new NumberDocument(5));
		txtZopfDurchmMax_cm.setInputVerifier(new NumberInputVerifier(0,100));
		txtZopfDurchmMax_cm.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtZopfDurchmMax_cm.selectAll();
			}
			@Override
			public void focusLost(FocusEvent arg0) {				
			}				
		});
		pnlSortimentsVorgaben.add(txtZopfDurchmMax_cm, c);
	}
	
	private void initPanelTree() {
		//init panel
		pnlTree = new JPanel(){
			@Override
			public
			void paintComponent(Graphics g){
				paintTree(g);
			}			
		};		
		pnlTree.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblBaumschaft)));
	}

	private void initPanelErgebnis() {
		//init panel
		pnlErgebnis = new JPanel();		
		pnlErgebnis.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblErgebnis)));
		
		//set layout
		pnlErgebnis.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//text area
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 100;
		c.weighty = 100;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,5,5,5);
		if (txtOutput == null) {
			txtOutput = new JTextArea();
			//txtOutput.setLineWrap(true);
			txtOutput.setEditable(false);
		}
		JScrollPane scrlTxtOutput =  new JScrollPane(txtOutput);
		pnlErgebnis.add(scrlTxtOutput, c);
	}

	private void initPanelButtons() {
		//init panel
		pnlButtons = new JPanel();
		
		//set layout
		pnlButtons.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//button
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0,5,5,5);
		btnStart = new JButton();
		btnStart.setAction(new AbstractAction(lang.getText(lang.lblBerechnungStarten)){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					btnRun_Click();
				} catch (Exception e1) {
					//write exception to std err
					e1.printStackTrace();
					
					//write exception to txtOutput
					txtOutput.setText("Fehler (" + e1.toString() + ")");
					txtOutput.setCaretPosition(0);
				}
			}        	
        });
		pnlButtons.add(btnStart, c);
		
		//place holder
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(0,5,5,5);
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		JLabel lblPlaceHolder1 = new JLabel();
		pnlButtons.add(lblPlaceHolder1, c);
	}

	private void btnRun_Click() {
		//daten übernehmen
        xx_datenAusFormUebernehmen();
        
        //einpassen
        String errorMsg = ssibsex.sortimentEinpassen(null, null);
        
        //darstellung ergebnis
        if (errorMsg == null) { //everything ok
            txtOutput.setText(
            		presenter.printSortimentStueck(ssibsex) + "\n\n" + 
            		"(SchaftformFunktion" + ": " + ssibsex.getSchaftDefinition().getSchaftformFunktion() + ")"); 
            paintSortStueck = true;
            pnlTree.repaint();
        } else {
        	txtOutput.setText(errorMsg);
        }
	}
	
	private void paintTree(Graphics g) {
		final int streckung = 10; //horziontale Streckung
		
		//Fläche löschen
		g.clearRect(0, 0, 1000, 1000);

		//draw Baumschaft
		BaumschaftDefinition bsDef = ssibsex.getSchaftDefinition();

		int center = pnlTree.getWidth() / 2;
		int insetBottom = pnlTree.getInsets().bottom;
		int insetTop = pnlTree.getInsets().top;
		int height_pxl = pnlTree.getHeight() - insetBottom - insetTop;
		double factor = height_pxl / (bsDef.getSchaftLaenge_m() * 100.0); //umrechnungsfaktor pixel / cm		

		Polygon pLeftIR = new Polygon();
		Polygon pRightIR = new Polygon();
//		Polygon pLeftOR = new Polygon();
//		Polygon pRightOR = new Polygon();
		
		for (int i=0; i<height_pxl; i+=5) {
			double height_m = i / (factor * 100.0);
			double drm_iR = bsDef.getDurchmesser_iR_cm(height_m);
//			double drm_oR = bsDef.getDurchmesser_oR_cm(height_m);

			//in Rinde
			int right = center + (int) Math.round(drm_iR * factor * streckung);
			int left  = center - (int) Math.round(drm_iR * factor * streckung);
			int height = insetTop + height_pxl - i;
			pRightIR.addPoint(right, height);
			pLeftIR.addPoint(left, height);

			//ohne Rinde
//			right = center + (int) Math.round(drm_oR * factor * streckung);
//			left  = center - (int) Math.round(drm_oR * factor * streckung);
//			pRightOR.addPoint(right, height);
//			pLeftOR.addPoint(left, height);
				
//			System.out.println(height + " / " + left + " / " + right);
		}

		//beide Baumhälften Zeichnen
		g.drawPolyline(pLeftIR.xpoints, pLeftIR.ypoints, pLeftIR.npoints);
		g.drawPolyline(pRightIR.xpoints, pRightIR.ypoints, pRightIR.npoints);
//		g.drawPolyline(pLeftOR.xpoints, pLeftOR.ypoints, pLeftOR.npoints);
//		g.drawPolyline(pRightOR.xpoints, pRightOR.ypoints, pRightOR.npoints);

		if (paintSortStueck == false) {
			return;
		}

		//draw Sortimentstueck
		double drmIR_basis = ssibsex.getSortimentStueck().getBasisDrmIR_cm();
		double drmIR_zopf = ssibsex.getSortimentStueck().getZopfDrmIR_cm();
		double laenge = ssibsex.getSortimentStueck().getLaenge_m();
		double posStamm = ssibsex.getSortimentStueck().getPositionDerBasisAmStamm_m();

		int posUnten = insetTop + height_pxl - (int) Math.round( (posStamm / bsDef.getSchaftLaenge_m()) * height_pxl);
		int posOben  = insetTop + height_pxl - (int) Math.round(  ((posStamm + laenge) / bsDef.getSchaftLaenge_m()) * height_pxl);


		int rightUnten = center + (int) Math.round(drmIR_basis * factor * streckung);
		int leftUnten  = center - (int) Math.round(drmIR_basis * factor * streckung);
		int rightOben = center + (int) Math.round(drmIR_zopf * factor * streckung);
		int leftOben  = center - (int) Math.round(drmIR_zopf * factor * streckung);
		
		Polygon sortimentStueck = new Polygon();
		sortimentStueck.addPoint(rightUnten, posUnten);
		sortimentStueck.addPoint(leftUnten, posUnten);
		sortimentStueck.addPoint(leftOben, posOben);
		sortimentStueck.addPoint(rightOben, posOben);
		sortimentStueck.addPoint(rightUnten, posUnten);

		g.setColor(new Color(0, 196, 0, 255));
		g.drawPolyline(sortimentStueck.xpoints, sortimentStueck.ypoints, sortimentStueck.npoints);
		
		paintSortStueck = false;
	}

//	protected void btnInfo_Click() {		
//		String title = lang.getText(lang.lblInfo);
//		
//		String text = "Passt ein Sortiment entsprechend bestimmten Sortimentsvorgaben in \n" +
//					  "einen durch SchaftDefinition bestimmten Baumschaft ein und liefert \n" +
//					  "in SortimentsStueck das Ergebnis zurück.";
//		
//		
//		JOptionPane.showMessageDialog(null, text, title, JOptionPane.NO_OPTION);	
//	}
	
	private void xx_initComboBoxes() {
        //Aushalte-Strategie
        cmbAushalteStrategie.removeAllItems();
        for (SortimentsAushalteStrategie item : SortimentsAushalteStrategie.values() ) {
        	cmbAushalteStrategie.addItem( item );
        }
        
        //Positionierung
        cmbPositionierung.removeAllItems();
        for (SortimentsStueckPositionierung item : SortimentsStueckPositionierung.values() ) {
        	cmbPositionierung.addItem( item );
        }
        
        //Baumarten
        cmbBaumart.removeAllItems();
        Baumart[] baumartListe = parent.getBaumSchaftformFunktion().getBaumartListe();
        for (Baumart baumart : baumartListe) {
        	cmbBaumart.addItem( baumart );
        }		
	}
	
	private void xx_initialisation() {
		BaumschaftDefinitionBuilder bsDefBuilder = new BaumschaftDefinitionBuilder();
		bsDefBuilder.setBaumart(Baumart.Fichte);
		bsDefBuilder.setBhd_cm(42);
		bsDefBuilder.setD7m_cm(38);
		bsDefBuilder.setSchaftLaenge_m(32);
		ssibsex = new SortimentStueckInBaumschaftEinpassen(bsDefBuilder.build(), parent.getBaumSchaftformFunktion());

    	SortimentsVorgabeBuilder builder = new SortimentsVorgabeBuilder();
    	builder.setLaengeMin_m(4);
    	builder.setLaengeMax_m(12);
    	builder.setMittenDurchmMin_cm(20);
    	builder.setMittenDurchmMax_cm(43);
    	builder.setZopfDurchmMin_cm(20);
    	builder.setZopfDurchmMax_cm(41);
    	builder.setLaengenZugabe_cm(0.1);
    	builder.setLaengenZugabe_Prozent(1);
    	builder.setPositionAmStammUnten_m(0);
    	builder.setPositionAmStammOben_m(32);
    	builder.setLaengenIntervall_m(0.2);
    	builder.setAushalteStrategie(SortimentsAushalteStrategie.MinimalLaenge);
    	builder.setPositionierung(SortimentsStueckPositionierung.ZuUnterst);
    	ssibsex.setSortimentsVorgaben(builder.build());
               
		xx_datenInFormEinfuellen();	
	}
	
	private void xx_datenInFormEinfuellen() {
		//trigger change listener
		chkHoeheBerechnen.setSelected(true);
		chkHoeheBerechnen.doClick(); //sets chk selection to false
		
		cmbBaumart.setSelectedItem(ssibsex.getSchaftDefinition().getBaumart());
		txtUntererDurchmesser_iR_cm					.setText(String.valueOf(ssibsex.getSchaftDefinition().getBhd_cm()));
		txtObererDurchmesser_iR_cm					.setText(String.valueOf(ssibsex.getSchaftDefinition().getD7m_cm()));
		txtSchaftLaenge_m							.setText(String.valueOf(ssibsex.getSchaftDefinition().getSchaftLaenge_m()));
		
		cmbAushalteStrategie.setSelectedIndex(ssibsex.getSortimentsVorgaben().getAushalteStrategie().ordinal());
		txtLaengeMin_m				.setText(String.valueOf(ssibsex.getSortimentsVorgaben().getLaengeMin_m()));
		txtLaengeMax_m				.setText(String.valueOf(ssibsex.getSortimentsVorgaben().getLaengeMax_m()));
		txtLaengenIntervall_m		.setText(String.valueOf(ssibsex.getSortimentsVorgaben().getLaengenIntervall_m()));
		txtMittenDurchmMin_cm		.setText(String.valueOf(ssibsex.getSortimentsVorgaben().getMittenDurchmMin_cm()));
		txtMittenDurchmMax_cm		.setText(String.valueOf(ssibsex.getSortimentsVorgaben().getMittenDurchmMax_cm()));
		txtPositionAmStammUnten_m	.setText(String.valueOf(ssibsex.getSortimentsVorgaben().getPositionAmStammUnten_m()));
		txtPositionAmStammOben_m	.setText(String.valueOf(ssibsex.getSortimentsVorgaben().getPositionAmStammOben_m()));
		cmbPositionierung.setSelectedIndex(ssibsex.getSortimentsVorgaben().getPositionierung().ordinal());
		txtZopfDurchmMin_cm			.setText(String.valueOf(ssibsex.getSortimentsVorgaben().getZopfDurchmMin_cm()));
		txtZopfDurchmMax_cm			.setText(String.valueOf(ssibsex.getSortimentsVorgaben().getZopfDurchmMax_cm()));
	}
	
	private void xx_datenAusFormUebernehmen() {
		BaumschaftDefinitionBuilder bsDefBuilder = new BaumschaftDefinitionBuilder();
		bsDefBuilder.setBaumart((Baumart) cmbBaumart.getSelectedItem());
		bsDefBuilder.setBhd_cm 									( Double.valueOf(txtUntererDurchmesser_iR_cm.getText()) );
		bsDefBuilder.setD7m_cm 									( Double.valueOf(txtObererDurchmesser_iR_cm.getText()) );
		bsDefBuilder.setSchaftLaenge_m 							( Double.valueOf(txtSchaftLaenge_m.getText()) );
		ssibsex = new SortimentStueckInBaumschaftEinpassen(bsDefBuilder.build(), parent.getBaumSchaftformFunktion());

    	SortimentsVorgabeBuilder builder = new SortimentsVorgabeBuilder();
    	builder.setLaengeMin_m			(Double.valueOf(txtLaengeMin_m.getText()));
    	builder.setLaengeMax_m			(Double.valueOf(txtLaengeMax_m.getText()));
    	builder.setMittenDurchmMin_cm	(Double.valueOf(txtMittenDurchmMin_cm.getText()));
    	builder.setMittenDurchmMax_cm	(Double.valueOf(txtMittenDurchmMax_cm.getText()));
    	builder.setZopfDurchmMin_cm		(Double.valueOf(txtZopfDurchmMin_cm.getText()));
    	builder.setZopfDurchmMax_cm		(Double.valueOf(txtZopfDurchmMax_cm.getText()));
    	builder.setPositionAmStammUnten_m(Double.valueOf(txtPositionAmStammUnten_m.getText()));
    	builder.setPositionAmStammOben_m(Double.valueOf(txtPositionAmStammOben_m.getText()));
    	builder.setLaengenIntervall_m	(Double.valueOf(txtLaengenIntervall_m.getText()));
    	builder.setAushalteStrategie(SortimentsAushalteStrategie.values()[cmbAushalteStrategie.getSelectedIndex()]);
    	builder.setPositionierung(SortimentsStueckPositionierung.values()[cmbPositionierung.getSelectedIndex()]);
    	ssibsex.setSortimentsVorgaben(builder.build());		
	}

	@Override
	public void setExtendedMode(boolean flag) {
	}

	@Override
	public void setSchaftformFunktion(BaumSchaftformFunktion schaftformFkt) {
		//Content muss neu gezeichnet werden, da unterschiedliche Schaftform-Fkt verschiedene Eingabefelder benötigen!
		this.isInitialized = false;
		
		//alte daten speichern
		this.xx_datenAusFormUebernehmen();
		boolean hoeheBerechnen = chkHoeheBerechnen.isSelected();
		String txtGrundflMiSt = txtGrundflaechenmittelstamm.getText();
		String txtHoeheGrFlMiSt = txtHoeheGrundflaechenmittelstamm.getText();
		
		//Felder neu laden
		this.initContent();
		this.xx_initComboBoxes();
		this.xx_datenInFormEinfuellen();
		chkHoeheBerechnen.setSelected(hoeheBerechnen);
		txtGrundflaechenmittelstamm.setText(txtGrundflMiSt);
		txtHoeheGrundflaechenmittelstamm.setText(txtHoeheGrFlMiSt);
		
		this.isInitialized = true;
		
		this.repaint();
	}

	@Override
	public void updateLabels() {		
		//Schaftdefinition
		((TitledBorder) pnlSchaftDefinition.getBorder()).setTitle(lang.getText(lang.lblSchaftdefinition));
		lblBaumart.setText(lang.getText(lang.lblBaumartCode));
		lblObererDurchmesser_iR_cm.setText(lang.getText(lang.lblObererDrmIR_cm));
		lblUntererDurchmesser_iR_cm.setText(lang.getText(lang.lblUntererDrm_IR_cm));
		lblSchaftLaenge_m.setText(lang.getText(lang.lblSchaftlaenge_m));
		chkHoeheBerechnen.setText(lang.getText(lang.lblHoeheAutomatischBerechnen));
		lblGrundflaechenmittelstamm.setText(lang.getText(lang.lblGrundflächenmittelstamm));
		lblHoeheGrundflaechenmittelstamm.setText(lang.getText(lang.lblHoeheGrundflaechenmittelstamm));
		
		//Sortimentsvorgaben
		((TitledBorder) pnlSortimentsVorgaben.getBorder()).setTitle(lang.getText(lang.lblSortimentsVorgaben));
		lblAushalteStrategie.setText(lang.getText(lang.lblAushalteStrategie));
		lblPositionierung.setText(lang.getText(lang.lblPositionierung));
		lblLaengenIntervall_m.setText(lang.getText(lang.lblIntervall_m));
		lblMin.setText(lang.getText(lang.lblMin));
		lblMax.setText(lang.getText(lang.lblMax));
		lblPosAmStamm.setText(lang.getText(lang.lblPosAmStamm));
		lblLaenge_m.setText(lang.getText(lang.lblLaenge_m));
		lblMiDrm_cm.setText(lang.getText(lang.lblMiDrm_cm));
		lblZopfDrm_cm.setText(lang.getText(lang.lblZopfDrm_cm));
		
		//Ergebnis
		((TitledBorder) pnlErgebnis.getBorder()).setTitle(lang.getText(lang.lblErgebnis));
		
		//Baumschaft
		((TitledBorder) pnlTree.getBorder()).setTitle(lang.getText(lang.lblBaumschaft));
		
		//Buttons
		btnStart.setText(lang.getText(lang.lblBerechnungStarten));
	}
	
	@Override
	public void setAnzahlNachkommastellen(int anzahl) {
		presenter.setAnzahlStellenNachKomma(anzahl);
	}
}
