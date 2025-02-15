package com.example.clashofcounters;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class Main_game extends AppCompatActivity {

    private FloatingActionButton fabLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);



        Button addButton = findViewById(R.id.add);
        Button subtractButton = findViewById(R.id.minus);
        Button multiplyButton = findViewById(R.id.times);
        Button viewHighScoresButton = findViewById(R.id.viewHighScores);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameWithOperation(0);
            }
        });

        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameWithOperation(1);
            }
        });

        multiplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameWithOperation(2);
            }
        });
        viewHighScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_game.this, HighscoresActivity.class);
                startActivity(intent);
            }
        });
        fabLogout = findViewById(R.id.floatingActionButtonLogout);

        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void startGameWithOperation(int operation) {
        Intent intent = new Intent(Main_game.this, GameActivity.class);
        intent.putExtra("operation", operation);
        startActivity(intent);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Main_game.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}