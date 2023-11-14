package com.example.pente.Model;

import static com.example.pente.Controller.TournamentController.ConvertToLabelledRowColumn;
import static com.example.pente.Model.Board.GetOppositeColour;
import static com.example.pente.Model.Board.WHITE_PIECE;

import android.util.Log;

import com.example.pente.View.GameActivity;

import java.io.Serializable;
import java.util.Random;

public class Strategy implements Serializable {
    Board _board;
    private int[] RightDiagUpCode = new int[]{-1, 1};
    private int[] RightDiagDownCode = new int[]{1, -1};
    private int[] LeftDiagUpCode = new int[]{-1, -1};
    private int[] LeftDiagDownCode = new int[]{1, 1};
    private int[] VerticalUpCode = new int[]{-1, 0};
    private int[] VerticalDownCode = new int[]{+1, 0};
    private int[] HorizontalLeftCode = new int[]{0, -1};
    private int[] HorizontalRightCode = new int[]{0, 1};
    private final int[][] codeSequence = {RightDiagUpCode, RightDiagDownCode, LeftDiagUpCode, LeftDiagDownCode, HorizontalRightCode, HorizontalLeftCode, VerticalUpCode, VerticalDownCode};

    public Strategy(Board board){
        _board = board;
    }
    /**
     * Generates random coordinates for a move.
     * @return Array containing row and column of the selected move.
     */
    int[] GenerateRandom() {
        try {

            Random rand = new Random();
            int row = rand.nextInt(19);
            int column = rand.nextInt(19);

            // Keep generating random moves until a valid and empty position on the board is found.
            while (!_board.isPositionEmpty(row, column)) {
                row = rand.nextInt(19);
                column = rand.nextInt(19);
            }

            // Return the selected move coordinates as an array.
            return new int[]{row, column};
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not generate random");

            // Return an invalid move if an error occurs.
            return new int[]{-1, -1};
        }
    }
    /**
     * Generates random coordinates for a second move, considering specific conditions.
     * @return Array containing row and column of the selected move.
     */
    int[] GenerateSecondRandom() {
        try {

            Random rand = new Random();
            int row = rand.nextInt(19);
            int column = rand.nextInt(19);

            // Keep generating random moves until a valid and empty position on the board is found.
            while (!_board.isPositionEmpty(row, column) && !_board.IsSecondPieceValid(row, column)) {
                row = rand.nextInt(19);
                column = rand.nextInt(19);
            }

            // Return the selected move coordinates as an array.
            return new int[]{row, column};
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not generate random");

            // Return an invalid move if an error occurs.
            return new int[]{-1, -1};
        }
    }
    /**
     * Calculates the point for a given position based on the color of the piece.
     * @param row The row of the position.
     * @param column The column of the position.
     * @param colour The color of the piece.
     * @return The calculated point for the position.
     */
    public int CalculatePoint(int row, int column, char colour) {
        try{
            int totalPoint = 0;

            int[] sequences = {
                    TotalRightDiagonalPieces(row, column, colour) + 1,
                    TotalLeftDiagonalPieces(row, column, colour) + 1,
                    TotalHorizontalPieces(row, column, colour) + 1,
                    TotalVerticalPieces(row, column, colour) + 1
            };

            for (int i : sequences) {
                if (i >= 4) {
                    if(i == 4)
                    {
                        totalPoint++;
                    }
                    else{
                        if(( i % 5) == 0){
                            totalPoint += i;
                        }
                        else{
                            totalPoint = ((i / 5) * 5) + ((i % 5) / 4);
                        }
                    }
                }
            }

            return totalPoint;
        }catch (Exception e){
            Log.d("TAG", "CalculatePoint: there was something wrong for calculating point for row "+ row + " column + " + column);
            return -1;
        }
    }
    /**
     * Calculates the total number of captured pieces in various directions from a given position.
     * @param row The row of the position.
     * @param column The column of the position.
     * @param colour The color of the piece.
     * @param removeFlag Flag indicating whether to remove captured pieces.
     * @return The total number of captured pieces.
     */
    public int CalculateTotalCapture(int row, int column, char colour, boolean removeFlag){
        try{
            int capturePoint = 0;
            int[] sequences = {
                    CheckForTotalNumberOfPiece(row + RightDiagUpCode[0], column + RightDiagUpCode[1], GetOppositeColour(colour), RightDiagUpCode, 0),
                    CheckForTotalNumberOfPiece(row + RightDiagDownCode[0], column + RightDiagDownCode[1], GetOppositeColour(colour), RightDiagDownCode, 0),
                    CheckForTotalNumberOfPiece(row + LeftDiagUpCode[0], column + LeftDiagUpCode[1], GetOppositeColour(colour), LeftDiagUpCode, 0),
                    CheckForTotalNumberOfPiece(row + LeftDiagDownCode[0], column + LeftDiagDownCode[1], GetOppositeColour(colour), LeftDiagDownCode, 0),
                    CheckForTotalNumberOfPiece(row + HorizontalRightCode[0], column + HorizontalRightCode[1], GetOppositeColour(colour), HorizontalRightCode, 0),
                    CheckForTotalNumberOfPiece(row + HorizontalLeftCode[0], column + HorizontalLeftCode[1], GetOppositeColour(colour), HorizontalLeftCode, 0),
                    CheckForTotalNumberOfPiece(row + VerticalUpCode[0], column + VerticalUpCode[1], GetOppositeColour(colour), VerticalUpCode, 0),
                    CheckForTotalNumberOfPiece(row + VerticalDownCode[0], column + VerticalDownCode[1], GetOppositeColour(colour), VerticalDownCode, 0),
            };
            for (int i = 0; i < sequences.length; i++) {
                if(sequences[i] == 2){
                    // the point 3 intersection away
                    int[] rowCol = CalculatePontAtPositionXFar(row, column, 3, codeSequence[i]);
                    if(rowCol[0] != -1 && rowCol[1] != -1){
                        if(_board.GetBoard()[rowCol[0]][rowCol[1]] == colour){
                            capturePoint++;
                            if(removeFlag){
                                // remove the pieces in middle
                                int[] oneIntersectionAway = CalculatePontAtPositionXFar(row, column, 1, codeSequence[i]);
                                int[] twoIntersectionAway = CalculatePontAtPositionXFar(row, column, 2, codeSequence[i]);
                                _board.RemovePiece(oneIntersectionAway[0], oneIntersectionAway[1]);
                                _board.RemovePiece(twoIntersectionAway[0], twoIntersectionAway[1]);
                            }
                        }
                    }
                }
            }
            return capturePoint;
        }catch (Exception e){
            Log.e("TAG", "CalculateTotalCapture: Something went wrong with capturing the point");
            return 0;
        }

    }
    /**
     * Calculates the position at a certain distance in a specific direction from a given position.
     * @param row The row of the starting position.
     * @param column The column of the starting position.
     * @param x The distance to calculate.
     * @param directionCode The code indicating the direction of calculation.
     * @return The calculated row and column for the new position.
     */
    public int[] CalculatePontAtPositionXFar(int row, int column, int x, int[] directionCode){
        try{
            int newRow = row + (x * directionCode[0]);
            int newColumn = column + (x * directionCode[1]);
            if (newRow > 18 || newRow < 0) {
                return new int[]{-1, -1};
            }
            if (newColumn > 18 || newColumn < 0) {
                return new int[]{-1, -1};
            }
            return new int[]{newRow, newColumn};
        }catch (Exception e){
            e.printStackTrace();
            return new int[]{-1, -1};
        }
    }
    /**
     * Calculates the total number of pieces in the right diagonal from a given position.
     * @param row The row of the starting position.
     * @param column The column of the starting position.
     * @param colour The color of the piece.
     * @return The total number of pieces in the right diagonal.
     */

