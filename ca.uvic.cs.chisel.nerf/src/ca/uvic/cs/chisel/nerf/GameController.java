/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ca.uvic.cs.chisel.nerf.fireworks.NerfHighscoreDialog;
import ca.uvic.cs.chisel.nerf.model.Game;
import ca.uvic.cs.chisel.nerf.model.INerfDB;
import ca.uvic.cs.chisel.nerf.model.NerfDB;
import ca.uvic.cs.chisel.nerf.model.PlayerNotFoundException;
import ca.uvic.cs.chisel.nerf.model.Throw;
import ca.uvic.cs.chisel.nerf.util.audio.NerfAudio;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 24-Jan-07
 */
public class GameController {

	public static boolean SHOW_FIREWORKS = false;
	
	private INerfUI ui;
	private INerfDB nerfDB;
	private Game game;
	private String player;
	private String gameID;
	private int maxDarts;
	private int lastThrow;
	private int sortBy = PlayerSorter.AVG;
	private boolean invertSort = false;

	public GameController(INerfUI ui, String gameID) {
		this.ui = ui;
		this.gameID = gameID;
		this.nerfDB = NerfDB.getInstance();
		this.game = new Game(gameID);
		this.player = null;
		maxDarts = 3;
		lastThrow = 0;
	}
	
	public void setSortBy(int sortBy) {
		switch (sortBy) {
			case PlayerSorter.NAME :
			case PlayerSorter.BEST :
			case PlayerSorter.AVG :
				if (this.sortBy == sortBy) {
					this.invertSort = !invertSort;
				}
				this.sortBy = sortBy;
				break;
		}
	}
	
	public void setMaxDarts(int maxDarts) {
		this.maxDarts = maxDarts;
	}
	
	public int getMaxDarts() {
		return maxDarts;
	}
	
	public boolean isGameOver() {
		return (game.getThrowCount() >= maxDarts);
	}
	
	public boolean isNewGame() {
		return (game.getThrowCount() == 0);
	}

	public void resetPlayer() {
		// @tag Nerf(resetPlayer): model needs to be able to reset a player's scores/stats 
	}

	public boolean addPlayer(String name) {
		boolean added = false;
		if (!nerfDB.playerExists(name)) {
			nerfDB.createPerson(name);
			added = true;
		}
		return added;
	}

	public void deletePlayer() {
		player = null;

		// @tag Nerf(deletePlayer): model needs to be able to delete a player 
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getPlayer() {
		return player;
	}
	
	public String getGameID() {
		return gameID;
	}
	
	public void setGameID(String gameID) {
		this.gameID = gameID;
	}
	
	public Game getGame() {
		return game;
	}	
	
	public void newGame() {
		this.game = new Game(gameID);
	}
	
	public int getLastThrow() {
		return lastThrow;
	}
	
	public List<Point2D> getDartLocations() {
		List<Point2D> points = new ArrayList<Point2D>();
		for (Throw t : game.getThrows()) {
			points.add(new Point(t.getX(), t.getY()));
		}
		return points;
	}
	
	public void addThrow(Point2D p, int points) {
		NerfAudio.playAudioFile("/sounds/boing.wav");

		if (player != null) {
			// reset when the next dart lands
			if (game.getThrowCount() >= maxDarts) {
				game = new Game(gameID);
			}
			
			game.addThrow((int)p.getX(), (int)p.getY(), points);
			
			// add the game to the model AFTER the game is over
			if (game.getThrowCount() == maxDarts) {
				try {
					if (game.getScore() > nerfDB.getCurrentScore(player, gameID)) {
						NerfAudio.playAudioFile("/sounds/highscore.wav");
						if (SHOW_FIREWORKS) {
							ui.stopGame();
							NerfHighscoreDialog dlg = new NerfHighscoreDialog(""+game.getScore(), true);
							dlg.addWindowListener(new WindowAdapter() {
								public void windowClosed(WindowEvent e) {
									ui.startGame();
								}
							});
						}
					} else {
						NerfAudio.playAudioFile("/sounds/gameover.wav");
					}
					nerfDB.addGame(player, game);
				} catch (PlayerNotFoundException e) {
					e.printStackTrace();
				}
			}
			lastThrow = points;
		}
	}

	public void dispose() {
		nerfDB.save();
	}

	public int getAverageScore() {
		try {
			return nerfDB.getAverageScore(player, gameID);
		} catch (PlayerNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getCurrentScore() {
		try {
			return nerfDB.getCurrentScore(player, gameID);
		} catch (PlayerNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getDartsThrown() {
		return game.getThrowCount();
	}
	
	public Object[][] getData() {
		List<String> playersList = nerfDB.getPlayers();
		Player[] players = new Player[playersList.size()];
		int i = 0;
		for (String playerName : playersList) {
			players[i] = new Player(playerName, 0, 0);
			try {
				players[i].setBestScore(new Integer(nerfDB.getCurrentScore(playerName, gameID)));
				players[i].setAverageScore(new Integer(nerfDB.getAverageScore(playerName, gameID)));
			} catch (PlayerNotFoundException e) {
				e.printStackTrace();
			}
			i++;
		}
		
		Arrays.sort(players, new PlayerSorter(sortBy, invertSort));
		
		return Player.convertToTableData(players);
	}

	public List<Game> getGames() {
		try {
			return nerfDB.getGames(player);
		} catch (PlayerNotFoundException e) {
			return Collections.emptyList();
		}
	}

}
