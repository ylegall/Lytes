package org.ygl.lytes;

import java.util.Random;

public class Grid {
	
	/* package-private */
	boolean[][] grid;
	public static int GRID_LENGTH = 5;
	int gameCode;		// the game ID
	int par;
	int totalClicks;
	private int count;	// the number of lights currently on
	
	public Grid(final int gridLength) {
		GRID_LENGTH = gridLength;
		// create the matrix:
		grid = new boolean[GRID_LENGTH][GRID_LENGTH];
		for (int i=0; i<grid.length; i++) {
			grid[i] = new boolean[GRID_LENGTH];
		}
		gameCode = 1;
	}
	
	/**
	 * Initializes a game configuration
	 * based on a game code.
	 * @param gameCode A 3 digit positive
	 * integer.
	 */
	public final void setupGame(final int gameCode) {
		
		this.gameCode = gameCode;
		this.totalClicks = 0;
		// create a random number generator
		// and calculate the total number
		// of 'clicks' that we will use
		// to set up the game:
		Random rand = new Random(gameCode);
		par = gameCode/4 + 1;
		par = (par > 100)? 100 : par;
		
		// clear the board first
		clear();
		
		// do a series of random clicks
		for(int i=0; i<par; i++) {
			toggle(rand.nextInt(Grid.GRID_LENGTH), rand.nextInt(GRID_LENGTH));
		}
	}
	
	/**
	 * clears the board.
	 */
	private final void clear() {
		count = 0;
		for(int i=0; i < GRID_LENGTH; i++) {
			for(int j=0; j < GRID_LENGTH; j++) {
				grid[i][j] = false;
			}
		}
	}
	
	/**
	 * Checks if the board has any lights turned on.
	 * @return true if there are no lights on.
	 */
	public final boolean isEmpty() {
		return this.count == 0;
	}
	
	/**
	 * clicks the cell at the given index,
	 * toggling itself and the 4 neighbors.
	 * @param i the i index of the cell
	 * @param j the j index of the cell
	 */
	public void click(int i, int j) {
		if(i >= 0 && i < GRID_LENGTH) {
			if(j >= 0 && j < GRID_LENGTH) {
				totalClicks++;
				toggle(i,j);
			}
		}
	}
	
	/**
	 * toggles the cell at the given index,
	 * as well as its 4 neighbors.
	 * @param i the i index of the cell
	 * @param j the j index of the cell
	 */
	private final void toggle(int i, int j) {
		
		// toggle the grid at (i,j):
		int max = GRID_LENGTH - 1;
		count = (grid[i][j])? count - 1 : count + 1;
		grid[i][j] = !grid[i][j];
		
		// up
		if(i > 0) {
			count = (grid[i-1][j])? count - 1 : count + 1;
			grid[i-1][j] = !grid[i-1][j];
		}
		
		// down
		if(i < max) {
			count = (grid[i+1][j])? count - 1 : count + 1;
			grid[i+1][j] = !grid[i+1][j];
		}
		
		// left
		if(j > 0) {
			count = (grid[i][j-1])? count - 1 : count + 1;
			grid[i][j-1] = !grid[i][j-1];
		}
		
		// right
		if(j < max) {
			count = (grid[i][j+1])? count - 1 : count + 1;
			grid[i][j+1] = !grid[i][j+1];
		}
	}
	
	
}
