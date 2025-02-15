package com.example.clashofcounters;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private TextView textScore;
    private TextView textLife;
    private String userEmail;
    private TextView textTimer;
    private TextView textQuestion;
    private EditText textAnswer;
    private Button buttonOK;
    private Button buttonNext;
    private TextView textMathQuote;

    private int score = 0;
    private int life = 3;
    private int time = 20;
    private int currentAnswer;
    private CountDownTimer timer;
    private boolean timerPaused = false;
    private boolean isAnswerCorrect = false;

    private String[] mathQuotes;
    private int currentQuoteIndex = 0;
    private final Handler quoteHandler = new Handler();
    private final Runnable updateQuoteRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentQuoteIndex < mathQuotes.length) {
                textMathQuote.setText(mathQuotes[currentQuoteIndex]);
                currentQuoteIndex++;
            } else {
                currentQuoteIndex = 0;
            }
            quoteHandler.postDelayed(this, 3000); 
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        textScore = findViewById(R.id.text_score);
        textLife = findViewById(R.id.text_life);
        textTimer = findViewById(R.id.text_timer);
        textQuestion = findViewById(R.id.text_question);
        textAnswer = findViewById(R.id.text_answer);
        buttonOK = findViewById(R.id.button_ok);
        buttonNext = findViewById(R.id.button_next);
        textMathQuote = findViewById(R.id.textMathQuote);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        } else {
            Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int operation = getIntent().getIntExtra("operation", 0);
        mathQuotes = getResources().getStringArray(R.array.math_quotes);

        updateUI();
        generateQuestion(operation);

        buttonOK.setOnClickListener(v -> {
            String userAnswer = textAnswer.getText().toString().trim();

            if (userAnswer.isEmpty()) {
                life--;
                textQuestion.setText("Answer is empty");
                resetTimer();
                updateUI();
                buttonNext.setEnabled(true);
            } else {
                checkAnswer(userAnswer);
                textAnswer.setText("");
                textQuestion.setText(isAnswerCorrect ? "Answer is correct" : "Answer is wrong");
                pauseTimer();
                buttonNext.setEnabled(false);
            }
            buttonNext.setEnabled(true);
            buttonOK.setEnabled(false);
        });

        buttonNext.setOnClickListener(v -> {
            textQuestion.setText("");
            resetTimer();

            if (life <= 0) {
                int userScore = score * 10;
                updateHighscoreInDatabase(userEmail, userScore);

                Intent intent = new Intent(GameActivity.this, DisplayActivity.class);
                intent.putExtra("userScore", userScore);
                startActivity(intent);
                finish();
            } else {
                generateQuestion(operation);
                startTimer();
                buttonNext.setEnabled(false);
                buttonOK.setEnabled(true);
            }
        });

        buttonNext.setEnabled(false);
        buttonOK.setEnabled(true);
        startTimer();


        quoteHandler.postDelayed(updateQuoteRunnable, 3000);
    }

    private void updateUI() {
        textScore.setText("Score: " + score * 10);
        textLife.setText("Life: " + life);
        textTimer.setText("Time: " + time);
    }

    private void generateQuestion(int operation) {
        Random random = new Random();
        int num1 = random.nextInt(50);
        int num2 = random.nextInt(50);

        if (operation == 1 && num1 < num2) {
            int temp = num1;
            num1 = num2;
            num2 = temp;
        }

        String question;
        int correctAnswer;

        switch (operation) {
            case 0:
                question = num1 + " + " + num2;
                correctAnswer = num1 + num2;
                break;
            case 1:
                question = num1 + " - " + num2;
                correctAnswer = num1 - num2;
                break;
            case 2:
                question = num1 + " * " + num2;
                correctAnswer = num1 * num2;
                break;
            default:
                question = "";
                correctAnswer = 0;
        }

        textQuestion.setText(question);
        currentAnswer = correctAnswer;
    }

    private void checkAnswer(String userAnswer) {
        try {
            int userAnswerInt = Integer.parseInt(userAnswer);

            if (userAnswerInt == currentAnswer) {
                score++;
                isAnswerCorrect = true;
            } else {
                life--;
                isAnswerCorrect = false;
            }
        } catch (NumberFormatException e) {
            life--;
            isAnswerCorrect = false;
        }

        updateUI();
    }

    private void startTimer() {
        timerPaused = false;
        timer = new CountDownTimer(time * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (!timerPaused) {
                    time = (int) millisUntilFinished / 1000;
                    textTimer.setText("Time: " + time);
                }
            }

            public void onFinish() {
                life--;
                updateUI();
                pauseTimer();
                resetTimer();
                time = 20;
                textQuestion.setText("Time's UP!");
                buttonNext.setEnabled(true);
                buttonOK.setEnabled(false);
            }
        }.start();
    }

    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
        }
        time = 20;
    }

    private void pauseTimer() {
        if (timer != null && !timerPaused) {
            timer.cancel();
            timerPaused = true;
        }
    }

    private void updateHighscoreInDatabase(String userEmail, int newHighscore) {
        DatabaseReference highscoreRef = FirebaseDatabase.getInstance().getReference("user_highscores");

        String key = getKeyFromEmail(userEmail);

        highscoreRef.child(key).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                UserHighscore userHighscore = mutableData.getValue(UserHighscore.class);

                if (userHighscore == null) {
                    userHighscore = new UserHighscore(userEmail, 0);
                }

                if (newHighscore > userHighscore.getHighscore()) {
                    userHighscore.setHighscore(newHighscore);
                    mutableData.setValue(userHighscore);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Toast.makeText(GameActivity.this, "Failed to update highscore: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getKeyFromEmail(String email) {
        return email.replaceAll("[^a-zA-Z0-9]", "");
    }
}
