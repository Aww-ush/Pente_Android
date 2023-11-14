package com.example.pente.Model;

import static com.example.pente.Controller.TournamentController.ConvertToLabelledRowColumn;

import android.util.Log;

import com.example.pente.Controller.TournamentController;
import com.example.pente.View.GameActivity;

import java.io.Serializable;
/**
 * The Human class represents a human player in the Pente game.
 * It extends the Player class and implements the makeMove method
 * to handle human player moves.
 */
public class Human extends Player implements Serializable {

    /**
     * Constructor for creating a Human object with the specified color.
     *
     * @param colour The color of the human player.
     */
    public Human(char colour) {
        try {
            if (!SetColour(colour)) {
                throw new IllegalArgumentException("Error setting up constructor");
            }
        } catch (Exception e) {
            Log.e("TAG", "Human: Error setting up constructor");
        }
    }

    /**
     * Constructor for creating a Human object with specified parameters.
     *
     * @param colour          The color of the human player.
     * @param tournamentPoints The tournament points of the human player.
     * @param roundPoints      The round points of the human player.
     * @param capturePoints    The capture points of the human player.
     * @param totalMove        The total moves made by the human player.
     */
    public Human(char colour, int tournamentPoints, int roundPoints, int capturePoints, int totalMove) {
        try {
            if (!SetColour(colour) || !SetRoundPoints(roundPoints) || !SetTotalMoves(totalMove) || !SetTournamentPoints(tournamentPoints)) {
                throw new IllegalArgumentException("Error setting up constructor");
            }
            if (!SetCapturePoints(capturePoints)) {
                throw new IllegalArgumentException("Error setting up constructor");
            }
        } catch (Exception e) {
            Log.e("TAG", "Human: Error setting up constructor");
        }
    }

    /**
     * Overrides the makeMove method from the Player class to handle human player moves.
     *
     * @param board        The game board.
     * @param strategy     The strategy used for making moves.
     * @param rowCol       The row and column indices for the move.
     * @param gameActivity The GameActivity instance.
     * @return True if the move is successful, false otherwise.
     */
    @Override
    public boolean makeMove(Board board, Strategy strategy, int[] rowCol, GameActivity gameActivity) {
        try {
            // If it is -1 -1, it means that it is waiting for human input or computer to decide the move
            if (GetTotalMoves() == 0 && GetColour() == Board.WHITE_PIECE) {
                if (rowCol[0] != Board.CENTER_BOARD && rowCol[1] != Board.CENTER_BOARD) {
                    gameActivity.CreateDialogBox("The first move must be in the center of the board J10");
                    return false;
                }
            }

            if (GetTotalMoves() == 1 && GetColour() == Board.WHITE_PIECE) {
                // The second Piece must be 3 intersections away from the center
                if (!board.IsSecondPieceValid(rowCol[0], rowCol[1])) {
                    gameActivity.AddLogMessage("The second move must be 3 intersections away from the center. The user clicked: " + ConvertToLabelledRowColumn(rowCol[0], rowCol[1]));
                    gameActivity.CreateDialogBox("The second move must be 3 intersections away from the center");
                    return false;
                }
            }

            if (board.InsertPiece(rowCol[0], rowCol[1], GetColour())) {
                // Check for point counter
                gameActivity.AddLogMessage("The user clicked: " + ConvertToLabelledRowColumn(rowCol[0], rowCol[1]));
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.d("TAG", "makeMove: Could not make move");
            return false;
        }
    }
}
