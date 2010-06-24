package org.ygl.lytes;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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

	Grid grid;
	
	public LytesGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
		grid = Lytes.grid;
		
		grid.loadImages(context);
	}
	
	@Override
	public void onMeasure(final int width, final int height) {
		int size = Math.min(View.MeasureSpec.getSize(width), View.MeasureSpec.getSize(height));
		//TILE_SIZE = size / SquareGrid.GRID_LENGTH;
		Grid.TILE_SIZE = size / Grid.GRID_LENGTH;
		setMeasuredDimension(size, size);
	}

	
    @Override
    public void onDraw(Canvas canvas) {

    	if(grid.draw(canvas)) {
    		invalidate();
    	}

    }
    
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			int x = (int)event.getX();
			int y = (int)event.getY();
//			x -= XOFFSET;
//			y -= YOFFSET;
			
			if(!grid.touchTile(x, y)) {
				// no tile touched
				return true;
			}
			
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
				
				String winText = String.format("level %d completed!", grid.gameCode);
				Toast toast = Toast.makeText(context, winText, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
				
				// show the next board and update the lables:
				Lytes.sessionData.currentLevel = grid.gameCode + 1;
				Lytes.setHighestLevel(grid.gameCode + 1);
				((Lytes)context).loadGame(grid.gameCode + 1);
			}
		}
		
		// always return true since we will consume the event:
		return true;
	}

}
