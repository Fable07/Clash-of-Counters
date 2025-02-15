package com.example.clashofcounters;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Button buttonRegisterFromLogin ;
    private DatabaseReference databaseReference;
    private EditText editTextAccount , editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLogin = findViewById(R.id.buttonLogin);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        buttonRegisterFromLogin = findViewById(R.id.buttonRegisterFromLogin);
        editTextAccount = findViewById(R.id.editTextAccountLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);

        buttonRegisterFromLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = editTextAccount.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
                    checkUserAndSignIn(account, password);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter account and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

    }



    private void showRegisterDialog() {
        Holder holder = new ViewHolder(R.layout.activity_register);


        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(holder)
                .setExpanded(false)
                .create();


        View dialogView = dialog.getHolderView();
        EditText editTextAccountRegister = dialogView.findViewById(R.id.editTextAccountRegister);
        EditText editTextPasswordRegister = dialogView.findViewById(R.id.editTextPasswordRegister);
        Button buttonRegister = dialogView.findViewById(R.id.buttonRegister);


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String account = editTextAccountRegister.getText().toString().trim();
                String password = editTextPasswordRegister.getText().toString().trim();


                if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {

                    registerUser(account, password);

                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter account and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void registerUser(String email, String password) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            saveUserToDatabase(user);
                            setInitialHighscore(user.getEmail());
                            Toast.makeText(MainActivity.this, "Registration succeeded.", Toast.LENGTH_SHORT).show();

                        } else {

                            Exception exception = task.getException();
                            if (exception != null) {
                                Toast.makeText(MainActivity.this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    private void checkUserAndSignIn(String account, String password) {
        firebaseAuth.signInWithEmailAndPassword(account, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String welcomeMessage = "Welcome " + account;
                            showToast(welcomeMessage);


                            Intent intent = new Intent(MainActivity.this, Main_game.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "User not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private void saveUserToDatabase(FirebaseUser user) {

        FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid())
                .child("email")
                .setValue(user.getEmail());
    }

    private void setInitialHighscore(String userEmail) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            DatabaseReference highscoreRef = FirebaseDatabase.getInstance().getReference("user_highscores");
            UserHighscore initialHighscore = new UserHighscore(userEmail, 0);

            highscoreRef.child(userId).setValue(initialHighscore);
        }
    }


    private String getKeyFromEmail(String email) {
        return email;
    }



}