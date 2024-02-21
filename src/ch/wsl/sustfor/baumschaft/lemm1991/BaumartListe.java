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
package ch.wsl.sustfor.baumschaft.lemm1991;

import java.util.HashMap;

import ch.wsl.sustfor.baumschaft.base.Baumart;


/**
 * 
 * @author Stefan Holm (Portierung nach Java)
 * @author Vinzenz Erni (Original in VB.NET)
 *
 */
public class BaumartListe {	
	
	private static HashMap<Baumart, RindenParameterSatz> hmRindenParameter;
	private static HashMap<Baumart, SchaftformInRindeParameterSatz> hmSchaftformParameter;	
	
	public BaumartListe() {
		if (hmRindenParameter == null) {
			initRindenParameter();
		}
		if (hmSchaftformParameter == null) {
			initSchaftformParameter();
		}
	}
	
	protected static class RindenParameterSatz {		
		public double r0, r1, r2, r3;		
	}
	
	public static class SchaftformInRindeParameterSatz {
		public double k21, k22;
		public double k31, k32;
		public double k41, k42;
		public double k51, k52;
	}
	
	private static void initRindenParameter() {
		hmRindenParameter = new HashMap<Baumart, RindenParameterSatz>();
		
		for (Baumart baumArt: Baumart.values()) {
			RindenParameterSatz rps = new RindenParameterSatz();
			
			switch (baumArt) {
			case UebrigesNadelholz:
			case Fichte:
				rps.r0 = 10.3695;
				rps.r1 = -12.6212;
				rps.r2 = 6.4394;
				rps.r3 = 0;
				break;
				
			case Tanne:
				rps.r0 = 8.6794;
				rps.r1 = -7.8333;
				rps.r2 = 4.9242;
				rps.r3 = 0;
				break;
				
			case Foehre:
				rps.r0 = 6.15553;
				rps.r1 = -5.9726;
				rps.r2 = 0;
				rps.r3 = 17.411;
				break;
				
			case Laerche:
				rps.r0 = 11.9118;
				rps.r1 = -7.7558;
				rps.r2 = 0;
				rps.r3 = 7.5019;
				break;
				
			case Eiche:
				rps.r0 = 15.515;
				rps.r1 = -22.7424;
				rps.r2 = 12.5;
				rps.r3 = 0;
				break;
				
			case Buche:
			case Esche:
			case Ahorn:
			case UebrigesLaubholz:
				rps.r0 = 8.3373;
				rps.r1 = -15.0758;
				rps.r2 = 10.2273;
				rps.r3 = 0;
				break;
				
			default:
				throw new RuntimeException("Unbekannter Baumart-Code");	
			}
			
			hmRindenParameter.put(baumArt, rps);
		}
	}

	private static void initSchaftformParameter() {
		hmSchaftformParameter = new HashMap<Baumart, SchaftformInRindeParameterSatz>();

		for (Baumart baumArt: Baumart.values()) {
			SchaftformInRindeParameterSatz sps = new SchaftformInRindeParameterSatz();

			switch (baumArt) {
			case UebrigesNadelholz:
			case Fichte:
				sps.k21 = -8.45802;
				sps.k22 = 0.14112;
				sps.k31 = 10.92778;
				sps.k32 = -0.12195;
				sps.k41 = -5.44222;
				sps.k42 = -0.10344;
				sps.k51 = 0.30472;
				sps.k52 = 0.12556;
				break;

			case Tanne:
				sps.k21 = -9.61211;
				sps.k22 = 0.05096;
				sps.k31 = 9.69011;
				sps.k32 = 0.11329;
				sps.k41 = -0.61716;
				sps.k42 = -0.37708;
				sps.k51 = -2.73283;
				sps.k52 = 0.24408;
				break;

			case Foehre:
				sps.k21 = -9.05273;
				sps.k22 = 0.05012;
				sps.k31 = 18.68771;
				sps.k32 = -0.17362;
				sps.k41 = -21.08579;
				sps.k42 = 0.255;
				sps.k51 = 9.40568;
				sps.k52 = -0.13024;
				break;

			case Laerche:
				sps.k21 = -3.2629;
				sps.k22 = -0.04598;
				sps.k31 = 2.65837;
				sps.k32 = 0.21485;
				sps.k41 = -1.66239;
				sps.k42 = -0.308941;
				sps.k51 = 0.76998;
				sps.k52 = 0.14557;
				break;

			case Eiche:
				sps.k21 = 14.41765;
				sps.k22 = -0.74146;
				sps.k31 = -39.30491;
				sps.k32 = 1.81216;
				sps.k41 = 41.3296;
				sps.k42 = -1.88384;
				sps.k51 = -15.24664;
				sps.k52 = 0.70529;
				break;

			case Buche:					
			case Esche:					
			case Ahorn:					
			case UebrigesLaubholz:
				sps.k21 = -4.15304;
				sps.k22 = 0.03465;
				sps.k31 = 7.68037;
				sps.k32 = -0.10779;
				sps.k41 = -9.0974;
				sps.k42 = 0.14586;
				sps.k51 = 4.18769;
				sps.k52 = -0.06717;
				break;

			default:
				throw new RuntimeException("Unbekannter Baumart-Code");	
			}

			hmSchaftformParameter.put(baumArt, sps);
		}
	}
	
	public SchaftformInRindeParameterSatz getSchaftformParameter(Baumart baumArtCode) {
		return hmSchaftformParameter.get(baumArtCode);
	}
	
	public RindenParameterSatz getRindenParameter(Baumart baumArtCode) {
		return hmRindenParameter.get(baumArtCode);
	}
}
