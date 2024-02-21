package ch.wsl.sustfor.baumschaft.spline.util;
//Code from: http://www.cse.unsw.edu.au/~lambert/splines/

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;


/** This class represents a curve defined by a sequence of control points */
public class ControlCurve {

	protected Polygon pts;

	public ControlCurve() {
		pts = new Polygon();
	}

	private static Font f = new Font("Courier",Font.PLAIN,12);

	/** paint this curve into g.*/
	public void paint(Graphics g){
		FontMetrics fm = g.getFontMetrics(f);
		g.setFont(f);
		int h = fm.getAscent()/2;

		for(int i = 0; i < pts.npoints; i++)  {
			String s = Integer.toString(i);
			int w = fm.stringWidth(s)/2;
			g.drawString(Integer.toString(i),pts.xpoints[i]-w,pts.ypoints[i]+h);
		}
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < pts.npoints; i++) {
			result.append(" " + pts.xpoints[i] + " " + pts.ypoints[i]);
		}
		return result.toString();
	}
}

