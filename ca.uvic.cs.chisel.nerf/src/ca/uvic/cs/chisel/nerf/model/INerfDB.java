package ca.uvic.cs.chisel.nerf.model;

import java.util.List;

public interface INerfDB {

	/**
	 * Name of the XML file
	 */
	public final static String SCORE_FILE = "DartScores.xml";
	
	/**
	 * Dispose the singleton and save the XML
	 */
	public abstract void save();

	/**
	 * Gets all the players
	 * @return
	 */
	public abstract List<String> getPlayers();

	/**
	 * Gets the current score from a player
	 * @param player
	 * @return
	 * @throws PlayerNotFoundException
	 */
	public abstract int getCurrentScore(String player, String gameID) throws PlayerNotFoundException;
	
	/**
	 * Gets the total attempts tried by the given player
	 * @param player
	 * @return
	 * @throws PlayerNotFoundException
	 */
	public abstract int getTotalAttempts(String player, String gameID) throws PlayerNotFoundException;
	
	/**
	 * Gets the total accumulated score by the given player
	 * @param player
	 * @return
	 * @throws PlayerNotFoundException
	 */
	public abstract int getTotalScore(String player, String gameID) throws PlayerNotFoundException;
	
	/**
	 * Gets the average score for the given player
	 * @param player
	 * @return
	 * @throws PlayerNotFoundException
	 */
	public int getAverageScore(String player, String gameID) throws PlayerNotFoundException;


	/**
	 * Adds a game to the database
	 * @param player
	 * @param game
	 * @throws PlayerNotFoundException
	 */
	public abstract void addGame(String player, Game game) throws PlayerNotFoundException;
	
	/**
	 * Checks if a player with the given name exists.
	 * @param player
	 * @return true if the player exists
	 */
	public boolean playerExists(String player);
	
	/**
	 * Creates a new player.  No checking is done if the player already exists.
	 * @param name
	 */
	public void createPerson(String name);

	/**
	 * Returns all the games for a given player.
	 * @param name
	 * @return
	 */
	public List<Game> getGames(String name) throws PlayerNotFoundException;
	
}