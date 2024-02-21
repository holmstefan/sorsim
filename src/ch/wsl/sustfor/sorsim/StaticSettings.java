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
package ch.wsl.sustfor.sorsim;

import java.awt.Color;

/**
 * 
 * @author Stefan Holm
 *
 */
public class StaticSettings {

	public static final float VERSION = 2.1f;	
	
	public static final boolean USE_COLORS = true;
	public static final Color COLOR_BAEUME = new Color(0,128,0,32);
	public static final Color COLOR_SORTIMENTSVORGABEN = new Color(128,128,0,32);
	public static final Color COLOR_DARSTELLUNG_ERGEBNIS = new Color(0,0,128,32);
	public static final Color COLOR_ERGEBNIS = new Color(255,255,255,128);	

	public static final String BASE_DIR;
	public static final boolean START_IM_STANDARDMODUS;	
	
	private static final boolean DEPLOY = true;
	
	static {
		if (DEPLOY) {
			START_IM_STANDARDMODUS = true;
			BASE_DIR = "";
		}
		else {
			START_IM_STANDARDMODUS = false;
			BASE_DIR = "data/";
		}
	}
}
