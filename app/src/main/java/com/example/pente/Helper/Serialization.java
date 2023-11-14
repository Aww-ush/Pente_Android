package com.example.pente.Helper;

import static com.example.pente.Controller.TournamentController.computerCode;
import static com.example.pente.Controller.TournamentController.humanCode;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import com.example.pente.Model.Board;
import com.example.pente.Model.Player;
import com.example.pente.View.GameActivity;
import com.example.pente.View.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The Serialization class is responsible for parsing and saving game data to and from files.
 * It includes methods for parsing the game state from a file, saving the game state to a file,
 * and displaying the content of a file.
 */
public class Serialization {

    private char[][] board;
    private int humanCapturePoint;
    private int computerCapturePoint;
    private int humanScore;
    private int computerScore;
    private int nextMover;
    private char humanColour;
    private char computerColour;

    /**
     * Default constructor initializes the board and sets the next mover.
     */
    public Serialization() {
        this.board = new char[19][19];
        this.nextMover = 0;
    }

    /**
     * Parses the game state from a file.
     *
     * @param filePath The path to the file containing the game state.
     * @return True if parsing is successful, false otherwise.
     */
    public boolean parseFile(String filePath) {
        BufferedReader fileReader = null;
        try {
            // Open the file for reading
            fileReader = new BufferedReader(new FileReader(filePath));
            if (fileReader == null) {
                // If fileReader is still null, the file couldn't be opened
                Log.d("TAG", "Could not open the file");
                return false;
            }

            String line;
            int count = 0;

            while ((line = fileReader.readLine()) != null) {
                // Parsing the game board
                if (count > 0 && count <= 19) {
                    List<Character> row = new ArrayList<>();

                    for (char c : line.toCharArray()) {
                        row.add(c);
                    }

                    for (int i = 0; i < line.length(); i++) {
                        board[count - 1][i] = line.charAt(i);
                    }
                }
                // Parsing capture points
                else if (count == 22 || count == 26) {
                    if (count == 22)
                        humanCapturePoint = returnCaptureOrScore(line);
                    else
                        computerCapturePoint = returnCaptureOrScore(line);
                }
                // Parsing scores
                else if (count == 23 || count == 27) {
                    if (count == 23)
                        humanScore = returnCaptureOrScore(line);
                    else
                        computerScore = returnCaptureOrScore(line);
                }
                // Parsing next player and colour
                else if (count == 29) {
                    setColour(line);
                }
                count++;
            }

            // Close the fileReader after reading
            fileReader.close();
            return true;

        } catch (FileNotFoundException e) {
            // Handle the case when the file is not found
            Log.e("TAG", "File not found: " + filePath, e);
            return false;

        } catch (IOException e) {
            // Handle other IOExceptions
            Log.e("TAG", "Error while reading the file", e);
            return false;

        } finally {
            try {
                // Close the fileReader in case of an exception
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                Log.e("TAG", "Error closing fileReader", e);
            }
        }
    }

    /**
     * Parses the capture points or scores from a line.
     *
     * @param line The line containing capture points or scores.
     * @return The parsed value.
     */
    private int returnCaptureOrScore(String line) {
        int found = line.indexOf(':');
        if (found == -1) {
            System.err.println("Internal Server Error: Could not split the string.");
            return 0;
        }
        String substring = line.substring(found + 1).trim();
        return Integer.parseInt(substring);
    }

