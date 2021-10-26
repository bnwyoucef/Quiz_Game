package com.bounoua.quiz_game2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.stream.Stream;

public class QuizActivity extends AppCompatActivity {

    private TextView timeTxt, correctTxt, wrongTxt, questionTxt, choiceOne, choiceTwo, choiceThree, choiceFour;
    private Button nextButton, exitButton;
    private FirebaseDatabase firebaseDatabase;
    private Object[] object;
    private String correctAnswer = "";
    private int numberQuestion;
    private int correctAnswerNumber;
    private int wrongAnswerNumber;
    private CountDownTimer timer;
    private int numberAllQuestion;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        initViews();
        /**
         * generate a unique random number in a table
         * **/
        object = Stream.generate(() -> (new Random()).nextInt(9)).distinct().limit(9).toArray();
        generateQuestion(0);
        numberAllQuestion = object.length -1;
        nextButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (numberQuestion < numberAllQuestion) {
                    numberQuestion++;
                    timer.cancel();
                    generateQuestion(numberQuestion);
                }else {
                    Toast.makeText(QuizActivity.this, "you answered all the questions", Toast.LENGTH_SHORT).show();
                    sendScoreDB();
                    startActivity(new Intent(QuizActivity.this, ScoreActivity.class));
                    finish();
                }
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this)
                        .setTitle("Exit")
                        .setMessage("Are you sure to exit the game?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendScoreDB();
                                startActivity(new Intent(getApplicationContext(),ScoreActivity.class));
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.create().show();
            }
        });

        choiceOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenClickAnswer(false);
                if (correctAnswer.equals("a")) {
                    choiceOne.setBackgroundColor(Color.GREEN);
                    correctAnswerNumber++;
                    correctTxt.setText(String.valueOf(correctAnswerNumber));
                }else {
                    choiceOne.setBackgroundColor(Color.RED);
                    showCorrectAnswer();
                    wrongAnswerNumber++;
                    wrongTxt.setText(String.valueOf(wrongAnswerNumber));
                }

            }
        });
        choiceTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenClickAnswer(false);
                if (correctAnswer.equals("b")) {
                    choiceTwo.setBackgroundColor(Color.GREEN);
                    correctAnswerNumber++;
                    correctTxt.setText(String.valueOf(correctAnswerNumber));
                }else {
                    choiceTwo.setBackgroundColor(Color.RED);
                    showCorrectAnswer();
                    wrongAnswerNumber++;
                    wrongTxt.setText(String.valueOf(wrongAnswerNumber));
                }
            }
        });
        choiceThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenClickAnswer(false);
                if (correctAnswer.equals("c")) {
                    choiceThree.setBackgroundColor(Color.GREEN);
                    correctAnswerNumber++;
                    correctTxt.setText(String.valueOf(correctAnswerNumber));
                }else {
                    choiceThree.setBackgroundColor(Color.RED);
                    wrongAnswerNumber++;
                    showCorrectAnswer();
                    wrongTxt.setText(String.valueOf(wrongAnswerNumber));
                }
            }
        });
        choiceFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenClickAnswer(false);
                if (correctAnswer.equals("d")) {
                    choiceFour.setBackgroundColor(Color.GREEN);
                    correctAnswerNumber++;
                    correctTxt.setText(String.valueOf(correctAnswerNumber));
                }else {
                    choiceFour.setBackgroundColor(Color.RED);
                    showCorrectAnswer();
                    wrongAnswerNumber++;
                    wrongTxt.setText(String.valueOf(wrongAnswerNumber));
                }
            }
        });

    }

    /**
     * show the questions and the answers
     * get the information from the realDB fire base
     * **/
    private void generateQuestion(int number) {
        //get a random number of question from the table
        int i = (int) object[number];
        /**
         * reset the background color to the original one
         * when the player chose an answer the color will change
         * **/
        choiceOne.setBackgroundColor(getResources().getColor(R.color.darkBlue));
        choiceTwo.setBackgroundColor(getResources().getColor(R.color.darkBlue));
        choiceThree.setBackgroundColor(getResources().getColor(R.color.darkBlue));
        choiceFour.setBackgroundColor(getResources().getColor(R.color.darkBlue));
        timer = new CountDownTimer(20000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeTxt.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                questionTxt.setText(getResources().getString(R.string.timeOut));
                whenClickAnswer(false);
            }
        }.start();
        whenClickAnswer(true);
        firebaseDatabase.getReference().child("questions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        correctAnswer = snapshot.child(String.valueOf(i)).child("answer").getValue().toString();
                        questionTxt.setText(snapshot.child(String.valueOf(i)).child("q").getValue().toString());
                        choiceOne.setText(snapshot.child(String.valueOf(i)).child("a").getValue().toString());
                        choiceTwo.setText(snapshot.child(String.valueOf(i)).child("b").getValue().toString());
                        choiceThree.setText(snapshot.child(String.valueOf(i)).child("c").getValue().toString());
                        choiceFour.setText(snapshot.child(String.valueOf(i)).child("d").getValue().toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(QuizActivity.this, "Something was wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    /**
     * if the player chose a wrong answer
     * we will show him the right one
     * **/
    private void showCorrectAnswer() {
        if (correctAnswer.equals("a")) {
            choiceOne.setBackgroundColor(Color.GREEN);
        }else if (correctAnswer.equals("b")) {
            choiceTwo.setBackgroundColor(Color.GREEN);
        }else if (correctAnswer.equals("c")) {
            choiceThree.setBackgroundColor(Color.GREEN);
        }else if (correctAnswer.equals("d")){
            choiceFour.setBackgroundColor(Color.GREEN);
        }
    }
    /**
     * the player can click one time to the answers
     * when the player chose one answer the timer will stop
     * **/
    private void whenClickAnswer(boolean isClicked) {
        choiceOne.setClickable(isClicked);
        choiceTwo.setClickable(isClicked);
        choiceThree.setClickable(isClicked);
        choiceFour.setClickable(isClicked);
        if (!isClicked)
            timer.cancel();
    }

    private void initViews() {
        timeTxt = findViewById(R.id.timeCount);
        questionTxt = findViewById(R.id.questionText);
        wrongTxt = findViewById(R.id.wrongAnswer);
        correctTxt = findViewById(R.id.correctAnswer);
        choiceOne = findViewById(R.id.firstChoice);
        choiceTwo = findViewById(R.id.secondChoice);
        choiceThree = findViewById(R.id.thirdChoice);
        choiceFour = findViewById(R.id.forthChoice);
        nextButton = findViewById(R.id.nextButton);
        exitButton = findViewById(R.id.exitButton);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        reference = firebaseDatabase.getReference();
    }
    /**
     * send the player score to the database
     * userID to make difference between all players
     * **/
    private void sendScoreDB() {
        String userID = firebaseAuth.getCurrentUser().getUid();
        reference.child("Score").child(userID).child("correct").setValue(correctAnswerNumber);
        reference.child("Score").child(userID).child("wrong").setValue(wrongAnswerNumber)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(QuizActivity.this, "game finished", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}