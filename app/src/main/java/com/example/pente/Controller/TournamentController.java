package com.example.pente.Controller;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.example.pente.Model.Board;
import com.example.pente.Model.Computer;
import com.example.pente.Model.Human;
import com.example.pente.Model.Player;
import com.example.pente.Model.Strategy;
import com.example.pente.View.GameActivity;
import com.example.pente.View.MainActivity;

import java.io.Serializable;
/**
 * TournamentController handles the logic for the Gomoku tournament.
 * It manages the game flow, player moves, and determines the winner.
 */
public class TournamentController implements Serializable {
    // Constants representing user types
    public static final int humanCode = 0;
    public static final int computerCode = 1;
    // Constant message for prompting continuation
    public static final String ContinueMessage = "Do you want to continue?";
    // Instance variables
    Board _board;
    Human _human;
    Computer _computer;
    Strategy _strategy;

    int _nextMover;
    boolean isLoaded = false;
    boolean usrWantsToContinue = true;
    Player[] _playerArray = new Player[2];

    /**
     * Constructor for TournamentController.
     *
     * @param board     The game board.
     * @param human     The human player.
     * @param computer  The computer player.
     * @param nextMover The code representing the next player to make a move.
     * @param isLoaded  Flag indicating whether the game is loaded.
     * @param strategy  The strategy used for making moves.
     */
    public TournamentController(Board board, Human human, Computer computer, int nextMover, boolean isLoaded, Strategy strategy) {
        try {
            if (!SetBoard(board) || !SetHuman(human) || !SetComputer(computer) || !SetNextMover(nextMover)) {
                throw new IllegalArgumentException("Error setting up constructor");
            }
            _playerArray[humanCode] = human;
            _playerArray[computerCode] = computer;
            _strategy = strategy;
            this.isLoaded = isLoaded;

        } catch (Exception e) {
            Log.e("TAG", "TournamentController: Error setting up constructor");
        }
    }
    /**
     * Starts the Gomoku tournament.
     *
     * @param row           The row where the player makes a move.
     * @param column        The column where the player makes a move.
     * @param gameActivity  The activity managing the game UI.
     * @return True if the move is successful, false otherwise.
     */

    public boolean StartTournament(int row, int column, GameActivity gameActivity) {
        try{
            Player currentPlayer = _playerArray[_nextMover];
            int rowCol[] = new int[]{row, column};
            if (currentPlayer.makeMove(_board, _strategy, rowCol, gameActivity)) {
                // count if it got any point
                int point = _strategy.CalculatePoint(rowCol[0], rowCol[1], currentPlayer.GetColour());
                int capturePoint = _strategy.CalculateTotalCapture(rowCol[0], rowCol[1], currentPlayer.GetColour(), true);
                currentPlayer.IncreaseRoundPoint(point);
                currentPlayer.IncreaseCapturePoints(capturePoint);
                if(point > 0){
                    String nextPlayer =  (_nextMover == TournamentController.humanCode) ?  "Human" : "Computer";
                    gameActivity.AddLogMessage(nextPlayer + " scored " + point + " point(s)");
                }
                if(capturePoint > 0){
                    String nextPlayer =  (_nextMover == TournamentController.humanCode) ?  "Human" : "Computer";
                    gameActivity.AddLogMessage(nextPlayer + " captured " + capturePoint * 2 +" piece(s)");
                }
                gameActivity.UpdateBoard();
                gameActivity.UpdateScores();
                currentPlayer.IncreaseTotalMoves();
                // count if it got any
                if (CheckForWin(currentPlayer, point)) {
                    // save the tournament score
                    String winnerMessage = WinnerMessage(currentPlayer.GetColour());
                    String nextPlayer =  (_nextMover == TournamentController.humanCode) ?  "Human" : "Computer";
                    gameActivity.AddLogMessage(nextPlayer + " won the match");
                    gameActivity.CreateWinDialogBox(winnerMessage);
                    // pop up for new game

                }
                SetNextMover(TournamentController.SwitchUser(_nextMover));
                return true;
            }
            return false;
            // Check for a win or draw
            }catch (Exception e) {
                Log.e("TAG", "StartTournament: there was error in moving piece and switching the players");
                return false;
             }

    }
    /**
     * Generates a winner message based on the player's color.
     *
     * @param colour The color of the winning player.
     * @return The winner message.
     */
    public String WinnerMessage(char colour){
        if(colour == _human.GetColour()){
            int totalScore = _human.GetTotalCapture()+ _human.GetRoundPoints() + _human.GetTotalPoints();
            return "Congratulations! You have won the game with the score of " + totalScore;
        }
        int totalScore = _computer.GetTotalCapture()+ _computer.GetRoundPoints() + _computer.GetTotalPoints();
        return "Oh no! Computer won the game with the total score of " + totalScore;
    }
    /**
     * Switches the current user.
     *
     * @param currUser The current user's code.
     * @return The code of the next user.
     */
    public static int SwitchUser(int currUser){
        return (currUser + 1) % 2;
    }
    /**
     * Gets the code representing the next mover.
     *
     * @return The code representing the next mover.
     */
    public int GetNextMover(){
        return _nextMover;
    }

    /**
     * Sets the code representing the next mover.
     *
     * @param nextMover The code representing the next mover.
     * @return True if successful, false otherwise.
     */
    public boolean SetNextMover(int nextMover) {
        try{
            _nextMover = nextMover;
            return true;
        }catch (Exception e){
            Log.e("TAG", "SetBoard: There was error setting board");
            return false;
        }
    }

    /**
     * Sets the game board.
     *
     * @param board The game board.
     * @return True if successful, false otherwise.
     */
    private boolean SetBoard(Board board) {
        try{
            if (board != null) {
                _board = board;
                return true;
            }
            return false;
        }catch (Exception e){
            Log.e("TAG", "SetBoard: There was error setting board");
            return false;
        }
    }

    /**
     * Sets the human player.
     *
     * @param human The human player.
     * @return True if successful, false otherwise.
     */
    private boolean SetHuman(Human human) {
        try{
            if (human != null) {
                _human = human;
                return true;
            }
            return false;
        }catch (Exception e){
            Log.e("TAG", "setHuman: There was error setting human");
            return false;
        }
    }
    /**
     * Sets the computer player.
     *
     * @param computer The computer player.
     * @return True if successful, false otherwise.
     */
    private boolean SetComputer(Computer computer) {
        try{
            if (computer != null) {
                _computer = computer;
                return true;
            }
            return false;
        }catch (Exception e){
            Log.e("TAG", "setComputer: There was error setting computer");
            return false;
        }
    }
    /**
     * Checks if the current player has won the game based on points or capture points.
     *
     * @param player       The current player.
     * @param currentPoint The points earned in the current move.
     * @return True if the player has won, false otherwise.
     */
    public boolean CheckForWin(Player player, int currentPoint){

        if(currentPoint >= 5){
            Log.d("TAG", "CheckForWin: The player won!!");
            return  true;
        }
        if(player.GetCapturePoints() >= 5){
            Log.d("TAG", "CheckForWin: The player won!!");
            return  true;
        }
        return  false;
    }
    /**
     * Converts the row and column indices to a labeled format.
     *
     * @param row    The row index.
     * @param column The column index.
     * @return The labeled row and column.
     */

    public static String ConvertToLabelledRowColumn(int row, int column) {
        // Convert column to capital alphabet
        char columnLabel = (char) ('A' + column);

        // Append row number and column label to the result string
        String result = String.format("%s%d", columnLabel, 19 - row );

        return result;
    }

}
