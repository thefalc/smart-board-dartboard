/**
 * SampledAudioFile.java
 * 
 * Created on Sep 28, 2004
 */
package ca.uvic.cs.chisel.nerf.util.audio;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Class for playing Sampled Audio files (WAV, AIF, AU).
 * 
 * @author ccallendar
 * @version 1.0
 * @date September 28th, 2004
 */
public class SampledAudioFile implements AudioFile {

	private String path = null;
	private URL url = null;
	private boolean loopContinuously = false;
	private int loopCount = 0;
	private int playCount = 0;
	private Clip clip = null;
	private AudioListener listener;

	/**
	 * Constructor for SampledAudioFile.
	 * @param path	the audio file path.
	 */
	public SampledAudioFile(String path) {
		this();
		this.path = path;
	}
	
	/**
	 * Constructor for SampledAudioFile.
	 * @param path	the audio file path.
	 */
	public SampledAudioFile(URL url) {
		this();
		this.url = url;
	}
	
	/**
	 * Constructor for SampledAudioFile.
	 * @param path	the audio file path.
	 * @param loopContinuously	if the audio file should loop.
	 */
	private SampledAudioFile() {
		this.loopContinuously = false;
		this.loopCount = 0;
		this.playCount = 0;
		this.listener = new AudioListener(this);
	}

	/**
	 * @return
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	private AudioInputStream getAudioInputStream() throws UnsupportedAudioFileException, IOException {
		AudioInputStream stream = null;
		if ((path != null) && (path.length() > 0)) {
			File audioFile = new File(path);
			stream = AudioSystem.getAudioInputStream(audioFile);
		} else if (url != null) {
			stream = AudioSystem.getAudioInputStream(url);
		}
		return stream;
	}
	
	/**
	 * @see org.oversoul.audio.AudioFile#open()
	 */
	public boolean open() {
		try {
			AudioInputStream stream = getAudioInputStream();
			if (stream == null) {
				return false;
			}
			
			// At present encodings must be converted to PCM_SIGNED before it can be played
			AudioFormat format = stream.getFormat();
			if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
				try {
					format = new AudioFormat(
							AudioFormat.Encoding.PCM_SIGNED,
							format.getSampleRate(),
							format.getSampleSizeInBits()*2,
							format.getChannels(),
							format.getFrameSize()*2,
							format.getFrameRate(),
							true);        // big endian
					stream = AudioSystem.getAudioInputStream(format, stream);
				} catch (IllegalArgumentException iae) {
				}
			}
	
			// Create the clip
			DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(), ((int)stream.getFrameLength()*format.getFrameSize()));
			clip = (Clip) AudioSystem.getLine(info);
	
			// This method does not return until the audio file is completely loaded
			clip.open(stream);
			clip.addLineListener(listener);		// closes the audio file when it stops playing 
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
		if ((clip == null) || !clip.isOpen()) {
			ok = open();
		}
		if (ok) {
			try {
				playCount++;
				clip.setFramePosition(0);
				if (loopContinuously) {
					clip.loop(Clip.LOOP_CONTINUOUSLY);
				} else if (loopCount >= 1) {
					clip.loop(loopCount);
				} else {
					clip.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Plays a sampled audio stream.
	 */
	protected void playStream() {
		try {
			AudioInputStream stream = getAudioInputStream();

			// At present encodings must be converted to PCM_SIGNED before it can be played
			AudioFormat format = stream.getFormat();
			if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
				format = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						format.getSampleRate(),
						format.getSampleSizeInBits()*2,
						format.getChannels(),
						format.getFrameSize()*2,
						format.getFrameRate(),
						true);        // big endian
				stream = AudioSystem.getAudioInputStream(format, stream);
			}

			// Create line
			SourceDataLine.Info info = new DataLine.Info(
				SourceDataLine.class, stream.getFormat(),
				((int)stream.getFrameLength()*format.getFrameSize()));
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(stream.getFormat());
			line.start();
   
			// Continuously read and play chunks of audio
			int numRead = 0;
			byte[] buf = new byte[line.getBufferSize()];
			while ((numRead = stream.read(buf, 0, buf.length)) >= 0) {
				int offset = 0;
				while (offset < numRead) {
					offset += line.write(buf, offset, numRead-offset);
				}
			}
			line.drain();
			line.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the audio volume.
	 * @param volume the volume from 0 - 100.
	 */
	public void setVolume(int volume) {
		if ((clip == null) || !clip.isOpen()) {
			open();
		}
		if (clip != null) {
			//Set Volume
			volume = Math.max(0, Math.min(100, volume));
			FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			double gain = volume / 100D;    // number between 0 and 1 (loudest)
			float dB = (float)(Math.log(gain)/Math.log(10.0)*20.0);
			gainControl.setValue(dB);
		}
	}
	
	public void setLoopCount(int loop) {
		this.loopCount = loop - 1; 	// zero based;
	}
	
	public void setLoopContinuously(boolean continuously) {
		this.loopContinuously = continuously;
	}
	
	/**
	 * Mutes the audio.
	 */
	public void mute() {
		if (clip != null) {
			BooleanControl muteControl = (BooleanControl)clip.getControl(BooleanControl.Type.MUTE);
			muteControl.setValue(true);	 // Mute On
		}
	}
	
	/**
	 * Unmutes the audio.
	 */
	public void unmute() {
		if (clip != null) {
			BooleanControl muteControl = (BooleanControl)clip.getControl(BooleanControl.Type.MUTE);
			muteControl.setValue(false); // Mute Off
		}
	}	

	/**
	 * @see org.oversoul.audio.AudioFile#stop()
	 */
	public void stop() {
		if (isPlaying()) {
			clip.stop();
			clip.flush();
		}
	}

	/**
	 * @see org.oversoul.audio.AudioFile#close()
	 */
	public void close() {
		if (clip != null) {
			try {
				if (clip.isRunning()) {
					clip.stop();
					clip.flush();
				}
				if (clip.isOpen()) {
					clip.close();
				}
			} catch (Exception ex) {
				System.err.println("Error stopping sampled audio file: " + ex.getMessage());	
			}
			clip = null;
		}
	}

	/**
	 * @see org.oversoul.audio.AudioFile#isPlaying()
	 */
	public boolean isPlaying() {
		return ((clip != null) && clip.isRunning());
	}

	/**
	 * Gets the filename path. 
	 * @return String
	 */
	public String getPath() {
		return (path != null ? path : "");
	}

	/**
	 * @see org.oversoul.audio.AudioFile#getLoopCount()
	 */
	public int getLoopCount() {
		return loopCount;
	}

	/**
	 * @see org.oversoul.audio.AudioFile#getPlayCount()
	 */
	public int getPlayCount() {
		return playCount;
	}

	/**
	 * @see org.oversoul.audio.AudioFile#isLooping()
	 */
	public boolean isLooping() {
		return loopContinuously;
	}
	
	/**
	 * @see org.oversoul.audio.AudioFile#setListener(org.oversoul.audio.AudioListener)
	 */
	public void setListener(AudioListener listener) {
		this.listener = listener;
	}

}
