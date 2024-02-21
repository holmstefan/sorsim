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

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.baumschaft.lemm1991.BaumSchaftformFkt_Lemm1991;
import ch.wsl.sustfor.baumschaft.lfispline.BaumschaftformFkt_Lfi_Spline;
import ch.wsl.sustfor.lang.SorSimLanguageManager;
import ch.wsl.sustfor.sorsim.StaticSettings;
import ch.wsl.sustfor.sorsim.gui.util.IStatusSubscriber;
import ch.wsl.sustfor.sorsim.gui.util.ProgressStatusBar;
import ch.wsl.sustfor.sorsim.gui.util.StatusHandler;


/**
 * 
 * @author Stefan Holm
 *
 */
public class MainWindow extends JFrame implements IStatusSubscriber {
	
	private static final long serialVersionUID = 1L;
	private static boolean isExtendedMode = ! StaticSettings.START_IM_STANDARDMODUS;
	private JPanel frameContainer;
	private ISorSimFrame[] frames = new ISorSimFrame[3];
	
	private SorSimLanguageManager lang = SorSimLanguageManager.getInstance();

	//Frame-Buttons
	private JToggleButton btnSortimentStueckInBsEinpassen;
	private JToggleButton btnSortimentstabelleErstellen;
	private JToggleButton btnBaumlisteSortieren;
	
	//Optionen
	private JPanel pnlOptionen;
	private JLabel lblSprachcode;
	private JComboBox<String> cmbSprachcode;
	private JLabel lblSchaftformFunktion;
	private JComboBox<BaumSchaftformFunktion> cmbSchaftformFunktion;
	private JLabel lblAnzahlNachkommastellen;
	private JSpinner txtAnzahlNachkommastellen;
	
	private ProgressStatusBar progressStatusBar = new ProgressStatusBar();

	public static void main(String[] args) {
		//load look & feel
		try {
			// Set System L&F
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			//UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel" );
		} 
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}
		
		//defaults
		String language = "de";
		BaumSchaftformFunktion schaftformFkt = new BaumSchaftformFkt_Lemm1991();
		int anzahlNachkommastellen = 2;
		
		//parse args		
		for (int i=0; i<args.length; i++) {
			String arg = args[i];
			
			//language
			if ( arg.equalsIgnoreCase("DE") ||  arg.equalsIgnoreCase("FR") ||  arg.equalsIgnoreCase("EN") ) {
				language = arg;
			}
			//standard mode
			else if ( arg.equalsIgnoreCase("std") ) {
				isExtendedMode = false;
			}
			//extended mode
			else if ( arg.equalsIgnoreCase("ext") ) {
				isExtendedMode = true;
			}
			//schaftform lemm 1991
			else if ( arg.equalsIgnoreCase("lemm") ) {
				schaftformFkt = new BaumSchaftformFkt_Lemm1991();
			}
			//schaftform lfi-splines
			else if ( arg.equalsIgnoreCase("lfi") ) {
				schaftformFkt = new BaumschaftformFkt_Lfi_Spline();
			}
			//anzahl nachkommastellen
			else if ( arg.matches("[0-9]+") ) {
				int number = Integer.valueOf(arg);
				if (number>=0 && number<=12) {
					//set genauigkeit
					anzahlNachkommastellen = number;
				} else {
					System.err.println("Ungültige Anzahl Nachkommastellen: " + number);
				}
			}
			else {
				System.err.println("Unbekanntes Programmargument: " + arg);
			}
		}
		
