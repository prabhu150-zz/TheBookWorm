package com.example.thebookworm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookworm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private String Tag = "Login";
    private ProgressBar createUserprogress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        createUserprogress = findViewById(R.id.createUserprogress);
    }


    private void checkIfEmpty(final EditText txtView, final String error) {

        txtView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if (txtView.getText().toString().isEmpty())
                        txtView.setError(error);
            }
        });

    }


    private void notifyByToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

    private void logit(String message) {
        Log.d(Tag, message);
    }

    @Override
    protected void onStart() {
        super.onStart();


        checkIfEmpty(email, getString(R.string.blank_email_error));
        checkIfEmpty(password, getString(R.string.blank_password_error));

        if (validUserInput()) {
            createUserprogress.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        notifyByToast("Logged in successfully!");
                        logit("Login Success");
                    } else {
                        notifyByToast("Login Failed. Error: " + task.getException().getMessage());
                        logit("Login Error: " + task.getException().getMessage());
                    }
                    createUserprogress.setVisibility(View.GONE);
                }
            });
        }


    }

    private boolean validUserInput() {

        boolean validSignIn = !(email.getText().toString().isEmpty() || password.getText().toString().isEmpty());

        Log.d("LogIn", "Valid fields: " + validSignIn);

        return validSignIn;

    }
}
