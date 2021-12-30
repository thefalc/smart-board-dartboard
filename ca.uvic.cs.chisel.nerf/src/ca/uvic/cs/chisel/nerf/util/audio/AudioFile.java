/**
 * Audio.java
 * 
 * Created on Sep 28, 2004
 */
package ca.uvic.cs.chisel.nerf.util.audio;


/**
 * Interface for MIDI and Audio files.
 * 
 * @author ccallendar
 * @version 1.0
 * @date September 28th, 2004
 */
public interface AudioFile {

	/**
	 * Opens the audio file.
	 * @return boolean if the audio file could be opened.
	 */
	public boolean open();

	/**
	 * Plays the audio file.
	 */
	public void play();
	
	/**
	 * Stops the currently playing audio file.
	 */
	public void stop();
	
	/**
	 * Closes the audio file.
	 */
	public void close();
	
	/**
	 * Returns true if the current audio file is playing.
	 * @return boolean
	 */
	public boolean isPlaying();
	
	/**
	 * Gets the filename path.
	 */
	public String getPath();
	
	/**
	 * Returns if the song is looping.
	 * @return boolean
	 */
	public boolean isLooping();
	
	/**
	 * Returns the number of times that the song will play.
	 * @return int
	 */
	public int getLoopCount();
	public void setLoopCount(int count);
	
	/**
	 * If true then the song wil continue to play indefinitely.
	 */
	public void setLoopContinuously(boolean continuously);
	
	/**
	 * Returns the number of times the song has been played.
	 * @return int
	 */
	public int getPlayCount();
	
	/**
	 * Sets the audio volume.
	 * @param volume the volume from 0-100.
	 */
	public void setVolume(int volume);
	
	/**
	 * Sets the audio listener.
	 * @param listener
	 */
	public void setListener(AudioListener listener);
	

}
