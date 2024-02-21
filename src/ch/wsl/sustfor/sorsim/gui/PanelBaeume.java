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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;

import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.baumschaft.base.Baumart;
import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition;
import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition.BaumschaftDefinitionBuilder;
import ch.wsl.sustfor.baumschaft.lfispline.BaumschaftformFkt_Lfi_Spline;
import ch.wsl.sustfor.sorsim.StaticSettings;
import ch.wsl.sustfor.sorsim.model.D7mBhdRelations;
import ch.wsl.sustfor.sorsim.model.D7mBhdRelations.Standort;

/**
 * 
 * @author Stefan Holm
 *
 */
public class PanelBaeume extends SorSimPanel {

	private JLabel lblBaumartCode;
	private JComboBox<Baumart> cmbBaumart;
	private JLabel lblD7mBhD;
	private JComboBox<D7mBhDRelation> cmbD7mBhD;
	private JLabel lblSchaftanteil;
	private JSpinner txtZuVerwendenderSchaftanteil;
	private JLabel lblVon;
	private JLabel lblBis;
	private JLabel lblBhd;
	private JSpinner txtBhd_cm_min;
	private JSpinner txtBhd_cm_max;
	private JLabel lblD7m;
	private JSpinner txtD7m_cm_min;
	private JSpinner txtD7m_cm_max;
	private JLabel lblSchaftlaenge;
	private JSpinner txtSchaftlaenge_m_min;
	private JSpinner txtSchaftlaenge_m_max;	
	
	private static final Dimension STANDARD_TEXTFIELD_SIZE = new Dimension(60,20);
	private boolean lfiSplineSelected;
	
	public PanelBaeume() {
		if (StaticSettings.USE_COLORS) {
			this.setBackground(StaticSettings.COLOR_BAEUME);
		}
		initContent();
		initBaumartListe();
	}

