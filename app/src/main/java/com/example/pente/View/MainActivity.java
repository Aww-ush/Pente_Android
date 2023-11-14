package com.example.pente.View;

import static androidx.activity.result.contract.ActivityResultContracts.*;
import static androidx.core.content.ContextCompat.startActivity;

import static com.example.pente.Helper.Serialization.DisplayFileContent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pente.Controller.TournamentController;
import com.example.pente.Helper.Serialization;
import com.example.pente.Model.Board;
import com.example.pente.Model.Computer;
import com.example.pente.Model.Human;
import com.example.pente.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;
/**
 * MainActivity class represents the main activity of the Pente game.
 * This class includes methods for starting a new game, handling the result of a coin flip,
 * loading a saved game, and displaying the content of a file.
 */

public class MainActivity extends AppCompatActivity {
    public static final int COIN_FLIP_REQUEST_CODE = 1;
    private boolean coinTossResult = false;

    /**
     * onCreate method is called when the activity is first created.
     * It sets the content view to the specified layout.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /**
     * DisplayFileContent method reads and returns the content of a file.
     *
     * @param filePath The path of the file to be read.
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
        return  content;

    }

    /**
     * StartNewGame method is called when the "Start New Game" button is clicked.
     * It starts the CoinFlipActivity using startActivityForResult.
     *
     * @param v The view that was clicked.
     */
    public void StartNewGame(View v) {
        Log.i("TAG", "StartNewGame: this method was clicked");
        // Start CoinFlipActivity with startActivityForResult
        Intent intent = new Intent(this, CoinFlipActivity.class);
        startActivityForResult(intent, COIN_FLIP_REQUEST_CODE);
    }
    /**
     * onActivityResult method is called when an activity launched by startActivityForResult finishes.
     * It handles the result from CoinFlipActivity and starts the game accordingly.
     *
     * @param requestCode The request code passed to startActivityForResult.
     * @param resultCode  The result code returned from the child activity.
     * @param data        An Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COIN_FLIP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Handle the result from CoinFlipActivity
                coinTossResult = data.getBooleanExtra("result", false);

                // Now that you have the result, proceed to start the game
                char humanColour, computerColour;
                int nextMover;

                if (coinTossResult) {
                    humanColour = Board.WHITE_PIECE;
                    computerColour = Board.BLACK_PIECE;
                    nextMover = TournamentController.humanCode;
                } else {
                    computerColour = Board.WHITE_PIECE;
                    humanColour = Board.BLACK_PIECE;
                    nextMover = TournamentController.computerCode;
                }
                Log.i("TAG", "coin toss made");

                Board board = new Board();
                Log.d("TAG", "onActivityResult: the human " + humanColour);
                Log.d("TAG", "onActivityResult: the computer " + computerColour);
                Human human = new Human(humanColour);

                Log.d("TAG", "onActivityResult: the human2 " +  human.GetColour());

                Computer computer = new Computer(computerColour);

                Intent tournamentIntent = new Intent(this, GameActivity.class);
                tournamentIntent.putExtra("humanColour", humanColour);
                tournamentIntent.putExtra("computerColour", computerColour);
                tournamentIntent.putExtra("board", board.GetBoard());
                tournamentIntent.putExtra("nextMover", nextMover);
                startActivity(tournamentIntent);

            }
        }


    }

    /**
     * LoadGame method is called when the "Load Game" button is clicked.
     * It displays a dialog to select a saved game file and loads the game accordingly.
     *
     * @param v The view that was clicked.
     */
    public void LoadGame(View v) {
        Log.i("TAG", "LoadGame: this method was clicked");
        AlertDialog.Builder loadFileDialog = new AlertDialog.Builder(MainActivity.this);
        loadFileDialog.setTitle("Select a saved game");

        String serializeDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
        final File directory = new File(serializeDirectory);
        File[] filesList = directory.listFiles();

        Vector<String> filesNames = new Vector<>();

        for (File file : filesList) {
            String fileName = file.getName();
            if (fileName.endsWith(".txt")) {
                filesNames.add(fileName);
            }
        }

        String[] fileNamesArray = new String[filesNames.size()];
        fileNamesArray = filesNames.toArray(fileNamesArray);

        loadFileDialog.setItems(fileNamesArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView select = ((AlertDialog) dialog).getListView();
                String fileName = (String) select.getAdapter().getItem(which);

                // Read and display the content of the selected file
                String filePath = directory + "/" + fileName;
                String fileContent = DisplayFileContent(filePath);

                // Show a dialog with the file content
                AlertDialog.Builder contentDialog = new AlertDialog.Builder(MainActivity.this);
                contentDialog.setTitle("File Content");
                contentDialog.setMessage(fileContent);
                contentDialog.setPositiveButton("Load", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Serialization serialization = new Serialization();
                        if(!serialization.parseFile(filePath)){
                            // file not found show read message under this input and ask to enter valid file, do not close the dialog until user enters valid file or presses cancel
                            Log.d("TAG", "onFileNameEntered: file could not be opened");
                            //ShowInputDialog(MainActivity.this, "File could not be opened", this::onFileNameEntered);

                        }
                        else{// Now that you have the result, proceed to start the game

                            Board board = new Board(serialization.GetBoard());
                            int nextMover = serialization.GetNextMover();
                            char humanColour = serialization.GetHumanColour();
                            char computerColour = serialization.GetComputerColour();
                            int humanTotalPoint = serialization.GetHumanScore();
                            int computerTotalPoint = serialization.GetComputerScore();
                            int humanCapturePoint = serialization.GetHumanCapturePoints();
                            int computerCapturePoint = serialization.GetComputerCapturePoints();

                            Intent tournamentIntent = new Intent(MainActivity.this, GameActivity.class);
                            tournamentIntent.putExtra("humanColour", humanColour);
                            tournamentIntent.putExtra("computerColour", computerColour);
                            tournamentIntent.putExtra("humanTotalPoint", humanTotalPoint);
                            tournamentIntent.putExtra("computerTotalPoint", computerTotalPoint);
                            tournamentIntent.putExtra("humanCapturePoint", humanCapturePoint);
                            tournamentIntent.putExtra("computerCapturePoint", computerCapturePoint);
                            tournamentIntent.putExtra("isLoaded", true);
                            tournamentIntent.putExtra("board", board.GetBoard());
                            tournamentIntent.putExtra("nextMover", nextMover);
                            startActivity(tournamentIntent);
                        }
                    }
                });
                contentDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                contentDialog.show();
            }
        });
        loadFileDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        loadFileDialog.show();
    }



}