		//create main window
		new MainWindow(language, schaftformFkt, anzahlNachkommastellen);
	}
	
	private MainWindow(String language, BaumSchaftformFunktion schaftformFkt, int genauigkeit) {
		//window properties
		this.setSize(1000, 700);
		this.setMinimumSize(new Dimension(790, 580));
		this.setLocationByPlatform(true);
		this.setTitle("SorSim");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		//set language
		lang.setActiveLanguage(language);
		
		//menu
		createMenu();	
		
		//content
		createContent();
		
		//set options
		setExtendedMode(isExtendedMode);
		cmbSchaftformFunktion.setSelectedItem(schaftformFkt);
		txtAnzahlNachkommastellen.setValue((Integer) genauigkeit);
		
		//subscribe for status updates
		StatusHandler.subscribe(this);	

		//show window
		this.setVisible(true);
	}
	
	private void createMenu() {
		//MenuBar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		this.setJMenuBar(menuBar);
		
		//Top-level entries
		JMenu menuFile = new JMenu(lang.getText(lang.mnuDatei));
		menuBar.add(menuFile);
		
		JMenu menuTools = new JMenu(lang.getText(lang.mnuTools));
		menuBar.add(menuTools);
		
		JMenu menuHelp = new JMenu(lang.getText(lang.mnuHilfe));
		menuBar.add(menuHelp);

		//menu action: standard-modus
		Action aMenuModusStandard = new AbstractAction(lang.getText(lang.mnuStandardModus)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				setExtendedMode(false);
			}			
		};
		JRadioButtonMenuItem menuModusStandard = new JRadioButtonMenuItem();
		menuModusStandard.setAction(aMenuModusStandard);
		menuModusStandard.setSelected(!isExtendedMode);
		menuFile.add(menuModusStandard);

		//menu action: experten-modus
		Action aMenuModusExpert = new AbstractAction(lang.getText(lang.mnuExtendedModus)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				setExtendedMode(true);
			}			
		};
		JRadioButtonMenuItem menuModusExpert = new JRadioButtonMenuItem();
		menuModusExpert.setAction(aMenuModusExpert);
		menuModusExpert.setSelected(isExtendedMode);
		menuFile.add(menuModusExpert);
		
		//radio button - button group
		ButtonGroup groupModus = new ButtonGroup();
		groupModus.add(menuModusStandard);
		groupModus.add(menuModusExpert);
		
		//separator
		menuFile.addSeparator();
		
		//menu actions: close
		Action aMenuClose = new AbstractAction(lang.getText(lang.mnuBeenden)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);				
			}			
		};
		menuFile.add(aMenuClose);

		//menu actions: tools / svl editor
		Action aMenuToolsSvEditor = new AbstractAction(lang.getText(lang.mnuSvListeBearbeiten)){
			@Override
			public void actionPerformed(ActionEvent e) {
				new SortimentsVorgabenListeBearbeiten();
			}			
		};
		menuTools.add(aMenuToolsSvEditor);

		//menu actions: help
		Action aMenuHelpShowManual = new AbstractAction(lang.getText(lang.mnuHandbuch)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				//IMPORTANT: help-files have to be distributed separately from the jar,
				// since files inside the jar file can not be read directly by the system.
				
				String fileName = "SorSim Handbuch.pdf";
				File file = new File(fileName);
				
				//try to open file
				try {
					Desktop.getDesktop().open(file);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1.getMessage(), lang.getText(lang.errFehlerBeimOeffnenDerDatei), JOptionPane.ERROR_MESSAGE);			
				}
			}
		};
		menuHelp.add(aMenuHelpShowManual);
		
		//separator
		menuHelp.addSeparator();

		//menu actions: info
		Action aMenuHelpBaumartListe = new AbstractAction(lang.getText(lang.mnuBaumartenListeAnzeigen)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				BaumSchaftformFunktion fkt = ((BaumSchaftformFunktion) cmbSchaftformFunktion.getSelectedItem());				
				StringBuilder sb = new StringBuilder();
				sb.append(lang.getText(lang.lblGewaehlteSchaftformFkt) + "\n");
				sb.append("  " + fkt.toString() + "\n"  );
				sb.append("\n");
				sb.append(lang.getText(lang.lblCode_Baumart) + "\n");
//				sb.append("\n");
				for (int i=0; i<fkt.getBaumartListe().length; i++) {
					sb.append(fkt.getBaumartListe()[i].getExternalCode() + ":    " + fkt.getBaumartListe()[i] + "\n");
				}
				
				JOptionPane.showMessageDialog(null, sb.toString(), lang.getText(lang.lblBaumartenliste), JOptionPane.NO_OPTION);
			}			
		};
		menuHelp.add(aMenuHelpBaumartListe);
		
		//separator
		menuHelp.addSeparator();

		//menu actions: info
		Action aMenuInfo = new AbstractAction(lang.getText(lang.mnuInfo)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				sb.append("SorSim v" + StaticSettings.VERSION + "\n");
				sb.append("\n");
				sb.append("Bereitgestellt durch:\n");
				sb.append("Eidg. Forschungsanstalt WSL\n");
				sb.append("Forschungsgruppe Forstliche Produktionssysteme\n");
				sb.append("Züricherstrasse 111\n");
				sb.append("CH-8903 Birmensdorf\n");
				sb.append("\n");
				sb.append("\n");
				sb.append("Development-History:\n");
				sb.append(" - Version 2.1 (Java 17 / Refactoring): Stefan Holm, 2024\n");
				sb.append(" - Version 2.0 (Java): Stefan Holm, 2012\n");
				sb.append(" - Version 1.0 (VB.NET): Vinzenz Erni, 2007\n");
				sb.append("\n");
				sb.append("\n");
				sb.append("Parts of the code use math libraries provided by Dr. Michael Thomas Flangan (www.ee.ucl.ac.uk/~mflanaga).");
				
				JOptionPane.showMessageDialog(null, sb.toString(), "Info", JOptionPane.NO_OPTION);
			}			
		};
		menuHelp.add(aMenuInfo);	
	}
	
	private void createContent(){
		//remove all
		this.getContentPane().removeAll();

		//set layout
		this.setLayout( new GridBagLayout() );
		GridBagConstraints c;	
		
		//button
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 30;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		btnSortimentStueckInBsEinpassen = new JToggleButton();
		btnSortimentStueckInBsEinpassen.setAction(new AbstractAction(lang.getText(lang.btnSortimentStueckInBsEinpassen, 25, true)){
			@Override
			public void actionPerformed(ActionEvent e) {
				showFrame(0);
			}        	
        });
		btnSortimentStueckInBsEinpassen.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSortimentStueckInBsEinpassen.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSortimentStueckInBsEinpassen.setIconTextGap(20);
		this.getContentPane().add(btnSortimentStueckInBsEinpassen, c);
		
		//button
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 30;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		btnSortimentstabelleErstellen = new JToggleButton();
		btnSortimentstabelleErstellen.setAction(new AbstractAction(lang.getText(lang.btnSortimentsTabelleErstellen, 25, true)){
			@Override
			public void actionPerformed(ActionEvent e) {
				showFrame(1);
			}        	
        });
		btnSortimentstabelleErstellen.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSortimentstabelleErstellen.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSortimentstabelleErstellen.setIconTextGap(20);
		this.getContentPane().add(btnSortimentstabelleErstellen, c);
		
		//button
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 30;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		btnBaumlisteSortieren = new JToggleButton();
		btnBaumlisteSortieren.setAction(new AbstractAction(lang.getText(lang.btnBaumlisteSortimentieren, 25, true)){
			@Override
			public void actionPerformed(ActionEvent e) {
				showFrame(2);
			}        	
        });
		btnBaumlisteSortieren.setHorizontalTextPosition(SwingConstants.CENTER);
		btnBaumlisteSortieren.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnBaumlisteSortieren.setIconTextGap(20);
		this.getContentPane().add(btnBaumlisteSortieren, c);
		
		//placeholder		
		JPanel placeholder0 = new JPanel();
		Dimension size = new Dimension (180, 20);
		placeholder0.setMinimumSize(size);
		placeholder0.setPreferredSize(size);
		placeholder0.setMaximumSize(size);
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.weighty = 200;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.NORTH;
        this.getContentPane().add(placeholder0, c);	 
		
		//panel optionen
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		initPanelOptionen();
		this.getContentPane().add(pnlOptionen, c);
		
		//form container
        c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 10;
		c.weightx = 100;
		c.weighty = 100;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		frameContainer = new JPanel();
		frameContainer.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		frameContainer.setLayout( new GridBagLayout() );
		this.getContentPane().add(frameContainer, c);
		
		//status bar
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 99;
		c.gridwidth = 2;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		progressStatusBar.setMinimum(0);
		progressStatusBar.setMaximum(1000);
		this.getContentPane().add(progressStatusBar, c);		
	}
	
	private void setExtendedMode(boolean flag) {
		//update var
		isExtendedMode = flag;
		
		//update options
		btnSortimentStueckInBsEinpassen.setVisible(flag);
		btnSortimentstabelleErstellen.setVisible(flag);
		lblSchaftformFunktion.setVisible(flag);
		cmbSchaftformFunktion.setVisible(flag);
		lblAnzahlNachkommastellen.setVisible(flag);
		txtAnzahlNachkommastellen.setVisible(flag);
		this.validate();
		
		//update frames
		for (int i=0; i<frames.length; i++) {
			ISorSimFrame frame = frames[i];
			if (frame != null) {
				frame.setExtendedMode(flag);			
			}
		}
	}
	
	private void initFrame(int frameNr) {
		//init frame when needed for the first time & keep afterwards
		switch (frameNr) {
		case 0:
			if (frames[frameNr] == null) {
				frames[frameNr] = new SortimentStueckInBaumschaftEinpassenGUI(this);
			}
			break;
			
		case 1:
			if (frames[frameNr] == null) {
				frames[frameNr] = new SortimentstabelleErstellenGUI();
			}
			break;
			
		case 2:
			if (frames[frameNr] == null) {
				frames[frameNr] = new BaumListeSortimentierenGUI();
			}
			break;
			
		default:
			throw new RuntimeException("this should never happen");
		}
		
		frames[frameNr].setExtendedMode(isExtendedMode);
		frames[frameNr].setAnzahlNachkommastellen((Integer)txtAnzahlNachkommastellen.getValue());
		frames[frameNr].setSchaftformFunktion((BaumSchaftformFunktion) cmbSchaftformFunktion.getSelectedItem());
	}
	
	private void showFrame(int frameNr) {
		//update buttons
		switch (frameNr) {
		case 0:
			btnSortimentStueckInBsEinpassen.setSelected(true);
			btnSortimentstabelleErstellen.setSelected(false);
			btnBaumlisteSortieren.setSelected(false);
			break;
			
		case 1:
			btnSortimentStueckInBsEinpassen.setSelected(false);
			btnSortimentstabelleErstellen.setSelected(true);
			btnBaumlisteSortieren.setSelected(false);
			break;
			
		case 2:
			btnSortimentStueckInBsEinpassen.setSelected(false);
			btnSortimentstabelleErstellen.setSelected(false);
			btnBaumlisteSortieren.setSelected(true);
			break;
			
		default:
			throw new RuntimeException("this should never happen");
		}
		
		//init new frame
		initFrame(frameNr);
		
		//hide and remove old components
		while (frameContainer.getComponentCount() > 0) {
			frameContainer.getComponent(0).setVisible(false);
			frameContainer.remove(0);
		}
		
		//add new frame to container		
        GridBagConstraints c = new GridBagConstraints();
		c.weightx = 100;
		c.weighty = 100;
		c.fill = GridBagConstraints.BOTH;		
		frameContainer.add((Component) frames[frameNr], c);
		
		//show and validate
		frameContainer.getComponent(0).setVisible(true);
		frameContainer.validate();
	}

	private void initPanelOptionen() {
		//init panel
		pnlOptionen = new JPanel();
		pnlOptionen.setBorder( BorderFactory.createTitledBorder(lang.getText(lang.lblOptionen)));
		
		//set layout
		pnlOptionen.setLayout( new GridBagLayout() );
		GridBagConstraints c;

		//label sprachcode
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,5,0,5);
		lblSprachcode = new JLabel(lang.getText(lang.lblSprachcode));
		pnlOptionen.add(lblSprachcode, c);		
		
		//combo box sprachcode
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		cmbSprachcode = new JComboBox<>();
		cmbSprachcode.addItem("de");
		cmbSprachcode.addItem("fr");
		cmbSprachcode.addItem("en");
		cmbSprachcode.setSelectedItem(lang.getActiveLanguage());
		cmbSprachcode.addActionListener(e -> updateLanguage());
		pnlOptionen.add(cmbSprachcode, c);
				
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,5,0,5);
		lblSchaftformFunktion = new JLabel(lang.getText(lang.lblSchaftformFkt));
		pnlOptionen.add(lblSchaftformFunktion, c);		
		
		//combo box
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 10;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		cmbSchaftformFunktion = new JComboBox<>();
		cmbSchaftformFunktion.addItem( new BaumSchaftformFkt_Lemm1991() );
