package com.example.thebookworm.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookworm.R;
import com.example.thebookworm.BackEnd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private ProgressBar createUserprogress;
    Button signIn, registerRedirect;
    private BackEnd backend;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        createUserprogress = findViewById(R.id.createUserprogress);
        signIn = findViewById(R.id.sign_in_button);
        registerRedirect = findViewById(R.id.register_redirect);

        String tag = "LoginAct#logger";
        backend = new BackEnd(this, tag);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            backend.logit("User is already logged in:" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
            backend.findCurrentUser();
        }

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


    private void autofill() {
        email.setText("abcd@gm.com");
        password.setText("123456");
    }



    @Override
    protected void onStart() {
        super.onStart();

        boolean debug = true;
        if (debug)
            autofill();


        checkIfEmpty(email, getString(R.string.blank_email_error));
        checkIfEmpty(password, getString(R.string.blank_password_error));

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validUserInput()) {
                    createUserprogress.setVisibility(View.VISIBLE);
                    signInUser();
                }
            }
        });


        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect(RegisterActivity.class);
            }
        });
    }

    private void redirect(Class<RegisterActivity> registerActivityClass) {
        Intent redirect = new Intent(this, registerActivityClass);
        startActivity(redirect);
    }


    private void signInUser() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    backend.notifyByToast("Logged in successfully!");
                    backend.logit("Login Success");
                    backend.findCurrentUser();

                } else {
                    backend.notifyByToast("Login Failed. Error: " + task.getException().getMessage());
                    backend.logit("Error: " + task.getException().getMessage());
                }
                createUserprogress.setVisibility(View.GONE);
            }
        });
    }




    private boolean validUserInput() {
        boolean validSignIn = !(email.getText().toString().isEmpty() || password.getText().toString().isEmpty());
        Log.d("LogIn", "Valid fields: " + validSignIn);
        return validSignIn;
    }
}
