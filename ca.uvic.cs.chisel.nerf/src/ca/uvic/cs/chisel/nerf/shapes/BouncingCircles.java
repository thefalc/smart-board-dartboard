/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf.shapes;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import ca.uvic.cs.chisel.nerf.INerfUI;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 25-Jan-07
 */
public class BouncingCircles extends AbstractShapeCollection implements ActionListener {

	public BouncingCircles(INerfUI ui) {
		super("Bouncing Circles", ui, 100);

		allowOverlapping = false;
		
		CircleNerfShape c1 = new CircleNerfShape(new Rectangle2D.Double(0.5, 0.5, 0.4, 0.4), 5, true, 0, 0, false);
		CircleNerfShape c2 = new CircleNerfShape(new Rectangle2D.Double(0.0, 0.0, 0.3, 0.3), 10, true, 5, 5, false);
		CircleNerfShape c3 = new CircleNerfShape(new Rectangle2D.Double(0.8, 0.2, 0.2, 0.2), 25, true, -15, 15, false);
		CircleNerfShape c4 = new CircleNerfShape(new Rectangle2D.Double(0.2, 0.8, 0.2, 0.2), 25, true, 15, -15, false);
		CircleNerfShape c5 = new CircleNerfShape(new Rectangle2D.Double(0.5, 0.8, 0.15, 0.15), 50, true, 5, -45, false);
		CircleNerfShape c6 = new CircleNerfShape(new Rectangle2D.Double(0.5, 0.2, 0.15, 0.15), 50, true, -5, 45, false);
		CircleNerfShape c7 = new CircleNerfShape(new Rectangle2D.Double(0.9, 0.5, 0.1, 0.1), 100, true, -70, 30, false);
		CircleNerfShape c8 = new CircleNerfShape(new Rectangle2D.Double(0.1, 0.5, 0.1, 0.1), 100, true, 70, 30, false);
		
		c1.setFillColor(Color.green);
		c2.setFillColor(Color.cyan);
		c3.setFillColor(Color.yellow);
		c4.setFillColor(Color.yellow);
		c5.setFillColor(Color.orange);
		c6.setFillColor(Color.orange);
		c7.setFillColor(Color.red);
		c8.setFillColor(Color.red);
		
		addShape(c1);
		addShape(c2);
		addShape(c3);
		addShape(c4);
		addShape(c5);
		addShape(c6);
		addShape(c7);
		addShape(c8);
		
		setBorderColors(Color.white);
	}

}
