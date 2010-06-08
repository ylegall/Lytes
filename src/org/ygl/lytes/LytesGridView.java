package org.ygl.lytes;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author ylegall
 * TODO: remove log statements when done debugging
 */
public class LytesGridView extends View implements View.OnTouchListener {

	SquareGrid grid;

	private Paint fullPaint, partPaint;
	private Bitmap onImage, offImage;
	static int TILE_SIZE = 64;
	static int XOFFSET = 0; //40;
	static int YOFFSET = 0; //80;
	static final String tag = "LYTES"; // TODO: remove
	static int ANIM_SPEED = 25;
	
	public LytesGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
		grid = Lytes.grid;
		
		// used for drawing:
		fullPaint = new Paint();
		partPaint = new Paint();
		
		loadImages();
	}
	
	private final void loadImages() {
		
		Resources r = this.getContext().getResources();
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
	public void onMeasure(final int width, final int height) {
		int size = Math.min(View.MeasureSpec.getSize(width), View.MeasureSpec.getSize(height));
		TILE_SIZE = size / SquareGrid.GRID_LENGTH;
		setMeasuredDimension(size, size);
	}

	
    @Override
    public void onDraw(Canvas canvas) {

    	boolean redraw = false;
    	Rect tileRect = new Rect(0, 0, TILE_SIZE, TILE_SIZE);
        
        for (int x = 0 ; x < SquareGrid.GRID_LENGTH; x++) {
        	int left = XOFFSET + x*TILE_SIZE;
            for (int y = 0 ; y < SquareGrid.GRID_LENGTH; y++) {           	
            	int top = YOFFSET + y*TILE_SIZE;
            	tileRect.offsetTo(left, top);
            	
            	if(grid.grid[y][x].isAnimating()) {
            		// offImage is always draw during animation to provide a background.
            		canvas.drawBitmap(offImage, null, tileRect, fullPaint);
            		
            		// draw onImage with opacity defined by the tiles alpha over top.
            		partPaint.setAlpha(grid.grid[y][x].alpha);
            		canvas.drawBitmap(onImage, null, tileRect, partPaint);
            		
            		// Now update the alpha based on the destination state
            		// Increasing the number passed to the update will increase
            		// the speed of the animation.
            		if (grid.grid[y][x].update(ANIM_SPEED)) {
            			redraw = true;
            		}
            		
            	}
            	else if(grid.grid[y][x].state) {
            		canvas.drawBitmap(onImage, null, tileRect, fullPaint);
            	}
            	else {
            		canvas.drawBitmap(offImage, null, tileRect, fullPaint);
            	}
            }
        }
        
        // Immediately invalidate the view if a redraw is needed.
        if(redraw)
        {
        	// Draw a visible indication of redraw for debug purposes.
        	//canvas.drawRect(0, 0, 10, 10, fullPaint);        	
        	invalidate();
        }

    }
    
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			int x = (int)event.getX();
			int y = (int)event.getY();
			x -= XOFFSET;
			y -= YOFFSET;
			x = x/TILE_SIZE;
			y = y/TILE_SIZE;

			grid.click(y, x);
			this.invalidate();
			
			Activity lytesActivity = ((Activity)this.getContext());
			TextView textView = ((TextView)lytesActivity.findViewById(R.id.clicksLabel));
			textView.setText("Clicks " + grid.totalClicks);
			if(grid.totalClicks > grid.par) {
				textView.setTextColor(Color.RED);
			}
			
			// check for win:
			if(grid.isEmpty()) {
				
				Context context = this.getContext();
				
				grid.setAll(false, true);
				invalidate();
//				grid.setAll(true, true);
//				invalidate();
				
				String winText = String.format("level %d completed!", grid.gameCode);
				Toast toast = Toast.makeText(context, winText, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
				
				// show the next board and update the lables:
				Lytes.setHighestLevel(grid.gameCode + 1);
				((Lytes)context).loadGame(grid.gameCode + 1);
			}
		}
		
		// always return true since we will consume the event:
		return true;
	}

}
