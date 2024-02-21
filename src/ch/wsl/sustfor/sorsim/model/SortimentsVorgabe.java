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
package ch.wsl.sustfor.sorsim.model;

import ch.wsl.sustfor.baumschaft.base.Baumart;
import ch.wsl.sustfor.lang.SorSimLanguageManager;

/**
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public class SortimentsVorgabe {

	private final int id;
    private final Baumart baumart;
    private final CodeLaengenKlassen laengenKlassenCode;
    private final CodeDrmKlassen durchmesserKlassenCode;
    private int anzahlStueck;
    private final double laengeMin_m;
    private final double laengeMax_m;
    private final double mittenDurchmMin_cm;
    private final double mittenDurchmMax_cm;
    private final double zopfDurchmMin_cm;
    private final double zopfDurchmMax_cm;
    private final double laengenZugabe_Prozent;
    private final double laengenZugabe_cm;
    private double positionAmStammUnten_m;
    private double positionAmStammOben_m;
    private final double laengenIntervall_m;
    private final SortimentsAushalteStrategie sortimentsAushalteStrategie;
    private final SortimentsStueckPositionierung sortimentsStueckPositionierung;
    private final double wertA;
    private final double wertB;
    private final double wertC;
    private final double wertD;
    private final double anteilA;
    private final double anteilB;
    private final double anteilC;
    private final double anteilD;

    public enum SortimentsAushalteStrategie {
    	MinimalLaenge,
    	MaximalLaenge;
    	
    	@Override
    	public String toString() {
    		return SorSimLanguageManager.getInstance().getText( this.getClass().getSimpleName() + "." + this.name() );
    	}
    }

    public enum SortimentsStueckPositionierung {
//    	Irgendwo,   	
    	ZuUnterst,
    	ZuOberst;
    	
    	@Override
    	public String toString() {
    		return SorSimLanguageManager.getInstance().getText(  this.getClass().getSimpleName() + "." + this.name() );
    	}
    }
    
    public enum CodeLaengenKlassen { //wichtig: konstantennamen dürfen nicht geändert werden, da vergleiche mittels name() !
    	L1,
    	L2,
    	L3,
    	L1L2,
    	L2L3,
    	L1L2L3,
    	Restholz;
        
        private SorSimLanguageManager lang = SorSimLanguageManager.getInstance();
        
    	@Override
    	public String toString() {
    		switch (this) {
    		case L1L2:
    			return "L1+L2";
    			
    		case L2L3:
    			return "L2+L3";
    			
    		case L1L2L3:
    			return "L1+L2+L3";
    			
    		case Restholz:
    			return lang.getText(lang.lblRestholz);

    		case L1:
    		case L2:
    		case L3:
    		default:
    			return this.name();
    		}
    	}
    }
    
    public enum CodeDrmKlassen {
    	Drm1a, // MiDrm 10 - <15, Zopf 0
    	Drm1b, // MiDrm 15 - <20, Zopf 14
    	Drm2a, // MiDrm 20 - <25, Zopf 18
    	Drm2b, // MiDrm 25 - <30, Zopf 18
    	Drm3a, // MiDrm 30 - <35, Zopf 18
    	Drm3b, // MiDrm 35 - <40, Zopf 18
    	Drm4,  // MiDrm 40 - <50, Zopf 22
    	Drm5,  // MiDrm 50 - <60, Zopf 22
    	Drm6,  // MiDrm >=60, Zopf 22
    	Restholz;
    	
    	@Override
    	public String toString() {
    		if (this == Restholz) {
    			return Restholz.name();
    		}
    		return this.name().substring(3); //remove "Drm" inside name
    	}

		public static CodeDrmKlassen valueOfText(String name) {
			if (name.equalsIgnoreCase(Restholz.name())) {
				return Restholz;
			}
			if ( ! name.startsWith("Drm") ){
				name = "Drm" + name;
			}
			return valueOf(name);
		}
    }

    private SortimentsVorgabe(SortimentsVorgabeBuilder builder) {
    	this.id = builder.id;
    	this.baumart = builder.baumart;
    	this.laengenKlassenCode = builder.laengenKlassenCode;
    	this.durchmesserKlassenCode = builder.durchmesserKlassenCode;
    	this.anzahlStueck = builder.anzahlStueck;
    	this.laengeMin_m = builder.laengeMin_m;
    	this.laengeMax_m = builder.laengeMax_m;
    	this.mittenDurchmMin_cm = builder.mittenDurchmMin_cm;
    	this.mittenDurchmMax_cm = builder.mittenDurchmMax_cm;
    	this.zopfDurchmMin_cm = builder.zopfDurchmMin_cm;
    	this.zopfDurchmMax_cm = builder.zopfDurchmMax_cm;
    	this.laengenZugabe_Prozent = builder.laengenZugabe_Prozent;
    	this.laengenZugabe_cm = builder.laengenZugabe_cm;
    	this.positionAmStammUnten_m = builder.positionAmStammUnten_m;
    	this.positionAmStammOben_m = builder.positionAmStammOben_m;
    	this.laengenIntervall_m = builder.laengenIntervall_m;
    	this.sortimentsAushalteStrategie = builder.sortimentsAushalteStrategie;
    	this.sortimentsStueckPositionierung = builder.sortimentsStueckPositionierung;
    	this.wertA = builder.wertA;
    	this.wertB = builder.wertB;
    	this.wertC = builder.wertC;
    	this.wertD = builder.wertD;
    	this.anteilA = builder.anteilA;
    	this.anteilB = builder.anteilB;
    	this.anteilC = builder.anteilC;
    	this.anteilD = builder.anteilD;
    }
    
    @Override
	public SortimentsVorgabe clone(){
    	SortimentsVorgabeBuilder builder = new SortimentsVorgabeBuilder();
    	builder.id = this.id;
    	builder.baumart = this.baumart;
    	builder.laengenKlassenCode = this.laengenKlassenCode;
    	builder.durchmesserKlassenCode = this.durchmesserKlassenCode;
    	builder.anzahlStueck = this.anzahlStueck;
    	builder.laengeMin_m = this.laengeMin_m;
    	builder.laengeMax_m = this.laengeMax_m;
    	builder.mittenDurchmMin_cm = this.mittenDurchmMin_cm;
    	builder.mittenDurchmMax_cm = this.mittenDurchmMax_cm;
    	builder.zopfDurchmMin_cm = this.zopfDurchmMin_cm;
    	builder.zopfDurchmMax_cm = this.zopfDurchmMax_cm;
    	builder.laengenZugabe_Prozent = this.laengenZugabe_Prozent;
    	builder.laengenZugabe_cm = this.laengenZugabe_cm;
    	builder.positionAmStammUnten_m = this.positionAmStammUnten_m;
    	builder.positionAmStammOben_m = this.positionAmStammOben_m;
    	builder.laengenIntervall_m = this.laengenIntervall_m;
    	builder.sortimentsAushalteStrategie = this.sortimentsAushalteStrategie;
    	builder.sortimentsStueckPositionierung = this.sortimentsStueckPositionierung;
    	builder.wertA = this.wertA;
    	builder.wertB = this.wertB;
    	builder.wertC = this.wertC;
    	builder.wertD = this.wertD;
    	builder.anteilA = this.anteilA;
    	builder.anteilB = this.anteilB;
    	builder.anteilC = this.anteilC;
    	builder.anteilD = this.anteilD;

    	SortimentsVorgabe result = builder.build();
    	return result;
    }
    
    public int getId() {
		return id;
	}

	public String getBeschrieb() {
		if (getLaengenKlassenCode() == CodeLaengenKlassen.Restholz) {
			return CodeLaengenKlassen.Restholz.toString();
		}
		return getLaengenKlassenCode() + " " + getDurchmesserKlassenCode();
	}

	public Baumart getBaumart() {
		return baumart;
	}

	public CodeLaengenKlassen getLaengenKlassenCode() {
		return laengenKlassenCode;
	}

	public CodeDrmKlassen getDurchmesserKlassenCode() {
		return durchmesserKlassenCode;
	}

	public int getAnzahlStueck() {
		return anzahlStueck;
	}

	public void reduceAnzahlStueck() {
		this.anzahlStueck--;
	}

	public double getLaengeMin_m() {
		return laengeMin_m;
	}

	public double getLaengeMax_m() {
		return laengeMax_m;
	}

	public double getMittenDurchmMin_cm() {
		return mittenDurchmMin_cm;
	}

	public double getMittenDurchmMax_cm() {
		return mittenDurchmMax_cm;
	}

	public double getZopfDurchmMin_cm() {
		return zopfDurchmMin_cm;
	}

	public double getZopfDurchmMax_cm() {
		return zopfDurchmMax_cm;
	}

	public double getLaengenZugabe_Prozent() {
		return laengenZugabe_Prozent;
	}

	public double getLaengenZugabe_cm() {
		return laengenZugabe_cm;
	}

	public double getPositionAmStammUnten_m() {
		return positionAmStammUnten_m;
	}

	public void setPositionAmStammUnten_m(double positionAmStammUnten_m) {
		this.positionAmStammUnten_m = positionAmStammUnten_m;
	}

	public double getPositionAmStammOben_m() {
		return positionAmStammOben_m;
	}

	public void setPositionAmStammOben_m(double positionAmStammOben_m) {
		this.positionAmStammOben_m = positionAmStammOben_m;
	}

	public double getLaengenIntervall_m() {
		return laengenIntervall_m;
	}
	
	public SortimentsAushalteStrategie getAushalteStrategie() {
		return sortimentsAushalteStrategie;
	}

	public SortimentsStueckPositionierung getPositionierung() {
		return sortimentsStueckPositionierung;
	}

	public double getWertProEinheit() {
		double result = 
				wertA * anteilA +
				wertB * anteilB +
				wertC * anteilC +
				wertD * anteilD;
		return result;
	}

	public double getWertA() {
		return wertA;
	}

	public double getWertB() {
		return wertB;
	}

	public double getWertC() {
		return wertC;
	}

	public double getWertD() {
		return wertD;
	}

	public double getAnteilA() {
		return anteilA;
	}

	public double getAnteilB() {
		return anteilB;
	}

	public double getAnteilC() {
		return anteilC;
	}

	public double getAnteilD() {
		return anteilD;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n" + "SortimentVorgaben" + "\n");
		sb.append("AushalteStrategie" + " = " + getAushalteStrategie() + 
				" (" + getAushalteStrategie() + ")" + "\n");
		sb.append("Positionierung" + " = " + getPositionierung() + 
				" (" + getPositionierung() + ")" + "\n");
		sb.append("PositionAmStammUnten_m" + " = " + getPositionAmStammUnten_m() + "\n");
		sb.append("PositionAmStammOben_m" + " = " + getPositionAmStammOben_m() + "\n");
		sb.append("LaengeMax_m" + " = " + getLaengeMax_m() + "\n");
		sb.append("LaengeMin_m" + " = " + getLaengeMin_m() + "\n");
		sb.append("MittenDurchmMax_cm" + " = " + getMittenDurchmMax_cm() + "\n");
		sb.append("MittenDurchmMin_cm" + " = " + getMittenDurchmMin_cm() + "\n");
		sb.append("ZopfDurchmMin_cm" + " = " + getZopfDurchmMin_cm() + "\n");
		sb.append("ZopfDurchmMax_cm" + " = " + getZopfDurchmMax_cm() + "\n");		
		
		return sb.toString();		
	}
	
	
	public static class SortimentsVorgabeBuilder {

		private int id = -1;
	    private Baumart baumart = Baumart.UebrigesNadelholz;
	    private CodeLaengenKlassen laengenKlassenCode;
	    private CodeDrmKlassen durchmesserKlassenCode;
	    private int anzahlStueck = 0;
	    private double laengeMin_m = -1;
	    private double laengeMax_m = -1;
	    private double mittenDurchmMin_cm = -1;
	    private double mittenDurchmMax_cm = -1;
	    private double zopfDurchmMin_cm = -1;
	    private double zopfDurchmMax_cm = -1;
	    private double laengenZugabe_Prozent = 0;
	    private double laengenZugabe_cm = 0;
	    private double positionAmStammUnten_m = -1;
	    private double positionAmStammOben_m = -1;
	    private double laengenIntervall_m = 0;
	    private SortimentsAushalteStrategie sortimentsAushalteStrategie = SortimentsAushalteStrategie.MaximalLaenge;
	    private SortimentsStueckPositionierung sortimentsStueckPositionierung = SortimentsStueckPositionierung.ZuUnterst;
	    private double wertA = 0;
	    private double wertB = 0;
	    private double wertC = 0;
	    private double wertD = 0;
	    private double anteilA = 0;
	    private double anteilB = 0;
	    private double anteilC = 0;
	    private double anteilD = 0;

		public void setId(int id) {
			this.id = id;
		}

		public void setBaumart(Baumart baumart) {
			this.baumart = baumart;
		}

		public void setLaengenKlassenCode(CodeLaengenKlassen laengenKlassenCode) {
			this.laengenKlassenCode = laengenKlassenCode;
		}

		public void setDurchmesserKlassenCode(CodeDrmKlassen durchmesserKlassenCode) {
			this.durchmesserKlassenCode = durchmesserKlassenCode;
		}

		public void setAnzahlStueck(int anzahlStueck) {
			this.anzahlStueck = anzahlStueck;
		}

		public void setLaengeMin_m(double laengeMin_m) {
			this.laengeMin_m = laengeMin_m;
		}

		public void setLaengeMax_m(double laengeMax_m) {
			this.laengeMax_m = laengeMax_m;
		}

		public void setMittenDurchmMin_cm(double mittenDurchmMin_cm) {
			this.mittenDurchmMin_cm = mittenDurchmMin_cm;
		}

		public void setMittenDurchmMax_cm(double mittenDurchmMax_cm) {
			this.mittenDurchmMax_cm = mittenDurchmMax_cm;
		}

		public void setZopfDurchmMin_cm(double zopfDurchmMin_cm) {
			this.zopfDurchmMin_cm = zopfDurchmMin_cm;
		}

		public void setZopfDurchmMax_cm(double zopfDurchmMax_cm) {
			this.zopfDurchmMax_cm = zopfDurchmMax_cm;
		}

		public void setLaengenZugabe_Prozent(double laengenZugabe_Prozent) {
			this.laengenZugabe_Prozent = laengenZugabe_Prozent;
		}

		public void setLaengenZugabe_cm(double laengenZugabe_cm) {
			this.laengenZugabe_cm = laengenZugabe_cm;
		}

		public void setPositionAmStammUnten_m(double positionAmStammUnten_m) {
			this.positionAmStammUnten_m = positionAmStammUnten_m;
		}

		public void setPositionAmStammOben_m(double positionAmStammOben_m) {
			this.positionAmStammOben_m = positionAmStammOben_m;
		}

		public void setLaengenIntervall_m(double laengenIntervall_m) {
			this.laengenIntervall_m = laengenIntervall_m;
		}

		public void setAushalteStrategie(SortimentsAushalteStrategie sortimentsAushalteStrategie) {
			this.sortimentsAushalteStrategie = sortimentsAushalteStrategie;
		}

		public void setPositionierung(SortimentsStueckPositionierung positionierung) {
			this.sortimentsStueckPositionierung = positionierung;
		}

		public void setWertA(double wertA) {
			this.wertA = wertA;
		}

		public void setWertB(double wertB) {
			this.wertB = wertB;
		}

		public void setWertC(double wertC) {
			this.wertC = wertC;
		}

		public void setWertD(double wertD) {
			this.wertD = wertD;
		}

		public void setAnteilA(double anteilA) {
			this.anteilA = anteilA;
		}

		public void setAnteilB(double anteilB) {
			this.anteilB = anteilB;
		}

		public void setAnteilC(double anteilC) {
			this.anteilC = anteilC;
		}

		public void setAnteilD(double anteilD) {
			this.anteilD = anteilD;
		}
		
		public SortimentsVorgabe build() {
			return new SortimentsVorgabe(this);
		}
	}
}

