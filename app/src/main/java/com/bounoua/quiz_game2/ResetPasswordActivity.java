package com.bounoua.quiz_game2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        EditText email = findViewById(R.id.resetPswEmail);
        Button sendReset = findViewById(R.id.sendResetPswEmail);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        ProgressDialog loader = new ProgressDialog(this);
        sendReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTxt = email.getText().toString();
                if (TextUtils.isEmpty(emailTxt)) {
                    email.setError("Enter your email");
                }else {
                    loader.setMessage("Loading...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    firebaseAuth.sendPasswordResetEmail(emailTxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "message sent successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(ResetPasswordActivity.this, "Something was wrong", Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
            }
        });
    }
}