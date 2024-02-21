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

import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition;
import ch.wsl.sustfor.lang.SorSimLanguageManager;

/**
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public final class SortimentsStueck {

    private final int id;
    private final double basisDrmOR_cm;
    private final double basisDrmIR_cm;
    private final double mittenDrmOR_cm;
    private final double mittenDrmIR_cm;
    private final double zopfDrmOR_cm;
    private final double zopfDrmIR_cm;
    private final double laenge_m;
    private final double positionDerBasisAmStamm_m;
    private final double volumenRestStueckUntenOR_m3;
    private final double volumenRestStueckObenOR_m3;
    
    private final BaumschaftDefinition bsDef; //Link zum zugehörigen Baumschaft
    private final SortimentsVorgabe sv; //Link zur zugehörigen Sortimentsvorgabe
    
    private final ReststueckKategorie reststueckKategorie; // 1=standard, 2=aufgrund mindestzopf; 1 unterteilt in "1 oben" und "1 unten"

    public enum ReststueckKategorie {
    	Kat1,
    	Kat1unten,
    	Kat1oben,
    	Kat2;
    	
    	private static SorSimLanguageManager lang = SorSimLanguageManager.getInstance();
    	
    	@Override
		public String toString() {
    		switch (this) {
    		case Kat1:
    			return "1";
    			
    		case Kat1unten:
    			return "1 " + lang.getText(lang.lblUnten);
    			
    		case Kat1oben:
    			return "1 " + lang.getText(lang.lblOben);
    			
    		case Kat2:
    			return "2";
    			
    		default:
    			throw new RuntimeException("Unbekannte Reststück-Kategorie");
    		}
    	}
    }
    
    private SortimentsStueck(SortimentsStueckBuilder builder) {
    	this.id = builder.id;
    	this.basisDrmOR_cm = builder.basisDrmOR_cm;
    	this.basisDrmIR_cm = builder.basisDrmIR_cm;
    	this.mittenDrmOR_cm = builder.mittenDrmOR_cm;
    	this.mittenDrmIR_cm = builder.mittenDrmIR_cm;
    	this.zopfDrmOR_cm = builder.zopfDrmOR_cm;
    	this.zopfDrmIR_cm = builder.zopfDrmIR_cm;
    	this.laenge_m = builder.laenge_m;
    	this.positionDerBasisAmStamm_m = builder.positionDerBasisAmStamm_m;
    	this.volumenRestStueckUntenOR_m3 = builder.volumenRestStueckUntenOR_m3;
    	this.volumenRestStueckObenOR_m3 = builder.volumenRestStueckObenOR_m3;
    	this.bsDef = builder.bsDef;
    	this.sv = builder.sv;
    	this.reststueckKategorie = builder.reststueckKategorie;
    }

	public int getId() {
		return id;
	}

	public double getBasisDrmOR_cm() {
		return basisDrmOR_cm;
	}

	public double getBasisDrmIR_cm() {
		return basisDrmIR_cm;
	}

	public double getMittenDrmOR_cm() {
		return mittenDrmOR_cm;
	}

	public double getMittenDrmIR_cm() {
		return mittenDrmIR_cm;
	}

	public double getZopfDrmOR_cm() {
		return zopfDrmOR_cm;
	}

	public double getZopfDrmIR_cm() {
		return zopfDrmIR_cm;
	}

	public double getLaenge_m() {
		return laenge_m;
	}

	public double getVolumenIR_m3() {
//		return Math.pow(mittenDrmIR_cm / 100 / 2, 2) * Math.PI * laenge_m; //falsch!!		
		return bsDef.getStammStueckVolumen_m3(positionDerBasisAmStamm_m, positionDerBasisAmStamm_m + laenge_m, 0.1, false);
	}

	public double getVolumenOR_m3() {
//		return Math.pow(mittenDrmOR_cm / 100 / 2, 2) * Math.PI * laenge_m; //falsch!!
		return bsDef.getStammStueckVolumen_m3(positionDerBasisAmStamm_m, positionDerBasisAmStamm_m + laenge_m, 0.1, true);
	}

	public double getPositionDerBasisAmStamm_m() {
		return positionDerBasisAmStamm_m;
	}

	public double getVolumenRestStueckUntenOR_m3() {
		return volumenRestStueckUntenOR_m3;
	}

	public double getVolumenRestStueckObenOR_m3() {
		return volumenRestStueckObenOR_m3;
	}

	public double getWert() {
		return getVolumenOR_m3() * sv.getWertProEinheit();
	}

	public BaumschaftDefinition getBsDef() {
		return bsDef;
	}

	public SortimentsVorgabe getSortimentsVorgabe() {
		return sv;
	}

	public ReststueckKategorie getReststueckKategorie() {
		return reststueckKategorie;
	}
	
	
	public static class SortimentsStueckBuilder {
	    private int id;
	    private double basisDrmOR_cm;
	    private double basisDrmIR_cm;
	    private double mittenDrmOR_cm;
	    private double mittenDrmIR_cm;
	    private double zopfDrmOR_cm;
	    private double zopfDrmIR_cm;
	    private double laenge_m;
	    private double positionDerBasisAmStamm_m;
	    private double volumenRestStueckUntenOR_m3;
	    private double volumenRestStueckObenOR_m3;
	    private BaumschaftDefinition bsDef;
	    private SortimentsVorgabe sv;
	    private ReststueckKategorie reststueckKategorie;

		public void setId(int id) {
			this.id = id;
		}

	    public void setBasisDrmOR_cm(double basisDrmOR_cm) {
			this.basisDrmOR_cm = basisDrmOR_cm;
		}

		public void setBasisDrmIR_cm(double basisDrmIR_cm) {
			this.basisDrmIR_cm = basisDrmIR_cm;
		}

		public void setMittenDrmOR_cm(double mittenDrmOR_cm) {
			this.mittenDrmOR_cm = mittenDrmOR_cm;
		}

		public void setMittenDrmIR_cm(double mittenDrmIR_cm) {
			this.mittenDrmIR_cm = mittenDrmIR_cm;
		}

		public void setZopfDrmOR_cm(double zopfDrmOR_cm) {
			this.zopfDrmOR_cm = zopfDrmOR_cm;
		}

		public void setZopfDrmIR_cm(double zopfDrmIR_cm) {
			this.zopfDrmIR_cm = zopfDrmIR_cm;
		}

		public void setLaenge_m(double laenge_m) {
			this.laenge_m = laenge_m;
		}

		public void setPositionDerBasisAmStamm_m(double positionDerBasisAmStamm_m) {
			this.positionDerBasisAmStamm_m = positionDerBasisAmStamm_m;
		}

		public void setVolumenRestStueckUntenOR_m3(double volumenRestStueckUntenOR_m3) {
			this.volumenRestStueckUntenOR_m3 = volumenRestStueckUntenOR_m3;
		}

		public void setVolumenRestStueckObenOR_m3(double volumenRestStueckObenOR_m3) {
			this.volumenRestStueckObenOR_m3 = volumenRestStueckObenOR_m3;
		}

		public void setBsDef(BaumschaftDefinition bsDef) {
			this.bsDef = bsDef;
		}

		public void setSortimentsVorgabe(SortimentsVorgabe sv) {
			this.sv = sv;
		}

		public void setReststueckKategorie(ReststueckKategorie reststueckKategorie) {
			this.reststueckKategorie = reststueckKategorie;
		}
		
		public SortimentsStueck build() {
			return new SortimentsStueck(this);
		}
	}
}
