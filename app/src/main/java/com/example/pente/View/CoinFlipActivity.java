// CoinFlipActivity.java
package com.example.pente.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pente.Controller.CoinTossController;
import com.example.pente.Model.Coin;
import com.example.pente.R;
/**
 * CoinTossActivity represents the activity for the coin toss screen.
 * Users can choose heads or tails, and the result is determined by the CoinTossController.
 */
public class CoinFlipActivity extends AppCompatActivity {

    CoinTossController CoinController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cointoss);
        CoinController = new CoinTossController();
        Button headButton = findViewById(R.id.coinHead);
        Button tailButton = findViewById(R.id.coinTail);

        headButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleCoinFlip(v, Coin.HEAD);
            }
        });
        tailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleCoinFlip(v, Coin.TAIL);
            }
        });
    }
    /**
     * Handles the coin flip when a button is clicked.
     *
     * @param v     The clicked button's view.
     * @param usrInput The user's input (HEAD or TAIL).
     */
    public void HandleCoinFlip(View v, String usrInput) {
        Log.d("TAG", "HandleCoinFlip: Clicked ");
        boolean result = CoinController.OnClickHandler(v, usrInput);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", result);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
