package com.example.thebookworm;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

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
    private TextView sellerName;
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



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_dashboard);

        viewCustomersButton = findViewById(R.id.viewCustomers);
        loadBooksButton = findViewById(R.id.addBooks);
        viewOrdersButton = findViewById(R.id.viewOrders);
        profilePic = findViewById(R.id.previewProfilePic);
        sellerName = findViewById(R.id.sellerName);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            //TODO add an error 404 page if auth is inaccessible due to poor internet
            redirect(LoginActivity.class);
        } else {
            retrieveCurrentUser();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        auth.signOut();
        redirect(LoginActivity.class);
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Books loaded!";
                loadInventory();
                notifyByToast(message);
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


    private void notifyByToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

    private void logit(String message) {
        Log.d(TAG, message);
    }

    private void redirect(Class nextActivity) {
        Intent redirect = new Intent(this, nextActivity);
        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(redirect);
    }

    private void retrieveCurrentUser() {
        String email = auth.getCurrentUser().getEmail();

        Query query = FirebaseDatabase.getInstance().getReference("/users/sellers/").orderByChild("email").equalTo(email);

        logit("Retrieving user from realtime database");

        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot currentTask : dataSnapshot.getChildren()) {
                        currentSeller = currentTask.getValue(Seller.class);

                        if (currentSeller == null)
                            throw new IllegalStateException("User not retrieved!");

                    }
                    logit("Loading seller details on UI. Seller Name " + currentSeller.name);
                    sellerName.setText(currentSeller.name);
                    Picasso.get().load(currentSeller.profilePic).into(profilePic);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        query.addListenerForSingleValueEvent(eventListener);

    }
}


