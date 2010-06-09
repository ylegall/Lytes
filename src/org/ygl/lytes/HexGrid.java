package org.ygl.lytes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class HexGrid extends Grid {
	

	public HexGrid(final int gridLength) {
		super(gridLength);
	}
	
	@Override
	protected void toggle(int i, int j, boolean animate) {
		// TODO Auto-generated method stub
	}
	
	private final void loadImages(Resources r) {
		
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
	
	@Override
	protected boolean draw(Canvas canvas) {
    	boolean redraw = false;
    	Rect tileRect = new Rect(0, 0, TILE_SIZE, TILE_SIZE);
        
        return redraw;
	}

}
