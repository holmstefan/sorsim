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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import ch.wsl.sustfor.baumschaft.base.Baumart;
import ch.wsl.sustfor.lang.SorSimLanguageManager;
import ch.wsl.sustfor.sorsim.controller.Presenter;
import ch.wsl.sustfor.sorsim.model.SortimentVorgabenListe;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.CodeDrmKlassen;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.CodeLaengenKlassen;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsAushalteStrategie;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsStueckPositionierung;
import ch.wsl.sustfor.sorsim.model.SortimentsVorgabe.SortimentsVorgabeBuilder;

/**
 * 
 * @author Stefan Holm
 *
 */
public class SortimentsVorgabenListeBearbeiten extends JDialog {

	private static final long serialVersionUID = 1L;	

	//Tabelle
	private JTable tblOutput;
	private MyDefaultTableModel tblOutputTableModel;
	
	//Buttons
	private JTextField txtFileNamePath;
	private JButton btnLoad;
	private JButton btnSave;
	
	//nicht-GUI-Variablen
    private Presenter presenter = new Presenter();
	private SorSimLanguageManager lang = SorSimLanguageManager.getInstance();	
	
	public SortimentsVorgabenListeBearbeiten() {
		this(null);
	}
	
	public SortimentsVorgabenListeBearbeiten(String fileToLoadDirectly) {
		//window properties
		this.setSize(1000, 700);
		this.setMinimumSize(new Dimension(700, 550));
		this.setLocationByPlatform(true);
		this.setTitle(lang.getText(lang.lblSvListeBearbeiten));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);	
		
		//content
		createContent();
		
		//load file if given
		if (fileToLoadDirectly != null) {
			loadFile(fileToLoadDirectly);
		}

		//show window
		this.setVisible(true);
	}
	
	private void createContent() {
		//set layout
		this.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//panel Allgemein
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		JPanel pnlAllgemein = initPanelAllgemein();
		this.add(pnlAllgemein, c);
		
		//panel Table
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 100;
		c.weighty = 100;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		JPanel pnlTable = initPanelTable();
		this.add(pnlTable, c);
		
		//panel Buttons
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		JPanel pnlButtons = initPanelButtons();
		this.add(pnlButtons, c);
	}

	private JPanel initPanelAllgemein() {
		//init panel
		JPanel pnlAllgemein = new JPanel();
		
		//set layout
		pnlAllgemein.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1000;
		c.insets = new Insets(0,5,0,5);
		JLabel lbl1 = new JLabel("<html>" + lang.getText(lang.lblSvListeAnmerkungen) + "<html>");
		pnlAllgemein.add(lbl1, c);
		
		return pnlAllgemein;
	}

	private JPanel initPanelTable() {	
		//init panel
		JPanel pnlTable = new JPanel();		
		pnlTable.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblSortimentsVorgaben)));

		//set layout
		pnlTable.setLayout( new GridBagLayout() );
		GridBagConstraints c;	

		//text area
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 100;
		c.weighty = 100;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,5,5,5);
		initTableModel();
		tblOutput = new JTable(tblOutputTableModel) {
			private static final long serialVersionUID = 1L;
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);				
				if (isCellSelected(rowIndex, vColIndex)) {
					if (!isCellEditable(rowIndex, vColIndex)) {
						c.setBackground(Color.DARK_GRAY);
					} else {
						c.setBackground(getSelectionBackground());
					}
				} else {
					if (!isCellEditable(rowIndex, vColIndex)) {
						c.setBackground(Color.LIGHT_GRAY);
					} else {
						c.setBackground(getBackground());
					}
				}
				
				return c;
			}
		};
		tblOutput.getModel().addTableModelListener(e -> {
			if (e.getType() == TableModelEvent.UPDATE && e.getColumn() >= 19 && e.getColumn() <= 26) {
				for (int i=e.getFirstRow(); i<=e.getLastRow(); i++) {
					double wertA = 		(Double) tblOutputTableModel.getValueAt(i, 19);
					double wertB = 		(Double) tblOutputTableModel.getValueAt(i, 20);
					double wertC = 		(Double) tblOutputTableModel.getValueAt(i, 21);
					double wertD = 		(Double) tblOutputTableModel.getValueAt(i, 22);
					double anteilA = 	(Double) tblOutputTableModel.getValueAt(i, 23);
					double anteilB = 	(Double) tblOutputTableModel.getValueAt(i, 24);
					double anteilC = 	(Double) tblOutputTableModel.getValueAt(i, 25);
					double anteilD = 	(Double) tblOutputTableModel.getValueAt(i, 26);

					double price = 
							wertA * anteilA +
							wertB * anteilB +
							wertC * anteilC +
							wertD * anteilD;

					tblOutputTableModel.setValueAt(price, i, 27);
				}			
			}
		});
		tblOutput.setShowHorizontalLines(false);
		tblOutput.setShowVerticalLines(false);
		tblOutput.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblOutput.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); 
		tblOutput.setCellSelectionEnabled(true);
