package com.bounoua.quiz_game2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditTxt,passwordEditTxt;
    private Button signUp;
    private TextView alreadyHaveAccount;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleWithSignUp();
            }
        });
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }

    private void initViews() {
        emailEditTxt = findViewById(R.id.emailSignUp);
        passwordEditTxt = findViewById(R.id.passwordSignUp);
        signUp = findViewById(R.id.signUpButton);
        alreadyHaveAccount = findViewById(R.id.HaveAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);
    }

    private void handleWithSignUp() {
        String email = emailEditTxt.getText().toString();
        String password= passwordEditTxt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditTxt.setError("Enter your email");
            return;
        }if (TextUtils.isEmpty(password)) {
            passwordEditTxt.setError("Enter your password");
            return;
        }else {
            loader.setMessage("Sign up ...");
            loader.setCanceledOnTouchOutside(false);
            loader.show();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        finish();
                    }else {
                        Toast.makeText(SignUpActivity.this, "Email or password was wrong!", Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                }
            });
        }
    }
}