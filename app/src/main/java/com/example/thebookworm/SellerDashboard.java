package com.example.thebookworm;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookworm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;


public class SellerDashboard extends Fragment {

    FirebaseAuth auth;
    CircleImageView profilePic;
    Button viewCustomersButton, loadBooksButton, viewOrdersButton;
    private String TAG = "SeeDash";
    private TextView sellerName;
    private Seller currentSeller;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vendor_dashboard, null);
    }


    private void handleSellerDashBoard() {

        viewCustomersButton = getView().findViewById(R.id.viewCustomers);
        loadBooksButton = getView().findViewById(R.id.addBooks);
        viewOrdersButton = getView().findViewById(R.id.viewOrders);
        profilePic = getView().findViewById(R.id.previewProfilePic);
        sellerName = getView().findViewById(R.id.sellerName);
        auth = FirebaseAuth.getInstance();

        loadBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Books loaded!";
                loadInventory();
                notifyByToast(message);
            }
        });
        currentSeller = Paper.book().read("currentUser");
        logit("Username retrieved from paper " + currentSeller.name);
        sellerName.setText(currentSeller.name);
        Picasso.get().load(currentSeller.profilePic).into(profilePic);
    }
//    }

//    private void handleBuyerDashBoard(){
//        notifyByToast("This is buyers dashboard!");
//        handleSellerDashBoard();
//    }

    @Override
    public void onStart() {
        super.onStart();
        handleSellerDashBoard();
    }

    public List<String> loadInventory() {
        AssetManager assetManager = getActivity().getAssets();
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

    }

    private void logit(String message) {
        Log.d(TAG, message);
    }


    }