    /**
     * Sets the colour of the players based on the parsed line.
     *
     * @param line The line containing player information.
     */
    private void setColour(String line) {
        try {
            int found = line.indexOf(':');
            if (found == -1) {
                System.err.println("Internal Server Error: Could not split the string.");
            }
            String substring = line.substring(found + 1).trim();
            int findDash = substring.indexOf('-');
            String nextPlayer = substring.substring(0, findDash).trim();
            String nextPlayerColour = substring.substring(findDash + 1).trim();

            if ("Human".equals(nextPlayer)) {
                nextMover = humanCode;
            } else {
                nextMover = computerCode;
            }

            if (nextMover == humanCode) {
                if ("White".equals(nextPlayerColour)) {
                    humanColour = Board.WHITE_PIECE;
                    computerColour = Board.BLACK_PIECE;
                } else {
                    humanColour = Board.BLACK_PIECE;
                    computerColour = Board.WHITE_PIECE;
                }
            } else {
                if ("White".equals(nextPlayerColour)) {
                    computerColour = Board.WHITE_PIECE;
                    humanColour = Board.BLACK_PIECE;
                } else {
                    computerColour = Board.BLACK_PIECE;
                    humanColour = Board.WHITE_PIECE;
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Internal Server Error: Setting Colour for Human and Computer");
        }
    }

    /**
     * Saves the current game state to a file.
     *
     * @param board            The game board.
     * @param human            The human player.
     * @param computer         The computer player.
     * @param nextPlayer       The next player.
     * @param nextPlayerColour The colour of the next player.
     */
    public static void SaveGame(Board board, Player human, Player computer, String nextPlayer, String nextPlayerColour) {
        String serializeDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
        final File directory = new File(serializeDirectory);
        File txtFile = new File(directory, "Game" + ".txt");
        // creating a text file.
        FileOutputStream fos = null;
        String gameBoard = "";
        for (int row = 0; row < 19; row++) {
            for (int column = 0; column < 19; column++) {
                gameBoard = gameBoard + board.GetBoard()[row][column];
            }
            gameBoard += '\n';
        }
        String gameData = "Board:\n" +
                gameBoard +
                "\n" +
                "Human:\n" +
                "Captured pairs: " + human.GetCapturePoints() + "\n" +
                "Score: " + human.GetTotalPoints() + "\n" +
                "\n" +
                "Computer:\n" +
                "Captured pairs: " + computer.GetCapturePoints() + "\n" +
                "Score: " + computer.GetTotalPoints() + "\n" +
                "\n" +
                "Next Player: " + nextPlayer + " - " + nextPlayerColour;
        try {
            fos = new FileOutputStream(txtFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(gameData);
            osw.flush();
            osw.close();
            fos.close();

        } catch (Exception e) {
            // on below line handling the exception.
            e.printStackTrace();
        }
    }

    /**
     * Displays the content of a file.
     *
     * @param filePath The path to the file.
     * @return The content of the file as a string.
     */
    public static String DisplayFileContent(String filePath) {
        BufferedReader reader = null;
        String content = "";

        try {
            // Open the file for reading
            reader = new BufferedReader(new FileReader(filePath));
            String line;

            // Read each line and append to the content StringBuilder
            while ((line = reader.readLine()) != null) {
                content += line + "\n";
            }
        } catch (IOException e) {
            // Handle exceptions, e.g., file not found or cannot be read
            Log.e("TAG", "DisplayFileContent: file not found");
        } finally {
            try {
                // Close the BufferedReader
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Handle exceptions related to closing the file
                e.printStackTrace();
            }
        }

        // Return the content as a string
        return content;
    }

    /**
     * Gets the game board.
     *
     * @return The game board.
     */
    public char[][] GetBoard() {
        return board;
    }

    /**
     * Gets the colour of the human player.
     *
     * @return The colour of the human player.
     */
    public char GetHumanColour() {
        return humanColour;
    }

    /**
     * Gets the colour of the computer player.
     *
     * @return The colour of the computer player.
     */
    public char GetComputerColour() {
        return computerColour;
    }

    /**
     * Gets the score of the human player.
     *
     * @return The score of the human player.
     */
    public int GetHumanScore() {
        return humanScore;
    }

    /**
     * Gets the score of the computer player.
     *
     * @return The score of the computer player.
     */
    public int GetComputerScore() {
        return computerScore;
    }

    /**
     * Gets the capture points of the human player.
     *
     * @return The capture points of the human player.
     */
    public int GetHumanCapturePoints() {
        return humanCapturePoint;
    }

    /**
     * Gets the capture points of the computer player.
     *
     * @return The capture points of the computer player.
     */
    public int GetComputerCapturePoints() {
        return computerCapturePoint;
    }

    /**
     * Gets the next mover.
     *
     * @return The next mover.
     */
    public int GetNextMover() {
        return nextMover;
    }
}