//		cmbSchaftformFunktion.addItem( new BaumSchaftformFkt_Spline() );
		cmbSchaftformFunktion.addItem( new BaumschaftformFkt_Lfi_Spline() );
		cmbSchaftformFunktion.addActionListener(e -> {
//			if (cmbSchaftformFunktion.getSelectedItem() instanceof BaumschaftformFkt_Lfi_Spline) {
//				String text = "Gewählte Schaftform-Funktion ist erst teilweise implementiert!";
//				JOptionPane.showMessageDialog(null, text, "SorSim", JOptionPane.WARNING_MESSAGE);
//			}

			for (int i=0; i<frames.length; i++) {
				if (frames[i] != null) {
					frames[i].setSchaftformFunktion((BaumSchaftformFunktion) cmbSchaftformFunktion.getSelectedItem());
				}
			}		
		});
		pnlOptionen.add(cmbSchaftformFunktion, c);
		
		//label
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,5,0,5);
		lblAnzahlNachkommastellen = new JLabel(lang.getText(lang.lblNachkommastellen, 30, true));
		pnlOptionen.add(lblAnzahlNachkommastellen, c);
		
		//text field
        c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 5;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		txtAnzahlNachkommastellen = new JSpinner(new SpinnerNumberModel(2, 0, 12, 1));
