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

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import ch.wsl.sustfor.sorsim.StaticSettings;
import ch.wsl.sustfor.sorsim.model.SortimentsStueck;

/**
 * 
 * @author Stefan Holm
 *
 */
public class PanelErgebnis extends SorSimPanel {

	//Ergebnis
	private JLabel lblGesamtWert;
	private JTextField txtGesamtWert;
	
	private JLabel lblGesamtVolumen;
	private JTextField txtGesamtVolumen;
	
	private JLabel lblAnzahlSortimente;
	private JTextField txtAnzahlSortimente;
	
	private JLabel lblStueckvolumen;
	private JTextField txtStueckvolumen;
	
	private JTable tblOutput;
	private DefaultTableModel tblOutputTableModel;
	
	private int anzahlStellenNachKomma = 4;
	
	//Standard-Konstruktor
	public PanelErgebnis() {
		if (StaticSettings.USE_COLORS) {
			this.setOpaque(false);
			this.setBackground(StaticSettings.COLOR_ERGEBNIS);
		}
		initContent();
	}
	
	@Override //necessary to avoid artefacts when painting backgrounds with an alpha value < 1
    protected void paintComponent(Graphics g) {
        g.setColor( getBackground() );
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

	private void initContent(){
		//init panel
		this.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblZusammenfassungErgebnis)));

		//set layout
		this.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		lblGesamtWert = new JLabel(lang.getText(lang.lblGesamtwert));
		this.add(lblGesamtWert, c);
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		txtGesamtWert = new JTextField("0");
		txtGesamtWert.setEditable(false);
		if (StaticSettings.USE_COLORS) {
			txtGesamtWert.setOpaque(false);
		}
		this.add(txtGesamtWert, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,15,5,5);
		lblGesamtVolumen = new JLabel(lang.getText(lang.lblGesamtvolumenORm3));
		this.add(lblGesamtVolumen, c);
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		txtGesamtVolumen = new JTextField("0");
		txtGesamtVolumen.setEditable(false);
		if (StaticSettings.USE_COLORS) {
			txtGesamtVolumen.setOpaque(false);
		}
		this.add(txtGesamtVolumen, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,15,5,5);
		lblAnzahlSortimente = new JLabel(lang.getText(lang.lblAnzahlSortimente));
		
		this.add(lblAnzahlSortimente, c);
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 0;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		txtAnzahlSortimente = new JTextField("0");
		txtAnzahlSortimente.setEditable(false);
		if (StaticSettings.USE_COLORS) {
			txtAnzahlSortimente.setOpaque(false);
		}
		this.add(txtAnzahlSortimente, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 6;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,15,5,5);
		lblStueckvolumen = new JLabel(lang.getText(lang.lblStueckvolumen));
		
		this.add(lblStueckvolumen, c);
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 7;
		c.gridy = 0;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		txtStueckvolumen = new JTextField("0");
		txtStueckvolumen.setEditable(false);
		if (StaticSettings.USE_COLORS) {
			txtStueckvolumen.setOpaque(false);
		}
		this.add(txtStueckvolumen, c);

		//text area
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 100;
		c.weighty = 100;
		c.gridwidth = 8;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,5,5,5);
		initTableModel();
		tblOutput = new JTable(tblOutputTableModel);
		tblOutput.setShowHorizontalLines(false);
		tblOutput.setShowVerticalLines(false);
		tblOutput.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrlTxtOutput =  new JScrollPane(tblOutput);
		this.add(scrlTxtOutput, c);	
	}
	
	private void initTableModel() {
		Object[] columnNames = {
				lang.getAutoText(lang.titleStratifizierungsMerkmal), //lang.getAutoText(lang.titleAufnahmeDatum),
				lang.getAutoText(lang.titleBaumart),
				lang.getAutoText(lang.titleLaengenklasse),
				lang.getAutoText(lang.titleStaerkenklasse),
				lang.getAutoText(lang.titleVolumenOR_m3),
				lang.getAutoText(lang.titleVolumenIR_m3),
				lang.getAutoText(lang.titleWert_CHF),
				lang.getAutoText(lang.titleAnzahl)};

		tblOutputTableModel = new DefaultTableModel( columnNames, 0 ){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int rowIndex, int ColumnIndex) {
				return false;
			}
			@Override
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case  7:
					return String.class;
				default:
					throw new RuntimeException("invalid column");
//					return Object.class;
				}
			}
		};
	}
	
	public void writeZusammenfassung(List<SortimentsStueck> ssListe) {
		//get some basic stats
	   	double dWertGesamt = 0;
    	double dVolumenORGesamt_m3 = 0;
    	int iCount = 0;
    	
       	for (int i=0; i<ssListe.size(); i++) {
    		SortimentsStueck ss = ssListe.get(i);
    		dWertGesamt += ss.getWert();
    		dVolumenORGesamt_m3 += ss.getVolumenOR_m3();
    		iCount++;
    	}
       	txtGesamtWert		.setText( rnd(dWertGesamt					) );
       	txtGesamtVolumen	.setText( rnd(dVolumenORGesamt_m3			) );
       	txtAnzahlSortimente	.setText( String.valueOf(iCount				) );
       	txtStueckvolumen	.setText( rnd(dVolumenORGesamt_m3 / iCount	) );

		//clear table
		tblOutputTableModel.setRowCount(0);
		
       	//get summary		
		String zusFas = presenter.printSortimentsStueckListeZusammenfassung(ssListe);
		String[] zusFasLines = zusFas.split("\n");
		for (int i=1; i<zusFasLines.length; i++) {
			String[] zusFasLineFields = zusFasLines[i].split("\t");
			tblOutputTableModel.addRow(zusFasLineFields);
		}
	}
	
	public void setAnzahlStellenNachKomma(int anzahl) {
		this.anzahlStellenNachKomma = anzahl;
		this.presenter.setAnzahlStellenNachKomma(anzahl);
	}
	
	public void updateLabels() {		
		((TitledBorder) this.getBorder()).setTitle(lang.getText(lang.lblZusammenfassungErgebnis));

		lblGesamtWert.setText(lang.getText(lang.lblGesamtwert));
		lblGesamtVolumen.setText(lang.getText(lang.lblGesamtvolumenORm3));
		lblAnzahlSortimente.setText(lang.getText(lang.lblAnzahlSortimente));
		lblStueckvolumen.setText(lang.getText(lang.lblStueckvolumen));
		
		tblOutputTableModel.fireTableStructureChanged(); //nötig fur update der Titel	
	}
	
	private String rnd(double d) {
		double factor = Math.pow(10, anzahlStellenNachKomma);
		double dResult = Math.round(d * factor) / factor;
		
		//safety check
		if (Math.abs(dResult - d) > 0.5) { //can happen if precision is to high
			throw new RuntimeException("Rounding Error (input=" + d + ", output=" +dResult + ")");
		}
		
		return String.valueOf(dResult);
	}
}
