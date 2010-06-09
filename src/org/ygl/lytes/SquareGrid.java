package org.ygl.lytes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class SquareGrid extends Grid {
	
	private final int MAX_NEIGHBORS = 4;
	
	public SquareGrid(final int gridLength) {
		GRID_LENGTH = gridLength;
		// create the matrix:
		grid = new Tile[GRID_LENGTH][GRID_LENGTH];
		for (int j=0; j<GRID_LENGTH; j++) {
			grid[j] = new Tile[GRID_LENGTH];
			for (int i=0; i<GRID_LENGTH; i++)
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
		Drawable d = r.getDrawable(R.drawable.on);
		onImage = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(onImage);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        d.draw(canvas);
        
		d = r.getDrawable(R.drawable.off);
		offImage = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(offImage);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        d.draw(canvas);
	}
	
	/**
	 * Returns a list of all neighboring tiles
	 */
	protected Tile[] getNeighborTiles(int i, int j)
	{
		// Sanity checks
		if (j < 0 || j >= grid.length || i < 0 || i >= grid[j].length)
			return null;
		
		// Always allocate list for max neighbors. If tile is invaild, list entries will be null.
		Tile[] neighbors = new Tile[MAX_NEIGHBORS];
		int neighIdx = 0;
		int max = GRID_LENGTH-1;
		
		// up
		if(j > 0) {
			neighbors[neighIdx++] = grid[j-1][i];
		}
		
		// down
		if(j < max) {
			neighbors[neighIdx++] = grid[j+1][i];
		}
		
		// left
		if(i > 0) {
			neighbors[neighIdx++] = grid[j][i-1];
		}
		
		// right
		if(i < max) {
			neighbors[neighIdx++] = grid[j][i+1];
		}
		
		// Nullify any remaining neighbors
		for(; neighIdx < MAX_NEIGHBORS; neighIdx++)
			neighbors[neighIdx] = null;
		
		return neighbors;
	}
	
	public void getTilePos(int i, int j, Point pos)
	{
		// TODO: Should these constants location be changed?
		int tile_size = LytesGridView.TILE_SIZE;
		
		pos.x = i*tile_size;
		pos.y = j*tile_size;
	}
	
	protected boolean touchTile(int mouse_x, int mouse_y)
	{
		mouse_x /= LytesGridView.TILE_SIZE;
		mouse_y /= LytesGridView.TILE_SIZE;
		
		if (mouse_x < GRID_LENGTH && mouse_y < GRID_LENGTH) {
			toggle(mouse_x, mouse_y, true);
			return true;
		}
		
		return false;
	}
}
