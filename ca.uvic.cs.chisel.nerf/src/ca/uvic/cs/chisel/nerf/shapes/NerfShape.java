/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 23-Jan-07
 */
public interface NerfShape {

	public void render(Graphics2D g, Rectangle2D bounds);
	
	public boolean contains(Point2D p);
	
	public Rectangle getBounds();
	
	public Shape getShape();
	
	public Color getBorderColor();
	public void setBorderColor(Color border);
	
	public Stroke getBorderStroke();
	public void setBorderStroke(Stroke stroke);
	
	public Color getFillColor();
	public void setFillColor(Color fill);
	
	public Color getHighlightColor();
	public void setHighlightColor(Color highlight);
	
	public Color getTextColor();
	public void setTextColor(Color text);
	
	public boolean isHighlighted();
	
	public void setHighlighted(boolean highlighted);
	
	public int getPoints();
	
	public void moveShape();
	
	public double getXSpeed();
	public double getYSpeed();
	public void setSpeeds(double xspeed, double yspeed);
	public double getMass();
	
	public boolean isSquare();
	
	/**
	 * Set to true if you want the playable area to be a square,
	 * if false the shape will not be restricted to a sqare, instead it will use the 
	 * full rectangular bounds.
	 * @param square
	 */
	public void setSquare(boolean square);

	public Rectangle2D getRelativeBounds();
	
	public void setRelativeBounds(Rectangle2D relativeBounds);

	/**
	 * @return true if the shapes overlap
	 */
	public boolean intersects(NerfShape shape);
	
}
