package com.example.pente.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pente.Controller.CoinTossController;
import com.example.pente.Model.Coin;
import com.example.pente.R;

public class CoinTossResultActivity extends AppCompatActivity {

    private TextView coinResultMessage;
    private ImageView resultCoinFace;
    /**
     * Displays the result of coin toss activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Log.i("TAG", "onCreate: created CoinToss result activity");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_coin_toss_result);

            coinResultMessage = findViewById(R.id.coinResultMessage);
            resultCoinFace = findViewById(R.id.resultImage);
            String face = getIntent().getStringExtra("face");
            String message = getIntent().getStringExtra("message");

            coinResultMessage.setText(message);
            if (face.equals(Coin.HEAD)) {
                resultCoinFace.setImageResource(R.mipmap.coin_head);
            } else {
                resultCoinFace.setImageResource(R.mipmap.ic_launcher_foreground);
            }

            Button okayButton = findViewById(R.id.okayButton);
            okayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
