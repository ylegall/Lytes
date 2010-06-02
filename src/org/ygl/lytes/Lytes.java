package org.ygl.lytes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Lytes extends Activity implements View.OnClickListener {
	
	static Grid grid;
	public static String ICICLE_KEY = "lytes";
	public static final int INVALID_GAME_CODE = 0;
	public static final int WIN_GAME = 1;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeContentView(R.layout.main);
        grid = new Grid(5);
    }
    
    // changes the content view between 2 options:
    // 1. the main menu
    // 2. the game screen
    private final void changeContentView(int id) {
    	if(id == R.layout.game) {
    		this.setContentView(id);
	        findViewById(R.id.backButton).setOnClickListener(this);
	        findViewById(R.id.resetButton).setOnClickListener(this);
    	} else if(id == R.layout.main) {
    		this.setContentView(R.layout.main);
            findViewById(R.id.newGameButton).setOnClickListener(this);
            findViewById(R.id.selectGameButton).setOnClickListener(this);
    	}
    }
    
	@Override
	public void onClick(View view) {
		
		// check if the select button pressed:
		switch(view.getId()) {
			case R.id.selectGameButton:
				
				// get the game code in the textView:
				int gameCode = 1;
				TextView textField = (TextView) findViewById(R.id.selectTextField);
				String text = textField.getText().toString();
				
				try {
					gameCode = Integer.parseInt(text);
				} catch (NumberFormatException nfe) {
					showDialog(INVALID_GAME_CODE);
					return;
				}
				
				// should be between 0 and 999:
				if(gameCode < 1) {
					showDialog(INVALID_GAME_CODE);
					return;
				} else if(gameCode > 999) {
					showDialog(INVALID_GAME_CODE);
					return;
				}
				
				grid.setupGame(gameCode);
				changeContentView(R.layout.game);
				((TextView)findViewById(R.id.parLabel)).setText("Par "+grid.par);
				((TextView)findViewById(R.id.levelLabel)).setText("Level "+grid.gameCode);
				break;
				
			case R.id.newGameButton:
				grid.setupGame(1);
				// start a new game:
				changeContentView(R.layout.game);
				((TextView)findViewById(R.id.levelLabel)).setText("Level 0");
				break;
				
			case R.id.resetButton:
				loadGame(grid.gameCode);
				break;
				
			case R.id.backButton:
				changeContentView(R.layout.main);
				break;
			default:
					// TODO: error code here
		}
	}
	
	public final void loadGame(int gameCode) {
		grid.setupGame(gameCode);
		TextView tv = (TextView)findViewById(R.id.clicksLabel);
		tv.setTextColor(Color.WHITE);
		tv.setText("Clicks 0");
		((TextView)findViewById(R.id.parLabel)).setText("Par "+grid.par);
		((TextView)findViewById(R.id.levelLabel)).setText("Level "+gameCode);
		findViewById(R.id.lytesGridView).invalidate();
	}
	
	@Override
	protected Dialog onCreateDialog(int dialogID) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch(dialogID) {
			case INVALID_GAME_CODE:
				builder.setMessage("Invalid game code.\nPlease enter an integer between 1 and 999.");
			    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.cancel();
			           }
			       });
				break;
				
			case WIN_GAME:
				builder.setMessage("You win!.\nContinue?");
				builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//TODO: implemente this
							dialog.cancel();
						}
					});
				builder.setPositiveButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//TODO: implemente this
							dialog.cancel();
						}
					});
				break;
		}
		builder.setCancelable(false);
		return builder.create();
	}
}