//		txtAnzahlNachkommastellen.setDocument(new NumberDocument(2, true));
//		txtAnzahlNachkommastellen.setInputVerifier(new NumberInputVerifier(0,12));
		txtAnzahlNachkommastellen.addChangeListener(e -> {
			int anzahlStellenNachKomma = (Integer)txtAnzahlNachkommastellen.getValue();
			for (int i=0; i<frames.length; i++) {
				if (frames[i] != null) {			
					frames[i].setAnzahlNachkommastellen(anzahlStellenNachKomma);
				}
			}});
//		txtAnzahlNachkommastellen.setText("2");
		pnlOptionen.add(txtAnzahlNachkommastellen, c);	
	}

	private void updateLanguage() {
		//set new active language
		lang.setActiveLanguage((String) cmbSprachcode.getSelectedItem());		
		
		//update main window
		createMenu();
		btnSortimentStueckInBsEinpassen.setText(lang.getText(lang.btnSortimentStueckInBsEinpassen, 25, true));
		btnSortimentstabelleErstellen.setText(lang.getText(lang.btnSortimentsTabelleErstellen, 25, true));
		btnBaumlisteSortieren.setText(lang.getText(lang.btnBaumlisteSortimentieren, 25, true));		
		((TitledBorder) pnlOptionen.getBorder()).setTitle(lang.getText(lang.lblOptionen));		
		lblSprachcode.setText(lang.getText(lang.lblSprachcode));
		lblSchaftformFunktion.setText(lang.getText(lang.lblSchaftformFkt));
		lblAnzahlNachkommastellen.setText(lang.getText(lang.lblNachkommastellen, 30, true));		
		
		//update frames		
		for (int i=0; i<frames.length; i++) {
			if (frames[i] != null) {
				frames[i].updateLabels();
			}
		}
	}

	@Override
	public void setText(String text) {
		System.out.println("Status-Update: " + text);		
	}

	@Override
	public void setProgress(double progress) {
		progressStatusBar.setValue((int) (progress * 1000));
	}
	
	
	public BaumSchaftformFunktion getBaumSchaftformFunktion() {
		return (BaumSchaftformFunktion) cmbSchaftformFunktion.getSelectedItem();
	}
}