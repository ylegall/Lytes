package org.ygl.lytes;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * This will be the base class for the different grid types. 
 * It should contain fields and methods that are common to
 * all types of grids, like hex, triange, sqaure, etc.
 *
 */
public abstract class Grid {
	
	protected Bitmap onImage, offImage;
	
	public static final int MIN_ALPHA = 0;
	public static final int MAX_ALPHA = 255;
	public static int GRID_LENGTH = 5;
	
	protected int gameCode;		// the game ID
	protected int par;			// number of clicks the user gets
	protected int totalClicks;	// total usre clicks so far
	protected int count;		// the number of lights currently on

	protected Tile[][] grid;	// the array of tiles:
	
	public Grid(final int gridLength) {
		GRID_LENGTH = gridLength;
		// create the matrix:
		grid = new Tile[GRID_LENGTH][GRID_LENGTH];
		for (int i=0; i<grid.length; i++) {
			grid[i] = new Tile[GRID_LENGTH];
			for (int j=0; j<GRID_LENGTH; j++)
				grid[i][j] = new Tile();
		}
		gameCode = 1;
	}
	
	/**
	 * 
	 * Inner class representing an individual tile.
	 * Tiles have a on/off state as well as an alpha value
	 */
	private class Tile {
		boolean state;
		int alpha;
		
		public Tile() {
			state = false;
			alpha = MIN_ALPHA;
		}
		
		/**
		 * toggles the state of this tile,
		 * and begins the animation state,
		 * if the tile is not already being
		 * animated.
		 * @return +1 if this tile was turned on,
		 * -1 otherwise
		 */
		public int toggle(boolean animate) {
			return set(!state, animate);
		}
		
		/**
		 * Sets this tile to the 
		 * @param state true to "turn on" the tile
		 * @param animate true to begin the animation
		 * @return 1 if the tile has been turned on, false otherwise
		 * (used for updating the count of lights on).
		 */
		public int set(boolean state, boolean animate) {
			this.state = state;
			if(animate && !isAnimating()) {
				alpha = (state ? MIN_ALPHA+1 : MAX_ALPHA-1);
			}
			return (state ? 1 : -1);
		}
		
		/**
		 * updates the alpha value for this <code>Tile</cdoe>.
		 * @param dt the change in alpha
		 * @return true if another animation will be
		 * needed, false if the animation is done.
		 */
		public boolean update(int dt) {
			
			// continue animating if
			// alhpa is between 0 and 255:
			if(!isAnimating()) {
				return false;
			}
			
			alpha += (state ? dt : -dt);
			if(alpha < MIN_ALPHA)
				alpha = MIN_ALPHA;
			else if(alpha > MAX_ALPHA)
				alpha = MAX_ALPHA;
			
			return true;
		}
		
		/**
		 * Checks if this <code>Tile</cdoe> is in
		 * the process of being animated.
		 * @return 
		 */
		public boolean isAnimating() {
			return (alpha > MIN_ALPHA && alpha < MAX_ALPHA);
		}
	}
	
	/**
	 * Initializes a game configuration
	 * based on a game code.
	 * @param gameCode A 3 digit positive
	 * integer.
	 */
	public final void setupGame(final int gameCode, final boolean animate) {
		
		this.gameCode = gameCode;
		this.totalClicks = 0;
		
		// create a random number generator
		// and calculate the total number
		// of 'clicks' that we will use
		// to set up the game:
		Random rand = new Random(gameCode);
		par = gameCode/4 + 1;
		if(par > 50) { par = 50; }
		
		// clear the board first
		clear(animate);
		
		// do a series of random clicks
		for(int i=0; i<par; i++) {
			toggle(rand.nextInt(SquareGrid.GRID_LENGTH), rand.nextInt(GRID_LENGTH), animate);
		}
	}

	/**
	 * toggles the cell at the given index,
	 * as well as its 4 neighbors.
	 * @param i the i index of the cell
	 * @param j the j index of the cell
	 */
	protected abstract void toggle(int i, int j, boolean animate);

	/**
	 * clears the board.
	 */
	protected final void clear(final boolean animate) {
		setAll(false, animate);
	}
	
	/* package private */
	final void setAll(boolean on, boolean animate) {
		count = (on)? GRID_LENGTH * GRID_LENGTH : 0;
		for(int i=0; i < GRID_LENGTH; i++) {
			for(int j=0; j < GRID_LENGTH; j++) {
				grid[i][j].set(on, animate);
			}
		}
	}
	
	/**
	 * 
	 * @param canvas
	 * @return
	 */
	protected abstract boolean draw(Canvas canvas);
	
	/**
	 * Checks if the board has any lights turned on.
	 * @return true if there are no lights on.
	 */
	public final boolean isEmpty() {
		return this.count == 0;
	}
	
}