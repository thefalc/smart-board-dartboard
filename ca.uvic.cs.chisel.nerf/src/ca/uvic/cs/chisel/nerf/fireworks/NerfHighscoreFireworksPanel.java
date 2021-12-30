package ca.uvic.cs.chisel.nerf.fireworks;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import javax.swing.JPanel;

import ca.uvic.cs.chisel.nerf.util.audio.NerfAudio;

public class NerfHighscoreFireworksPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 7056245788130042778L;

	/** firework blast sound */
	private static final String ROCKET_BURST = "/sounds/fire.au";
	
	/** constants for fireworks drawing */
	private static final int ANIMATION_SPEED = 10;
	private static final int ROCKET_STYLE_VARIABILITY = 50;
	private static final int MAX_ROCKET_NUMBER = 300;
	private static final int MAX_ROCKET_EXPLOSION_ENERGY = 1500;
	private static final int MAX_ROCKET_PATCH_NUMBER = 50;
	private static final int MAX_ROCKET_PATCH_LENGTH = 100;
	private static final int GRAVITY = 10;
	
	/** the maximum font size drawn for a new high score */
	private static final int MAX_FONT_SIZE = 300;
	
	/** the starting font size for a high score */
	private int fontSize = 1;
	
	/** a player's score */
	private String score;
	
	private int width, height;
	private Thread thread = null;

	private Rocket rocket[];
	
	public NerfHighscoreFireworksPanel() {}

	public void init(String score) {
		this.score = score;
		width = getSize().width;
		height = getSize().height;

		rocket = new Rocket[MAX_ROCKET_NUMBER];
		for (int i = 0; i < MAX_ROCKET_NUMBER; i++) {
			rocket[i] = new Rocket(width, height, GRAVITY);
		}
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(100 / ANIMATION_SPEED);
				
				this.repaint();
			} 
			catch (InterruptedException x) {
			}
		}
	}
	
	private void paintRockets(Graphics g) {
		int e = (int) (Math.random() * MAX_ROCKET_EXPLOSION_ENERGY * 3 / 4)
			+ MAX_ROCKET_EXPLOSION_ENERGY / 4 + 1, p = (int) (Math.random()
			* MAX_ROCKET_PATCH_NUMBER * 3 / 4)
			+ MAX_ROCKET_PATCH_NUMBER / 4 + 1, l = (int) (Math.random()
			* MAX_ROCKET_PATCH_LENGTH * 3 / 4)
			+ MAX_ROCKET_PATCH_LENGTH / 4 + 1;
		
		long s = (long) (Math.random() * 10000);

		if (Math.random() * 100 < ROCKET_STYLE_VARIABILITY) {
			e = (int) (Math.random() * MAX_ROCKET_EXPLOSION_ENERGY * 3 / 4)
					+ MAX_ROCKET_EXPLOSION_ENERGY / 4 + 1;
			p = (int) (Math.random() * MAX_ROCKET_PATCH_NUMBER * 3 / 4)
					+ MAX_ROCKET_PATCH_NUMBER / 4 + 1;
			l = (int) (Math.random() * MAX_ROCKET_PATCH_LENGTH * 3 / 4)
					+ MAX_ROCKET_PATCH_LENGTH / 4 + 1;
			s = (long) (Math.random() * 10000);
		}
		
		for (int i = 0; i < MAX_ROCKET_NUMBER; i++) {
			if (rocket[i].sleep && Math.random() * MAX_ROCKET_NUMBER * l < 1) {
				NerfAudio.playAudioFile(ROCKET_BURST);

				rocket[i].init(e, p, l, s);
				rocket[i].start();
			}
			rocket[i].show((Graphics2D)g);
		}
	}
	
	private void paintHighScore(Graphics2D g) {
		if(score == null) return;
		
		try {
			Font font = g.getFont().deriveFont((float)fontSize);
			Rectangle2D textBounds = font.getStringBounds(score, g.getFontRenderContext());
			
			g.setColor(Color.RED);
			g.setFont(font);
			g.drawString(score, (int)(width / 2 - textBounds.getWidth() / 2), (int)(height / 2 - textBounds.getHeight() / 2));
			
			fontSize++;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void paint(Graphics g) {
		super.paint(g);

		g.setColor(Color.black);
		g.fillRect(0, 0, width + 1, height + 1);
		
		// draw animated high score total until we reach the max font size
		if(fontSize < MAX_FONT_SIZE) {
			paintHighScore((Graphics2D)g);
		}
		else {
			paintRockets(g);
		}
	}
}

class Rocket {
	public boolean sleep = true;

	private int energy, patch, length, mx, my, gravity, ox, oy, vx[], vy[], x,
			y, red, blue, green, t;
	
	private Random random;
	
	private final static float DEFAULT_STROKE_WIDTH = 3.0f;

	public Rocket(int a, int b, int g) {
		mx = a;
		my = b;

		gravity = g;
	}

	public void init(int e, int p, int l, long seed) {
		int i;

		energy = e;
		patch = p;
		length = l;

		random = new Random(seed);

		vx = new int[patch];
		vy = new int[patch];

		red = (int) (random.nextDouble() * 128) + 128;
		blue = (int) (random.nextDouble() * 128) + 128;
		green = (int) (random.nextDouble() * 128) + 128;

		ox = (int) (Math.random() * mx / 2) + mx / 4;
		oy = (int) (Math.random() * my / 2) + my / 4;

		for (i = 0; i < patch; i++) {
			vx[i] = (int) (Math.random() * energy) - energy / 2;
			vy[i] = (int) (Math.random() * energy * 7 / 8) - energy / 8;
		}
	}

	public void start() {
		t = 0;
		sleep = false;
	}

	public void show(Graphics2D g) {
		if(g == null) return;
		
		g.setStroke(new BasicStroke(DEFAULT_STROKE_WIDTH));
		
		if (!sleep)
			if (t < length) {
				int c = (int) (random.nextDouble() * 64) - 32 + red;
				if (c >= 0 && c < 256)
					red = c;
				c = (int) (random.nextDouble() * 64) - 32 + blue;
				if (c >= 0 && c < 256)
					blue = c;
				c = (int) (random.nextDouble() * 64) - 32 + green;
				if (c >= 0 && c < 256)
					green = c;
				
				Color color = new Color(red, blue, green);

				for (int i = 0; i < patch; i++) {
					double s = (double) t / 100;
					x = (int) (vx[i] * s);
					y = (int) (vy[i] * s - gravity * s * s);
					
					g.setColor(color);
					g.drawLine(ox + x, oy - y, ox + x, oy - y);

					if (t >= length / 2) {
						int j;
						for (j = 0; j < 2; j++) {
							s = (double) ((t - length / 2) * 2 + j) / 100;
							x = (int) (vx[i] * s);
							y = (int) (vy[i] * s - gravity * s * s);

							g.setColor(Color.black);
							g.drawLine(ox + x, oy - y, ox + x, oy - y);
						}
					}
				}

				t++;
			} 
			else {
				sleep = true;
			}
	}
}
