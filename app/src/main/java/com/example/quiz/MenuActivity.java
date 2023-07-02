package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuActivity extends FullScreenActivity {
    RelativeLayout startGameButton;
    ImageView settingsButton;
    RelativeLayout shopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SharedPreferences prefs = ((MyApplication) getApplication()).getPrefs();
        int currentCoins = prefs.getInt("coins", 100);
        TextView coinTextView = findViewById(R.id.coinText);
        coinTextView.setText(String.valueOf(currentCoins));


        startGameButton = findViewById(R.id.startGameButton);
        settingsButton = findViewById(R.id.settingsButton);
        shopButton = findViewById(R.id.shopButton);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the dashboard activity
               Intent intent = new Intent(MenuActivity.this, SplashActivity.class);
                startActivity(intent);


            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });



    }
}