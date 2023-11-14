package com.example.pente.Model;

import android.util.Log;

import com.example.pente.Controller.TournamentController;
import com.example.pente.View.GameActivity;

import java.io.Serializable;
/**
 * Player is an abstract class representing a participant in the Pente game.
 * It includes common attributes and methods for both Human and Computer players.
 * Subclasses must implement the makeMove method to define their specific move logic.
 */
public abstract class Player implements Serializable {
    private int _totalPoints;
    private int _totalMoves ;
    private int _totalCapture;
    private int _roundPoints;
    private char _colour;

    /**
     * Returns total points
     */
    public int GetTotalPoints() {
        return _totalPoints;
    }
    /**
     * Increases the round points for the player.
     *
     * @param points The points to be added to the round total.
     * @return True if successful, false otherwise.
     */
    public boolean IncreaseRoundPoint(int points) {
        _roundPoints += points;
        return true;
    }

    /**
     * Returns total capture points
     */
    public int GetTotalCapture() {
        return _totalCapture;
    }

    public boolean SetTotalCapture(int number) {
        _totalCapture += number;
        return true;
    }
    /**
     * Sets the total capture points for the player.
     *
     * @param number The number of capture points to set.
     * @return True if successful, false otherwise.
     */
    public boolean SetTotalMoves(int number) {
        try{
            _totalMoves = number;
            return true;
        }catch (Exception e){
            Log.e("TAG", "SetTotalMoves: There was error setting the move", e);
            return false;
        }
    }
    /**
     * Increases the total moves made by the player.
     *
     * @return True if successful, false otherwise.
     */
    public boolean IncreaseTotalMoves() {
        try{
            _totalMoves++;
            return true;
        }catch (Exception e){
            Log.e("TAG", "SetTotalMoves: There was error setting the move", e);
            return false;
        }
    }
    /**
     * Gets the total number of moves made by the player.
     *
     * @return The total number of moves.
     */
    public int GetTotalMoves() {
        return _totalMoves;
    }

    // Set and get colour
    /**
     * Sets the colour of the player.
     *
     * @param colour The colour to set.
     * @return True if successful, false otherwise.
     */
    public boolean SetColour(char colour) {
        try{
            _colour = colour;
            return true;
        }catch (Exception e){
            Log.e("TAG", "SetColour: There was error setting the colour", e);
            return false;
        }
    }
    /**
     * Sets the round points for the player.
     *
     * @param roundPoints The round points to set.
     * @return True if successful, false otherwise.
     */
    public boolean SetRoundPoints(int roundPoints) {
        try{
            _roundPoints = roundPoints;
            return true;
        }catch (Exception e){
            Log.e("TAG", "SetColour: There was error setting the colour", e);
            return false;
        }
    }
    /**
     * Sets the tournament points for the player.
     *
     * @param tournamentPoints The tournament points to set.
     * @return True if successful, false otherwise.
     */
    public boolean SetTournamentPoints(int tournamentPoints) {
        try{
            _totalPoints = tournamentPoints;
            return true;
        }catch (Exception e){
            Log.e("TAG", "SetColour: There was error setting the colour", e);
            return false;
        }
    }
    /**
     * Sets the capture points for the player.
     *
     * @param capturepoints The capture points to set.
     * @return True if successful, false otherwise.
     */
    public boolean SetCapturePoints(int capturepoints) {
        try{
            this._totalCapture = capturepoints;
            return true;
        }catch (Exception e){
            Log.e("TAG", "SetColour: There was error setting the CAPTURE", e);
            return false;
        }
    }
    /**
     * Increases the capture points for the player.
     *
     * @param capturepoints The capture points to add.
     * @return True if successful, false otherwise.
     */
    public boolean IncreaseCapturePoints(int capturepoints) {
        try{
            _totalCapture += capturepoints;
            return true;
        }catch (Exception e){
            Log.e("TAG", "SetColour: There was error setting the colour", e);
            return false;
        }
    }
    /**
     * Gets the capture points of the player.
     *
     * @return The capture points.
     */
    public int GetCapturePoints() {
        return _totalCapture;
    }

    /**
     * Gets the tournament points of the player.
     *
     * @return The tournament points.
     */
    public int GetTournamentPoints() {
        return _totalPoints;
    }

    /**
     * Gets the round points of the player.
     *
     * @return The round points.
     */
    public int GetRoundPoints() {
        return _roundPoints;
    }

    /**
     * Gets the colour of the player.
     *
     * @return The colour of the player.
     */
    public char GetColour() {
        return this._colour;
    }

    /**
     * Abstract method to be implemented by subclasses for making a move in the game.
     *
     * @param board         The game board.
     * @param strategy      The strategy for making a move.
     * @param rowCol        The row and column of the move.
     * @param gameActivity  The GameActivity instance.
     * @return True if the move is successful, false otherwise.
     */
    public abstract boolean makeMove(Board board, Strategy strategy, int[] rowCol, GameActivity gameActivity);

    /**
     * Resets the player's statistics for a new game.
     *
     * @return True if successful, false otherwise.
     */
    public boolean reset() {
        _totalPoints = _roundPoints + _totalCapture + _totalPoints;
        _roundPoints = 0;
        _totalCapture = 0;
        _totalMoves = 0;
        return true;
    }

}
