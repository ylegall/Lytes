package org.ygl.lytes;

/**
 * 
 * @author ylegall
 *
 * Container class for game data.
 */
public class SessionData {
	int gridType;
	int difficulty;
	int currentLevel;
	int clicks; // current number of user clicks
	// TODO: store the configuration of the grid
	//int score;
	
	/**
	 * creates a SessionData object with default values:
	 * square grid, medium difficulty, level 1.
	 */
	public SessionData() {
		gridType = Grid.GRID_TYPE_SQAURE;
		difficulty = Grid.DIFFICULTY_MED;
		currentLevel = 1;
	}
}
