package com.example.thebookworm.Activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Seller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, email, password, confirmPassword, nickname;
    private Switch type;
    private Button signin, signup, uploadImageButton;
    private CircleImageView profilePic;
    private ProgressBar createUserprogress;
    private int IMAGE_REQUEST = 11;
    private BackEnd backend;
    private Uri imageURI;
    private final boolean debug = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);
        backend = new BackEnd(this, "RegisterAct#logger");

//        backend.logout();

        backend.logit("Checking if user is logged in?");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            backend.logit("User is already logged in:" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
            backend.findCurrentUser();
        }

        //
//        FirebaseApp.initializeApp(this); should only uncomment this the first time

        findIDs();

        type.setChecked(false);
        type.setText(R.string.sellerSelected);

        if (debug)
            autofill();


    }

    private void findIDs() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        nickname = findViewById(R.id.nickname);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        type = findViewById(R.id.type);
        signin = findViewById(R.id.sign_in_button);
        signup = findViewById(R.id.sign_up_button);
        uploadImageButton = findViewById(R.id.select_image_button);
        profilePic = findViewById(R.id.previewProfilePic);
        createUserprogress = findViewById(R.id.createUserprogress);
    }

    @Override
    protected void onStart() {
        super.onStart();



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backend.logit("Going to Login screen!");
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

    private void autofill() {
        name.setText("someName");
        email.setText("abc@gm.com");
        nickname.setText("abcd");
        password.setText("123456");
        confirmPassword.setText("123456");

    }

    private void checkIfEmpty(final EditText txtView, final String error) {
        if (debug)
            autofill();
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

    private void redirect(Class nextActivity) {
        Intent redirect = new Intent(this, nextActivity);
        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(redirect);
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

                    FirebaseDatabase.getInstance().getReference().child("users/buyers").orderByChild("nickname").equalTo(nickname.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            createUserprogress.setVisibility(View.GONE);

                            if (dataSnapshot.getChildrenCount() > 0) {
                                createUserprogress.setVisibility(View.GONE);
                                nickname.setError("That one is taken!");
                                notifyByToast("Please select a different nickname!");
                            } else {
                                createUserprogress.setVisibility(View.VISIBLE);
                                backend.logit("Creating user with unique nickname!");
                                createNewUser();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            backend.logit("Database Error: " + databaseError.getMessage());
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
                createUserprogress.setVisibility(View.GONE);
                if (it.isSuccessful()) {
                    backend.logit("New user added to auth!");
                    storeUserDetails();

                } else {
                    backend.logit("Failed to create user error: " + it.getException().getMessage());
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
            } else {
                notifyByToast("No File Selected!");
            }

    }

    private void storeUserDetails() {

        DatabaseReference userDbReference = FirebaseDatabase.getInstance().getReference().child("/users/");
        String userId = userDbReference.push().getKey();

        if (userId == null)
            throw new IllegalStateException("Database couldn't generate new key!");

        if (imageURI != null) {
            backend.logit("Uploading an image...");
            uploadImageToStorage(userId);
        } else {
            pushToRealTimeDb(userId, "");
        }

    }

    private void uploadImageToStorage(final String userID) {

        final StorageReference profilePics = FirebaseStorage.getInstance().getReference().child("/profile-pics/");

        if (imageURI == null)
            return;

        final String fileName = System.currentTimeMillis() + "" + getFileExtension(imageURI);

        StorageReference fileReference = profilePics.child(fileName);

        createUserprogress.setVisibility(View.VISIBLE);

        fileReference.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                backend.logit("Uploaded image sucessfully");
                notifyByToast("Profile pic uploaded!");

                profilePics.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        backend.logit("Url retrieved is : " + uri.toString());
                        pushToRealTimeDb(userID, uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        backend.logit("Couldn't fetch URL, using default image");
                        pushToRealTimeDb(userID, "");
                    }
                });

            }
        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        createUserprogress.setProgress(progress);

                    }
                });

        createUserprogress.setVisibility(View.GONE);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        if (uri != null)
            return mime.getExtensionFromMimeType(cR.getType(uri));
        else
            return "";
    }


    private void pushToRealTimeDb(String userID, String profilePic) {

        backend.logit("pushToRealTimeDb method");
        backend.logit(type.getText().toString() + " is the selected option");

        if (type.getText().toString().equals("Buyer")) {

            Buyer guestLogin = new Buyer(userID, name.getText().toString(), email.getText().toString(), nickname.getText().toString());

            backend.saveToPersistentStorage("currentUser", guestLogin);

            if (!profilePic.isEmpty())
                guestLogin.setProfilePic(profilePic);

            backend.logit("Current buyer added: " + guestLogin.getName());

            FirebaseDatabase.getInstance().getReference().child("/users/buyers/").child(userID).setValue(guestLogin);

        } else {

            Seller guestLogin = new Seller(userID, name.getText().toString(), email.getText().toString());


            backend.saveToPersistentStorage("currentUser", guestLogin);


            if (!profilePic.isEmpty()) {
                guestLogin.setProfilePic(profilePic);
            }


            backend.logit("Current seller added: " + guestLogin.getName());

            FirebaseDatabase.getInstance().getReference().child("/users/sellers/").child(userID).setValue(guestLogin);

        }

        signin();


    }


    private void signin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> it) {
                if (it.isSuccessful()) {
                    backend.logit("User signed in!");
                    redirectToDashBoard(BaseActivity.class);
                } else {
                    backend.logit("Couldn't Sign-In Error: " + it.getException().getMessage());
                    notifyByToast("Couldn't Sign-In Error: " + it.getException().getMessage());
                }
            }
        });
    }

    private void redirectToDashBoard(Class<BaseActivity> baseActivityClass) {
        Intent redirect = new Intent(this, baseActivityClass);
        redirect.putExtra("currentUserType", type.getText().toString().toLowerCase());
        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        backend.saveToPersistentStorage("currentUserType", type.getText().toString().toLowerCase());
        startActivity(redirect);
    }

    private Boolean validateUserInput() {

        boolean validSignup =
                !(TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(
                        nickname.getText().toString()
                ) || TextUtils.isEmpty(password.getText().toString()) || !(password.getText().toString().equals(confirmPassword.getText().toString()) && password.getText().toString().length() > 5) || !validateEmail(
                        email.getText().toString()
                ));

        backend.logit("All fields are valid: " + validSignup);
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
