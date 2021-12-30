package ca.uvic.cs.chisel.nerf.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

public class RoundGradientPaintFill extends JFrame {

	public static void main(String[] args) {
		RoundGradientPaintFill f = new RoundGradientPaintFill();
		f.setTitle("RoundGradientPaintFill v1.0");
		f.setSize(300, 300);
		f.setLocation(400, 200);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Ellipse2D r = new Ellipse2D.Float(50, 50, 200, 200);
		RoundGradientPaint rgp = new RoundGradientPaint(150, 150, Color.magenta, new Point2D.Double(0, 200), Color.blue);
		g2.setPaint(rgp);
		g2.fill(r);
	}
	
}
