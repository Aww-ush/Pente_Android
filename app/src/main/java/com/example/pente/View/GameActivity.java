package com.example.pente.View;
import static com.example.pente.Model.Board.BLACK_PIECE;
import static com.example.pente.Model.Board.WHITE_PIECE;
import static com.example.pente.View.MainActivity.COIN_FLIP_REQUEST_CODE;

import com.example.pente.Controller.TournamentController;
import com.example.pente.Helper.Serialization;
import com.example.pente.Model.Computer;
import com.example.pente.Model.Human;
import com.example.pente.Model.Board;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.pente.Model.Strategy;
import com.example.pente.R;
import android.content.Context;
/**
 * GameActivity class represents the main gameplay activity of the Pente game.
 * This class includes methods for handling user actions, updating the game state,
 * and displaying dialogs for various game events.
 */
public class GameActivity extends AppCompatActivity {

    private Human _human;
    private Computer _computer;
    public String logMessage = "";
    private Strategy _strategy;
    public static final String SaveGameMessage = "Do you want to save the game?";

    private Board _board;

    private  TournamentController _tournament;

    private int _nextMover;
    /**
     * onCreate method is called when the activity is first created.
     * It initializes the game state, sets up the UI, and starts the tournament.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);

        Button quitBtn = findViewById(R.id.quitBtn);
        Button helpBtn = findViewById(R.id.helpBtn);
        Button logBtn = findViewById(R.id.showLog);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLoggerDialog(GameActivity.this);
            }
        });
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddLogMessage("The onclick listener for quit was set /n");
                onQuitButtonClick();
            }
        });
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHelpButtonClick();
            }
        });

        _board = new Board((char[][]) getIntent().getSerializableExtra("board"));
        _strategy = new Strategy(_board);
        _nextMover = (int)  getIntent().getSerializableExtra("nextMover");

        char humanColour = (char) getIntent().getSerializableExtra("humanColour");
        char computerColour = (char) getIntent().getSerializableExtra("computerColour");
        boolean isLoaded = getIntent().getBooleanExtra("isLoaded", false);
        Log.d("TAG", "here");
        int  humanTotalPoint = 0, computerTotalPoint = 0, humanCapturePoint = 0, computerCapturePoint = 0;

        if(isLoaded){

            computerCapturePoint = (int) getIntent().getSerializableExtra("computerCapturePoint");
            humanCapturePoint = (int) getIntent().getSerializableExtra("humanCapturePoint");
            computerTotalPoint = (int) getIntent().getSerializableExtra("computerTotalPoint");
            humanTotalPoint = (int) getIntent().getSerializableExtra("humanTotalPoint");
            _human = new Human(humanColour, humanTotalPoint, _strategy.CalculatedScoreInLoadedRound(humanColour), humanCapturePoint, _board.GetTotalMoveByColour(humanColour));
            _computer = new Computer(computerColour, computerTotalPoint, _strategy.CalculatedScoreInLoadedRound(computerColour), computerCapturePoint, _board.GetTotalMoveByColour(computerColour));
        }
        else{
            _human = new Human((char) getIntent().getSerializableExtra("humanColour"));
            _computer = new Computer((char) getIntent().getSerializableExtra("computerColour"));
        }

        _tournament = new TournamentController(_board, _human, _computer, _nextMover, isLoaded, _strategy);
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        createBoard(gridLayout); // Just call the createBoard method
        UpdateScores();
        _tournament.StartTournament(-1, -1, this);
    }


    /**
     * UpdateBoard method updates the graphical representation of the game board.
     */
    public void UpdateBoard(){
        for (int row = 0; row < 19; row++) {
            for (int column = 0; column < 19; column++) {
                int buttonId = (row * 19) + column;
                Button button = findViewById(buttonId);
                Drawable boardPiece;
                char colour = _board.GetBoard()[row][column];
                if (colour == WHITE_PIECE) {
                    boardPiece = ResourcesCompat.getDrawable(getResources(), R.drawable.board_white_piece, null);
                } else if (colour == BLACK_PIECE) {
                    boardPiece = ResourcesCompat.getDrawable(getResources(), R.drawable.board_black_piece, null);
                } else {
                    boardPiece = ResourcesCompat.getDrawable(getResources(), R.drawable.board_empty_piece, null);
                }
                button.setBackground(boardPiece);
            }
        }
    }
    /**
     * UpdateScores method updates the displayed scores for both the human and computer players.
     */
    public void UpdateScores(/*int humanTournamentScore, int humanCaptureScore, int humanRoundPoints,
                             int computerTournamentScore, int computerCaptureScore, int computerRoundPoints*/){
        // update the scores
        TextView  humanTournamentScoreView =  findViewById(R.id.humanTournamentScore);
        TextView humanCaptureScoreView = findViewById(R.id.humanCaputureScore);
        TextView humanRoundPointsView  = findViewById(R.id.humanRoundScore);
        TextView  computerTournamentScoreView =  findViewById(R.id.computerTotalScore);
        TextView computerCaptureScoreView = findViewById(R.id.computerCaptureScore);
        TextView computerRoundPointsView  = findViewById(R.id.computerRoundScore);
        TextView computerColour  = findViewById(R.id.computerColour);
        TextView humanColour  = findViewById(R.id.humanColour);

        humanTournamentScoreView.setText("Total Score: " + _human.GetTotalPoints() );
        humanCaptureScoreView.setText("Total Capture Point: " + _human.GetTotalCapture() );
        humanRoundPointsView.setText("Total Round Score: " + _human.GetRoundPoints() );
        computerTournamentScoreView.setText("Total Score: " + _computer.GetTotalPoints() );
        computerCaptureScoreView.setText("Total  Capture Point: " + _computer.GetTotalCapture() );
        computerRoundPointsView.setText("Total Round Score: " + _computer.GetRoundPoints() );

        String humanColourFull, computerColourFull;
        if(_human.GetColour() == WHITE_PIECE){
            humanColourFull = "White";
            computerColourFull = "Black";
        }else{
            computerColourFull = "White";
            humanColourFull = "Black";
        }
        humanColour.setText("Colour: " +humanColourFull );
        computerColour.setText("Colour: " +computerColourFull );
    }
    /**
     * onHelpButtonClick method is called when the "Help" button is clicked.
     * It highlights the suggested move on the game board.
     */
    private void onHelpButtonClick() {
       int[] rowCol = _strategy.BestMove(_human.GetColour(), this, _human.GetTotalMoves(), true);
        int buttonId = (rowCol[0] * 19) + rowCol[1];
        Button button = findViewById(buttonId);
        Drawable boardPiece = ResourcesCompat.getDrawable(getResources(), R.drawable.board_suggestion_piece, null);
        button.setBackground(boardPiece);

    }
    /**
     * onQuitButtonClick method is called when the "Quit" button is clicked.
     * It displays a dialog indicating the winner and prompts to save the game.
     */
    private void onQuitButtonClick() {
        this.logMessage = "";
        //create a winner dialog box
        int humanTotalGamePoints = _human.GetTotalPoints() + _human.GetCapturePoints() + _human.GetRoundPoints();
        int computerTotalGamePoints = _computer.GetTotalPoints() + _computer.GetCapturePoints() + _computer.GetRoundPoints();
        String message = (humanTotalGamePoints > computerTotalGamePoints) ? "Congratulations! You win the overall tournament with the score of " + humanTotalGamePoints:
                "Oh no! Computer wins the overall tournament with the score of " + computerTotalGamePoints ;
        if(humanTotalGamePoints == computerTotalGamePoints) {
            message = "Both Players had total score of " + humanTotalGamePoints + ".\n So, it is a draw!";
        }
        CreateWinDialogBoxOnQuit(message);
    }
    /**
     * AddLogMessage method appends a log message to the existing log.
     *
     * @param logMessage The message to be added to the log.
     */

