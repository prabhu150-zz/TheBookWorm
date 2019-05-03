package com.example.thebookworm;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookworm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class DashBoard extends AppCompatActivity {

    FirebaseAuth auth;
    CircleImageView profilePic;
    Button viewCustomersButton, loadBooksButton, viewOrdersButton;
    private Seller currentSeller;
    private String TAG = "SeeDash";

    public static File getFilefromAssets(Context context, String filename) throws IOException {
        File cacheFile = new File(context.getCacheDir(), filename);
        try {
            InputStream inputStream = context.getAssets().open(filename);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new IOException("Could not open inventory", e);
        }
        return cacheFile;
    }

    private void redirect(Class nextActivity) {

        Intent redirect = new Intent(this, nextActivity);
        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(redirect);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_dashboard);

        viewCustomersButton = findViewById(R.id.viewCustomers);
        loadBooksButton = findViewById(R.id.addBooks);
        viewOrdersButton = findViewById(R.id.viewOrders);
        profilePic = findViewById(R.id.previewProfilePic);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            redirect(LoginActivity.class);
        } else {
            retrieveCurrentUser();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Books loaded!";


                loadInventory();

                toastNotify(message);
            }
        });
    }

    public List<String> loadInventory() {
        AssetManager assetManager = getAssets();

        List<String> res = new ArrayList<>();

        try {
            InputStream myInput;
            myInput = assetManager.open("Books.xls");
            currentSeller.loadInventory(myInput);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return res;
    }


    private void toastNotify(String message) {
        Toast.makeText(DashBoard.this, message, Toast.LENGTH_SHORT).show();
    }

    /*
        private fun getUserByNickName(nickName: String): User {

            val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
            var currentUser = User()

            if (userEmail.isEmpty())
                throw IllegalStateException("Invalid user!")

            val query3 = FirebaseDatabase.getInstance().getReference("/users/")
                .orderByChild("nickName")
                .equalTo(nickName)

            val postListener = object : ValueEventListener {
                override fun onDataChange(tasks: DataSnapshot) {
                    logDebug("Inside on data change module")
                    if (tasks.exists())
                        for (currentTask in tasks.children) {
                            currentUser = currentTask.getValue(User::class.java) ?: User()
                            toastNotification("Viewing all posts by ${currentUser.nickName}")
                        }

                    logDebug("User nickname : ${currentUser.userID}")
                    retrievePosts(currentUser)
                    updateUI(currentUser)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    logDebug("""Database error:${databaseError.message}""")
                }
            }

            query3.addListenerForSingleValueEvent(postListener)

            logDebug("For custom users, recieved ${currentUser.name} as name")

            return currentUser
        }
         */
    private void retrieveCurrentUser() {
        String email = auth.getCurrentUser().getEmail();

        Query query = FirebaseDatabase.getInstance().getReference("/users/").orderByChild("email").equalTo(email);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot currentTask : dataSnapshot.getChildren()) {
                        currentSeller = currentTask.getValue(Seller.class);

                        if (currentSeller == null)
                            throw new IllegalStateException("User not retrieved!");


                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        query.addListenerForSingleValueEvent(eventListener);

    }
}


