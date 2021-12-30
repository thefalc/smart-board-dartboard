/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf.shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 23-Jan-07
 */
public abstract class AbstractNerfShape implements NerfShape {

	private static final Color BORDER = Color.blue;
	private static final Color FILL = Color.white;
	private static final Color HIGHLIGHT = new Color(221, 86, 45);	//new Color(93, 166, 219); 	// Color.cyan;
	private static final Color TEXT = Color.black;
	
	protected static final double DISPLACEMENT_UNIT = 0.001;
	protected static final int MIN_SPEED = -100;
	protected static final int MAX_SPEED = 100; 
	protected static final int MAX_SPEED_CHANGE = 6; 
	protected static final double MIN_REL_BOUNDS = 0;
	protected static final double MAX_REL_BOUNDS = 1; 
	protected static final int RIGHT = 1;
	protected static final int LEFT = -1;
	protected static final int DOWN = 1;
	protected static final int UP = -1;
	
	protected Color fill;
	protected Color highlight;
	protected Color textColor;
	protected Color border;
	protected Stroke borderStroke;
	
	protected Rectangle2D relativeBounds;	// all between 0.0 - 1.0
	protected final RectangularShape shape;
	private boolean isHighlighted;
	protected boolean isSquare;
	protected boolean isCenter;
	protected final int points; 
	
	protected double xspeed;	// x speed [-100 -> 100]
	protected double yspeed;	// y speed
	protected boolean fixedSpeed;
	
	public AbstractNerfShape(RectangularShape shape, Rectangle2D relativeBounds, int points) {
		this(shape, relativeBounds, points, true, 0, 0);
	}
	
	/**
	 * @param fixedSpeed if the speed shouldn't change
	 * @param xspeed the speed from -100 -> 100
	 * @param yspeed the speed from -100 -> 100
	 */
	public AbstractNerfShape(RectangularShape shape, Rectangle2D relativeBounds, int points, 
			boolean fixedSpeed, int xspeed, int yspeed) {
		this(shape, relativeBounds, points, fixedSpeed, xspeed, yspeed, true);
	}
	
	/**
	 * @param fixedSpeed if the speed shouldn't change
	 * @param xspeed the speed from -100 -> 100
	 * @param yspeed the speed from -100 -> 100
	 * @param isSquare if the shape should be constrained within a square instead of the
	 * 	full rectangular bounds, true by default.
	 */
	public AbstractNerfShape(RectangularShape shape, Rectangle2D relativeBounds, int points, 
			boolean fixedSpeed, int xspeed, int yspeed, boolean isSquare) {
		this.shape = shape;
		this.relativeBounds = relativeBounds;
		this.points = points;
		this.isSquare = isSquare;
		this.isCenter = true;
		this.fill = FILL;
		this.border = BORDER;
		this.highlight = HIGHLIGHT;
		this.textColor = TEXT;
		this.borderStroke = new BasicStroke(3f);
		this.fixedSpeed = fixedSpeed;
		setSpeeds(xspeed, yspeed);
	}
	
	public boolean isSquare() {
		return isSquare;
	}
	
	public void setSquare(boolean isSquare) {
		this.isSquare = isSquare;
	}
	
	public void moveShape() {
		if (!fixedSpeed) {
			// calculate a new speed value
			double dx = (Math.random() * MAX_SPEED_CHANGE) - (MAX_SPEED_CHANGE / 2);	// [-3, +3]
			double dy = (Math.random() * MAX_SPEED_CHANGE) - (MAX_SPEED_CHANGE / 2);	// [-3, +3]
			System.out.println("DX=" + dx + "  DY=" + dy);
			setSpeeds(xspeed + dx, yspeed + dy);
		}
		
		// change size (width/height)?
		
		//update the relative position
		double dx = DISPLACEMENT_UNIT * xspeed;
		double dy = DISPLACEMENT_UNIT * yspeed;
		double newX = relativeBounds.getX() + dx;
		double newY = relativeBounds.getY() + dy;
		final double W = relativeBounds.getWidth();
		final double H = relativeBounds.getHeight();
		
		// ensure that we don't go outside the relative bounds
		// "bounce" back into the bounds
		if (newX < MIN_REL_BOUNDS) {
			newX = MIN_REL_BOUNDS + (MIN_REL_BOUNDS - newX);
			xspeed = -xspeed;	// change direction
		} else if ((newX + W) > MAX_REL_BOUNDS) {
			newX = MAX_REL_BOUNDS - ((newX + W) - MAX_REL_BOUNDS) - W;
			xspeed = -xspeed;	// change direction
		}
		if (newY < MIN_REL_BOUNDS) {
			newY = MIN_REL_BOUNDS + (MIN_REL_BOUNDS - newY);
			yspeed = -yspeed;	// change direction
		} else if ((newY + H) > MAX_REL_BOUNDS) {
			newY = MAX_REL_BOUNDS - ((newY + H) - MAX_REL_BOUNDS) - H;
			yspeed = -yspeed;	// change direction
		}
		
		relativeBounds.setFrame(newX, newY, W, H);
	}
	
