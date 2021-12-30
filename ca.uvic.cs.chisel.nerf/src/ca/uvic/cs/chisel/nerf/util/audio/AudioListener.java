/**
 * AudioListener.java
 * 
 * Created on 4-Oct-04
 */
package ca.uvic.cs.chisel.nerf.util.audio;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * Listener for audio files to determine when a song stops playing.
 * 
 * @author 		ccallendar
 * @version 	1.0
 */
public class AudioListener implements MetaEventListener, LineListener {

	private static final int MIDI_FILE = 47;

	private AudioFile audioFile = null;

	/**
	 * Constructor for AudioListener.java
	 * @param audioFile the audio file.
	 * @param millis the time in milliseconds between checks.
	 */
	public AudioListener(AudioFile audioFile) {
		this.audioFile = audioFile;
		this.audioFile.setListener(this);
	}

	/**
	 * Plays the song and listens for it to end before signalling to UI.
	 */
	public void play() {
		if (audioFile != null) {
			if (!audioFile.isPlaying()) {
				audioFile.play();	
			}
		}
	}

	/**
	 * @see javax.sound.midi.MetaEventListener#meta(javax.sound.midi.MetaMessage)
	 */
	public void meta(MetaMessage meta) {
		if (meta.getType() == MIDI_FILE) {
			stopped();
		}
	}

	/**
	 * @see javax.sound.sampled.LineListener#update(javax.sound.sampled.LineEvent)
	 */
	public void update(LineEvent event) {
		if (event.getType() == LineEvent.Type.STOP) {
			stopped();
		}
	}
	
	/**
	 * Causes the song to loop or repeat if requested, or signals to the ui that the song
	 * has stopped.
	 */
	private void stopped() {
		if (audioFile.isLooping() || (audioFile.getPlayCount() < (audioFile.getLoopCount()+1))) {
			audioFile.play();
		} else {
			audioFile.close();
		}
			
	}


}
