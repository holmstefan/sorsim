package ch.wsl.sustfor.baumschaft.spline.util;
//Code from: http://www.cse.unsw.edu.au/~lambert/splines/

import java.awt.Graphics;
import java.awt.Polygon;

public class NatCubic extends ControlCurve{

	/* calculates the natural cubic spline that interpolates
	y[0], y[1], ... y[n]
	The first segment is returned as
	C[0].a + C[0].b*u + C[0].c*u^2 + C[0].d*u^3 0<=u <1
	the other segments are in C[1], C[2], ...  C[n-1] */

	public Cubic[] calcNaturalCubic(int n, int[] x) {
		float[] gamma = new float[n+1];
		float[] delta = new float[n+1];
		float[] D = new float[n+1];
		int i;
		/* We solve the equation
	       [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
	       |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
	       |  1 4 1   | | .  | = |      .         |
	       |    ..... | | .  |   |      .         |
	       |     1 4 1| | .  |   |3(x[n] - x[n-2])|
	       [       1 2] [D[n]]   [3(x[n] - x[n-1])]

	       by using row operations to convert the matrix to upper triangular
	       and then back sustitution.  The D[i] are the derivatives at the knots.
		 */

		gamma[0] = 1.0f/2.0f;
		for ( i = 1; i < n; i++) {
			gamma[i] = 1/(4-gamma[i-1]);
		}
		gamma[n] = 1/(2-gamma[n-1]);

		delta[0] = 3*(x[1]-x[0])*gamma[0];
		for ( i = 1; i < n; i++) {
			delta[i] = (3*(x[i+1]-x[i-1])-delta[i-1])*gamma[i];
		}
		delta[n] = (3*(x[n]-x[n-1])-delta[n-1])*gamma[n];

		D[n] = delta[n];
		for ( i = n-1; i >= 0; i--) {
			D[i] = delta[i] - gamma[i]*D[i+1];
		}

		/* now compute the coefficients of the cubics */
		Cubic[] C = new Cubic[n];
		for ( i = 0; i < n; i++) {
			C[i] = new Cubic((float)x[i], D[i], 3*(x[i+1] - x[i]) - 2*D[i] - D[i+1],
					2*(x[i] - x[i+1]) + D[i] + D[i+1]);
		}
		return C;
	}

	private final int STEPS = 12;

	/* draw a cubic spline */
	@Override
	public void paint(Graphics g){
		super.paint(g);
		if (pts.npoints >= 2) {
			Cubic[] X = calcNaturalCubic(pts.npoints-1, pts.xpoints);
			Cubic[] Y = calcNaturalCubic(pts.npoints-1, pts.ypoints);

			/* very crude technique - just break each segment up into steps lines */
			Polygon p = new Polygon();
			p.addPoint((int) Math.round(X[0].eval(0)),
					(int) Math.round(Y[0].eval(0)));
			for (int i = 0; i < X.length; i++) {
				for (int j = 1; j <= STEPS; j++) {
					float u = j / (float) STEPS;
					p.addPoint(Math.round(X[i].eval(u)),
							Math.round(Y[i].eval(u)));
				}
			}
			g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
		}
	}
}

