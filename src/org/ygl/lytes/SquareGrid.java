package org.ygl.lytes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class SquareGrid extends Grid {
	
	private final int MAX_NEIGHBORS = 4;
	
	public SquareGrid(final int gridLength) {
		super(gridLength);
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
		//int tile_size = Grid.TILE_SIZE;
		
		pos.x = i*TILE_SIZE;
		pos.y = j*TILE_SIZE;
	}
	
	protected boolean touchTile(int mouse_x, int mouse_y)
	{
		mouse_x /= TILE_SIZE;
		mouse_y /= TILE_SIZE;
		
		if (mouse_x < GRID_LENGTH && mouse_y < GRID_LENGTH) {
			toggle(mouse_x, mouse_y, true);
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean draw(Canvas canvas) {
    	boolean redraw = false;
    	Rect tileRect = new Rect(0, 0, TILE_SIZE, TILE_SIZE);
    	Point pos = new Point();
        
        for (int y = 0 ; y < GRID_LENGTH; y++) {
            for (int x = 0 ; x < GRID_LENGTH; x++) {
            	this.getTilePos(x, y, pos);
            	tileRect.offsetTo(pos.x, pos.y);
            	
            	if(grid[y][x].isAnimating()) {
            		// offImage is always draw during animation to provide a background.
            		canvas.drawBitmap(offImage, null, tileRect, fullPaint);
            		
            		// draw onImage with opacity defined by the tiles alpha over top.
            		partPaint.setAlpha(grid[y][x].alpha);
            		canvas.drawBitmap(onImage, null, tileRect, partPaint);
            		
            		// Now update the alpha based on the destination state
            		// Increasing the number passed to the update will increase
            		// the speed of the animation.
            		if (grid[y][x].update(ANIM_SPEED)) {
            			redraw = true;
            		}
            		
            	}
            	else if(grid[y][x].state) {
            		canvas.drawBitmap(onImage, null, tileRect, fullPaint);
            	}
            	else {
            		canvas.drawBitmap(offImage, null, tileRect, fullPaint);
            	}
            }
        }
        
        return redraw;
	}
}