    public int TotalRightDiagonalPieces(int row, int column, char colour) {
        return CheckForTotalNumberOfPiece(row + RightDiagUpCode[0], column + RightDiagUpCode[1], colour, RightDiagUpCode, 0)
                + CheckForTotalNumberOfPiece(row + RightDiagDownCode[0], column + RightDiagDownCode[1], colour, RightDiagDownCode, 0);
    }
    /**
     * Calculates the total number of pieces in the left diagonal from a given position.
     * @param row The row of the starting position.
     * @param column The column of the starting position.
     * @param colour The color of the piece.
     * @return The total number of pieces in the left diagonal.
     */

    public int TotalLeftDiagonalPieces(int row, int column, char colour) {
        return CheckForTotalNumberOfPiece(row + LeftDiagUpCode[0], column + LeftDiagUpCode[1], colour, LeftDiagUpCode, 0)
                + CheckForTotalNumberOfPiece(row + LeftDiagDownCode[0], column + LeftDiagDownCode[1], colour, LeftDiagDownCode, 0);
    }
    /**
     * Calculates the total number of pieces in the vertical from a given position.
     * @param row The row of the starting position.
     * @param column The column of the starting position.
     * @param colour The color of the piece.
     * @return The total number of pieces in the vertical.
     */

    public int TotalVerticalPieces(int row, int column, char colour) {
        return CheckForTotalNumberOfPiece(row + VerticalUpCode[0], column + VerticalUpCode[1], colour, VerticalUpCode, 0)
                + CheckForTotalNumberOfPiece(row + VerticalDownCode[0], column + VerticalDownCode[1], colour, VerticalDownCode, 0);
    }
    /**
     * Calculates the total number of pieces in the horizontal from a given position.
     * @param row The row of the starting position.
     * @param column The column of the starting position.
     * @param colour The color of the piece.
     * @return The total number of pieces in the horizontal direction.
     */

