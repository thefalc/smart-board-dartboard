/**
 * MidiFile.java
 * 
 * Created on Sep 28, 2004
 */
package ca.uvic.cs.chisel.nerf.util.audio;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

/**
 * Class for playing MIDI files.
 * 
 * @author ccallendar
 * @version 1.0
 * @date September 28th, 2004
 */
public class MidiFile implements AudioFile {

	private String path = null;
	private URL url = null;
	private boolean loopContinuously = false;
	private int loopCount = 0;
	private boolean started = false;
	private int playCount = 0;
	private AudioListener listener;

	private Sequencer sequencer = null;
	
	/**
	 * Constructor for MidiFile.
	 * @param path	the midi file path.
	 */
	public MidiFile(String path) {
		this();
		this.path = path;
	}
	
	/**
	 * Constructor for MidiFile.
	 * @param path	the midi file path.
	 */
	public MidiFile(URL url) {
		this();
		this.url = url;
	}

	/**
	 * Constructor for MidiFile.
	 */
	private MidiFile() {
		this.loopContinuously = false;
		this.loopCount = 0;
		this.listener = new AudioListener(this);
	}

	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount - 1;	//zero based
	}
	
	public void setLoopContinuously(boolean continuously) {
		this.loopContinuously = continuously;
	}
	
	/**
	 * Sets the midi volume.
	 * @param volume the volume from 0-100.
	 */
	public void setVolume(int volume) {
		boolean ok = true;
		if ((sequencer == null) || !sequencer.isOpen()) {
			ok = open();
		}
		if (ok) {
			volume = Math.max(0, Math.min(100, volume));
			if ((sequencer != null) && (sequencer instanceof Synthesizer)) {
				Synthesizer synthesizer = (Synthesizer)sequencer;
				MidiChannel[] channels = synthesizer.getChannels();
	    
				// gain is a value between 0 and 1 (loudest)
				double gain = volume / 100D;
				for (int i = 0; i < channels.length; i++) {
					channels[i].controlChange(7, (int)(gain * 127.0));
				}
			}
		}		
	}

	private Sequence getSequence() throws InvalidMidiDataException, IOException {
		Sequence sequence = null;
		if ((path != null) && (path.length() > 0)) {
			File midiFile = new File(path);
			sequence = MidiSystem.getSequence(midiFile);
		} else if (url != null) {
			sequence = MidiSystem.getSequence(url);
		}
		return sequence;
	}

	/**
	 * @see org.oversoul.audio.AudioFile#open()
	 */
	public boolean open() {
		try {
			Sequence sequence = getSequence();
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(sequence);
			sequencer.addMetaEventListener(listener);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see org.oversoul.audio.AudioFile#play()
	 */
	public void play() {
		boolean ok = true;
		if ((sequencer == null) || !sequencer.isOpen()) {
			ok = open();
		}
		if (ok) {
			try {
				sequencer.setTickPosition(0);
				sequencer.start();
				started = true;
				playCount++;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * @see org.oversoul.audio.AudioFile#stop()
	 */
	public void stop() {
		if (isPlaying()) {
			sequencer.stop();
		}
	}

	/**
	 * @see org.oversoul.audio.AudioFile#close()
	 */
	public void close() {
		if (sequencer != null) {
			try {
				if (sequencer.isRunning()) {
					sequencer.stop();
				}
				if (sequencer.isOpen()) {
					sequencer.close();
				}
			} catch (Exception ex) {
				System.err.println("Error stopping MIDI file: " + ex.getMessage());	
			}
		}
	}

	/**
	 * @see org.oversoul.audio.AudioFile#isPlaying()
	 */
	public boolean isPlaying() {
		boolean playing = ((sequencer != null) && sequencer.isRunning());
		return (loopContinuously && started) || playing;
	}

	/**
	 * Returns if the midi file is set to loop.
	 * @return boolean
	 */
	public boolean isLooping() {
		return loopContinuously;		
	}
	
	/**
	 * Gets the maximum loop count.
	 * @return int loop count
	 */
	public int getLoopCount() {
		return loopCount;	
	}
	
	/**
	 * Returns how many times the midi file has been played.
	 * @return int play count.
	 */
	public int getPlayCount() {
		return playCount;
	}

	/**
	 * Gets the filename path. 
	 * @return String
	 */
	public String getPath() {
		return (path != null ? path : "");
	}

	/**
	 * Sets the audio listener.
	 * @param listener
	 */
	public void setListener(AudioListener listener) {
		this.listener = listener;
	}

}
