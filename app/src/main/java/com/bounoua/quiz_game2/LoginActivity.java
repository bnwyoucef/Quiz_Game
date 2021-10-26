package com.bounoua.quiz_game2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditTxt,passwordEditTxt;
    private Button loginButton;
    private TextView forgetPasswordTxt,signUpText;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loader;
    private SignInButton signInButtonGoogle;
    private GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intiViews();
        TextView textView = (TextView) signInButtonGoogle.getChildAt(0);
        textView.setText(R.string.signInGoogle);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginOperation();
            }
        });
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });
        forgetPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ResetPasswordActivity.class));
            }
        });
        signInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               handleSignInGoogle();
            }
        });
    }

    private void intiViews() {
        emailEditTxt = findViewById(R.id.emailSignIn);
        passwordEditTxt = findViewById(R.id.passwordSignIn);
        loginButton = findViewById(R.id.loginButton);
        forgetPasswordTxt = findViewById(R.id.forgotPassword);
        signUpText = findViewById(R.id.dontHaveAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);
        signInButtonGoogle = findViewById(R.id.signInGoogle);
    }

    private void handleLoginOperation() {
        String email = emailEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditTxt.setError("Enter your email");
            return;
        }else if (TextUtils.isEmpty(password)){
            passwordEditTxt.setError("Enter your password");
            return;
        }else {
            loader.setTitle("Sign in...");
            loader.setCanceledOnTouchOutside(false);
            loader.show();
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this, "Email or password was wrong", Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                }
            });
        }
    }
    /*
     * sign in with google account */

    private void handleSignInGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        signInGoogle();
    }

    private void signInGoogle() {
        Intent gIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(gIntent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(getIntent());
            firebaseSignInWithGoogle(task);
        }
    }

    private void firebaseSignInWithGoogle(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(this, "Sign in successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            firebaseGoogleAccount(account);
        } catch (ApiException e) {
            Toast.makeText(this, "Something was wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(null != user) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }
}