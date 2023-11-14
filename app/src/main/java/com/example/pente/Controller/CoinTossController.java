package com.example.pente.Controller;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.pente.Model.Coin;
import com.example.pente.View.CoinTossResultActivity;
import com.example.pente.View.GameActivity;

import java.util.Random;
/**
 * CoinTossController handles the logic for a coin toss in a game.
 * It provides methods to determine the winner or loser based on the user's input.
 */
public class CoinTossController {
    private static final String playerWonMessage = "The face you choose has landed. You will be WHITE";
    private static final String playerLostMessage = "The face you choose did not land. You will be BLACK";
    /**
     * Determines whether the user won or lost the coin toss.
     *
     * @param usrInput The user's input representing the chosen face (HEAD or TAIL).
     * @return True if the user's input matches the coin toss result, false otherwise.
     */
    public boolean WinOrLose(String usrInput) {
        try {
            Random random = new Random();
            int randomNumber = random.nextInt(2);
            String result = (randomNumber == 0) ? Coin.HEAD : Coin.TAIL;
            return usrInput.equals(result);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Could not calculate coin toss winner or loser");
            return false;
        }
    }
    /**
     * Handles the onClick event for the coin toss, determines the result, and displays it.
     *
     * @param view     The view from which the method is called.
     * @param usrInput The user's input representing the chosen face (HEAD or TAIL).
     * @return True if the user won the coin toss, false otherwise.
     */
    public boolean OnClickHandler(View view, String usrInput) {
        try {
            boolean result = WinOrLose(usrInput);
            String message = result ? playerWonMessage : playerLostMessage;
            String face = (result) ? usrInput : GetOppositeFace(usrInput);

            // Create an Intent to start CoinTossResultActivity
            Intent intent = new Intent(view.getContext(), CoinTossResultActivity.class);
            intent.putExtra("face", face);
            intent.putExtra("message", message);

            // Start the CoinTossResultActivity using startActivity
            view.getContext().startActivity(intent);

            // After returning from displaying result
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Could not calculate coin toss winner or loser");
            return false;
        }
    }

    /**
     * Gets the opposite face of the given face.
     *
     * @param usrInput The input face (HEAD or TAIL).
     * @return The opposite face.
     */
    private String GetOppositeFace(String usrInput){
        return (usrInput == Coin.HEAD) ? Coin.TAIL : Coin.HEAD;
    }
}
