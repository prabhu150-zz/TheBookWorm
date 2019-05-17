package com.example.thebookworm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.bookworm.R;
import com.example.thebookworm.BackEnd;

public class CheckOut extends Fragment {

    String total;
    Double grandTotal;
    AwesomeValidation awesomeValidation;
    Button placeYourOrder;
    EditText fullName, addressLine1, addressLine2, city, stateEditText, zipEditText, email, phone;
    EditText personNameEditTextBilling, address01EditTextBilling, address02EditTextBilling, cityEditTextBilling, stateEditTextBilling, zipEditTextBilling, phoneEditTextBilling;
    private BackEnd backEnd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.payment_options, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backEnd = new BackEnd(getActivity(), "CheckOutAct#logger");
        findIDs();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // validateInput()


    }

    private void findIDs() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        fullName = getView().findViewById(R.id.fullName);
        addressLine1 = getView().findViewById(R.id.address1);
        addressLine2 = getView().findViewById(R.id.address2);
        city = getView().findViewById(R.id.city);
        email = getView().findViewById(R.id.email);
        phone = getView().findViewById(R.id.phone);
        stateEditText = getView().findViewById(R.id.state);
        zipEditText = getView().findViewById(R.id.zipCode);
        personNameEditTextBilling = getView().findViewById(R.id.fullName_billing_EditText);
        address01EditTextBilling = getView().findViewById(R.id.adress01_billing_EditText);
        address02EditTextBilling = getView().findViewById(R.id.address02_billing_EditText);
        cityEditTextBilling = getView().findViewById(R.id.city_billing_EditText);
        phoneEditTextBilling = getView().findViewById(R.id.phone_billing_EditText);
        stateEditTextBilling = getView().findViewById(R.id.state_billing_EditText);
        zipEditTextBilling = getView().findViewById(R.id.zip_billing_EditText);
        placeYourOrder = getView().findViewById(R.id.place_Order_Button);
    }


}
