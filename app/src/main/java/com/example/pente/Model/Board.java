package com.example.pente.Model;

import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The Board class represents the game board in the Pente game.
 * It includes methods for initializing, modifying, and querying the state of the game board.
 */
public class Board implements Serializable {

    public static int BOARD_SIZE = 19;
    public static final char WHITE_PIECE = 'W';
    public static final char BLACK_PIECE = 'B';
    public static final char INITIAL_BOARD_PIECE = 'O';
    public static final int CENTER_BOARD = 9;
    private char[][] _board;

    /**
     * Default constructor to initialize the game board with initial pieces.
     */
    public Board() {
        _board = new char[19][19];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                _board[row][column] = INITIAL_BOARD_PIECE;
            }
        }
    }

    /**
     * Constructor to create a Board object with a given board configuration.
     *
     * @param board The initial state of the game board.
     */
    public Board(char[][] board) {
        _board = new char[19][19];

        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[0].length; column++) {
                _board[row][column] = board[row][column];
            }
        }
    }

    /**
     * Sets the state of the board to the given board configuration.
     *
     * @param board The new state of the game board.
     * @return True if the board is successfully set, false otherwise.
     */
    public boolean SetBoard(char[][] board) {
        try {
            _board = board;
            return true;
        } catch (Exception e) {
            Log.d("TAG", "SetBoard: Internal server error, failed to set board");
            return false;
        }
    }

    /**
     * Gets the current state of the game board.
     *
     * @return The 2D array representing the game board.
     */
    public char[][] GetBoard() {
        return _board;
    }

    /**
     * Inserts a piece of the specified type at the given position on the board.
     *
     * @param row       The row index.
     * @param column    The column index.
     * @param pieceType The type of the piece to insert.
     * @return True if the piece is successfully inserted, false otherwise.
     */
    public boolean InsertPiece(int row, int column, char pieceType) {
        try {
            if (row < 0 || row >= BOARD_SIZE) {
                Log.d("TAG", "InsertPiece: The row is not within the board");
                return false;
            }
            if (column < 0 || column >= BOARD_SIZE) {
                Log.d("TAG", "InsertPiece: The column is not within the board");
                return false;
            }
            if (!isPieceValid(pieceType)) {
                Log.d("TAG", "InsertPiece: The piece is not of valid color");
                return false;
            }
            if (!isPositionEmpty(row, column)) {
                Log.d("TAG", "InsertPiece: The position is not empty");
                return false;
            }
            _board[row][column] = pieceType;
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println("Could not insert the piece");
            return false;
        }
    }

    /**
     * Checks if a given position on the board is empty.
     *
     * @param row    The row index.
     * @param column The column index.
     * @return True if the position is empty, false otherwise.
     */
    public boolean isPositionEmpty(int row, int column) {
        return _board[row][column] == INITIAL_BOARD_PIECE;
    }

    /**
     * Checks if the given piece type is a valid color.
     *
     * @param input The piece type to check.
     * @return True if the piece type is valid, false otherwise.
     */
    public boolean isPieceValid(char input) {
        return input == WHITE_PIECE || input == BLACK_PIECE;
    }

    /**
     * Checks if the position for the second piece is valid.
     *
     * @param row    The row index.
     * @param column The column index.
     * @return True if the position is valid, false otherwise.
     */
    public boolean IsSecondPieceValid(int row, int column) {
        try {
            if (!isPositionEmpty(row, column)) {
                Log.d("TAG", "IsSecondPieceValid: The position is not empty");
                return false;
            }
            if ((row >= 6) && (row <= 12) && (column >= 6) && (column <= 12)) {
                Log.d("TAG", "IsSecondPieceValid: The piece must be kept three intersections away");
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println("Failed to validate the piece");
            return false;
        }
    }

    /**
     * Removes a piece from the specified position on the board.
     *
     * @param row    The row index.
     * @param column The column index.
     * @return True if the piece is successfully removed, false otherwise.
     */
    public boolean RemovePiece(int row, int column) {
        try {
            if (isPositionEmpty(row, column)) {
                Log.d("TAG", "RemovePiece: The position is empty, so no piece to remove");
                return false;
            }
            _board[row][column] = INITIAL_BOARD_PIECE;
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println("Failed to remove the piece");
            return false;
        }
    }

    /**
     * Gets the opposite color of the given color.
     *
     * @param colour The color for which to get the opposite color.
     * @return The opposite color.
     */
    public static char GetOppositeColour(char colour) {
        return (colour == WHITE_PIECE) ? BLACK_PIECE : WHITE_PIECE;
    }

    /**
     * Resets the game board to its initial state.
     *
     * @return True if the board is successfully reset, false otherwise.
     */
    public Boolean ResetBoard() {
        try {
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int column = 0; column < BOARD_SIZE; column++) {
                    _board[row][column] = INITIAL_BOARD_PIECE;
                }
            }
            return true;
        } catch (Exception e) {
            Log.d("TAG", "ResetBoard: " + e);
            return false;
        }
    }

    /**
     * Gets the total number of moves made by a specific color on the board.
     *
     * @param colour The color for which to count the moves.
     * @return The total number of moves made by the specified color.
     */
    public int GetTotalMoveByColour(char colour) {
        try {
            int totalMove = 0;
            for (int row = 0; row < _board.length; row++) {
                for (int column = 0; column < _board.length; column++) {
                    if (_board[row][column] == colour) {
                        totalMove++;
                    }
                }
            }
            return totalMove;
        } catch (Exception e) {
            return -1;
        }
    }
}

