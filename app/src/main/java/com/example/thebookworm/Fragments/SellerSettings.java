package com.example.thebookworm.Fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookworm.R;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Seller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerSettings extends Fragment {


    private final int IMAGE_REQUEST = 6665;
    CircleImageView updatedProfilePic;
    Button updateDP, updateInventory, updateInfo;
    String type;
    private BackEnd backEnd;
    private EditText name, email, newPassword, confirmPassword, oldPassword;
    private TextView heading;
    private ProgressBar createUserprogress;
    private Uri imageURI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_user_settings, container, false);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backEnd = new BackEnd(getActivity(), "Settings#logger");
    }


    @Override
    public void onStart() {
        super.onStart();
        findIDs();


        updateDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });
        autofill();

        email.setEnabled(false);

        updateInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");
                currentSeller.loadInventory(getActivity());
                backEnd.notifyByToast("Books Loaded!");
            }
        });


        updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (validUserInput())

                    if (type.equals("buyer")) {
                        Buyer currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");

                        if (imageURI != null) {
                            backEnd.logit("Uploading an image...");
                            uploadImageToStorage(currentBuyer.getUserID());
                        } else {
                            updateRealTimeDb(currentBuyer.getUserID(), "");
                        }

                    } else if (type.equals("seller")) {
                        Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");

                        if (imageURI != null) {
                            backEnd.logit("Uploading an image...");
                            uploadImageToStorage(currentSeller.getUserID());
                        } else {
                            updateRealTimeDb(currentSeller.getUserID(), "");
                        }

                    }


            }
        });

    }



    /*
    TODO add hints not set text for edit texts
    make password a password field



     */

    private boolean validUserInput() {

        boolean isValid = !(TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(newPassword.getText().toString()) || TextUtils.isEmpty(confirmPassword.getText().toString()) || TextUtils.isEmpty(email.getText().toString()));

        if (isValid) {
            if (!(newPassword.getText().toString()).equals(confirmPassword.getText().toString())) {
                confirmPassword.setError("Passwords dont match!");
                isValid = false;
            }
            if ((newPassword.getText().toString()).length() < 6) {
                newPassword.setError("Minimum newPassword size: 6 characeters");
                isValid = false;
            }

        }

        backEnd.logit("User input fields are valid? " + isValid);
        return isValid;

    }


    private void updateRealTimeDb(String userID, String profilePic) {

        backEnd.logit("updateRealTimeDb method");
        backEnd.logit("Current user is a " + type);

        if (type.equals("buyer")) {

            Buyer currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");

            if (!profilePic.isEmpty())
                currentBuyer.setProfilePic(profilePic);

            currentBuyer.setName(name.getText().toString());


            backEnd.logit("Current buyer added: " + currentBuyer.getName());

            if (!currentBuyer.getEmail().equals(email.getText().toString())) {
                currentBuyer.setEmail(email.getText().toString());
                updateEmails();
            }


            backEnd.saveToPersistentStorage("currentUser", currentBuyer);


            FirebaseDatabase.getInstance().getReference().child("/users/buyers/").child(userID).setValue(currentBuyer);


        } else {

            Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");

            if (!profilePic.isEmpty())
                currentSeller.setProfilePic(profilePic);

            currentSeller.setName(name.getText().toString());

            if (!currentSeller.getEmail().equals(email.getText().toString())) {
                currentSeller.setEmail(email.getText().toString());
                updateEmails();
            }

            backEnd.saveToPersistentStorage("currentUser", currentSeller);

            if (!profilePic.isEmpty())
                currentSeller.setProfilePic(profilePic);

            backEnd.logit("Current buyer added: " + currentSeller.getName());

            FirebaseDatabase.getInstance().getReference().child("/users/buyers/").child(userID).setValue(currentSeller);

        }


        updatePasswords();

    }

    private void updateEmails() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final String currentEmail = currentUser.getEmail();

        final AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, oldPassword.getText().toString());

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        backEnd.logit("Current user has been re authenticated!");

                        currentUser.updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                backEnd.logit("User email has been updated!");
                            }
                        });
                    }
                });


    }

    private void updatePasswords() {
        FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword.getText().toString());
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
                backEnd.logit("Uploaded image sucessfully");
                backEnd.notifyByToast("Profile pic uploaded!");

                profilePics.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        backEnd.logit("Url retrieved is : " + uri.toString());
                        updateRealTimeDb(userID, uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        backEnd.logit("Couldn't fetch URL, using default image");
                        updateRealTimeDb(userID, "");
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
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        if (uri != null)
            return mime.getExtensionFromMimeType(cR.getType(uri));
        else
            return "";
    }


    private void autofill() {

        type = backEnd.getFromPersistentStorage("currentUserType").toString();

        if (type.equals("buyer")) {
            Buyer currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
            heading.setText(currentBuyer.getName());
            name.setText(currentBuyer.getName());
            email.setText(currentBuyer.getEmail());
            newPassword.setText("");
            confirmPassword.setText("");
            updateInventory.setVisibility(View.INVISIBLE);
        } else if (type.equals("seller")) {
            Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");
            heading.setText(currentSeller.getName());
            name.setText(currentSeller.getName());
            email.setText(currentSeller.getEmail());
            newPassword.setText("");
            confirmPassword.setText("");
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null)
            if (data.getData() != null) {
                imageURI = data.getData();
                updatedProfilePic.setVisibility(View.VISIBLE);
                Picasso.get().load(imageURI).into(updatedProfilePic);
                updateDP.setElevation(-1);
            } else {
                backEnd.notifyByToast("No File Selected!");
            }

    }

    private void pickFromGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(gallery, IMAGE_REQUEST);
    }

    private void findIDs() {
        name = getView().findViewById(R.id.updateName);
        email = getView().findViewById(R.id.updateEmail);
        oldPassword = getView().findViewById(R.id.oldPassword);
        newPassword = getView().findViewById(R.id.updatePassword);
        confirmPassword = getView().findViewById(R.id.confirmUpdatePassword);
        heading = getView().findViewById(R.id.heading);
        updateDP = getView().findViewById(R.id.updateProfilePic);
        updateInventory = getView().findViewById(R.id.updateInventory);
        updateInfo = getView().findViewById(R.id.updateInfoButton);
        createUserprogress = getView().findViewById(R.id.updateUserProgress);
        updatedProfilePic = getView().findViewById(R.id.previewProfilePic);
    }
}
