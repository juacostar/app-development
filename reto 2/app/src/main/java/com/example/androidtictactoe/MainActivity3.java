package com.example.androidtictactoe;

import androidx.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity3 extends AppCompatActivity {

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
    private int mResults;
    private SharedPreferences mPrefs;

    // Results text displayed
    private TextView mHumanWinTextView;
    private TextView mTieWinTextView;
    private TextView mAndroidWinTextView;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    MediaPlayer mWin;
    private boolean mHTurn;
    String playerName;
    String roomName;
    String role;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference boardReference;
    DatabaseReference winPRef;
    DatabaseReference turnRef;
    DatabaseReference msgRef;
    DatabaseReference resRef;

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
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mResults", mResults);
        ed.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        setContentView(R.layout.activity_main3);
        mInfoTextView = (TextView) findViewById(R.id.information);
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mHumanWinTextView = (TextView) findViewById(R.id.humanR);
        mTieWinTextView = (TextView) findViewById(R.id.tieR);
        mAndroidWinTextView = (TextView) findViewById(R.id.androidR);
        mBoardView.setGame(mGame);
        mResults =0;
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
            mResults = savedInstanceState.getInt("mResults");
//            mHTurn = savedInstanceState.getBoolean("mHTurn");
        }
        displayScores();
        // TODO: turnos

        firebaseDatabase = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            roomName = extras.getString("roomName");
            if(roomName.equals(playerName)){
                role = "host";
            }else{
                role = "guest";
            }
        }

        /*mBoardView.setOnClickListener(view -> {

            String boardMessage = role + ":" + getSequence(mGame.getBoardState());
            boardRef.setValue(boardMessage);
        });*/

        boardReference = firebaseDatabase.getReference("rooms/" + roomName + "/message");

        winPRef = firebaseDatabase.getReference("rooms/" + roomName + "/winC");
        winPRef.setValue(mGameOver);

        turnRef = firebaseDatabase.getReference("rooms/"  + roomName + "/turn");
        msgRef = firebaseDatabase.getReference("rooms/"  + roomName + "/text");
        resRef = firebaseDatabase.getReference("rooms/"  + roomName + "/results");

        if(role.equals("host")){
            turnRef.setValue(mHTurn);
            //boardRef.setValue(getSequence(mGame.getBoardState()));
            boardReference.setValue("         ");
            if(mHTurn){
                //mInfoTextView.setText(R.string.first_computer);
                msgRef.setValue("Player 2 first");
            }else{
                //mInfoTextView.setText(R.string.first_human);
                msgRef.setValue("Player 1 first");
            }
            resRef.setValue(mResults);
            startNewGame();
        }

        makeMoveEventListener();
        changeTurnEventListener();
        endGameEventListener();
        updateMessageEventListener();
        resultsEventListener();


    }

    private void resultsEventListener(){
        resRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mResults = snapshot.getValue(int.class);
                displayScores();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateMessageEventListener(){
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mInfoTextView.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void endGameEventListener(){
        winPRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mGameOver = snapshot.getValue(boolean.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeMoveEventListener(){
        boardReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Message received
                char[] board = new char[9];
                String a = snapshot.getValue(String.class);
                for(int i = 0; i<9;i++){
                    board[i] = a.charAt(i);
                }
                mGame.setBoardState(board);
                mBoardView.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeTurnEventListener(){
        turnRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean turn = snapshot.getValue(Boolean.class);
                if(!turn && role.equals("host")){
                    mBoardView.setEnabled(true);
                }else if(turn && role.equals("guest")){
                    mBoardView.setEnabled(true);
                }else{
                    mBoardView.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayScores() {
        mHumanWinTextView.setText("Player 2: " + (mResults/100));
        mTieWinTextView.setText("Tie: " + ((mResults%100)/10));
        mAndroidWinTextView.setText("Player 1: " + ((mResults%100)%10));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mResults", mResults);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putBoolean("mHTurn", mHTurn);
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
                boardReference.setValue("         ");
                winPRef.setValue(mGameOver);
                if(mHTurn){
                    //mInfoTextView.setText(R.string.first_computer);
                    msgRef.setValue("Player 2 first");
                }else{
                    //mInfoTextView.setText(R.string.first_human);
                    msgRef.setValue("Player 1 first");
                }
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
                                MainActivity3.this.finish();
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
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            if(!mGameOver){
                if(role.equals("host")){
                    if (setMove(TicTacToeGame.HUMAN_PLAYER, pos)){
                        setMove(TicTacToeGame.HUMAN_PLAYER, pos);
                        mHumanMediaPlayer.start();
                        int winner = mGame.checkForWinner();
                        if (winner == 0) {
                            //mInfoTextView.setText(R.string.turn_computer);
                            msgRef.setValue("Player 2 turn");
                            mHTurn = true;
                        }else if (winner == 1) {
                            mResults = mResults + 10;
                            //mInfoTextView.setText(R.string.result_tie);
                            msgRef.setValue("It´s a tie");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }else if (winner == 2){
                            mResults = mResults + 1;
                            //mInfoTextView.setText(R.string.result_human_wins);
                            msgRef.setValue("Player 1 won");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }else{
                            mResults = mResults + 100;
                            //mInfoTextView.setText(R.string.result_computer_wins);
                            msgRef.setValue("Player 2 won");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }
                    }
                }else{
                    if (setMove(TicTacToeGame.COMPUTER_PLAYER, pos)) {
                        setMove(TicTacToeGame.COMPUTER_PLAYER, pos);
                        mComputerMediaPlayer.start();

                        int winner = mGame.checkForWinner();
                        if (winner == 0) {
                            //mInfoTextView.setText(R.string.turn_human);
                            msgRef.setValue("Player 1 turn");
                            mHTurn = false;
                        }else if (winner == 1) {
                            mResults = mResults + 10;
                            //mInfoTextView.setText(R.string.result_tie);
                            msgRef.setValue("It´s a tie");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }else if (winner == 2){
                            mResults = mResults + 1;
                            //mInfoTextView.setText(R.string.result_human_wins);
                            msgRef.setValue("Player 1 won");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }else{
                            mResults = mResults + 100;
                            //mInfoTextView.setText(R.string.result_computer_wins);
                            msgRef.setValue("Player 2 won");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }
                    }
                }
                displayScores();
                resRef.setValue(mResults);
                turnRef.setValue(mHTurn);
                boardReference.setValue(getSequence(mGame.getBoardState()));
                winPRef.setValue(mGameOver);
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private String getSequence(char[] seq){
        String text = "";
        for(char let:seq){
            text += let;
        }
        return text;
    }
}