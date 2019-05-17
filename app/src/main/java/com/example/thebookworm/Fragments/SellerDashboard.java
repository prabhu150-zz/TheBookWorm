//package com.example.thebookworm.Fragments;
//
//import android.content.Context;
//import android.content.res.AssetManager;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.bookworm.R;
//import com.example.thebookworm.BackEnd;
//import com.example.thebookworm.Models.Seller;
//import com.google.firebase.auth.FirebaseAuth;
//import com.squareup.picasso.Picasso;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//
//public class SellerDashboard extends Fragment {
//
//    FirebaseAuth auth;
//    private String TAG = "SeeDash";
//    private BackEnd singleton;
//
//    public static File getFilefromAssets(Context context, String filename) throws IOException {
//        File cacheFile = new File(context.getCacheDir(), filename);
//        try {
//            InputStream inputStream = context.getAssets().open(filename);
//            try {
//                FileOutputStream outputStream = new FileOutputStream(cacheFile);
//                try {
//                    byte[] buf = new byte[1024];
//                    int len;
//                    while ((len = inputStream.read(buf)) > 0) {
//                        outputStream.write(buf, 0, len);
//                    }
//                } finally {
//                    outputStream.close();
//                }
//            } finally {
//                inputStream.close();
//            }
//        } catch (IOException e) {
//            throw new IOException("Could not open inventory", e);
//        }
//        return cacheFile;
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        singleton = new BackEnd(getContext(), "SellerDashBoard");
//        return inflater.inflate(R.layout.update_user_settings, container, false);
//    }
//
//    private void handleSellerDashBoard() {
//
//        updateSellerUI();
//        Button viewCustomersButton, loadBooksButton, viewOrdersButton;
//
//        viewCustomersButton = getView().findViewById(R.id.viewCustomers);
////        loadBooksButton = getView().findViewById(R.id.addBooks);
////        viewOrdersButton = getView().findViewById(R.id.viewOrders);
//
//        auth = FirebaseAuth.getInstance();
//
//        loadBooksButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String message = "Books loaded!";
////                loadInventory();
//                notifyByToast(message);
//            }
//        });
//
//        viewCustomersButton.setOnClickListener(new View.OnClickListener() {
//                                                   @Override
//                                                   public void onClick(View v) {
//                                                       notifyByToast("See Customers");
//                                                   }
//                                               }
//        );
//
//        viewOrdersButton.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    notifyByToast("See Orders");
//                                                }
//                                            }
//        );
//
//
//    }
//
//    private void updateSellerUI() {
//        CircleImageView profilePic = getView().findViewById(R.id.previewProfilePic);
//        TextView sellerName = getView().findViewById(R.id.sellerName);
//
//        Seller currentSeller = (Seller) singleton.getFromPersistentStorage("currentUser");
//
//        Picasso.get().load(currentSeller.getProfilePic()).into(profilePic);
//        sellerName.setText(currentSeller.getName());
//    }
//
//    private void notifyByToast(String message) {
//        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        handleSellerDashBoard();
//    }
//
//
//    // delete this later
//    public List<String> loadInventory() {
//        AssetManager assetManager = getActivity().getAssets();
//        List<String> res = new ArrayList<>();
//        Seller currentSeller = (Seller) singleton.getFromPersistentStorage("currentUser");
//
//        try {
//            InputStream myInput;
//            myInput = assetManager.open("Books.xls");
//            currentSeller.loadItemsFromFile(myInput);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return res;
//    }
//
//}
//
//
//
//
//
