package com.example.thebookworm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private String Tag = "Login";
    private ProgressBar createUserprogress;
    private final boolean debug = true;
    Button signIn, registerRedirect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        createUserprogress = findViewById(R.id.createUserprogress);
        signIn = findViewById(R.id.sign_in_button);
        registerRedirect = findViewById(R.id.register_redirect);
        Paper.init(this);
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

    private void autofill() {
        email.setText("abc@gm.com");
        password.setText("123456");

    }


    private void logit(String message) {
        Log.d(Tag, message);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (debug)
            autofill();


        checkIfEmpty(email, getString(R.string.blank_email_error));
        checkIfEmpty(password, getString(R.string.blank_password_error));

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validUserInput()) {
                    createUserprogress.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                notifyByToast("Logged in successfully!");
                                logit("Login Success");
                                storeCurrentUser();
                                redirect(DashBoard.class);
                            } else {
                                notifyByToast("Login Failed. Error: " + task.getException().getMessage());
                                logit("Login Error: " + task.getException().getMessage());
                            }
                            createUserprogress.setVisibility(View.GONE);
                        }
                    });
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

    private void storeCurrentUser() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Query query = FirebaseDatabase.getInstance().getReference("/users/sellers/").orderByChild("email").equalTo(email);

        logit("Retrieving seller from realtime database");

        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot currentTask : dataSnapshot.getChildren()) {
                        Seller currentSeller = currentTask.getValue(Seller.class);
                        if (currentSeller == null)
                            throw new IllegalStateException("User not retrieved!");

                        Paper.book().write("currentUser", currentSeller);

                    }
                } else {
                    logit("Its probably a buyer. No-one from sellers found!");
                    getCurrentBuyer();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        query.addListenerForSingleValueEvent(eventListener);
    }

    private void getCurrentBuyer() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Query query = FirebaseDatabase.getInstance().getReference("/users/sellers/").orderByChild("email").equalTo(email);

        logit("Retrieving seller from realtime database");

        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot currentTask : dataSnapshot.getChildren()) {
                        Buyer currentBuyer = currentTask.getValue(Buyer.class);
                        if (currentBuyer == null)
                            throw new IllegalStateException("User not retrieved!");

                        Paper.book().write("currentUser", currentBuyer);

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        query.addListenerForSingleValueEvent(eventListener);
    }

    private void redirect(Class nextActivity) {

        Intent redirect = new Intent(this, nextActivity);
        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(redirect);
        finish();
    }
    private boolean validUserInput() {

        boolean validSignIn = !(email.getText().toString().isEmpty() || password.getText().toString().isEmpty());

        Log.d("LogIn", "Valid fields: " + validSignIn);

        return validSignIn;

    }
}
