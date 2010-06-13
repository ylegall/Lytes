package org.ygl.lytes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class Lytes extends Activity implements View.OnClickListener {
	
	static Grid grid;
	public static final String ICICLE_KEY = "lytes";
	public static final int INVALID_GAME_CODE = 0;
	public static final int NEW_GAME_DIALOG = 1;
	
	private static int highestLevel = 1;

	/**
	 * Called when the activity is first created. 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //grid = new SquareGrid(5);
        grid = new HexGrid(5);
        changeContentView(R.layout.main);
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
            findViewById(R.id.exitGameButton).setOnClickListener(this);
    		
            // Only show the continue game button when a game is in progess.
    		Button contGame = (Button)findViewById(R.id.continueButton);
    		contGame.setOnClickListener(this);
    		if(grid.gameCode != INVALID_GAME_CODE) {
    			contGame.setVisibility(View.VISIBLE);
    		}
    		else {
    			contGame.setVisibility(View.GONE);
    		}
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
				
				// try to parse it as an int:
				try {
					gameCode = Integer.parseInt(text);
				} catch (NumberFormatException nfe) {
					showDialog(INVALID_GAME_CODE);
					return;
				}
				
				// check if it's between 0 and 999:
				if(gameCode < 1) {
					showDialog(INVALID_GAME_CODE);
					return;
				} else if(gameCode > 999) {
					showDialog(INVALID_GAME_CODE);
					return;
				}
				
				// Force the soft keyboard to hide when game starts.
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(textField.getWindowToken(), 0);

				grid.setupGame(gameCode, false);
				changeContentView(R.layout.game);
				((TextView)findViewById(R.id.parLabel)).setText("Par "+grid.par);
				((TextView)findViewById(R.id.levelLabel)).setText("Level "+grid.gameCode);
				break;
				
			case R.id.newGameButton:
				grid.setupGame(1, false);
				// start a new game:
				changeContentView(R.layout.game);
				((TextView)findViewById(R.id.levelLabel)).setText("Level 0");
				break;
				
			case R.id.continueButton:
//		        SharedPreferences prefs = getPreferences(Activity.MODE_PRIVATE); 
//		        gameCode = prefs.getInt("highestLevel", INVALID_GAME_CODE);
				changeContentView(R.layout.game);
				loadGame(highestLevel);
				break;
				
			case R.id.exitGameButton:
				finish();
				break;
				
			case R.id.resetButton:
				loadGame(grid.gameCode);
				break;
				
			case R.id.backButton:
				changeContentView(R.layout.main);
				break;
		}
	}
	
	/**
	 * Loads a particular game (level). 
	 * Called from the game layout.
	 * @param gameCode The ID of the game to load.
	 */
	final void loadGame(final int gameCode) {
		grid.setupGame(gameCode, true);
		TextView tv = (TextView)findViewById(R.id.clicksLabel);
		tv.setTextColor(Color.WHITE);
		tv.setText("Clicks 0");
		((TextView)findViewById(R.id.parLabel)).setText("Par "+grid.par);
		((TextView)findViewById(R.id.levelLabel)).setText("Level "+gameCode);
		findViewById(R.id.lytesGridView).invalidate();
	}
	
	final static void setHighestLevel(final int level) {
		if(level > highestLevel) {
			highestLevel = level;
		}
	}
	
    /**
     * Upon being resumed we can retrieve the current state. This allows us
     * to update the state if it was changed at any time while paused. 
     * I tihnk this is also called when the activity is started.
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(Activity.MODE_PRIVATE); 
        highestLevel = prefs.getInt("highestLevel", INVALID_GAME_CODE);
        if(highestLevel != INVALID_GAME_CODE) {
        	changeContentView(R.layout.game);
        	loadGame(highestLevel);
        }
    }

    /**
     * Any time we are paused we need to save away the current state, so it
     * will be restored correctly when we are resumed. I tihnk this is also
     * called when the activity is closed.
     */
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getPreferences(Activity.MODE_PRIVATE).edit();
        editor.putInt("highestLevel", highestLevel);
        editor.commit();
    }
	
	/**
	 * Creates and shows different message dialogs based on
	 * a static integer code. Invoked by calling <code>showDialog(int)</code>.
	 * 
	 * can show a newGameDialog
	 */
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
			case NEW_GAME_DIALOG:
				break;
		}
		builder.setCancelable(false);
		return builder.create();
	}
}