package org.ygl.lytes;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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

	Grid grid;

	private Paint fullPaint, partPaint;
	private Bitmap onImage, offImage;
	static int TILE_SIZE = 64;
	static int XOFFSET = 0; //40;
	static int YOFFSET = 0; //80;
	static final String tag = "LYTES"; // TODO: remove
	static int ANIM_SPEED = 50;

//	private Canvas offscreenCanvas;
//	private Bitmap offscreenImage;
	
	//static final String tag = "LYTES"; // TODO: remove
	
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
		onImage = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(onImage);
        d.setBounds(0, 0, TILE_SIZE, TILE_SIZE);
        d.draw(canvas);
        
		d = r.getDrawable(R.drawable.off);
		offImage = Bitmap.createBitmap(TILE_SIZE, TILE_SIZE, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(offImage);
        d.setBounds(0, 0, TILE_SIZE, TILE_SIZE);
        d.draw(canvas);
	}
	
	@Override
	public void onMeasure(final int width, final int height) {
		int size = Math.min(View.MeasureSpec.getSize(width), View.MeasureSpec.getSize(height));
		TILE_SIZE = size / Grid.GRID_LENGTH;
		setMeasuredDimension(size, size);
		
//        offscreenCanvas = new Canvas();
//        offscreenImage = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
//        offscreenCanvas.setBitmap(offscreenImage);
	}
	
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        if (offscreenImage != null) {
//        	offscreenImage .recycle();
//        }
//        offscreenCanvas = new Canvas();
//        offscreenImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        offscreenCanvas.setBitmap(offscreenImage);
//    }
//    
//    public void destroy() {
//        if (offscreenImage != null) {
//        	offscreenImage.recycle();
//        }
//    }
	
    @Override
    public void onDraw(Canvas canvas) {

    	boolean redraw = false;
        
        for (int x = 0 ; x < Grid.GRID_LENGTH; x++) {
        	float left = XOFFSET + x*TILE_SIZE;
            for (int y = 0 ; y < Grid.GRID_LENGTH; y++) {           	
            	float top = YOFFSET + y*TILE_SIZE;
            	
            	if(grid.grid[y][x].isAnimating()) {
            		// offImage is always draw during animation to provide a background.
            		canvas.drawBitmap(offImage, left, top, fullPaint);
            		
            		// draw onImage with opacity defined by the tiles alpha over top.
            		partPaint.setAlpha(grid.grid[y][x].alpha);
            		canvas.drawBitmap(onImage, left, top, partPaint);
            		
            		// Now update the alpha based on the destination state
            		// Increasing the number passed to the update will increase
            		// the speed of the animation.
            		if (grid.grid[y][x].update(ANIM_SPEED))
            			redraw = true;
            		
            	}
            	else if(grid.grid[y][x].state) {
            		canvas.drawBitmap(onImage, left, top, fullPaint);
            	}
            	else {
            		canvas.drawBitmap(offImage, left, top, fullPaint);
            	}
            }
        }
        
        // Immediately invalidate the view if a redraw is needed.
        if(redraw)
        {
        	// Draw a visible indication of redraw for debug purposes.
        	canvas.drawRect(0, 0, 10, 10, fullPaint);
        	
        	invalidate();
        }

		//canvas.drawBitmap(offscreenImage, 0, 0, paint);
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

			//paint.setAlpha(255);
			this.invalidate();
			
			// animation loop
//			for(double i=0; i < 1; i += 0.01) {
//				paint.setAlpha((int)(255*i));
//				this.invalidate();
//			}
			
			Activity lytesActivity = ((Activity)this.getContext());
			TextView tv = ((TextView)lytesActivity.findViewById(R.id.clicksLabel));
			tv.setText("Clicks " + grid.totalClicks);
			if(grid.totalClicks > grid.par) {
				tv.setTextColor(Color.RED);
			}
			
			// check for win:
			if(grid.isEmpty()) {
				
				Context context = this.getContext();
				Toast.makeText(context, "you win!", Toast.LENGTH_SHORT).show();
				
				// show the next board and update the lables:
				((Lytes)context).loadGame(grid.gameCode + 1);
			}
		}
		
		// always return true since we will consume the event:
		return true;
	}

}
