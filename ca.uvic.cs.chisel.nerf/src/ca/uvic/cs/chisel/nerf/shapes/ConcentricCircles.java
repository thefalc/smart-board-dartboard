package ca.uvic.cs.chisel.nerf.shapes;

import java.awt.Color;
import java.awt.geom.Rectangle2D;



/**
 * Creates a set of 5 concentric circles:
 * 
 * @author Chris Callendar
 * @date 23-Jan-07
 */
public class ConcentricCircles extends AbstractShapeCollection {

	public static final String NAME = "Concentric Circles (Easy)";
	
	public ConcentricCircles() {
		super(NAME);

		CircleNerfShape c1 = new CircleNerfShape(new Rectangle2D.Double(0.05, 0.05, 0.9, 0.9), 5);
		CircleNerfShape c2 = new CircleNerfShape(new Rectangle2D.Double(0.15, 0.15, 0.7, 0.7), 10);
		CircleNerfShape c3 = new CircleNerfShape(new Rectangle2D.Double(0.25, 0.25, 0.5, 0.5), 25);
		CircleNerfShape c4 = new CircleNerfShape(new Rectangle2D.Double(0.35, 0.35, 0.3, 0.3), 50);
		CircleNerfShape c5 = new CircleNerfShape(new Rectangle2D.Double(0.45, 0.45, 0.1, 0.1), 100);
		
		c1.setFillColor(new Color(230, 255, 255));
		c2.setFillColor(new Color(220, 250, 255));
		c3.setFillColor(new Color(215, 245, 255));
		c4.setFillColor(new Color(210, 240, 255));
		c5.setFillColor(new Color(205, 235, 255));
		
		addShape(c1);
		addShape(c2);
		addShape(c3);
		addShape(c4);
		addShape(c5);
	}
		
}