    public int TotalHorizontalPieces(int row, int column, char colour) {
        return CheckForTotalNumberOfPiece(row + HorizontalLeftCode[0], column + HorizontalLeftCode[1], colour, HorizontalLeftCode, 0)
                + CheckForTotalNumberOfPiece(row + HorizontalRightCode[0], column + HorizontalRightCode[1], colour, HorizontalRightCode, 0);
    }
    /**
     * Recursively checks the total number of pieces of a specific color in a given direction, following a multiplier code.
     *
     * @param row           The starting row.
     * @param column        The starting column.
     * @param colour        The color to count.
     * @param multiplierCode The direction in which to check.
     * @param totalPieces   The running total of pieces encountered.
     * @return The total number of pieces of the specified color in the given direction.
     */
    public int CheckForTotalNumberOfPiece(int row, int column, char colour, int[] multiplierCode, int totalPieces){
        try{
            if (row < 0 || row > 18) {
                return totalPieces;
            }
            if (column < 0 || column > 18) {
                return totalPieces;
            }
            if (_board.isPositionEmpty(row, column)) {
                return totalPieces;
            }
            if (_board.GetBoard()[row][column] == GetOppositeColour(colour)) {
                return totalPieces;
            }
            return CheckForTotalNumberOfPiece(row + multiplierCode[0], column + multiplierCode[1], colour, multiplierCode, ++totalPieces);
        }catch (Exception e){
            Log.d("TAG", "CalculatePoint: there was something wrong for calculating point for row "+ row + " column + " + column);
            return  -1;
        }
    }
    /**
     * Recursively checks the total number of empty positions in a given direction, following a multiplier code.
     *
     * @param row           The starting row.
     * @param column        The starting column.
     * @param multiplierCode The direction in which to check.
     * @param totalPieces   The running total of empty positions encountered.
     * @return The total number of empty positions in the given direction.
     */
    public int CheckForTotalNumberOfEmptyPiece(int row, int column, int[] multiplierCode, int totalPieces){
        try{
            if (row < 0 || row > 18) {
                return totalPieces;
            }
            if (column < 0 || column > 18) {
                return totalPieces;
            }
            if (!_board.isPositionEmpty(row, column)) {
                return totalPieces;
            }
            return CheckForTotalNumberOfEmptyPiece(row + multiplierCode[0], column + multiplierCode[1], multiplierCode, ++totalPieces);
        }catch (Exception e){
            Log.d("TAG", "CheckForTotalNumberOfEmptyPiece: there was something wrong for calculating point for row "+ row + " column + " + column);
            return  -1;
        }
    }
    /**
     * Finds the best position for building initiative based on the current position of a piece.
     * @param row The row of the current position.
     * @param column The column of the current position.
     * @param colour The color of the piece.
     * @return The best position for building initiative.
     */
    public int[] BestPositionForBuildingInitiative(int row, int column, char colour){

        int[] sequences = {
                CheckForTotalNumberOfEmptyPiece(row + RightDiagUpCode[0], column + RightDiagUpCode[1],RightDiagUpCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + RightDiagDownCode[0], column + RightDiagDownCode[1],RightDiagDownCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + LeftDiagUpCode[0], column + LeftDiagUpCode[1], LeftDiagUpCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + LeftDiagDownCode[0], column + LeftDiagDownCode[1],  LeftDiagDownCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + HorizontalRightCode[0], column + HorizontalRightCode[1], HorizontalRightCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + HorizontalLeftCode[0], column + HorizontalLeftCode[1], HorizontalLeftCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + VerticalUpCode[0], column + VerticalUpCode[1], VerticalUpCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + VerticalDownCode[0], column + VerticalDownCode[1], VerticalDownCode, 0),
        };
        int bestDirIndex = 0;
        int greatestPoint = 0;
        for(int i = 0; i < sequences.length; i++){
            if(greatestPoint < sequences[i]){
                bestDirIndex = i;
                greatestPoint = sequences[i];
            }
        }
        return (greatestPoint >= 3) ? CalculatePontAtPositionXFar(row, column, 3, codeSequence[bestDirIndex]) : new int[]{-1,-1};
    }