	private void initContent() {
		//init panel
		this.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblBaeume)) );

		//set layout
		this.setLayout( new GridBagLayout() );
		GridBagConstraints c;

		//label
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblBaumartCode = new JLabel(lang.getText(lang.lblBaumartCode));
		this.add(lblBaumartCode, c);		

		//combo box
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		cmbBaumart = new JComboBox<>();
		cmbBaumart.addActionListener(e -> updateD7Combo());
		cmbBaumart.setMaximumRowCount(12);
		this.add(cmbBaumart, c);

		//label
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblD7mBhD = new JLabel(lang.getText(lang.lblD7mBHDRelation));
		this.add(lblD7mBhD, c);		

		//combo box
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		cmbD7mBhD = new JComboBox<>();
		cmbD7mBhD.addActionListener(e -> updateD7Values());
		this.add(cmbD7mBhD, c);

		//label
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblSchaftanteil = new JLabel(lang.getText(lang.lblZuVerwendenderSchaftanteil));
		this.add(lblSchaftanteil, c);		

		//text field
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtZuVerwendenderSchaftanteil = new JSpinner( new SpinnerNumberModel(100, 0, 100, 1) );
		txtZuVerwendenderSchaftanteil.setMinimumSize(STANDARD_TEXTFIELD_SIZE);
		txtZuVerwendenderSchaftanteil.setPreferredSize(STANDARD_TEXTFIELD_SIZE);
		setSpinnerToDisallowInvalidCharacters(txtZuVerwendenderSchaftanteil);
		//			txtZuVerwendenderSchaftanteil.setDocument(new NumberDocument(5));
		//			txtZuVerwendenderSchaftanteil.setInputVerifier(new NumberInputVerifier(0,1));
		this.add(txtZuVerwendenderSchaftanteil, c);	

		//placeholder
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		//			c.weightx = 1;
		JLabel lblPlaceHolder = new JLabel();
		lblPlaceHolder.setMinimumSize(STANDARD_TEXTFIELD_SIZE);
		lblPlaceHolder.setPreferredSize(STANDARD_TEXTFIELD_SIZE);
		this.add(lblPlaceHolder, c);	

		//label
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblVon = new JLabel(lang.getText(lang.lblVon));
		this.add(lblVon, c);	

		//label
		c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblBis = new JLabel(lang.getText(lang.lblBis));
		this.add(lblBis, c);	

		//label
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblBhd = new JLabel(lang.getText(lang.lblBHD_cm));
		this.add(lblBhd, c);		

		//text field
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtBhd_cm_min = new JSpinner( new SpinnerNumberModel(35, 0, 200, 1) );
		txtBhd_cm_min.setMinimumSize(STANDARD_TEXTFIELD_SIZE);
		txtBhd_cm_min.setPreferredSize(STANDARD_TEXTFIELD_SIZE);
		txtBhd_cm_min.addChangeListener(e -> updateD7Values());
		setSpinnerToDisallowInvalidCharacters(txtBhd_cm_min);
		//			txtBhd_cm_min.setDocument(new NumberDocument(5));
		//			txtBhd_cm_min.setInputVerifier(new NumberInputVerifier(0,200));
		this.add(txtBhd_cm_min, c);	

		//text field
		c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtBhd_cm_max = new JSpinner( new SpinnerNumberModel(40, 0, 200, 1) );
		txtBhd_cm_max.setMinimumSize(STANDARD_TEXTFIELD_SIZE);
		txtBhd_cm_max.setPreferredSize(STANDARD_TEXTFIELD_SIZE);
		txtBhd_cm_max.addChangeListener(e -> updateD7Values());
		setSpinnerToDisallowInvalidCharacters(txtBhd_cm_max);
		//			txtBhd_cm_max.setDocument(new NumberDocument(5));
		//			txtBhd_cm_max.setInputVerifier(new NumberInputVerifier(0,200));
		this.add(txtBhd_cm_max, c);	

		//label
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblD7m = new JLabel(lang.getText(lang.lblD7m_cm));
		this.add(lblD7m, c);		

		//text field
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtD7m_cm_min = new JSpinner( new SpinnerNumberModel(35, 0, 200, 1) );
		txtD7m_cm_min.setMinimumSize(STANDARD_TEXTFIELD_SIZE);
		txtD7m_cm_min.setPreferredSize(STANDARD_TEXTFIELD_SIZE);
		txtD7m_cm_min.setEnabled(false);
		setSpinnerToDisallowInvalidCharacters(txtD7m_cm_min);
		//			txtBhd_cm_min.setDocument(new NumberDocument(5));
		//			txtBhd_cm_min.setInputVerifier(new NumberInputVerifier(0,200));
		this.add(txtD7m_cm_min, c);	

		//text field
		c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtD7m_cm_max = new JSpinner( new SpinnerNumberModel(40, 0, 200, 1) );
		txtD7m_cm_max.setMinimumSize(STANDARD_TEXTFIELD_SIZE);
		txtD7m_cm_max.setPreferredSize(STANDARD_TEXTFIELD_SIZE);
		txtD7m_cm_max.setEnabled(false);
		setSpinnerToDisallowInvalidCharacters(txtBhd_cm_max);
		//			txtBhd_cm_max.setDocument(new NumberDocument(5));
		//			txtBhd_cm_max.setInputVerifier(new NumberInputVerifier(0,200));
		this.add(txtD7m_cm_max, c);	

		//label
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblSchaftlaenge = new JLabel(lang.getText(lang.lblSchaftlaenge_m));
		this.add(lblSchaftlaenge, c);		

		//text field
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtSchaftlaenge_m_min = new JSpinner( new SpinnerNumberModel(15, 0, 100, 1) );
		txtSchaftlaenge_m_min.setMinimumSize(STANDARD_TEXTFIELD_SIZE);
		txtSchaftlaenge_m_min.setPreferredSize(STANDARD_TEXTFIELD_SIZE);
		setSpinnerToDisallowInvalidCharacters(txtSchaftlaenge_m_min);
		//			txtSchaftlaenge_m_min.setDocument(new NumberDocument(5));
		//			txtSchaftlaenge_m_min.setInputVerifier(new NumberInputVerifier(0,100));
		this.add(txtSchaftlaenge_m_min, c);	

		//text field
		c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtSchaftlaenge_m_max = new JSpinner( new SpinnerNumberModel(40, 0, 100, 1) );
		txtSchaftlaenge_m_max.setMinimumSize(STANDARD_TEXTFIELD_SIZE);
		txtSchaftlaenge_m_max.setPreferredSize(STANDARD_TEXTFIELD_SIZE);
		setSpinnerToDisallowInvalidCharacters(txtSchaftlaenge_m_max);
		//			txtSchaftlaenge_m_max.setDocument(new NumberDocument(5));
		//			txtSchaftlaenge_m_max.setInputVerifier(new NumberInputVerifier(0,100));
		this.add(txtSchaftlaenge_m_max, c);
	}
	
	private void setSpinnerToDisallowInvalidCharacters(JSpinner spinner) {
		// get editor
		JSpinner.DefaultEditor editor = (DefaultEditor) spinner.getEditor();
		
		//get text field
		JFormattedTextField field = editor.getTextField();
		
		//get formatter
		DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
		
		//disable invalid charcaters
		formatter.setAllowsInvalid(false); 
	}

	public void updateLabels() {
		((TitledBorder)this.getBorder()).setTitle(lang.getText(lang.lblBaeume));
		lblBaumartCode	.setText(lang.getText(lang.lblBaumartCode));
		lblD7mBhD		.setText(lang.getText(lang.lblD7mBHDRelation));
		lblSchaftanteil	.setText(lang.getText(lang.lblZuVerwendenderSchaftanteil));
		lblVon			.setText(lang.getText(lang.lblVon));
		lblBis			.setText(lang.getText(lang.lblBis));
		lblBhd			.setText(lang.getText(lang.lblBHD_cm));
		lblD7m			.setText(lang.getText(lang.lblD7m_cm));
		lblSchaftlaenge	.setText(lang.getText(lang.lblSchaftlaenge_m));	
	}
	
	private int getBhdMin() {
		return (Integer) this.txtBhd_cm_min.getValue();
	}
	
	private int getBhdMax() {
		return (Integer) this.txtBhd_cm_max.getValue();
	}
	
	private int getSchaftlaengeMin() {
		return (Integer) this.txtSchaftlaenge_m_min.getValue();
	}
	
	private int getSchaftlaengeMax() {
		return (Integer) this.txtSchaftlaenge_m_max.getValue();
	}
	
	public int getSchaftanteil() {
		return (Integer) this.txtZuVerwendenderSchaftanteil.getValue();
	}
	
	public Baumart getBaumart() {
		return (Baumart) cmbBaumart.getSelectedItem();
	}
	
	private void initBaumartListe() {
		Baumart[] baumarten = Baumart.values();
		cmbBaumart.removeAllItems();
		for (int i=0; i<baumarten.length; i++) {
			cmbBaumart.addItem(baumarten[i]);
		}
	}
	
	public void addBaumartChangeListener(ActionListener al) {
		cmbBaumart.addActionListener(al);		
	}
	
	private void setD7mVisible(boolean flag) {
		lblD7mBhD.setVisible(flag);
		cmbD7mBhD.setVisible(flag);
		lblD7m.setVisible(flag);
		txtD7m_cm_min.setVisible(flag);
		txtD7m_cm_max.setVisible(flag);
	}
	
	private void updateD7Combo() {		
		cmbD7mBhD.removeAllItems();
		
		Baumart baumart = getBaumart();
		if (baumart == null) {
			return;
		}
		
		Double valGut = D7mBhdRelations.getRelation(baumart, Standort.Gut);
		Double valMager = D7mBhdRelations.getRelation(baumart, Standort.Mager);
		
		if (valGut != null) {
			cmbD7mBhD.addItem(new D7mBhDRelation(lang.getText(lang.lblStandortGut), valGut));
		}
		
		if (valMager != null) {
			cmbD7mBhD.addItem(new D7mBhDRelation(lang.getText(lang.lblStandortMager), valMager));
		}
	}
	
	private void updateD7Values() {
		if (cmbD7mBhD.getSelectedItem() == null) {
			return;
		}
		
		double value = ((D7mBhDRelation) cmbD7mBhD.getSelectedItem()).getValue();
		txtD7m_cm_min.setValue(this.getBhdMin() * value);
		txtD7m_cm_max.setValue(this.getBhdMax() * value);
	}
	
	private class D7mBhDRelation {		
		private String text;
		private double value;
		
		private D7mBhDRelation(String text, double value) {
			this.text = text;
			this.value = value;
		}
		
		public double getValue() {
			return value;
		}		
		
		@Override
		public String toString() {
			return text + " = " + value;
		}
	}
	
	public void baumschaefteEinfuellen(List<BaumschaftDefinition> bsDefListe) {
		Baumart baumart = getBaumart();
		int iBhdMin = getBhdMin();
		int iBhdMax = getBhdMax();
		int iSchaftlaengeMin = getSchaftlaengeMin();
		int iSchaftlaengeMax = getSchaftlaengeMax();

		// -----------------------------------
		// Füllt Baumschäfte ein, um eine Sortimentstabelle 
		// für das Projekt Energieholzkarte von C. Rosset zu erstellen. 
		// Baumschaftdaten 
		// - BHD in 1cm-Schritten 
		// - Höhen  in 1m-Schritten
		// -----------------------------------

		bsDefListe.clear();

		int lastId = 0;
		for (int iBHD_cm=iBhdMin; iBHD_cm<=iBhdMax; iBHD_cm++) {
			for (int iHoehe_m=iSchaftlaengeMin; iHoehe_m<=iSchaftlaengeMax; iHoehe_m++) {
				BaumschaftDefinitionBuilder bsDefBuilder = new BaumschaftDefinitionBuilder();

				bsDefBuilder.setBaumart(baumart);
				bsDefBuilder.setId( ++lastId );
				bsDefBuilder.setBhd_cm(iBHD_cm);
				bsDefBuilder.setSchaftLaenge_m(iHoehe_m);
				
				if (lfiSplineSelected == true) {		
					if (cmbD7mBhD.getSelectedItem() != null) {
						double value = ((D7mBhDRelation) cmbD7mBhD.getSelectedItem()).getValue();
						bsDefBuilder.setD7m_cm(iBHD_cm * value);	
					}
				}

				//set date to current year
				bsDefBuilder.setDatum(String.valueOf( (new GregorianCalendar()).get(Calendar.YEAR) ));

				bsDefListe.add(bsDefBuilder.build());
			}
		}
	}
	
	public void setSchaftformFunktion(BaumSchaftformFunktion schaftformFkt) {		
		lfiSplineSelected = (schaftformFkt instanceof BaumschaftformFkt_Lfi_Spline);
		setD7mVisible(lfiSplineSelected);
	}
}
