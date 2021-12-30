package ca.uvic.cs.chisel.nerf.shapes;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 23-Jan-07
 */
public class CircleNerfShape extends AbstractNerfShape {

	public CircleNerfShape(Rectangle2D relativeBounds, int points) {
		super(new Ellipse2D.Double(0, 0, 0, 0), relativeBounds, points);
	}

	public CircleNerfShape(Rectangle2D relativeBounds, int points, 
			boolean fixedSpeed, int xspeed, int yspeed) {
		super(new Ellipse2D.Double(0, 0, 0, 0), relativeBounds, points, fixedSpeed, xspeed, yspeed);
	}

	public CircleNerfShape(Rectangle2D relativeBounds, int points, 
			boolean fixedSpeed, int xspeed, int yspeed, boolean isSquare) {
		super(new Ellipse2D.Double(0, 0, 0, 0), relativeBounds, points, fixedSpeed, xspeed, yspeed, isSquare);
	}
	
	@Override
	public boolean intersects(NerfShape nerfShape) {
		boolean intersects = false;
		
		Rectangle2D r1 = getRelativeBounds();
		Rectangle2D r2 = nerfShape.getRelativeBounds();
		
		double x1 = r1.getCenterX();
		double y1 = r1.getCenterY();
		double x2 = r2.getCenterX();
		double y2 = r2.getCenterY();
		double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		
		double radii = (r1.getWidth() + r2.getWidth()) / 2; 
		if (distance < radii) {
			intersects = true;
		}
		
		return intersects;
	}

	/**
	 * @param nerfShape
	 * @return
	 */
	protected boolean pathIteratorIntersects(NerfShape nerfShape) {
		boolean intersects = false;
		Shape shape = getShape();
		Shape shape2 = nerfShape.getShape();
		PathIterator pathIterator = shape2.getPathIterator(null);
		pathIterator.next();
		do {
			double[] d = new double[6];
			int type = pathIterator.currentSegment(d);
			Point2D p = null;
			switch (type) {
				case PathIterator.SEG_MOVETO :
				case PathIterator.SEG_LINETO :
					p = new Point2D.Double(d[0], d[1]);
					break;
				case PathIterator.SEG_QUADTO :
					p = new Point2D.Double(d[2], d[3]);
					break;
				case PathIterator.SEG_CUBICTO :
					p = new Point2D.Double(d[4], d[5]);
					break;
			}
			
			if ((p != null) && shape.contains(p)) {
				System.out.println("intersects");
				intersects = true;
				break;
			}
			
			pathIterator.next();
		} while (!pathIterator.isDone());
		return intersects;
	}
	
}
