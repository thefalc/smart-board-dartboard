package ca.uvic.cs.chisel.nerf.fireworks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.MemoryImageSource;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;

public class NerfHighscoreDialog extends JDialog {
	private static final long serialVersionUID = -3098484887123827329L;
	
	/** duration of the high score celebration */
	private static int TIMER_DELAY = 20000;
	
	/** timer for disposing this window after a certain delay */
	private Timer timer = new Timer();

	public NerfHighscoreDialog(final String score, boolean alwaysOnTop) {
		this.setAlwaysOnTop(alwaysOnTop);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		final NerfHighscoreDialog dialog = this;
		final NerfHighscoreFireworksPanel fireworks = new NerfHighscoreFireworksPanel();
		this.getContentPane().add(fireworks, BorderLayout.CENTER);
		
		this.setBackground(Color.black);
		
		// setup fullscreen mode
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gdev = ge.getDefaultScreenDevice();
		setUndecorated(true);
		setVisible(true);
		setResizable(false);
		gdev.setFullScreenWindow(this);
		
		// make the cursor disappear
		int[] pixels = new int[16 * 16];
		Image image = Toolkit.getDefaultToolkit().createImage(
		        new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor =
		        Toolkit.getDefaultToolkit().createCustomCursor
		             (image, new Point(0, 0), "invisibleCursor");
		
		this.setCursor(transparentCursor);
		
		this.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				fireworks.setSize(getSize());
				fireworks.init(score);
				fireworks.start();
				
				timer.schedule(new CloseDialogTask(dialog), TIMER_DELAY);
			}
			public void componentShown(ComponentEvent e) {}			
		});
	}
	
	public static void main(String[] args) {
		new NerfHighscoreDialog("50", true); 
	}
}

class CloseDialogTask extends TimerTask {
	private JDialog dialog;
	
	public CloseDialogTask(JDialog dialog) {
		this.dialog = dialog;
	}
	
	@Override
	public void run() {
		dialog.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		dialog.dispose();	
	}
}

