/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf.shapes;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;

import ca.uvic.cs.chisel.nerf.GameController;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 23-Jan-07
 */
public interface ShapeCollection {

	public List<NerfShape> getShapes();
	
	public int getShapeCount();
	
	public void addShape(NerfShape shape);
	
	public NerfShape removeShape(int index);
	
	public void removeShape(NerfShape shape);
	
	public void clearShapes();
	
	public void render(Graphics2D g, Rectangle bounds);
	
	public void mousePressed(Point2D p);
	
	public void mouseReleased(Point2D p);
	
	public void setGameController(GameController controller);
	
	public String getName();
	
	public String toString();
	
	public void startTimer();
	
	public void stopTimer();	
}
