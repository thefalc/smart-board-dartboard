package ca.uvic.cs.chisel.nerf.shapes;

import java.awt.geom.Rectangle2D;

import ca.uvic.cs.chisel.nerf.INerfUI;

public class PulsatingNerfShapeCollection extends AbstractShapeCollection {
	public PulsatingNerfShapeCollection(INerfUI ui) {
		super("Pulsating Nerf Game", ui, 10);
		
		addShape(new PulsatingNerfShape(new Rectangle2D.Double(0.05, 0.05, 0.9, 0.9), 5));
		addShape(new PulsatingNerfShape(new Rectangle2D.Double(0.15, 0.15, 0.7, 0.7), 10));
		addShape(new PulsatingNerfShape(new Rectangle2D.Double(0.25, 0.25, 0.5, 0.5), 25));
		addShape(new PulsatingNerfShape(new Rectangle2D.Double(0.35, 0.35, 0.3, 0.3), 50));
		addShape(new PulsatingNerfShape(new Rectangle2D.Double(0.45, 0.45, 0.1, 0.1), 100));
	}
}
