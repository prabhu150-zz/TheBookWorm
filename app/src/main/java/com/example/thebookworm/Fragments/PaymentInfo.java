//package com.example.thebookworm.Fragments;
//
//import android.os.Bundle;
//import android.util.Patterns;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.basgeekball.awesomevalidation.AwesomeValidation;
//import com.basgeekball.awesomevalidation.ValidationStyle;
//import com.example.bookworm.R;
//
//
//public class PaymentInfo extends AppCompatActivity {
//    // CartDetails cartDetails;
//    String total;
//    Double grandTotal;
//    AwesomeValidation awesomeValidation;
//    Button placeYourOrder;
//
//    EditText personNameEditText, address01EditText, address02EditText, cityEditText, stateEditText, zipEditText, emailEditText, phoneEditText;
//
//    EditText personNameEditTextBilling, address01EditTextBilling, address02EditTextBilling, cityEditTextBilling, stateEditTextBilling, zipEditTextBilling, phoneEditTextBilling;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.payment_options);
//
//      /*  Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_personalInformation);
//        setSupportActionBar(myToolbar);
//        myToolbar.findViewById(R.id.toolbar_title_personalInformation).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent goToHome = new Intent(v.getContext(),HomePage.class);
//                finish();
//                goToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(goToHome);
//            }
//        });*/
//
//
//        placeYourOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                    submitForm(view);
//
//            }
//        });
//
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//        // cartDetails = new CartDetails(this);
//        //drawSummary();
//        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
//        personNameEditText = findViewById(R.id.fullName);
//        address01EditText = findViewById(R.id.address1);
//        address02EditText = findViewById(R.id.address2);
//        cityEditText = findViewById(R.id.city);
//        emailEditText = findViewById(R.id.email);
//        phoneEditText = findViewById(R.id.phone);
//        stateEditText = findViewById(R.id.state);
//        zipEditText = findViewById(R.id.zipCode);
//
//        personNameEditTextBilling = findViewById(R.id.fullName_billing_EditText);
//        address01EditTextBilling = findViewById(R.id.adress01_billing_EditText);
//        address02EditTextBilling = findViewById(R.id.address02_billing_EditText);
//        cityEditTextBilling = findViewById(R.id.city_billing_EditText);
//        phoneEditTextBilling = findViewById(R.id.phone_billing_EditText);
//        stateEditTextBilling = findViewById(R.id.state_billing_EditText);
//        zipEditTextBilling = findViewById(R.id.zip_billing_EditText);
//        placeYourOrder = findViewById(R.id.place_Order_Button);
//
//        //adding validation to edit texts
//        awesomeValidation.addValidation(this, R.id.fullName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
//        awesomeValidation.addValidation(this, R.id.address1, "^[A-Za-z0-9\\.\\,\\#\\-\\s]{1,}$", R.string.addresserror);
//        awesomeValidation.addValidation(this, R.id.city, "^[A-Za-z\\s]{1,}$", R.string.cityerror);
//        awesomeValidation.addValidation(this, R.id.state, "^[A-Za-z\\s]{1,}$", R.string.stateerror);
//        awesomeValidation.addValidation(this, R.id.zipCode, "^[0-9]{5}$", R.string.ziperror);
//        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
//        awesomeValidation.addValidation(this, R.id.phone, "^[0-9]{10}$", R.string.phoneerror);
//
//        awesomeValidation.addValidation(this, R.id.fullName_billing_EditText, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
//        awesomeValidation.addValidation(this, R.id.adress01_billing_EditText, "^[A-Za-z0-9\\.\\,\\#\\-\\s]{1,}$", R.string.addresserror);
//        awesomeValidation.addValidation(this, R.id.city_billing_EditText, "^[A-Za-z\\s]{1,}$", R.string.cityerror);
//        awesomeValidation.addValidation(this, R.id.state_billing_EditText, "^[A-Za-z\\s]{1,}$", R.string.stateerror);
//        awesomeValidation.addValidation(this, R.id.zip_billing_EditText, "^[0-9]{5}$", R.string.ziperror);
//        awesomeValidation.addValidation(this, R.id.phone_billing_EditText, "^[0-9]{10}$", R.string.phoneerror);
//
//        placeYourOrder.setOnClickListener((View.OnClickListener) this);
//    }
//
//    //display order summery
//    /*private void drawSummary() {
//        int cartSize = cartDetails.getCartSize();
//        total = cartDetails.getTotal();
//        Double tax = Double.valueOf(total)*0.0775;
//        grandTotal = Double.valueOf(total) + tax;
//        ((TextView)findViewById(R.id.itemsCount_Summuy_Textview)).setText("Item: (" + String.valueOf(cartSize) + ")");
//        ((TextView)findViewById(R.id.itemsValue_Summury_TextView)).setText("$ " + total);
//        ((TextView)findViewById(R.id.shippingValues_TextView)).setText("$ 0.00");
//        ((TextView)findViewById(R.id.totalTaxValue_TextView)).setText("$ "+ String.format("%.2f", tax));
//        ((TextView)findViewById(R.id.OrderTotalValue_Textview)).setText("$ "+ String.format("%.2f", grandTotal));
//    }
//    */
//    //if billing address same as shipping address
//    public void sameAddress(View view) {
//        boolean checked = ((CheckBox) view).isChecked();
//        if (checked) {
//            personNameEditTextBilling.setText(personNameEditText.getText());
//            address01EditTextBilling.setText(address01EditText.getText());
//            address02EditTextBilling.setText(address02EditText.getText());
//            cityEditTextBilling.setText(cityEditText.getText());
//            phoneEditTextBilling.setText(phoneEditText.getText());
//            stateEditTextBilling.setText(stateEditText.getText());
//            zipEditTextBilling.setText(zipEditText.getText());
//        }
//    }
//
//    //place order
// /*   private void submitForm(final View view) {
//        if (awesomeValidation.validate()) {
//            CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
//
//            Intent goToConfirmationPage = new Intent(view.getContext(),ConfirmationPage.class);
//            String personName = ((TextView)findViewById(R.id.fullName_EditText)).getText().toString();
//            String address = ((TextView)findViewById(R.id.address01_EditText)).getText().toString() + ", " +
//                    ((TextView)findViewById(R.id.address02_EditText)).getText().toString() + ",\n" +
//                    ((TextView)findViewById(R.id.city_EditText)).getText().toString() + ",\n" +
//                    ((TextView)findViewById(R.id.state_EditText)).getText().toString() + " " +
//                    ((TextView)findViewById(R.id.zip_EditText)).getText().toString();
//            goToConfirmationPage.putExtra("Name",personName);
//            goToConfirmationPage.putExtra("GrandTotal", String.format("%.2f", grandTotal));
//            goToConfirmationPage.putExtra("Address",address);
//            Card cardToSave = mCardInputWidget.getCard();
//            if (cardToSave == null) {
//                Toast.makeText(view.getContext(),R.string.carderror, Toast.LENGTH_SHORT).show();
//            }else {
//                Stripe stripe = new Stripe(view.getContext(), "pk_test_Hbbk5YhQWrLvVrENe9ThYkJa");
//                finish();
//                goToConfirmationPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(goToConfirmationPage);
//                stripe.createToken(
//                        cardToSave,
//                        new TokenCallback() {
//                            public void onSuccess(Token token) {
//                                //Charge customer here
//                            }
//                            public void onError(Exception error) {
//                                //Display Error
//                            }
//                        }
//                );
//            }
//        }
//    }
//*/
//
//  /*  public boolean onCreateOptionsMenu(final Menu menu) {
//        getMenuInflater().inflate(R.menu.without_search_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_add_contact2:
//                startActivity(new Intent(this, Cart.class));
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//*/
//
//
//}
