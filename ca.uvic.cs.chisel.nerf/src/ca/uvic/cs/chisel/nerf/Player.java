/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 30-Jan-07
 */
public class Player {

	private String name;
	private int bestScore;
	private int averageScore;
	
	public Player(String name) {
		this(name, 0, 0);
	}

	public Player(String name, int bestScore, int avgScore) {
		this.name = name;
		this.bestScore = bestScore;
		this.averageScore = avgScore;
	}
	
	/**
	 * Returns the name
	 * @return Player
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the averageScore
	 * @return Player
	 */
	public int getAverageScore() {
		return averageScore;
	}
	
	/**
	 * @param averageScore The averageScore to set.
	 */
	public void setAverageScore(int averageScore) {
		this.averageScore = averageScore;
	}
	
	/**
	 * Returns the bestScore
	 * @return Player
	 */
	public int getBestScore() {
		return bestScore;
	}
	
	/**
	 * @param bestScore The bestScore to set.
	 */
	public void setBestScore(int bestScore) {
		this.bestScore = bestScore;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public static Object[][] convertToTableData(Player[] players) {
		Object[][] data = new Object[players.length][3];
		int i = 0;
		for (Player p : players) {
			data[i][0] = p.getName();
			data[i][1] = new Integer(p.getBestScore());
			data[i][2] = new Integer(p.getAverageScore());
			i++;
		}
		return data;
	}
}