    public void AddLogMessage(String logMessage){
        this.logMessage += logMessage +"\n";
    }

    /**
     * onBoardButtonClick method is called when a button on the game board is clicked.
     * It handles the user's move if it's the human player's turn.
     *
     * @param row    The row of the clicked button.
     * @param column The column of the clicked button.
     */
    public void onBoardButtonClick(int row, int column) {
        // Check if it's the human's turn (you can use _tournament.GetNextMover())
        if (_tournament.GetNextMover() == TournamentController.humanCode) {
            // The user clicked a button; handle the human's move
            int[] rowColumn = new int[]{row, column};
            onHumanMove(rowColumn);
        }
    }

    /**
     * onHumanMove method processes the human player's move and triggers the computer player's move.
     *
     * @param rowColumn An array containing the row and column of the human player's move.
     */
    public void onHumanMove(int[] rowColumn) {
        int row = rowColumn[0];
        int column = rowColumn[1];
        if (_tournament.GetNextMover() == TournamentController.humanCode) {
            if(_tournament.StartTournament(row, column, this)){
                // after human makes move then switch to computer
                _tournament.StartTournament(-1, -1, this);
            }

        }
    }

    /**
     * CreateDialogBox method displays an information dialog with the given message.
     *
     * @param message The message to be displayed in the dialog.
     */
    public void CreateDialogBox(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Information")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the positive button click
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * CreateWinDialogBox method displays a dialog announcing the winner and prompting for the next action.
     *
     * @param message The message to be displayed in the dialog.
     */
    public void CreateWinDialogBox(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Information")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the positive button click
                        //dialog.cancel();
                        CreateDialogBoxForReset(TournamentController.ContinueMessage);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * CreateWinDialogBoxOnQuit method displays a dialog announcing the winner when quitting the game.
     * It prompts to save the game before exiting.
     *
     * @param message The message to be displayed in the dialog.
     */
    public void CreateWinDialogBoxOnQuit(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Information")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the positive button click
                        //dialog.cancel();
                       CreateDialogBoxForSavingGame(SaveGameMessage);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * CreateDialogBoxForReset method displays a dialog asking the user if they want to reset the game.
     * It gives options to reset or save the game before proceeding.
     *
     * @param message The message to be displayed in the dialog.
     */
    public void CreateLoggerDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Information");
        // Create a ScrollView and a TextView for the scrollable message
        ScrollView scrollView = new ScrollView(context);
        TextView textView = new TextView(context);
        textView.setText(logMessage);
        textView.setPadding(10, 10, 10, 10); // Add padding for better visibility
        textView.setMovementMethod(new ScrollingMovementMethod());

        scrollView.addView(textView);

        // Set the custom view
        builder.setView(scrollView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Handle the positive button click
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * CreateDialogBoxForSavingGame method displays a dialog asking the user if they want to save the game.
     * It gives options to save or discard the game before exiting.
     *
     * @param message The message to be displayed in the dialog.
     */

    public void CreateDialogBoxForReset(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Information")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // reset the board'
                        dialog.cancel();
                        _board.ResetBoard();
                        _human.reset();
                        _computer.reset();
                        UpdateBoard();
                        // reflect the updated score
                        UpdateScores();
                        // find out the next mover
                        if(_human.GetTotalPoints() == _computer.GetTotalPoints()){
                            Intent intent = new Intent(GameActivity.this, CoinFlipActivity.class);
                            startActivityForResult(intent, COIN_FLIP_REQUEST_CODE);
                        }
                        else if(_human.GetTotalPoints() > _computer.GetTotalPoints()){
                            // create a dialogue
                            _human.SetColour(_board.WHITE_PIECE);
                            _computer.SetColour(_board.BLACK_PIECE);
                            CreateDialogBox("You will be white and will be going first, since you have higher score in previous round");
                            _tournament.SetNextMover(TournamentController.humanCode);
                            _tournament.StartTournament(-1, -1, GameActivity.this);

                        }else {
                            _computer.SetColour(_board.WHITE_PIECE);
                            _human.SetColour(_board.BLACK_PIECE);
                            CreateDialogBox("You will be black and will be going second, since you had lower score in previous round");
                            _tournament.SetNextMover(TournamentController.computerCode);
                            _tournament.StartTournament(-1, -1, GameActivity.this);
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the negative button click
                        // ask to save the game
                        // show the winner of tournament
                        int humanTotalGamePoints = _human.GetTotalPoints() + _human.GetCapturePoints() + _human.GetRoundPoints();
                        int computerTotalGamePoints = _computer.GetTotalPoints() + _computer.GetCapturePoints() + _computer.GetRoundPoints();
                        String message = (humanTotalGamePoints > computerTotalGamePoints) ? "Congratulations! You win the overall tournament with the score of " + humanTotalGamePoints:
                                "Oh no! Computer wins the overall tournament with the score of " + computerTotalGamePoints ;
                        if(humanTotalGamePoints == computerTotalGamePoints) {
                            message = "Both Players had total score of " + humanTotalGamePoints + ".\n So, it is a draw!";
                        }
                        CreateWinDialogBoxOnQuit(message);
                        //CreateDialogBoxForSavingGame(SaveGameMessage);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * CreateDialogBoxForSavingGame method displays a dialog asking the user if they want to save the game.
     * It gives options to save or discard the game before exiting.
     *
     * @param message The message to be displayed in the dialog.
     */

    public void CreateDialogBoxForSavingGame(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Information")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // create a save file string
                        String nextPlayer =  (_nextMover == TournamentController.humanCode) ?  "Human" : "Computer";
                        char nextPlayerColourCode = (nextPlayer.equals("Human")) ? _human.GetColour() : _computer.GetColour();
                        String nextPlayerColour = (nextPlayerColourCode == 'W') ? "White" : "Black";
                        Serialization.SaveGame(_board, _human, _computer, nextPlayer, nextPlayerColour);
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * createBoard method generates the game board by dynamically creating buttons for each cell.
     *
     * @param gridLayout The GridLayout that will contain the generated buttons.
     */
    private void createBoard(GridLayout gridLayout) {
        for (int row = 0; row < 19; row++) {
            for (int column = 0; column < 19; column++) {
                Button button = new Button(this);
                int buttonId = (row * 19) + column;
                button.setId(buttonId);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int buttonId = v.getId();
                        int row = buttonId / 19;
                        int column = buttonId % 19;

                        // Handle the user's move
                        onBoardButtonClick(row, column);
                    }
                });
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 50;
                params.height = 50;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                button.setLayoutParams(params);
                Drawable boardPiece;
                char colour = _board.GetBoard()[row][column];
                if (colour == 'W') {
                    boardPiece = ResourcesCompat.getDrawable(getResources(), R.drawable.board_white_piece, null);
                } else if (colour == 'B') {
                    boardPiece = ResourcesCompat.getDrawable(getResources(), R.drawable.board_black_piece, null);
                } else {
                    boardPiece = ResourcesCompat.getDrawable(getResources(), R.drawable.board_empty_piece, null);
                }

                button.setBackground(boardPiece);
                gridLayout.addView(button); // Add the button to the main gridLayout
            }
        }
    }

    /**
     * onActivityResult method is called when an activity launched by startActivityForResult finishes.
     * It handles the result from CoinFlipActivity and starts the game accordingly.
     *
     * @param requestCode The request code passed to startActivityForResult.
     * @param resultCode  The result code returned from the child activity.
     * @param data        An Intent that carries the result data.
     */    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COIN_FLIP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Handle the result from CoinFlipActivity
               boolean coinTossResult = data.getBooleanExtra("result", false);

                // Now that you have the result, proceed to start the game
                char humanColour, computerColour;
                int nextMover;

                if (coinTossResult) {
                    _human.SetColour(_board.WHITE_PIECE);
                    _computer.SetColour(_board.BLACK_PIECE);
                    CreateDialogBox("You will be white and will be going first");
                    _tournament.SetNextMover(TournamentController.humanCode);
                    _tournament.StartTournament(-1, -1, GameActivity.this);
                } else {
                    _computer.SetColour(_board.WHITE_PIECE);
                    _human.SetColour(_board.BLACK_PIECE);
                    CreateDialogBox("You will be white and will be going second");
                    _tournament.SetNextMover(TournamentController.computerCode);
                    _tournament.StartTournament(-1, -1, GameActivity.this);
                }

            }
        }


    }


}