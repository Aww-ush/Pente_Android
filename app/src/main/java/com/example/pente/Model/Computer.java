package com.example.pente.Model;

import static com.example.pente.Controller.TournamentController.ConvertToLabelledRowColumn;

import android.util.Log;

import com.example.pente.Controller.TournamentController;
import com.example.pente.View.GameActivity;

import java.io.Serializable;

public class Computer extends Player implements Serializable {
    /**
     * Constructs a Computer player with the specified color.
     *
     * @param colour The color of the computer player.
     */
    public Computer(char colour){
        try {
            if (!SetColour(colour)) {
                throw new IllegalArgumentException("Error setting up constructor");
            }

        } catch (Exception e) {
            Log.e("TAG", "Computer: Error setting up constructor");
        }
    }
    /**
     * Constructs a Computer player with specified colour, tournament points, roundPoints, capture points and total Move.
     *
     * @param colour            The color of the computer player.
     * @param tournamentPoints  The tournament points of the computer player.
     * @param roundPoints       The round points of the computer player.
     * @param capturePoints     The capture points of the computer player.
     * @param totalMove         The total moves made by the computer player.
     */
    public Computer(char colour, int tournamentPoints, int roundPoints, int capturePoints, int totalMove){
        try {
            if (!SetColour(colour) || !SetRoundPoints(roundPoints) || !SetTotalMoves(totalMove) || !SetTournamentPoints(tournamentPoints) || !SetTotalCapture(capturePoints)) {
                throw new IllegalArgumentException("Error setting up constructor");
            }

        } catch (Exception e) {
            Log.e("TAG", "Computer: Error setting up constructor");
        }
    }
    /**
     * Makes a move for the computer player using the specified strategy.
     *
     * @param board         The game board.
     * @param strategy      The strategy used to determine the move.
     * @param rowCol        An array to store the calculated row and column for the move.
     * @param gameActivity  The GameActivity instance for logging and UI updates.
     * @return True if the move is successfully made, false otherwise.
     */
    @Override
    public boolean makeMove(Board board, Strategy strategy, int[] rowCol, GameActivity gameActivity) {
        try {

                int[] calculatedRowCol = strategy.BestMove(GetColour(), gameActivity, GetTotalMoves(), false);
                rowCol[0] = calculatedRowCol[0];
                rowCol[1] = calculatedRowCol[1];


            if (board.InsertPiece(rowCol[0], rowCol[1], GetColour())) {
                gameActivity.AddLogMessage("So the computer moved to " + ConvertToLabelledRowColumn(rowCol[0], rowCol[1]));
                return true;
            }

            Log.d("TAG", "makeMove: Could not insert row: " + rowCol[0] + " column: " + rowCol[1]);
            return false;
        } catch (Exception e) {
            Log.d("TAG", "makeMove: Could not make move");
            return false;
        }
    }

}
