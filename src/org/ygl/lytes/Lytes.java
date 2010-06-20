package org.ygl.lytes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

public class Lytes extends Activity implements View.OnClickListener {
	
	static SessionData sessionData;
	static Grid grid;
	public static final String ICICLE_KEY = "lytes";
	public static final int INVALID_GAME_CODE = 0;
	// public static final int NEW_GAME_DIALOG = 1;
	
	// highest level data:
	private static int highestSquareEasy = 1;
	private static int highestSquareMed = 1;
	private static int highestSquareHard = 1;
	private static int highestHexEasy = 1;
	private static int highestHexMed = 1;
	private static int highestHexHard = 1;

	/**
	 * Called when the activity is first created. 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        grid = new SquareGrid(5);
        //grid = new HexGrid(5);
        sessionData = new SessionData();
        changeContentView(R.layout.main);
    }
    
    // changes the content view between 2 options:
    // 1. the main menu
    // 2. the game screen
    private final void changeContentView(int id) {
    	switch(id) {
    		case R.layout.game:
        		this.setContentView(id);
    	        findViewById(R.id.backButton).setOnClickListener(this);
    	        findViewById(R.id.resetButton).setOnClickListener(this);
    	        break;
    		case R.layout.main:
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
    			break;
    		case R.layout.new_game_form:
    			this.setContentView(id);
    			findViewById(R.id.newGameOK).setOnClickListener(this);
    			findViewById(R.id.newGameCancel).setOnClickListener(this);
    			break;
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
				
				//TODO: only allow user to chose a game that they have completed?

				grid.setupGame(gameCode, false);
				changeContentView(R.layout.game);
				((TextView)findViewById(R.id.parLabel)).setText("Par "+grid.par);
				((TextView)findViewById(R.id.levelLabel)).setText("Level "+grid.gameCode);
				break;
				
			case R.id.newGameButton:
				changeContentView(R.layout.new_game_form);
//				grid.setupGame(1, false);
//				// start a new game:
//				changeContentView(R.layout.game);
//				((TextView)findViewById(R.id.levelLabel)).setText("Level 0");
				break;
				
			case R.id.continueButton:
//		        SharedPreferences prefs = getPreferences(Activity.MODE_PRIVATE); 
//		        gameCode = prefs.getInt("highestLevel", INVALID_GAME_CODE);
				changeContentView(R.layout.game);
				loadGame(sessionData.currentLevel);
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
			case R.id.newGameCancel:
				changeContentView(R.layout.main);
				break;
			case R.id.newGameOK:
				
				RatingBar ratingBar = (RatingBar)findViewById(R.id.difficulty);
				sessionData.currentLevel = 1;
				sessionData.difficulty = (int)(ratingBar.getRating()) + 3;
				RadioButton hexButton = (RadioButton)findViewById(R.id.radio_hex);
				if(hexButton.isChecked()) {
					sessionData.gridType = Grid.GRID_TYPE_HEX;
					grid = new HexGrid(sessionData.difficulty);
				} else {
					sessionData.gridType = Grid.GRID_TYPE_SQAURE;
					grid = new SquareGrid(sessionData.difficulty);
				}
				
				changeContentView(R.layout.game);
				loadGame(sessionData.currentLevel);
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
		if(sessionData.gridType == Grid.GRID_TYPE_HEX) {
			switch(sessionData.difficulty) {
				case Grid.DIFFICULTY_EASY:
					if(level > highestHexEasy) { highestHexEasy = level; }
				case Grid.DIFFICULTY_MED:
					if(level > highestHexMed) { highestHexMed = level; }
				case Grid.DIFFICULTY_HARD:
					if(level > highestHexHard) { highestHexHard = level; }
			}
		} else {
			switch(sessionData.difficulty) {
				case Grid.DIFFICULTY_EASY:
					if(level > highestSquareEasy) { highestSquareEasy = level; }
				case Grid.DIFFICULTY_MED:
					if(level > highestSquareMed) { highestSquareMed = level; }
				case Grid.DIFFICULTY_HARD:
					if(level > highestSquareHard) { highestSquareHard = level; }
			}
		}
	}
	
    /**
     * Upon being resumed we can retrieve the current state. This allows us
     * to update the state if it was changed at any time while paused.
     * It's also called when the activity is started.
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(Activity.MODE_PRIVATE); 

        highestSquareEasy = prefs.getInt("highestSquareEasy", INVALID_GAME_CODE);
        highestSquareMed = prefs.getInt("highestSquareMed", INVALID_GAME_CODE);
        highestSquareHard = prefs.getInt("highestSquareHard", INVALID_GAME_CODE);
        highestHexEasy = prefs.getInt("highestHexEasy", INVALID_GAME_CODE);
        highestHexMed = prefs.getInt("highestHexMed", INVALID_GAME_CODE);
        highestHexHard = prefs.getInt("highestHexHard", INVALID_GAME_CODE);
        
        sessionData.currentLevel = prefs.getInt("currentLevel", INVALID_GAME_CODE);
        sessionData.gridType = prefs.getInt("gridType", Grid.GRID_TYPE_SQAURE);
        sessionData.difficulty = prefs.getInt("difficulty", Grid.DIFFICULTY_MED);
        
        if(sessionData.currentLevel != INVALID_GAME_CODE) {
        	changeContentView(R.layout.game);
        	loadGame(sessionData.currentLevel);
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
        editor.putInt("highestSquareEasy", highestSquareEasy);
        editor.putInt("highestSquareMed", highestSquareMed);
        editor.putInt("highestSquareHard", highestSquareHard);
        editor.putInt("highestHexEasy", highestHexEasy);
        editor.putInt("highestHexMed", highestHexMed);
        editor.putInt("highestHexHard", highestHexHard);
        
        editor.putInt("currentLevel", sessionData.currentLevel);
        editor.putInt("gridType", sessionData.gridType);
        editor.putInt("difficulty", sessionData.difficulty);
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
		}
		//builder.setCancelable(false);
		return builder.create();
	}
}