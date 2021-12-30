/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf.shapes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import ca.uvic.cs.chisel.nerf.GameController;
import ca.uvic.cs.chisel.nerf.INerfUI;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 23-Jan-07
 */
public abstract class AbstractShapeCollection implements ShapeCollection, ActionListener {
	private static final int DART_DIAMETER = 15;
	private static final Color DART_FILL_COLOR = Color.orange;
	private static final Color DART_OUTER_COLOR = new Color(120, 29, 211);
	private static final Color DART_INNER_COLOR = Color.red;
	
	// callback to the NerfUI for repainting after moving/scaling the shapes
	private INerfUI ui;	
	private Timer timer;
	protected boolean allowOverlapping;
	
	protected String name;
	protected List<NerfShape> shapes;
	protected GameController controller;

	public AbstractShapeCollection(String name) {
		this(name, null, 0);
	}
	
	public AbstractShapeCollection(String name, INerfUI ui, int timerDelay) {
		this.name = name;
		this.shapes = new ArrayList<NerfShape>();
		this.allowOverlapping = true;
		if (ui != null) {
			this.ui = ui;
			this.timer = new Timer(timerDelay, this);
		}
	}

	@Override
	public String toString() {
		return getName();
	}
	
	public String getName() {
		return name;
	}
		
	public void actionPerformed(ActionEvent e) {
		if ((ui != null) && ui.isActive()) {
			moveShapes();
			ui.repaintDartBoard();
		}
	}
	
	protected void setBorderColors(Color c) {
		for (NerfShape shape : getShapes()) {
			shape.setBorderColor(c);
		}
	}

	protected void setFillColors(Color c) {
		for (NerfShape shape : getShapes()) {
			shape.setFillColor(c);
		}
	}

	protected void setTextColors(Color c) {
		for (NerfShape shape : getShapes()) {
			shape.setTextColor(c);
		}
	}

	protected void setBorderStrokes(Stroke stroke) {
		for (NerfShape shape : getShapes()) {
			shape.setBorderStroke(stroke);
		}
	}
	
	protected void moveShapes() {
		if (!allowOverlapping) {
			checkForCollisions();
		}
		
		List<NerfShape> shapes = getShapes();
		for (NerfShape shape : shapes) {
			shape.moveShape();
		}
	}

	protected void checkForCollisions() {
		List<NerfShape> shapes = getShapes();
		// check for collision, once one is found then stop
		for (int i = 1; i < shapes.size(); i++) {
			for (int j = 0; j < i; j++) {
				NerfShape shape1 = shapes.get(i);
				NerfShape shape2 = shapes.get(j);
				if (shape1.intersects(shape2)) {
					performCollision(shape1, shape2);
					break;
				}
			}
		}
	}
	
	protected void performCollision(NerfShape shape1, NerfShape shape2) {
		double m1 = shape1.getMass();
		double m2 = shape2.getMass();
		double x1 = shape1.getBounds().getCenterX();
		double y1 = shape1.getBounds().getCenterY();
		double r1 = shape1.getBounds().getWidth() / 2;
		double vx1 = shape1.getXSpeed();
		double vy1 = shape1.getYSpeed();
		double x2 = shape2.getBounds().getCenterX();
		double y2 = shape2.getBounds().getCenterY();
		double r2 = shape2.getBounds().getWidth() / 2;
		double vx2 = shape2.getXSpeed();
		double vy2 = shape2.getYSpeed();

		double r12, m21, d, gammav, gammaxy, dgamma, dr, dc, sqs, t, dvx2, a, x21, y21, vx21, vy21;

		r12 = r1 + r2;
		m21 = m2 / m1;
		x21 = x2 - x1;
		y21 = y2 - y1;
		vx21 = vx2 - vx1;
		vy21 = vy2 - vy1;

		double alpha;

		// return old positions and velocities if relative velocity = 0  
		if (vx21 == 0 && vy21 == 0) {
			return;
		}

		// calculate relative velocity angle
		gammav = Math.atan2(-vy21, -vx21);
		d = Math.sqrt(x21 * x21 + y21 * y21);

		// calculate relative position angle and normalized impact parameter
		gammaxy = Math.atan2(y21, x21);
		dgamma = gammaxy - gammav;
		if (dgamma > Math.PI) {
			dgamma = dgamma - Math.PI;
		} 
		else if (dgamma < -Math.PI) {
			dgamma = dgamma + Math.PI;
		}
		dr = d * Math.sin(dgamma) / r12;

		// return old positions and velocities if balls do not collide 
		if ((Math.abs(dgamma) > Math.PI / 4 && Math.abs(dgamma) < 0.75 * Math.PI) || Math.abs(dr) > 1) {
			return;
		}

		// calculate impact angle if balls do collide
		alpha = Math.asin(dr);

		// calculate time to collision
		dc = d * Math.cos(dgamma);
		if (dc > 0) {
			sqs = 1.0;
		} 
		else {
			sqs = -1.0;
		}
		t = (dc - sqs * r12 * Math.sqrt(1 - dr * dr)) / Math.sqrt(vx21 * vx21 + vy21 * vy21);
		
		// update positions
		x1 = x1 + vx1 * t;
		y1 = y1 + vy1 * t;
		x2 = x2 + vx2 * t;
		y2 = y2 + vy2 * t;

		// update velocities
		a = Math.tan(gammav + alpha);
		dvx2 = -2 * (vx21 + a * vy21) / ((1 + a * a) * (1 + m21));

		vx2 = vx2 + dvx2;
		vy2 = vy2 + a * dvx2;
		vx1 = vx1 - m21 * dvx2;
		vy1 = vy1 - a * m21 * dvx2;

		shape1.setSpeeds(vx1, vy1);
		shape2.setSpeeds(vx2, vy2);
	}
		
