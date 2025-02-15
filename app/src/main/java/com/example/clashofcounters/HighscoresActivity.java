package com.example.clashofcounters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HighscoresActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HighscoresAdapter adapter;
    private List<UserHighscore> highscoresList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        highscoresList = new ArrayList<>();
        adapter = new HighscoresAdapter(highscoresList);
        recyclerView.setAdapter(adapter);

        Button btnBackToChooseGame = findViewById(R.id.btnBackToChooseGame);
        btnBackToChooseGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HighscoresActivity.this, Main_game.class);
                startActivity(intent);
                finish();
            }
        });


        fetchHighscoresFromDatabase();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void fetchHighscoresFromDatabase() {
        DatabaseReference highscoreRef = FirebaseDatabase.getInstance().getReference("user_highscores");

        Map<String, Integer> highestScoresMap = new HashMap<>();

        highscoreRef.orderByChild("highscore").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                updateUserHighscore(dataSnapshot, highestScoresMap);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                updateUserHighscore(dataSnapshot, highestScoresMap);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserHighscore(DataSnapshot dataSnapshot, Map<String, Integer> highestScoresMap) {
        UserHighscore userHighscore = dataSnapshot.getValue(UserHighscore.class);
        if (userHighscore != null) {
            if (highestScoresMap.containsKey(userHighscore.getUserEmail())) {
                int currentScore = userHighscore.getHighscore();
                int highestScore = highestScoresMap.get(userHighscore.getUserEmail());
                if (currentScore > highestScore) {
                    highestScoresMap.put(userHighscore.getUserEmail(), currentScore);
                    highscoresList.removeIf(entry -> entry.getUserEmail().equals(userHighscore.getUserEmail()));
                    highscoresList.add(userHighscore);
                }
            } else {
                highestScoresMap.put(userHighscore.getUserEmail(), userHighscore.getHighscore());
                highscoresList.add(userHighscore);
            }


            Collections.sort(highscoresList, (user1, user2) -> Integer.compare(user2.getHighscore(), user1.getHighscore()));

            adapter.notifyDataSetChanged();
        }
    }





}