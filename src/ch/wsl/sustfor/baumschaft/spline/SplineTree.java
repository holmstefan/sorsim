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

import java.awt.Polygon;

import ch.wsl.sustfor.baumschaft.spline.util.Cubic;
import ch.wsl.sustfor.baumschaft.spline.util.NatCubic;

/**
 * 
 * @author Stefan Holm
 *
 */
public class SplineTree {

	private int height_cm;
	private int drmOben_cm;
	private int drmUnten_cm;

	private final int hoeheUnten_cm = 130;
	private final int hoeheOben_cm = 700;

	private Polygon pts = new Polygon();
	
	private int radius[];
	
	private NatCubic spline;

	public SplineTree(int drmOben_cm, int drmUnten_cm, int height_cm) {
		this.height_cm = height_cm;
		this.drmOben_cm = drmOben_cm;
		this.drmUnten_cm = drmUnten_cm;
		
		initSpline();
		calc();
	}
	
	private void initSpline() {		
		spline = new NatCubic();
		
		pts.addPoint(0 - drmUnten_cm, 0);             // boden, links
		pts.addPoint(0 - drmUnten_cm, hoeheUnten_cm); // bhd, links
		pts.addPoint(0 - drmOben_cm , hoeheOben_cm);  // drmOben, links
		pts.addPoint(0			    , height_cm);     // ** Baumspitze **
		pts.addPoint(0 + drmOben_cm , hoeheOben_cm);  // drmOben, rechts
		pts.addPoint(0 + drmUnten_cm, hoeheUnten_cm); // bhd, rechts
		pts.addPoint(0 + drmUnten_cm, 0);		      // boden, rechts
	}
	
	private void calc() {
		final int STEPS = 10;
		Polygon p = new Polygon();
		
		if (pts.npoints >= 2) {
			Cubic[] X = spline.calcNaturalCubic(pts.npoints-1, pts.xpoints);
			Cubic[] Y = spline.calcNaturalCubic(pts.npoints-1, pts.ypoints);

			/* very crude technique - just break each segment up into steps lines */
			p.addPoint( Math.round(X[0].eval(0)), Math.round(Y[0].eval(0)));
			for (int i = 0; i < X.length; i++) {
				for (int j = 0; j <= STEPS; j++) {
					float u = j / (float) STEPS;
					p.addPoint(Math.round(X[i].eval(u)),
							Math.round(Y[i].eval(u)));
				}
			}
		}
		radius = new int[height_cm + 1];
		
		for (int i=0; i<=height_cm; i++) {
			int maxRadius = 0;
			while (p.contains(maxRadius, i) == true) {
				maxRadius++;
			}			
			radius[i] = maxRadius;
		}
	}
	
	public double getDrmAtHeight(int height_cm) {		
		return radius[height_cm];
	}
}