	// this one doesn't work as well as the above
	// overlapping occurs sometimes.
	protected void performCollision2(NerfShape shape1, NerfShape shape2) {
		/*
	 	momentum: P = m1u1 + m2u2 = m1v1 + m2v2 in (x, y)
		energy: E = (1/2)m1u1^2 + (1/2)m2u2^2 = (1/2)m1v1^2 + (1/2)m2v2^2   (can cancel the 1/2's)
		
		then solve for v1 (and get v2 from the P = m1v1 + m2v2 eqn)
		 */
		double m1 = shape1.getMass();	// positive, non-zero number
		double m2 = shape2.getMass();
		
		// X-Direction
		double u1 = shape1.getXSpeed();
		double u2 = shape2.getXSpeed();
		double P = (m1 * u1) + (m2 * u2);
		double E = (m1 * u1 * u1) + (m2 * u2 * u2);
		
		// quadratic formula:  x = (1/2*a) * (-b +/- sqrt(b^2 - 4ac))
		double a = m2 * (m1 + m2);
		double b = -2 * P * m2;
		double c = (P * P) - (E * m1);
		double vx2 = (-b + Math.sqrt((b*b) - (4*a*c))) / (2 * a);
		double vx1 = (P - (m2 * vx2)) / m1;
		
		// Y-Direction
		u1 = shape1.getYSpeed();
		u2 = shape2.getYSpeed();
		P = (m1 * u1) + (m2 * u2);
		E = (m1 * u1 * u1) + (m2 * u2 * u2);
		
		// quadratic formula:  x = (1/2*a) * (-b +/- sqrt(b^2 - 4ac))
		a = m2 * (m1 + m2);
		b = -2 * P * m2;
		c = (P * P) - (E * m1);
		double vy2 = (-b + Math.sqrt((b*b) - (4*a*c))) / (2 * a);
		double vy1 = (P - (m2 * vy2)) / m1;

		shape1.setSpeeds(vx1, vy1);
		shape2.setSpeeds(vx2, vy2);
	}

	public void setGameController(GameController controller) {
		this.controller = controller;
	}

	public int getShapeCount() {
		return shapes.size();
	}

	public List<NerfShape> getShapes() {
		return shapes;
	}
	
	public void addShape(NerfShape shape) {
		shapes.add(shape);
	}
	
	public NerfShape removeShape(int index) {
		if ((index >= 0) && (index < shapes.size())) {
			return shapes.remove(index);
		}
		return null;
	}
	
	public void removeShape(NerfShape shape) {
		shapes.remove(shape);
	}
	
	public void clearShapes() {
		shapes.clear();
	}
	
	public void startTimer() {
		if (timer != null) {
			timer.start();
		}
	}
	
	public void stopTimer() {
		if (timer != null) {
			timer.stop();
		}
	}
		
	public void render(Graphics2D g, Rectangle bounds) {
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// render the shapes (e.g. circles)
		for (NerfShape shape : shapes) {
			shape.render(g, bounds);
		}
				
		// render the dart locations for this game
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		if (controller != null) {
			for (Point2D p : controller.getDartLocations()) {
				renderDartLocation(g, p);
			}
		}
	}
	
	private void renderDartLocation(Graphics2D g, Point2D p) {
		int x = (int)(p.getX() - DART_DIAMETER / 2D);
		int y = (int)(p.getY() - DART_DIAMETER / 2D);
		
		g.setColor(DART_OUTER_COLOR);
		g.drawOval(x, y, DART_DIAMETER, DART_DIAMETER);
		g.setColor(DART_INNER_COLOR);
		g.drawOval(x+1, y+1, DART_DIAMETER-2, DART_DIAMETER-2);
		g.setColor(DART_FILL_COLOR);
		g.drawOval(x+2, y+2, DART_DIAMETER-4, DART_DIAMETER-4);
	}
	
	public void mousePressed(Point2D p) {
		dartLanded(p);
	}
	
	public void mouseReleased(Point2D p) {
		unhighlightShapes();
	}
	
	protected void dartLanded(Point2D p) {
		NerfShape hitShape = null;
		// this assumes that the "smallest" shape is last
		// that way the smallest shape that contains p will be set to hitShape
		for (NerfShape shape : shapes) {
			if (shape.contains(p)) {
				hitShape = shape;
			}
		}

		int score = 0;		
		if (hitShape != null) {
			highlightShape(hitShape);
			score = hitShape.getPoints();
		}
		if (controller != null) {
			controller.addThrow(p, score);
		}
	}

	protected void unhighlightShapes() {
		for (NerfShape shape : shapes) {
			shape.setHighlighted(false);
		}
	}

	protected void highlightShape(NerfShape highlightShape) {
		for (NerfShape shape : shapes) {
			if (highlightShape == shape) {
				shape.setHighlighted(true);
			} else {
				shape.setHighlighted(false);
			}
		}
	}
	
}