//		tblOutput.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tblOutput.getColumnModel().getColumn(17).setCellEditor(new DefaultCellEditor(new JComboBox<>(SortimentsAushalteStrategie.values())));
		tblOutput.getColumnModel().getColumn(18).setCellEditor(new DefaultCellEditor(new JComboBox<>(SortimentsStueckPositionierung.values())));
		JScrollPane scrlTxtOutput =  new JScrollPane(tblOutput);
		pnlTable.add(scrlTxtOutput, c);
		
		return pnlTable;
	}
	
	private void initTableModel() {
		String[] columnNames = {
				lang.getText(lang.titleId), 
				lang.getText(lang.titleBeschriebBaumNr), 
				lang.getText(lang.titleBaumart),
				lang.getText(lang.titleLaengenklasse),
				lang.getText(lang.titleStaerkenklasse),
				lang.getText(lang.titleAnzahlStueck),
				lang.getText(lang.titleLaengeMin_m),
				lang.getText(lang.titleLaengeMax_m),
				lang.getText(lang.titleMittenDrmMin_cm),
				lang.getText(lang.titleMittenDrmMax_cm),
				lang.getText(lang.titleZopfDrmMin_cm),
				lang.getText(lang.titleZopfDrmMax_cm),
				lang.getText(lang.titleLaengenZugabe_cm),
				lang.getText(lang.titleLaengenZugabe_Prozent),
				lang.getText(lang.titlePositionAmStammUnten_m),
				lang.getText(lang.titlePositionAmStaummOben_m),
				lang.getText(lang.titleLaengenIntervall_m),
				lang.getText(lang.titleAushaltestrategie),
				lang.getText(lang.titlePositionierung),
				lang.getText(lang.titleWertA),
				lang.getText(lang.titleWertB),
				lang.getText(lang.titleWertC),
				lang.getText(lang.titleWertD),
				lang.getText(lang.titleAnteilA),
				lang.getText(lang.titleAnteilB),
				lang.getText(lang.titleAnteilC), 
				lang.getText(lang.titleAnteilD),
				lang.getText(lang.titleWertProEinheit)
				};		

		tblOutputTableModel = new MyDefaultTableModel( columnNames, 0 );
	}
	
	private class MyDefaultTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		
		public MyDefaultTableModel(String[] columnNames, int i) {
			super(columnNames, i);
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (CodeLaengenKlassen.Restholz.equals(tblOutputTableModel.getValueAt(rowIndex, 3))) {
				if (columnIndex <= 18) {
					return false;
				}
			}
			
			switch (columnIndex) {
			case 5:
			case 16:
			case 17:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
				return true;
			default:
				return false;
			}
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class getColumnClass(int column) {
			switch (column) {
			case 0:
				return Integer.class;
			case 1:
				return String.class;
			case 2:
				return Baumart.class;
			case 3:
				return CodeLaengenKlassen.class;
			case 4:
				return CodeDrmKlassen.class;
			case 5:
				return Integer.class;
			case  6:
			case  7:
			case  8:
			case  9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
				return Double.class;
			case 17:
				return SortimentsAushalteStrategie.class;
			case 18:
				return SortimentsStueckPositionierung.class;
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
				return Double.class;
			default:
				throw new RuntimeException("invalid column");
//				return Object.class;
			}
		}
		@Override
		public Object getValueAt(int row, int column) {
			if (CodeLaengenKlassen.Restholz.equals(super.getValueAt(row, 3))) {
				if (column >= 5 && column <= 18) {
					return null;
				}
			}
			return super.getValueAt(row, column);
		}
		
		public Object getStoredValueAt(int row, int column) {
			return super.getValueAt(row, column);
		}
	}
	
	private JPanel initPanelButtons() {
		//init panel
		JPanel pnlButtons = new JPanel();
		
		//set layout
		pnlButtons.setLayout( new GridBagLayout() );
		GridBagConstraints c;
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		JLabel lbl1 = new JLabel(lang.getText(lang.lblSortimentsVorgabenFile));
		pnlButtons.add(lbl1, c);		
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,5,5);
		txtFileNamePath = new JTextField();
		txtFileNamePath.setEditable(false);		
		pnlButtons.add(txtFileNamePath, c);
		
		//button
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(0,5,5,5);
		c.anchor = GridBagConstraints.SOUTH;
		btnLoad = new JButton();
		btnLoad.setAction(new AbstractAction(lang.getText(lang.lblLaden)){
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(){
					@Override
					public void run() {
						btnLoad.setEnabled(false);
						btnLoad.setText(lang.getText(lang.lblBitteWarten));
						loadFile(null);
						btnLoad.setEnabled(true);	
						btnLoad.setText(lang.getText(lang.lblLaden));
					}
				};
				t.start();
			}        	
        });
		pnlButtons.add(btnLoad, c);
		
		//button
        c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(0,5,5,5);
		c.anchor = GridBagConstraints.SOUTH;
		btnSave = new JButton();
		btnSave.setAction(new AbstractAction(lang.getText(lang.lblSpeichern)){
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(){
					@Override
					public void run() {
						btnSave.setEnabled(false);
						btnSave.setText(lang.getText(lang.lblBitteWarten));
						saveFile();
						btnSave.setEnabled(true);	
						btnSave.setText(lang.getText(lang.lblSpeichern));					
					}
				};
				t.start();
			}        	
        });
		pnlButtons.add(btnSave, c);
		
		return pnlButtons;
	}

	private void loadFile(String filename) {
		SortimentVorgabenListe svl = new SortimentVorgabenListe();
        try {
			if (filename == null) {
				filename = showFileDialog(false);
			}
			if (filename != null)  {
				txtFileNamePath.setText(filename);
				presenter.readSortimentsVorgabenFromFile(svl, filename);
			}
			
		} catch (Exception e) {
	        JOptionPane.showMessageDialog(this, e.getClass().getSimpleName() + "\n" + e.getMessage(), lang.getText(lang.errFehler), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}
        
        showList(svl);        
	}
	
	private String showFileDialog(boolean saveDialog) {

		FileDialog fd = new FileDialog((Frame) null, lang.getText(lang.lblDateiAuswaehlen), saveDialog ? FileDialog.SAVE : FileDialog.LOAD);
		fd.setDirectory(".\\data");
		fd.setVisible(true);
		
		if (fd.getFile() != null) {
			return fd.getDirectory() + fd.getFile();
		}
		
		return null;
	}
	
	private void showList(SortimentVorgabenListe svl) {
		//clear table
		tblOutputTableModel.setRowCount(0);
			
		for (int i=0; i<svl.size(); i++) {
			SortimentsVorgabe sv = svl.get(i);
			
			//insert row for current SortStueck
			Object[] line = new Object[28];	
			line[ 0] = sv.getId();
			line[ 1] = sv.getBeschrieb();
			line[ 2] = sv.getBaumart();
			line[ 3] = sv.getLaengenKlassenCode();
			line[ 4] = sv.getDurchmesserKlassenCode();
			line[ 5] = sv.getAnzahlStueck();
			line[ 6] = sv.getLaengeMin_m();
			line[ 7] = sv.getLaengeMax_m();
			line[ 8] = sv.getMittenDurchmMin_cm();
			line[ 9] = sv.getMittenDurchmMax_cm();
			line[10] = sv.getZopfDurchmMin_cm();
			line[11] = sv.getZopfDurchmMax_cm();
			line[12] = sv.getLaengenZugabe_cm();
			line[13] = sv.getLaengenZugabe_Prozent();
			line[14] = sv.getPositionAmStammUnten_m();
			line[15] = sv.getPositionAmStammOben_m();
			line[16] = sv.getLaengenIntervall_m();
			line[17] = sv.getAushalteStrategie();
			line[18] = sv.getPositionierung();
			line[19] = sv.getWertA();
			line[20] = sv.getWertB();
			line[21] = sv.getWertC();
			line[22] = sv.getWertD();
			line[23] = sv.getAnteilA();
			line[24] = sv.getAnteilB();
			line[25] = sv.getAnteilC();
			line[26] = sv.getAnteilD();
			line[27] = sv.getWertProEinheit();
			tblOutputTableModel.addRow(line);			
		}
	}
	
	private SortimentVorgabenListe parseList() {
		SortimentVorgabenListe svl = new SortimentVorgabenListe();
			
		for (int i=0; i<tblOutputTableModel.getRowCount(); i++) {
			tblOutputTableModel.getValueAt(i, 0);
			
			//insert row for current SortStueck
	    	SortimentsVorgabeBuilder builder = new SortimentsVorgabeBuilder();
	    	builder.setId(					(Integer)   tblOutputTableModel.getStoredValueAt(i,  0));
//			builder.setBeschrieb(			(String) 	tblOutputTableModel.getStoredValueAt(i,  1));
	    	builder.setBaumart(				(Baumart) 	tblOutputTableModel.getStoredValueAt(i,  2));
	    	builder.setLaengenKlassenCode(		(CodeLaengenKlassen) 	tblOutputTableModel.getStoredValueAt(i,  3));
	    	builder.setDurchmesserKlassenCode(	(CodeDrmKlassen)		tblOutputTableModel.getStoredValueAt(i,  4));
	    	builder.setAnzahlStueck(			(Integer)  	tblOutputTableModel.getStoredValueAt(i,  5));
	    	builder.setLaengeMin_m(				(Double)  	tblOutputTableModel.getStoredValueAt(i,  6));
	    	builder.setLaengeMax_m(				(Double)  	tblOutputTableModel.getStoredValueAt(i,  7));
	    	builder.setMittenDurchmMin_cm(		(Double)  	tblOutputTableModel.getStoredValueAt(i,  8));
	    	builder.setMittenDurchmMax_cm(		(Double)  	tblOutputTableModel.getStoredValueAt(i,  9));
	    	builder.setZopfDurchmMin_cm(		(Double)  	tblOutputTableModel.getStoredValueAt(i, 10));
	    	builder.setZopfDurchmMax_cm(		(Double) 	tblOutputTableModel.getStoredValueAt(i, 11));
	    	builder.setLaengenZugabe_cm(		(Double) 	tblOutputTableModel.getStoredValueAt(i, 12));
	    	builder.setLaengenZugabe_Prozent(	(Double) 	tblOutputTableModel.getStoredValueAt(i, 13));
	    	builder.setPositionAmStammUnten_m(	(Double)  	tblOutputTableModel.getStoredValueAt(i, 14));
	    	builder.setPositionAmStammOben_m(	(Double) 	tblOutputTableModel.getStoredValueAt(i, 15));
	    	builder.setLaengenIntervall_m(		(Double)  	tblOutputTableModel.getStoredValueAt(i, 16));
	    	builder.setAushalteStrategie(	(SortimentsAushalteStrategie) 	tblOutputTableModel.getStoredValueAt(i, 17));
	    	builder.setPositionierung(	(SortimentsStueckPositionierung) 	tblOutputTableModel.getStoredValueAt(i, 18));
	    	builder.setWertA(				(Double)  	tblOutputTableModel.getStoredValueAt(i, 19));
	    	builder.setWertB(				(Double)  	tblOutputTableModel.getStoredValueAt(i, 20));
	    	builder.setWertC(				(Double)  	tblOutputTableModel.getStoredValueAt(i, 21));
	    	builder.setWertD(				(Double)  	tblOutputTableModel.getStoredValueAt(i, 22));
	    	builder.setAnteilA(				(Double)  	tblOutputTableModel.getStoredValueAt(i, 23));
	    	builder.setAnteilB(				(Double)  	tblOutputTableModel.getStoredValueAt(i, 24));
	    	builder.setAnteilC(				(Double)  	tblOutputTableModel.getStoredValueAt(i, 25));
	    	builder.setAnteilD(				(Double)  	tblOutputTableModel.getStoredValueAt(i, 26));
			
			SortimentsVorgabe sv = builder.build();
			
			if (sv.getAnteilA() + sv.getAnteilB() + sv.getAnteilC() + sv.getAnteilD() != 1.0) {
				throw new ArithmeticException(lang.getText(lang.errSummeNicht100));
			}
			
			svl.add(sv);			
		}
		return svl;
	}
	
	private void saveFile() {
		String filename = showFileDialog(true);
		if (filename != null)  {
			txtFileNamePath.setText(filename);
			try {
				presenter.writeSortimentsVorgabenListeToFile(parseList(), filename);
			} catch (ArithmeticException e) {
				String msg = lang.getText(lang.errDateiNichtGeschriebenSummeNicht1);
				JOptionPane.showMessageDialog(null, msg, lang.getText(lang.errFehler), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