    /**
     * Determines the best position to fill for gaining initiative in the game.
     *
     * @param row    The starting row.
     * @param column The starting column.
     * @param colour The color to consider.
     * @return An array representing the best position [row, column] to fill for gaining initiative.
     */
    public int[] BestPositionForFillingInitiative(int row, int column, char colour){
        int[] sequences = {
                CheckForTotalNumberOfEmptyPiece(row + RightDiagUpCode[0], column + RightDiagUpCode[1],RightDiagUpCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + RightDiagDownCode[0], column + RightDiagDownCode[1],RightDiagDownCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + LeftDiagUpCode[0], column + LeftDiagUpCode[1], LeftDiagUpCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + LeftDiagDownCode[0], column + LeftDiagDownCode[1],  LeftDiagDownCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + HorizontalRightCode[0], column + HorizontalRightCode[1], HorizontalRightCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + HorizontalLeftCode[0], column + HorizontalLeftCode[1], HorizontalLeftCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + VerticalUpCode[0], column + VerticalUpCode[1], VerticalUpCode, 0),
                CheckForTotalNumberOfEmptyPiece(row + VerticalDownCode[0], column + VerticalDownCode[1], VerticalDownCode, 0),
        };
        for (int i = 0; i < sequences.length; i++) {
            if(sequences[i] == 2){
                // the point 3 intersection away
                int[] rowCol = CalculatePontAtPositionXFar(row, column, 3, codeSequence[i]);
                    if (rowCol[0] != -1 ) {
                        if (_board.GetBoard()[rowCol[0]][rowCol[1]] == colour) {
                            int[] oneIntersectionAway = CalculatePontAtPositionXFar(row, column, 1, codeSequence[i]);
                            if(_board.isPositionEmpty(oneIntersectionAway[0], oneIntersectionAway[1])){
                                return  oneIntersectionAway;
                            }
                        }
                    }

            }
        }
        return new int[]{-1, -1};
    }
    /**
     * Calculates the best position for gaining points on the board for a given color.
     *
     * @param colour The color to consider.
     * @return An array representing the best position [row, column, points] for gaining points.
     */
    public int[] CalculateBestPositionForPoints(char colour){
        try{
            int[] greatestPosition = new int[3];
            int greatestPoint = 0;
            for (int row = 0; row < 19; row++) {
                for (int column = 0; column < 19; column++) {
                    if (_board.isPositionEmpty(row, column)) {
                        int tmpPoint = CalculatePoint(row, column, colour);
                        if(row ==2 & column == 2){
                            Log.e("TAG", "CalculateBestPositionForPoints: here");
                        }
                        if (tmpPoint > greatestPoint) {
                            greatestPosition[0] = row;
                            greatestPosition[1] = column;
                            greatestPosition[2] = tmpPoint;
                            greatestPoint = tmpPoint;
                        }
                    }
                }
            }
            return greatestPosition;
        } catch (Exception e) {
            Log.e("TAG", "CalculateBestPositionForPoints: something went wrong");
            return new int[]{-1, -1, -1};
        }
    }
    /**
     * Calculates the best position for capturing opponent pieces.
     *
     * @param colour The color of the player making the calculation.
     * @return An array representing the best position [row, column, points] for capturing opponent pieces.
     */
    public int[] CalculateBestPositionForCapture(char colour) {
        int[] greatestPosition = new int[3];
        int greatestPoint = 0;
        for (int row = 0; row < 19; row++) {
            for (int column = 0; column < 19; column++) {
                if (row == 7 && column == 9) {
                    Log.d("TAG", "CalculateBestPositionForCapture: ");
                }
                if (_board.isPositionEmpty(row, column)) {
                    int tmpPoint = CalculateTotalCapture(row, column, colour, false);
                    if (tmpPoint > greatestPoint) {
                        greatestPosition[0] = row;
                        greatestPosition[1] = column;
                        greatestPosition[2] = tmpPoint;
                        greatestPoint = tmpPoint;
                    }
                }
            }
        }
        return greatestPosition;
    }

