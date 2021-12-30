/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf.shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;



/**
 * Creates a set of 5 concentric circles:
 * 
 * @author Chris Callendar
 * @date 23-Jan-07
 */
public class HarderConcentricCircles extends AbstractShapeCollection {

	public static final String NAME = "Concentric Circles (Hard)";
	
	public HarderConcentricCircles() {
		super(NAME);

		CircleNerfShape c1 = new CircleNerfShape(new Rectangle2D.Double(0.05, 0.05, 0.9, 0.9), 1);
		CircleNerfShape c2 = new CircleNerfShape(new Rectangle2D.Double(0.125, 0.125, 0.75, 0.75), 5);
		CircleNerfShape c3 = new CircleNerfShape(new Rectangle2D.Double(0.2, 0.2, 0.6, 0.6), 10);
		CircleNerfShape c4 = new CircleNerfShape(new Rectangle2D.Double(0.275, 0.275, 0.45, 0.45), 25);
		CircleNerfShape c5 = new CircleNerfShape(new Rectangle2D.Double(0.35, 0.35, 0.3, 0.3), 50);
		CircleNerfShape c6 = new CircleNerfShape(new Rectangle2D.Double(0.425, 0.425, 0.15, 0.15), 100);
		CircleNerfShape c7 = new CircleNerfShape(new Rectangle2D.Double(0.475, 0.475, 0.05, 0.05), 200);

//		c1.setFillColor(new Color(255, 255, 255));
//		c2.setFillColor(new Color(255, 255, 220));
//		c3.setFillColor(new Color(255, 255, 210));
//		c4.setFillColor(new Color(255, 235, 210));
//		c5.setFillColor(new Color(255, 210, 210));
//		c6.setFillColor(new Color(255, 195, 195));
//		c7.setFillColor(new Color(255, 175, 175));
		c7.setFillColor(Color.magenta);
		c6.setFillColor(Color.blue);
		c5.setFillColor(Color.cyan);
		c4.setFillColor(Color.green);
		c3.setFillColor(Color.yellow);
		c2.setFillColor(Color.orange);
		c1.setFillColor(Color.red);
		
		addShape(c1);
		addShape(c2);
		addShape(c3);
		addShape(c4);
		addShape(c5);
		addShape(c6);
		addShape(c7);
		
		setBorderStrokes(new BasicStroke(0f));
		setBorderColors(Color.white);
	}
		
}
