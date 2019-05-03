package com.example.thebookworm;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookworm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {


    private String Tag = "SignUpUser";
    private EditText name, email, password, confirmPassword, nickname;
    private Switch type;
    private Button signin, signup, uploadImageButton;
    private CircleImageView profilePic;
    private ProgressBar createUserprogress;
    private int IMAGE_REQUEST = 11;


    private Uri imageURI;
    private StorageTask<UploadTask.TaskSnapshot> currentUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        FirebaseApp.initializeApp(this);


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            redirect(DashBoard.class);
        }

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        nickname = findViewById(R.id.nickname);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        type = findViewById(R.id.type);

        type.setChecked(true);
        type.setText(R.string.buyerSelected);

        signin = findViewById(R.id.sign_in_button);
        signup = findViewById(R.id.sign_up_button);
        uploadImageButton = findViewById(R.id.select_image_button);
        profilePic = findViewById(R.id.previewProfilePic);
        createUserprogress = findViewById(R.id.createUserprogress);
    }


    private void redirect(Class nextActivity) {

        Intent redirect = new Intent(this, nextActivity);
        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(redirect);
    }

    @Override
    protected void onStart() {
        super.onStart();


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect(LoginActivity.class);
            }

        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });


        type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    type.setText(R.string.buyerSelected);
                else
                    type.setText(R.string.sellerSelected);
            }
        });

        signUpCurrentUser();


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

    private void signUpCurrentUser() {
        checkIfEmpty(name, getString(R.string.name_error));
        checkIfEmpty(nickname, getString(R.string.blank_nickname_error));
        checkIfEmpty(password, getString(R.string.blank_confirmpassword_error));

        checkEmailFormat(email);
        checkPasswordsMatch();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserInput()) {
                    createUserprogress.setVisibility(View.VISIBLE);

                    FirebaseDatabase.getInstance().getReference().child("users").orderByChild("nickName")
                            .equalTo(((EditText) findViewById(R.id.nickname)).getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            createUserprogress.setVisibility(View.GONE);
                            if (dataSnapshot.getChildrenCount() > 0) {
                                findViewById(R.id.createUserprogress).setVisibility(View.GONE);
                                ((EditText) findViewById(R.id.nickname)).setError("That one is taken!");
                                notifyByToast("Please select a different nickname!");
                            } else {
                                findViewById(R.id.createUserprogress).setVisibility(View.VISIBLE);
                                logit("Creating user with unique nickname!");
                                createNewUser();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            logit("Database Error: " + databaseError.getMessage());
                        }
                    });

                } else {
                    notifyByToast("Improper fields, please re-enter values");
                }
            }
        });

    }


    private void createNewUser() {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> it) {
                if (it.isSuccessful()) {
                    logit("New user added to auth!");
                    storeUserDetails();
                    signin();
                } else {
                    logit("Failed to create user error: " + it.getException().getMessage());
                    notifyByToast("Failed Signup: " + it.getException().getMessage());
                }
            }
        });
    }

    private void pickFromGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(gallery, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null)
            if (data.getData() != null) {
                imageURI = data.getData();
                profilePic.setVisibility(View.VISIBLE);
                Picasso.get().load(imageURI).into(profilePic);
                uploadImageButton.setElevation(-1);
//                uploadImageButton.setVisibility(View.INVISIBLE);
            } else {
                notifyByToast("No File Selected!");
            }

    }

    private void storeUserDetails() {

        DatabaseReference userDbReference = FirebaseDatabase.getInstance().getReference().child("/users/");
        String userId = userDbReference.push().getKey();


        if (userId == null)
            throw new IllegalStateException("Database couldnt generate new key!");

        if (imageURI != null) {
            logit("uploading an image...");
            uploadImageToStorage(userId);
        } else {
            pushToRealTimeDb(userId, "");
        }

    }

    private void uploadImageToStorage(final String userID) {

        final StorageReference profilePics = FirebaseStorage.getInstance().getReference().child("/profile-pics");

        if (imageURI == null)
            return;

        String fileName = System.currentTimeMillis() + "" + getFileExtension(imageURI);

        StorageReference fileReference = profilePics.child(fileName);

        fileReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    logit("Profile Pic uploaded!");
                    notifyByToast("Image uploaded!");

                    profilePics.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                logit("Got download url as : " + task.toString());
                                pushToRealTimeDb(userID, task.toString());
                            }
                        }
                    });

                } else {
                    if (task.getException() != null)
                        logit("Pic did not upload. Error: " + task.getException().getMessage());
                }

                createUserprogress.setVisibility(View.GONE);

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                createUserprogress.setProgress(progress);

            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        if (uri != null)
            return mime.getExtensionFromMimeType(cR.getType(uri));
        return "";
    }


    private void pushToRealTimeDb(String userID, String profilePic) {


        if (type.getText().equals("Buyer")) {
            Buyer guestLogin = new Buyer(userID, name.getText().toString(), email.getText().toString(), nickname.getText().toString());

            if (!profilePic.isEmpty())
                guestLogin.setProfilePic(profilePic);

            logit("Current buyer added: " + guestLogin.nickname);

            FirebaseDatabase.getInstance().getReference().child("/users/buyers/").child(userID).setValue(guestLogin);
        } else {
            Seller guestLogin = new Seller(userID, name.getText().toString(), email.getText().toString());

            if (!profilePic.isEmpty())
                guestLogin.setProfilePic(profilePic);

            logit("Current seller added: " + guestLogin.name);

            FirebaseDatabase.getInstance().getReference().child("/users/sellers/").child(userID).setValue(guestLogin);
        }



    }


    private void signin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> it) {
                if (it.isSuccessful()) {
                    logit("User signed in!");
                    redirect(DashBoard.class);
                } else {
                    logit("Couldn't Sign-In Error: " + it.getException().getMessage());
                    notifyByToast("Couldn't Sign-In Error: " + it.getException().getMessage());
                }
            }
        });
    }

    private Boolean validateUserInput() {

        boolean validSignup =
                !(TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(
                        nickname.getText().toString()
                ) || TextUtils.isEmpty(password.getText().toString()) || !(password.getText().toString().equals(confirmPassword.getText().toString()) && password.getText().toString().length() > 5) || !validateEmail(
                        email.getText().toString()
                ));

        logit("All fields are valid: " + validSignup);
        return validSignup;

    }

    private void checkPasswordsMatch() {

        password.setError(null);

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (password.getText().toString().isEmpty()) {
                        password.setError(getString(R.string.blank_password_error));
                    } else if (password.getText().toString().length() < 6)
                        password.setError(getString(R.string.password_invalid_error));
                }
            }

        });


        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (confirmPassword.getText().toString().isEmpty() && password.getText().toString().length() > 0)
                        confirmPassword.setError(getString(R.string.blank_confirmpassword_error));
                    else if (!password.getText().toString().equals(confirmPassword.getText().toString()))
                        confirmPassword.setError(getString(R.string.password_dont_match_error));
                }
            }
        });


    }


    private void checkEmailFormat(final EditText email) {

        email.setError(null);

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (email.getText().toString().isEmpty()) {
                        email.setError(getString(R.string.blank_email_error));
                        return;
                    }
                    if (!validateEmail(email.getText().toString()))
                        email.setError(getString(R.string.invalid_email_error));
                    else
                        email.setError(null);
                }
            }
        });

    }

    private boolean validateEmail(String email) {
        Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");
        return emailPattern.matcher(email).matches();
    }


}
