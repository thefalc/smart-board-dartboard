/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf.util.audio;

import java.io.File;
import java.net.URL;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 24-Jan-07
 */
public class NerfAudio {
	
	public static boolean PLAY = false;
	
	public static void playAudioFile(String relativePath) {
		if (!PLAY) {
			return;
		}
		
		try {
			URL url = NerfAudio.class.getResource(relativePath);
			// using the URL allows the wav file to be inside the nerf.jar file
			playAudioFile(url);
			/*
			if (url != null) {
				File file = new File(System.getProperty("user.dir") + "/" + relativePath);
				playAudioFile(file);
			} else {
				System.err.println("Couldn't find file: " + relativePath);
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void playAudioFile(File file) {
		if (PLAY && (file != null) && file.exists()) {
			startThread(file.getAbsolutePath());
		}
	}

	public static void playAudioFile(final URL url) {
		if (PLAY && (url != null)) {
			new Thread(new Runnable() {
				public void run() {
					try {
						new SampledAudioFile(url).play();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	private static void startThread(final String path) {
		new Thread(new Runnable() {
			public void run() {
				try {
					new SampledAudioFile(path).play();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