    /**
     * Calculates the best position for filling initiative on the board for a given color.
     *
     * @param colour The color to consider.
     * @return An array representing the best position [row, column] for filling initiative.
     */
    public int[] CalculateBestPositionForFillingInitiative(char colour) {
        int[] bestPosition = new int[]{-1, -1};
        for (int row = 0; row < 19; row++) {
            for (int column = 0; column < 19; column++) {
                if (_board.GetBoard()[row][column] == colour) {
                    int[] tmpPoint = BestPositionForFillingInitiative(row, column, colour);
                    if (tmpPoint[0] >= 0 && tmpPoint[1] >= 0) {
                        bestPosition[0] = tmpPoint[0];
                        bestPosition[1] = tmpPoint[1];
                        return bestPosition;
                    }
                }
            }
        }
        return bestPosition;
    }

    /**
     * Calculates the best position for building initiative on the board for a given color.
     *
     * @param colour The color to consider.
     * @return An array representing the best position [row, column] for building initiative.
     */
    public int[] CalculateBestPositionForBuildingInitiative(char colour) {
        int[] bestPosition = new int[]{-1, -1};
        for (int row = 0; row < 19; row++) {
            for (int column = 0; column < 19; column++) {
                if (_board.GetBoard()[row][column] == colour) {
                    int[] tmpPoint = BestPositionForBuildingInitiative(row, column, colour);
                    if (tmpPoint[0] >= 0 && tmpPoint[1] >= 0) {
                        bestPosition[0] = tmpPoint[0];
                        bestPosition[1] = tmpPoint[1];
                        return bestPosition;
                    }
                }
            }
        }
        return bestPosition;
    }

