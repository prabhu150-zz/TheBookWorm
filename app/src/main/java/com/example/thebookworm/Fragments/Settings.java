package com.example.thebookworm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookworm.R;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Seller;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends Fragment {

    CircleImageView updatedProfilePic;
    Button updateInventory;
    String type;
    private BackEnd backEnd;
    private TextView name, email, nickname;
    private TextView heading;
    private ProgressBar createUserprogress;

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
        autofill();

        updateInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");

                backEnd.notifyByToast("Books loaded!");
                currentSeller.loadInventory(getActivity());
            }
        });
    }


    private void autofill() {

        type = backEnd.getFromPersistentStorage("currentUserType").toString();

        if (type.equals("buyer")) {
            Buyer currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
            heading.setText(currentBuyer.getName());
            name.setText(currentBuyer.getName());
            email.setText(currentBuyer.getEmail());
            updateInventory.setVisibility(View.INVISIBLE);
            nickname.setText(currentBuyer.getNickname());

            Picasso.get().load(currentBuyer.getProfilePic()).into(updatedProfilePic);

        } else if (type.equals("seller")) {
            Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");
            Picasso.get().load(currentSeller.getProfilePic()).into(updatedProfilePic);
            heading.setText(currentSeller.getName());
            name.setText(currentSeller.getName());
            email.setText(currentSeller.getEmail());
            nickname.setVisibility(View.GONE);
        }


    }


    private void findIDs() {
        name = getView().findViewById(R.id.updateName);
        email = getView().findViewById(R.id.updateEmail);
        heading = getView().findViewById(R.id.heading);
        createUserprogress = getView().findViewById(R.id.updateUserProgress);
        updatedProfilePic = getView().findViewById(R.id.updatedProfilePic);
        updateInventory = getView().findViewById(R.id.updateInventory);
        nickname = getView().findViewById(R.id.updateNickName);
    }
}
