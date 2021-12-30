package ca.uvic.cs.chisel.nerf.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
	
	List<Throw> listOfThrows = new ArrayList<Throw>();
	String gameID = null;
	
	public Game(String gameID) {
		this.gameID = gameID;
	}
	
	public void addThrow( int x, int y, int score ) {
		listOfThrows.add(new Throw(x, y, score));
	}
	
	public List<Throw> getThrows() {
		return this.listOfThrows;
	}

	public int getScore() {
		int score = 0;
		for (Throw t : getThrows()) {
			score += t.getPoint();
		}
		return score;
	}

	public int getThrowCount() {
		return listOfThrows.size();
	}
	
	@Override
	public String toString() {
		return gameID + ": " + getThrows();
	}
}
