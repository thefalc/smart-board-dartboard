package ca.uvic.cs.chisel.nerf.shapes;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class PulsatingNerfShape extends AbstractNerfShape {
	private static final int MAX_WIDTH_CHANGE = 300;
	
	private double widthChangeIncrement = MAX_WIDTH_CHANGE;
	private double widthDifference = 1;
	
	//private boolean fixed;
	
	public PulsatingNerfShape(Rectangle2D relativeBounds, int points) {
		super(new Ellipse2D.Double(0, 0, 0, 0), relativeBounds, points);		
	}

	protected void scaleShape(Rectangle2D bounds) {
		double boundsX = bounds.getX();
		double boundsY = bounds.getY();
		double boundsW = bounds.getWidth() - widthChangeIncrement;
		
		widthChangeIncrement += widthDifference;
		if(widthChangeIncrement >= MAX_WIDTH_CHANGE) widthDifference = -10 * Math.random();
		else if(widthChangeIncrement <= 50) widthDifference = 10 * Math.random();
		
		double boundsH = bounds.getHeight();
		if (isSquare) {
			//boundsW = Math.min(boundsW, boundsH);
			boundsH = boundsW;
			if (isCenter) {
				boundsX = bounds.getCenterX() - (boundsW / 2d);
				boundsY = bounds.getCenterY() - (boundsH / 2d);
			}
		}
		
		double x = boundsX + (relativeBounds.getX() * boundsW);
		double y = boundsY + (relativeBounds.getY() * boundsH);
		double w = relativeBounds.getWidth() * boundsW;
		double h = relativeBounds.getHeight() * boundsH;
		shape.setFrame(x, y, w, h);
	}
}