	public void setSpeeds(double newXSpeed, double newYSpeed) {
		this.xspeed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, newXSpeed));
		this.yspeed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, newYSpeed));
	}
	
	public boolean intersects(NerfShape shape) {
		// this only works for a rectangle shape
		return getRelativeBounds().intersects(shape.getRelativeBounds());
	}
	
	public double getXSpeed() {
		return xspeed;
	}
	
	public double getYSpeed() {
		return yspeed;
	}
	
	public double getMass() {
		double diameter = getRelativeBounds().getWidth() * 10;	// width is between [0.01 - 1], so make it between [0.1 - 10]
		return Math.max(0.1, Math.PI * diameter);	// ensures that the mass is non-zero and positive
	}

	public Color getBorderColor() {
		return border;
	}

	public void setBorderColor(Color border) {
		this.border = border;
	}
	
	public Color getFillColor() {
		return fill;
	}
	
	public void setFillColor(Color fill) {
		this.fill = fill;
	}

	public Color getHighlightColor() {
		return highlight;
	}
	
	public void setHighlightColor(Color highlight) {
		this.highlight = highlight;
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	public void setTextColor(Color text) {
		this.textColor = text;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(shape.getBounds());
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public Stroke getBorderStroke() {
		return borderStroke;
	}
	
	public void setBorderStroke(Stroke stroke) {
		this.borderStroke = stroke;
	}
	
	public boolean isHighlighted() {
		return isHighlighted;
	}
	
	public void setHighlighted(boolean b) {
		this.isHighlighted = b;
	}
		
	public int getPoints() {
		return points;
	}
	
	public Rectangle2D getRelativeBounds() {
		return relativeBounds;
	}
	
	public void setRelativeBounds(Rectangle2D relativeBounds) {
		this.relativeBounds = relativeBounds;
	}

	public boolean contains(Point2D p) {
		return shape.contains(p);
	}

	public void render(Graphics2D g, Rectangle2D bounds) {
		scaleShape(bounds);
		g.setColor(isHighlighted() ? getHighlightColor() : getFillColor());
		//g.setPaint(new RoundGradientPaint(bounds.getCenterX(), bounds.getCenterY(), Color.cyan, 
		//		new Point2D.Double(bounds.getWidth()/6, bounds.getHeight()/6), Color.white));
		
		Shape shape = getShape();
		g.fill(shape);
		g.setColor(getBorderColor());
		g.setStroke(getBorderStroke());
		g.draw(shape);
		
		Rectangle2D shapeBounds = shape.getBounds2D();
		// draw the points
		String string = ""+points;
		int h = g.getFontMetrics().getHeight();
		int w = g.getFontMetrics().stringWidth(string);
		if (w > shapeBounds.getWidth()) {
			float size = Math.max(6f, g.getFont().getSize2D() - 2f);
			g.setFont(g.getFont().deriveFont(size));
			w = g.getFontMetrics().stringWidth(string);
		}
		
		g.setColor(getTextColor());
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		double x = shapeBounds.getCenterX() - (w / 2); 
		double y = shapeBounds.getY() + h + 2;
		g.drawString(string, (float) x, (float) y);
	}

	protected void scaleShape(Rectangle2D bounds) {
		double boundsX = bounds.getX();
		double boundsY = bounds.getY();
		double boundsW = bounds.getWidth();
		double boundsH = bounds.getHeight();
		if (isSquare()) {
			boundsW = Math.min(boundsW, boundsH);
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
