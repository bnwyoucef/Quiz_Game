package com.bounoua.quiz_game2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ScoreActivity extends AppCompatActivity {

    private TextView correctAnswer, wrongAnswer;
    private Button exitBtn,playAgainBtn;
    private DatabaseReference reference;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        initViews();
        reference.child("Score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                correctAnswer.setText(snapshot.child(userID).child("correct").getValue().toString());
                wrongAnswer.setText(snapshot.child(userID).child("wrong").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScoreActivity.this, "Something was wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScoreActivity.this)
                        .setMessage("Are you sure to exit the game?")
                        .setTitle("Exit")
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAffinity();
                                System.exit(0);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScoreActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    private void initViews() {
        correctAnswer = findViewById(R.id.scoreCorrect);
        wrongAnswer = findViewById(R.id.scoreWrong);
        exitBtn = findViewById(R.id.exitFinaleScore);
        playAgainBtn = findViewById(R.id.playAgainFinaleScore);
        reference = FirebaseDatabase.getInstance().getReference();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}