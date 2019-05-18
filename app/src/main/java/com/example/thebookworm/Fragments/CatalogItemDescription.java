package com.example.thebookworm.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookworm.R;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Book;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

public class CatalogItemDescription extends Fragment {


    BackEnd backEnd;
    Buyer currentBuyer;

    ImageView productImage;
    TextView price, productName, soldBy, stocks, author, genre, publisher, bookStocks, title, pages, datePublished;
    JustifiedTextView itemDescription;
    Button addToCart, buyNow;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        backEnd = new BackEnd(getActivity(), "ProductDescription#logger");

        return inflater.inflate(R.layout.product_description, container, false);
    }

    private void findIDs() {
        currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
        productImage = getView().findViewById(R.id.productImage);
        price = getView().findViewById(R.id.sellerPrice);
        productName = getView().findViewById(R.id.productName);
        soldBy = getView().findViewById(R.id.seller);
        addToCart = getView().findViewById(R.id.add_to_cart);
        buyNow = getView().findViewById(R.id.buy_now);
        stocks = getView().findViewById(R.id.stock);
        author = getView().findViewById(R.id.author);
        genre = getView().findViewById(R.id.genre);
        publisher = getView().findViewById(R.id.publisher);
        bookStocks = getView().findViewById(R.id.bookStocks);
        title = getView().findViewById(R.id.title);
        pages = getView().findViewById(R.id.pages);
        datePublished = getView().findViewById(R.id.datePublished);
        itemDescription = getView().findViewById(R.id.itemDescription);
        addToCart = getView().findViewById(R.id.add_to_cart);

    }


    @Override
    public void onStart() {
        super.onStart();
        findIDs();
        checkArguments();

    }

    private void checkArguments() {

        String pid = getArguments().getString("pid");

//        Buyer currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");

        if (pid == null)
            throw new IllegalArgumentException("Arguments not recieved!");

        retrieveProduct(getArguments());
    }

    private boolean checkItemStatus(String pid, Buyer currentBuyer) {


        if (currentBuyer.checkInCart(pid)) {
            addToCart.setText(getResources().getString(R.string.removeCartLabel));
            addToCart.setBackgroundColor(getResources().getColor(R.color.danger_red));
            return true;
        } else {
            addToCart.setText(getResources().getString(R.string.addCartLabel));
            addToCart.setBackgroundColor(getResources().getColor(R.color.sky_blue));
            return false;
        }
    }

    private void retrieveProduct(final Bundle arguments) {

        String pid = getArguments().getString("pid");
        String productType = getArguments().getString("productType");


        FirebaseDatabase.getInstance().getReference("market/products/" + productType).child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Product currentProduct = backEnd.fetchProduct(dataSnapshot, arguments);

                    Log.d("checkUrl", "onDataChange: " + currentProduct.getImageURL());
                    updateBuyersUI(currentProduct);

                } else {


                    // TODO redirect to catalog

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateBuyersUI(final Product currentProduct) {


//        checkItemStatus(currentProduct.getPID(), currentBuyer);

        buyNow.setText("Buy Now!");

        Picasso.get().load(currentProduct.getImageURL()).into(productImage);
        productName.setText(currentProduct.getName());
        soldBy.setText(currentProduct.getSoldBy());
        price.setText(String.format("$%.2f", currentProduct.getPrice()));

        getItemDetails((Book) currentProduct, author, genre, publisher, bookStocks, title, pages, datePublished, itemDescription);


        String stocks_str = "Stocks: " + currentProduct.getAvailableStock() + " items";
        stocks.setText(stocks_str);

        currentBuyer = (Buyer) (backEnd.getFromPersistentStorage("currentUser"));

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!checkItemStatus(currentProduct.getPID(), currentBuyer)) {
                    currentBuyer.addToCart(currentProduct);
                    backEnd.saveToPersistentStorage("currentUser", currentBuyer);
                    backEnd.updateBuyeronBackEnd(currentBuyer, currentBuyer.cartSize());
                    backEnd.notifyByToast("Added to Cart!");
                } else {
                    backEnd.removeItemFromCart(currentProduct.getPID());
                    backEnd.notifyByToast("Removed from Cart!");
                }

            }
        });


        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add to cart first
                // redirect to orders
            }
        });


    }

    private void getItemDetails(Book currentProduct, TextView author, TextView genre, TextView publisher, TextView bookStocks, TextView title, TextView pages, TextView datePublished, JustifiedTextView itemDescription) {
        Book currentBook = currentProduct;
        author.setText(currentBook.getAuthor());
        genre.setText(currentBook.getGenre());
        publisher.setText(currentBook.getPublisher());
        bookStocks.setText(String.valueOf(currentBook.getAvailableStock()));
        title.setText(currentBook.getTitle());
        pages.setText(String.valueOf(currentBook.getPages()));
        datePublished.setText(currentBook.getDatePublished());
        itemDescription.setText(currentBook.getDescription());
    }
}
