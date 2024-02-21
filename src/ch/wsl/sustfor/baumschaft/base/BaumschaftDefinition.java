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
package ch.wsl.sustfor.baumschaft.base;

import ch.wsl.sustfor.baumschaft.lemm1991.BaumSchaftformFkt_Lemm1991;
import ch.wsl.sustfor.sorsim.model.D7mBhdRelations;
import ch.wsl.sustfor.sorsim.model.D7mBhdRelations.Standort;


/**
 * "BaumschaftDefinition" dient als Objekt für die Definition (elementaren Beschrieb) eines Baumschaftes,
 *  und wird als Input für Schaftformfunktionen benötigt.
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public class BaumschaftDefinition {	

	/** //FIXME: comment outdated.
     * Die Baumart ist hier als Integer definiert, 
     * damit verschiedene Funktionen (Lemm, Kublin, u. a. ) mit ihren jeweils unterschiedlichen Baumartlisten
     * diese Klasse als Basis verwenden können!
     * Der Integer-Code wird dann je nach Funktion als andere Baumart interpretiert.
	 */
    private final Baumart baumArt;

    private final double bhd_cm;
    
    private double d7m_cm;
    private double schaftLaenge_m;
    
	private final String beschrieb;
    private final String datum; //aufnahmedatum, fakultativ
    private final int id;
    
    private double fuerSortimenteZuVerwendenderSchaftanteil;
    private double kronenansatzHoeheAnteil = 1;
    
    private BaumSchaftformFunktion schaftformFkt = new BaumSchaftformFkt_Lemm1991();
    
    private BaumschaftDefinition(BaumschaftDefinitionBuilder builder) {
    	this.baumArt = builder.baumArt;
    	this.bhd_cm = builder.bhd_cm;
    	this.d7m_cm = builder.d7m_cm;
    	this.schaftLaenge_m = builder.schaftLaenge_m;
    	this.beschrieb = builder.beschrieb;
    	this.datum = builder.datum;
    	this.id = builder.id;
    }

	public void selectSchaftfunktion(BaumSchaftformFunktion schaftFunktion) {
		this.schaftformFkt = schaftFunktion;
	}
	
	public BaumSchaftformFunktion getSchaftformFunktion() {
		return this.schaftformFkt;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SchaftDefinition" + "\n");
		sb.append("Id: " + id);
        sb.append("Baumart: " + baumArt + "\n");
        sb.append("BHD_cm: " + bhd_cm + "\n");
        sb.append("D7m_cm: " + d7m_cm + "\n");
        sb.append("SchaftLaenge_m: " + schaftLaenge_m + "\n");
        sb.append("Beschrieb: " + beschrieb + "\n");        
        return sb.toString();
	}
	
	public double getDurchmesser_iR_cm(double messHoehe_m) {
		return schaftformFkt.getDurchmesser_iR_cm(this, messHoehe_m);
	}
	
	public double getDurchmesser_oR_cm(double messHoehe_m) {
		return schaftformFkt.getDurchmesser_oR_cm(this, messHoehe_m);
	}
	
	public double getHoehe_m(double durchmesser_cm,	boolean durchmesserAngabeInRinde, double toleranzHoehe_m) {
		return schaftformFkt.getHoehe_m(this, durchmesser_cm, durchmesserAngabeInRinde, toleranzHoehe_m);
	}

	public double getStammStueckVolumen_m3(double stueckBasisHoeheAmStamm_m, double stueckEndHoeheAmStamm_m, double messIntervall_m, boolean ohneRinde) {
		return schaftformFkt.getStammStueckVolumen_m3(this, stueckBasisHoeheAmStamm_m, stueckEndHoeheAmStamm_m, messIntervall_m, ohneRinde);
	}

	public Baumart getBaumart() {
		return baumArt;
	}

	public double getBhd_cm() {
		return bhd_cm;
	}
	
	public double getD7m_cm() {
		return d7m_cm;
	}
	
	public void autoCalcD7mIfZero(Standort standort) {
		if (getD7m_cm() == 0) { //FIXME: what if d7m_cm == -1 (initial value)? change behavior and rename method to calcIfUndefined?
			//D7m berechnen
			double value = D7mBhdRelations.getNonNullRelation(getBaumart(), standort);
			d7m_cm = value * getBhd_cm();
		}
	}

	public double getSchaftLaenge_m() {
		return schaftLaenge_m;
	}
	
	public void autoCalcSchaftLaengeIfZero(double grundflaechenmittelstamm, double hoeheGrundflaechenmittelstamm) {
		if (getSchaftLaenge_m() == 0) { //FIXME: what if schaftLaenge_m == -1 (initial value)? change behavior and rename method to calcIfUndefined?
			//Schaftlänge nicht vorhanden -> berechnen
			double schaftLaenge = ScheitelHoehe.getScheitelHoehe(
					getBaumart(), 
					getBhd_cm(),
					grundflaechenmittelstamm,
					hoeheGrundflaechenmittelstamm
					);
//    		schaftLaenge_m = schaftLaenge;
    		schaftLaenge_m = Math.round(schaftLaenge); //FIXME: funktioniert nicht ohne round!
		} 
	}

	public String getBeschrieb() {
		return beschrieb;
	}
	
	public String getDatum() {
		return datum;
	}
	
    public int getId() {
		return id;
	}
	
	public double getFuerSortimenteZuVerwendenderSchaftanteil() {
		return this.fuerSortimenteZuVerwendenderSchaftanteil;
	}

	public void setFuerSortimenteZuVerwendenderSchaftanteil(double fuerSortimenteZuVerwendenderSchaftanteil) {
		this.fuerSortimenteZuVerwendenderSchaftanteil = fuerSortimenteZuVerwendenderSchaftanteil;
	}
    
    public double getKronenansatzHoeheAnteil() {
    	return this.kronenansatzHoeheAnteil;
	}

	public void calcKronenansatzHoeheAnteil(double h0) {
		double a,b,c,d;
		
		switch (getBaumart()) {
		case Buche:
			a = 0.25704;
			b = 0.11819;
			c = 0.002065;
			d = 0.13831;
			break;
			
		case Eiche:
			a = -0.5268;
			b = 0.2287;
			c = 0.00453;
			d = 0.4712;
			break;
			
		case Esche:
		case UebrigesLaubholz:
			a = -0.3708;
			b = 0.4211;
			c = 0.0030;
			d = 0.3242;
			break;
			
		case Ahorn:
			a = -0.3191;
			b = 0.0475;
			c = 0.0057;
			d = 0.4066;
			break;

		case Fichte:
		case Tanne:
		case Foehre:
		case Laerche:
		case UebrigesNadelholz:
		default:
			this.kronenansatzHoeheAnteil = 1.0; //Keine Laubbaumart
			return;
		}
		
		double h = getSchaftLaenge_m();
		double bhd = getBhd_cm();
		
		//nach Döbbeler et al, 2002
		this.kronenansatzHoeheAnteil = 1.0 - Math.exp( -Math.abs(a + b*h/bhd - c*bhd + d*Math.log(h0)) );
	}
    
    
    public static class BaumschaftDefinitionBuilder {
        private Baumart baumArt = Baumart.Fichte;
        private double bhd_cm = -1;
        private double d7m_cm = -1;
        private double schaftLaenge_m = -1;
    	private String beschrieb;
        private String datum; //aufnahmedatum, fakultativ
        private int id = -1;

    	public BaumschaftDefinitionBuilder setBaumart(Baumart baumart) {
    		this.baumArt = baumart;
    		return this;
    	}

    	public BaumschaftDefinitionBuilder setBhd_cm(double bhd_cm) {
    		if (bhd_cm < 0) {
    			throw new RuntimeException("BHD muss positiv sein"); 
    		}
    		this.bhd_cm = bhd_cm;
    		return this;
    	}

    	public BaumschaftDefinitionBuilder setD7m_cm(double d7m_cm) {
    		if (d7m_cm < 0) {
    			throw new RuntimeException("D7m muss positiv sein"); 
    		}
    		this.d7m_cm = d7m_cm;
    		return this;
    	}
    	
    	public BaumschaftDefinitionBuilder setSchaftLaenge_m(double schaftLaenge_m) {
    		if (schaftLaenge_m < 0) {
    			throw new RuntimeException("Schaftlänge muss positiv sein"); 
    		}
    		this.schaftLaenge_m = schaftLaenge_m;
    		return this;
    	}
    	
    	public BaumschaftDefinitionBuilder setBeschrieb(String beschrieb) {
    		this.beschrieb = beschrieb;
    		return this;
    	}
    	
    	public BaumschaftDefinitionBuilder setDatum(String datum) {
    		this.datum = datum;
    		return this;
    	}

    	public BaumschaftDefinitionBuilder setId(int id) {
    		this.id = id;
    		return this;
    	}
    	
    	public BaumschaftDefinition build() {
    		return new BaumschaftDefinition(this);
    	}
    }
}
