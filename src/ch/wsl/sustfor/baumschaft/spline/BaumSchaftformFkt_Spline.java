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
package ch.wsl.sustfor.baumschaft.spline;

import ch.wsl.sustfor.baumschaft.base.BaumSchaftformFunktion;
import ch.wsl.sustfor.baumschaft.base.BaumschaftDefinition;


/**
 * 
 * @author Stefan Holm
 *
 */
@Deprecated
public class BaumSchaftformFkt_Spline extends BaumSchaftformFunktion {
	
	private SplineTree tree;
	private double treePropertiesHash;	
	
	@Override
	public String getInfo() {
		throw new RuntimeException("TODO");
	}
	
	@Override
	public double getDurchmesser_iR_cm(BaumschaftDefinition bsDef, double messHoehe_m) {
		if (checkNewTreeCalcNecessary(bsDef) == true) {
			tree = getTree(bsDef);			
		}
		int messHoehe_cm = (int) Math.round(messHoehe_m * 100);
		
		return tree.getDrmAtHeight(messHoehe_cm);
	}
	
	@Override
	public double getRindenDickeSummeBeidseitig_cm(BaumschaftDefinition bsDef, double messHoehe_m) {
		return 0;
	}
	
	@Override
	public String toString(){
		return "Spline";				
	}
	
	private boolean checkNewTreeCalcNecessary(BaumschaftDefinition bsDef) {
		//calc hash of relevant tree properties
		double tempTreePropertiesHash =
				bsDef.getD7m_cm() +
				bsDef.getBhd_cm() * 1000 +
				bsDef.getSchaftLaenge_m() * 1000 * 1000 ;

		if (tree == null || tempTreePropertiesHash != this.treePropertiesHash) {
			//properties changed, calc new tree
			this.treePropertiesHash = tempTreePropertiesHash;
			return true;
		}
		return false;
	}
	
	private SplineTree getTree(BaumschaftDefinition bsDef) {
		SplineTree tree = new SplineTree(
				(int) Math.round(bsDef.getD7m_cm() ), 
				(int) Math.round(bsDef.getBhd_cm() ),
				(int) Math.round(bsDef.getSchaftLaenge_m() * 100) );
		
		return tree;
	}
}
