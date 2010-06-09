
package org.ygl.lytes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class HexGrid extends Grid {
	
	private static final int MAX_NEIGHBORS = 6;
	
	public HexGrid(final int gridLength) {
		GRID_LENGTH = gridLength;
		// create the matrix:
		grid = new Tile[GRID_LENGTH][];
		int altGridLen = GRID_LENGTH-1;
		for (int j=0; j<GRID_LENGTH; j++) {
			// Alternate between GRID_LENGTH and GRID_LENGTH-1 for hex tiles.
			grid[j] = new Tile[altGridLen + j%2];
			for (int i=0; i<(altGridLen + j%2); i++)
				grid[j][i] = new Tile();
		}
		gameCode = 1;
	}
	
	/**
	 * Loads tile images relevant to this grid.
	 * @param context
	 */
	public void loadImages(Context context)
	{
		Resources r = context.getResources();
		Drawable d = r.getDrawable(R.drawable.hex_on);
		onImage = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(onImage);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        d.draw(canvas);
        
		d = r.getDrawable(R.drawable.hex_off);
		offImage = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(offImage);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        d.draw(canvas);
        
	}
	@Override
	protected Tile[] getNeighborTiles(int i, int j) {
		// Sanity checks
		if (j < 0 || j >= grid.length || i < 0 || i >= grid[j].length)
			return null;
		
		Tile[] neighbors = new Tile[MAX_NEIGHBORS];
		int neighIdx = 0;
		int jig = (j%2) == 0 ? 1 : -1;
		int new_i = i + jig;
		
		// Left
		if (i > 0) {
			neighbors[neighIdx++] = grid[j][i-1];
		}
		
		// Right
		if (i < grid[j].length-1) {
			neighbors[neighIdx++] = grid[j][i+1];
		}
		
		// Tops
		if (j > 0) {
			
			if (i < grid[j-1].length) {
				neighbors[neighIdx++] = grid[j-1][i];
			}
			
			if (new_i >= 0 && new_i < grid[j-1].length) {
				neighbors[neighIdx++] = grid[j-1][new_i];
			}
		}
		
		// Bottoms
		if (j < grid.length-1) {
			
			if (i < grid[j+1].length) {
				neighbors[neighIdx++] = grid[j+1][i];
			}
			
			if (new_i >= 0 && new_i < grid[j+1].length) {
				neighbors[neighIdx++] = grid[j+1][new_i];
			}
		}
		
		return neighbors;
	}
	
	public void getTilePos(int i, int j, Point pos)
	{
		// TODO: move TILE_SIZE to Grid.java
		// TODO: dynamically determine at load time
		int tile_size = 55; 
		
		int jig = (j%2) == 0 ? tile_size/2 : 0;
		
		pos.x = 10 + i*tile_size + jig;
		pos.y = 0 + j*(tile_size - 5);
	}
	
	protected boolean touchTile(int mouse_x, int mouse_y)
	{
		// TODO: move TILE_SIZE to Grid.java
		// TODO: dynamically determine at load time
		int tile_size = 55;
		
		mouse_y /= tile_size;
		
		if (mouse_y >= GRID_LENGTH) {
			return false;
		}
		
		int jig = (mouse_y%2) == 0 ? tile_size/2 : 0;
		mouse_x -= jig;
		if (mouse_x < 0) {
			return false;
		}
			
		mouse_x /= tile_size;
		if (mouse_x < grid[mouse_y].length) {
			toggle(mouse_x, mouse_y, true);
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 */
	@Override
	protected boolean draw(Canvas canvas) {
		// TODO: implement this!
    	boolean redraw = false;
    	Rect tileRect = new Rect(0, 0, TILE_SIZE, TILE_SIZE);
        
        return redraw;
	}
}