    /**
     * Determines the best move for the given color, taking into account various strategies.
     *
     * @param colour       The color of the player making the move.
     * @param gameActivity The GameActivity instance associated with the game.
     * @param totalMove    The total number of moves played in the game.
     * @param fromHelp     Indicates whether the move is generated from a help request.
     * @return An array representing the best move [row, column] for the given color.
     */
    public int[] BestMove(char colour, GameActivity gameActivity, int totalMove, boolean fromHelp){
        // test bestMove for capture and other stuff here
        if(totalMove == 0 && colour == WHITE_PIECE){
            if(fromHelp){
                gameActivity.CreateDialogBox("Since it this is the first move place it in the center of the board" + ConvertToLabelledRowColumn(9, 9));
            }
            gameActivity.AddLogMessage("Since it this is the first move place it in the center of the board" + ConvertToLabelledRowColumn(9,9));
            return new int[] {9, 9};
        }
        if(totalMove == 1 && colour == WHITE_PIECE){
            int[] pointCalculation = GenerateSecondRandom();
            if(fromHelp){
                gameActivity.CreateDialogBox("Since it this is the second move place it three intersection away from the center: " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1]));
            }
            gameActivity.AddLogMessage("Since it this is the second move place it three intersection away from the center: " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1]));
            return pointCalculation;
        }
        //finish 5 in row
        Log.d("TAG", "BestMove: Checking 5 in row");
        int[] pointCalculation = CalculateBestPositionForPoints(colour);
        if(pointCalculation[2] >= 5 ){
            if(fromHelp){
                gameActivity.CreateDialogBox("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will score 5 in row and you will win game");
            }
            gameActivity.AddLogMessage("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will score 5 in row and you will win game" );
            Log.d("TAG", "BestMove: This is the best move for winning the game");
            return pointCalculation;
        }
        // block 5 in a row
        Log.d("TAG", "BestMove: Checking blocking 5 in a row");
        pointCalculation = CalculateBestPositionForPoints(GetOppositeColour(colour));
        if(pointCalculation[2] >= 5 ){
            if(fromHelp){
                gameActivity.CreateDialogBox("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will block 5 in row");
            }
            gameActivity.AddLogMessage("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will block 5 in row" );

            Log.d("TAG", "BestMove: This is the best move for blocking opponent from wining the game");
            return pointCalculation;
        }
        //capture pieces
        Log.d("TAG", "BestMove:Checking capturing");
        pointCalculation = CalculateBestPositionForCapture(colour);
        if(pointCalculation[2] >= 1){
            if(fromHelp){
                gameActivity.CreateDialogBox("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will capture opponents piece(s)");
            }
            gameActivity.AddLogMessage("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will capture opponents piece(s)" );
            Log.d("TAG", "BestMove: This is the best move for capturing the opponent's pieces");
            return pointCalculation;
        }
        // stop form being captured
        Log.d("TAG", "BestMove:Blocking capture");
        pointCalculation = CalculateBestPositionForCapture(GetOppositeColour(colour));
        if(pointCalculation[2] >= 1){
            if(fromHelp){
                gameActivity.CreateDialogBox("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will prevent piece form being captured");
            }
            gameActivity.AddLogMessage("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will prevent piece form being captured" );
            Log.d("TAG", "BestMove: This is the best move for blocking from being captured");
            return pointCalculation;
        }
        //blocking
        Log.d("TAG", "BestMove:Checking blocking opponent");
        pointCalculation = CalculateBestPositionForPoints(GetOppositeColour(colour));
        if(pointCalculation[2] >= 1 ){
            if(fromHelp){
                gameActivity.CreateDialogBox("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will block opponent from scoring");
            }
            gameActivity.AddLogMessage("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will block opponent from scoring" );

            Log.d("TAG", "BestMove: This is the best position for blocking the maximum point");
            return pointCalculation;
        }
        //score
        Log.d("TAG", "BestMove:Checking for scoring");
        pointCalculation = CalculateBestPositionForPoints(colour);
        if(pointCalculation[2] > 0){
            if(fromHelp){
                gameActivity.CreateDialogBox("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will score " + pointCalculation[2] + " point(s)" );
            }
            gameActivity.AddLogMessage("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " will score " + pointCalculation[2] + " point(s)" );
            Log.d("TAG", "BestMove: This is the best position for scoring the maximum point");
            return pointCalculation;
        }

        //blocking sequence

        pointCalculation = CalculateSequenceInRow(GetOppositeColour(colour));
        if(pointCalculation[0] != -1){
            if(fromHelp){
                gameActivity.CreateDialogBox("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " block initiative for "+ pointCalculation[2]+ " in row"  );
            }
            gameActivity.AddLogMessage("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " block initiative for "+ pointCalculation[2]+ " in row" );

            Log.d("TAG", "BestMove: This is the best position for blocking row of opponents piece");

            return pointCalculation;
        }
        // building sequence
        pointCalculation = CalculateSequenceInRow(colour);
        if(pointCalculation[0] != -1){
            if(fromHelp){
                gameActivity.CreateDialogBox("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " start initiative for 4 in row"  );
            }
            gameActivity.AddLogMessage("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " start initiative for 4 in row" );

            Log.d("TAG", "BestMove: This is the best position for building sequence");

            return pointCalculation;
        }

        Log.d("TAG", "BestMove:Checking for blocking filling initiative");
        pointCalculation = CalculateBestPositionForFillingInitiative(GetOppositeColour(colour));
        if(pointCalculation[0] != -1){
            if(fromHelp){
                gameActivity.CreateDialogBox("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " block initiative for 4 in row"  );
            }
            gameActivity.AddLogMessage("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " block initiative for 4 in row" );

            Log.d("TAG", "BestMove: Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " block initiative for 4 in row");

            return pointCalculation;
        }
        Log.d("TAG", "BestMove:Checking filling initiative");
        pointCalculation = CalculateBestPositionForFillingInitiative(colour);
        if(pointCalculation[0] != -1){
            if(fromHelp){
                gameActivity.CreateDialogBox("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " filling initiative for 4 in row"  );
            }
            gameActivity.AddLogMessage("Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " filling initiative for 4 in row" );
            Log.d("TAG", "BestMove: Placing the piece in " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1])+ " filling initiative for 4 in row" );
            return pointCalculation;
        }
        Log.d("TAG", "BestMove:Checking for building initiative");
        pointCalculation = CalculateBestPositionForBuildingInitiative(colour);
        if(pointCalculation[0] != -1){
            if(fromHelp){
                gameActivity.CreateDialogBox("BestMove: This is the best position for starting the initiative : " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1]) );
            }
            gameActivity.AddLogMessage("BestMove: This is the best position for starting the initiative : " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1]) );
            Log.d("TAG", "BestMove: This is the best position for starting the initiative: " + ConvertToLabelledRowColumn(pointCalculation[0], pointCalculation[1]));
            return pointCalculation;
        }
        if(fromHelp){
            gameActivity.CreateDialogBox("This is random position since no other options were valid");
        }
        Log.d("TAG", "BestMove:Checking for random position");
        gameActivity.AddLogMessage("Generating a random position" );
        return GenerateRandom();
    }
    /**
     * Calculates the sequence of pieces in a row for the given color, considering diagonals, horizontal, and vertical directions.
     *
     * @param colour The color for which to calculate the sequence.
     * @return An array [row, column, length] representing the position and length of the calculated sequence, or [-1, -1, -1] if no sequence is found.
     */
   public int[] CalculateSequenceInRow(char colour){
        for (int row = 0; row < 19; row++) {
            for (int column = 0; column < 19; column++) {
                if(_board.isPositionEmpty(row, column)){
                    int[] sequences = {
                            TotalRightDiagonalPieces(row, column, colour) + 1,
                            TotalLeftDiagonalPieces(row, column, colour) + 1,
                            TotalHorizontalPieces(row, column, colour) + 1,
                            TotalVerticalPieces(row, column, colour) + 1
                    };
                    for (int i : sequences) {
                        if (i > 2) {
                            return new int[]{row, column, i};
                        }
                    }
                }
            }
        }
        return new int[]{-1,-1, -1};
    }
    /**
     * Calculates the total score of the loaded round for the given color.
     *
     * @param colour The color for which to calculate the score.
     * @return The total score for the loaded round, or -1 in case of an error.
     */
    public int CalculatedScoreInLoadedRound(char colour){
        try{
            int totalScore = 0;
            // holds the board temporarily
            char[][] tmpBoard = new char[19][19];

            for (int row = 0; row < _board.GetBoard().length; row++)
            {
                for (int column = 0; column < _board.GetBoard().length; column++)
                {
                    tmpBoard[row][column] = _board.GetBoard()[row][column];
                }
            }
            for (int row = 0; row < _board.GetBoard().length; row++)
            {
                for (int column = 0; column < _board.GetBoard().length; column++)
                {
                    if(!_board.isPositionEmpty(row, column)){
                        totalScore += CalculatePoint(row, column, colour);
                        if(!_board.RemovePiece(row, column)){
                            Log.e("TAG", "CalculatedScoreInLoadedRound: Internal Server Error: failed to remove a piece");
                            return -1;
                        }
                    }
                }
            }
            if (!_board.SetBoard(tmpBoard)) {
                Log.e("TAG", "CalculatedScoreInLoadedRound: Internal Server Error: CalculatedScoreInLoadedRound Failed to set the board");
            }
            return totalScore;
        }catch (Exception e){
            Log.d("TAG", "CalculatedScoreInLoadedRound: Could not caculate the captured piece in the loaded round");
            return -1;
        }
    }
}
