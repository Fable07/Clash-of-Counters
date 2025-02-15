package com.example.clashofcounters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayActivity extends AppCompatActivity {
    private Button buttonPlayAgain , buttonViewScores;
    private Button buttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        buttonViewScores = findViewById(R.id.buttonViewScores);

        Toast.makeText(this, "GAME OVER", Toast.LENGTH_SHORT).show();

        buttonPlayAgain = findViewById(R.id.buttonPlayAgain);
        buttonExit = findViewById(R.id.buttonExit);

        Intent intent = getIntent();
        int userScore = intent.getIntExtra("userScore", 0);

        TextView textScore = findViewById(R.id.textScore);
        textScore.setText("YOUR SCORE: " + userScore);

        buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DisplayActivity.this, Main_game.class);
                startActivity(intent);
                finish();
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finishAffinity();
            }
        });
        buttonViewScores.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, HighscoresActivity.class);
                startActivity(intent);
            }
        });
    }
}
