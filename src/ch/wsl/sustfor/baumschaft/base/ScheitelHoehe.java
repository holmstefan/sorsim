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

/**
 * 
 * @author Stefan Holm
 *
 */
public class ScheitelHoehe {
	
	public static void main(String args[]) {
		for (Baumart baumart: Baumart.values()) {
			System.out.println(baumart);
			for (int bhd = 0; bhd<=80; bhd+=4) {
				System.out.println(bhd + "\t" + getScheitelHoehe(baumart, bhd, 35, 30));
			}
			System.out.println();
		}
	}

	public static double getScheitelHoehe(Baumart baumart, double bhd, double grundflaechenmittelstamm, double hoeheGrundflaechenmittelstamm) {
		double dg = grundflaechenmittelstamm;
		double hg = hoeheGrundflaechenmittelstamm;
		
		switch (baumart) {
		case Fichte:
		case Tanne:
		case UebrigesNadelholz:
			return 1.3+(hg-1.3)*Math.exp(0.18290951*(1.0-(dg/bhd)))*Math.exp(5.68789430*((1.0/dg)-(1.0/bhd)));
			
		case Foehre:
			return 1.3+(hg-1.3)*Math.exp(0.25963741*(1.0-(dg/bhd)))*Math.exp(1.30645374*((1.0/dg)-(1.0/bhd)));
			
		case Laerche:
			return 1.3+(hg-1.3)*Math.exp(0.12931522*(1.0-(dg/bhd)))*Math.exp(4.44234560*((1.0/dg)-(1.0/bhd)));
			
		case Buche:
		case UebrigesLaubholz:
			return 1.3+(hg-1.3)*Math.exp(0.20213328*(1.0-(dg/bhd)))*Math.exp(5.64023296*((1.0/dg)-(1.0/bhd)));
			
		case Eiche:
		case Esche:
		case Ahorn:
			return 1.3+(hg-1.3)*Math.exp(0.14657227*(1.0-(dg/bhd)))*Math.exp(3.78686023*((1.0/dg)-(1.0/bhd)));
			
		default:
			throw new RuntimeException("Unbekannte Baumart");
		}
	}
}
