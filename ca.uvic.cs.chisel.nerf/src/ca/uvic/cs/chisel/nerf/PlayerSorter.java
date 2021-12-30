/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf;

import java.util.Comparator;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 30-Jan-07
 */
public class PlayerSorter implements Comparator<Player> {

	public static final int NAME = 0;
	public static final int BEST = 1;
	public static final int AVG = 2;
		
	private int sortBy;
	private boolean invert;
	
	public PlayerSorter(int sortBy, boolean invert) {
		this.sortBy = sortBy;
		this.invert = invert;
	}

	public int compare(Player p1, Player p2) {
		if (invert) {
			Player temp = p1;
			p1 = p2;
			p2 = temp;
		}
		
		if (p1 == null && p2 == null) {
            return 0;
        } else if (p1 == null) { // Define null less than everything.
            return -1;
        } else if (p2 == null) {
            return 1;
        }
		
		switch (sortBy) {
			case BEST :
				return compareNumbers(p1.getBestScore(), p2.getBestScore());
			case AVG :
				return compareNumbers(p1.getAverageScore(), p2.getAverageScore());
			case NAME :
			default :
				return p1.getName().compareToIgnoreCase(p2.getName());
		}
	}
	
	private static int compareNumbers(int d1, int d2) {
		 if (d1 < d2) {
             return 1;			// put highest first
         } else if (d1 > d2) {
             return -1;
         } else {
             return 0;
         }
	}

}
