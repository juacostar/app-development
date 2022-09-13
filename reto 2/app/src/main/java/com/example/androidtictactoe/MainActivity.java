package com.example.androidtictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    private static final int DIALOG_ABOUT_ID = 2;

    private TicTacToeGame mGame;
    // Buttons making up the board
    //Game over variable
    private boolean mGameOver;
    private Button mBoardButtons[];
    // Various text displayed
    private TextView mInfoTextView;
    private BoardView mBoardView;
    private int mResults[];
    private SharedPreferences mPrefs;

    // Results text displayed
    private TextView mHumanWinTextView;
    private TextView mTieWinTextView;
    private TextView mAndroidWinTextView;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    MediaPlayer mWin;

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.xsound2);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.osound);
        mWin = MediaPlayer.create(getApplicationContext(), R.raw.win);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
        mWin.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
// Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mResults[0]);
        ed.putInt("mComputerWins", mResults[2]);
        ed.putInt("mTies", mResults[1]);
        ed.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        mInfoTextView = (TextView) findViewById(R.id.information);
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mHumanWinTextView = (TextView) findViewById(R.id.humanR);
        mTieWinTextView = (TextView) findViewById(R.id.tieR);
        mAndroidWinTextView = (TextView) findViewById(R.id.androidR);
        mBoardView.setGame(mGame);
        mResults = new int[3];
        for(int i=0;i<3;i++){
            mResults[i] = 0;
        }
        // Restore the scores
        mResults[0] = mPrefs.getInt("mHumanWins", 0);
        mResults[2] = mPrefs.getInt("mComputerWins", 0);
        mResults[1] = mPrefs.getInt("mTies", 0);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        if (savedInstanceState == null) {
            startNewGame();
        }
        else {
            // Restore the game's state
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mResults[0] = savedInstanceState.getInt("mHumanWins");
            mResults[2] = savedInstanceState.getInt("mComputerWins");
            mResults[1] = savedInstanceState.getInt("mTies");
//            mHTurn = savedInstanceState.getBoolean("mHTurn");
        }
        displayScores();

    }

    private void displayScores() {
        mHumanWinTextView.setText("Human: " + (mResults[0]));
        mTieWinTextView.setText("Tie: " + (mResults[1]));
        mAndroidWinTextView.setText("Android: " + (mResults[2]));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mHumanWins", mResults[0]);
        outState.putInt("mComputerWins", mResults[2]);
        outState.putInt("mTies", mResults[1]);
        outState.putCharSequence("info", mInfoTextView.getText());
//        outState.putBoolean("mHTurn", mHTurn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // selected is the radio button that should be selected.
                int selected = 2;
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
                                // TODO: Set the diff level of mGame based on which item was selected.
                                // Display the selected difficulty level
                                if (item == 0)
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                else if (item == 1)
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                else
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                mGame.getDifficultyLevel();
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }



        // Set up the game board.
        private void startNewGame () {
            mGame.clearBoard();
            mBoardView.invalidate(); // Redraw the board
            mGameOver = false;
            // Human goes first
       /* mGameOver = false;
        boolean t = mGameStartsH.nextBoolean();
        if(!mGameStartsH.nextBoolean()){
            mInfoTextView.setText(R.string.turn_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mComputerMediaPlayer.start();
            mInfoTextView.setText(R.string.first_computer);
        }else{
            mInfoTextView.setText(R.string.first_human);
        }*/
        }

        // Handles clicks on the game board buttons
        private class ButtonClickListener implements View.OnClickListener {
            int location;

            public ButtonClickListener(int location) {
                this.location = location;
            }

            public void onClick(View view) {
                if (mBoardButtons[location].isEnabled()) {
                    setMove(TicTacToeGame.HUMAN_PLAYER, location);
// If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        mInfoTextView.setText("It's Android's turn.");
                        int move = mGame.getComputerMove();
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        winner = mGame.checkForWinner();
                    }
                    if (winner == 0)
                        mInfoTextView.setText("It's your turn.");
                    else if (winner == 1)
                        mInfoTextView.setText("It's a tie!");
                    else if (winner == 2)
                        mInfoTextView.setText(R.string.result_human_wins);
                    else
                        mInfoTextView.setText("Android won!");
                }
            }
        }

        private boolean setMove ( char player, int location){
            if (mGame.setMove(player, location)) {
                mBoardView.invalidate(); // Redraw the board
                return true;
            }
            return false;
        }

        // Listen for touches on the board
        View.OnTouchListener mTouchListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // Determine which cell was touched
                int col = (int) event.getX() / mBoardView.getBoardCellWidth();
                int row = (int) event.getY() / mBoardView.getBoardCellHeight();
                int pos = row * 3 + col;
                if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)){
                    setMove(TicTacToeGame.HUMAN_PLAYER, pos);
                    mHumanMediaPlayer.start();
                    // If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        mInfoTextView.setText(R.string.turn_computer);
                        int move = mGame.getComputerMove();
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        mComputerMediaPlayer.start();
                        winner = mGame.checkForWinner();
                    }
                    if (winner == 0)
                        mInfoTextView.setText(R.string.turn_human);
                    else if (winner == 1) {
                        mResults[1] = mResults[1] + 1;
                        mTieWinTextView.setText("Tie: " + (mResults[1]));
                        mInfoTextView.setText(R.string.result_tie);
                        mGameOver = true;
                    }else if (winner == 2){
                        mResults[0] = mResults[0] + 1;
                        mHumanWinTextView.setText("Human: " + (mResults[0]));
                        mInfoTextView.setText(R.string.result_human_wins);
                        mWin.start();
                        mGameOver = true;
                    }else{
                        mResults[2] = mResults[2] + 1;
                        mAndroidWinTextView.setText("Android: " + (mResults[2]));
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mGameOver = true;
                    }
                }
                // So we aren't notified of continued events when finger is moved
                return false;
            }
        };
}