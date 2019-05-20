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
import com.example.thebookworm.Activities.BaseActivity;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Book;
import com.example.thebookworm.Models.Product;
import com.example.thebookworm.Models.Seller;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

public class InventoryItemDescription extends Fragment {

    private BackEnd backEnd;
    ImageView productImage;
    TextView price, productName, soldBy, stocks, author, genre, publisher, bookStocks, title, pages, datePublished;
    JustifiedTextView itemDescription;
    Button modify, delete;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inventory_item_description, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backEnd = new BackEnd(getActivity(), "SellerSpecificItem#logger");
        checkArguments();
    }

    private void checkArguments() {
        String pid = getArguments().getString("pid");

        if (pid == null)
            throw new IllegalArgumentException("Arguments not recieved!");

        retrieveProduct(getArguments());
    }

    private void retrieveProduct(final Bundle arguments) {

        String pid = getArguments().getString("pid");
        String productType = getArguments().getString("productType");
        Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");

        FirebaseDatabase.getInstance().getReference("users/sellers/" + currentSeller.getUserID() + "/inventory/" + productType).child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Product currentProduct = backEnd.fetchProduct(dataSnapshot, arguments);
                    backEnd.logit("Found one item! P:" + currentProduct.getPID());
                    Log.d("checkUrl", "onDataChange: " + currentProduct.getImageURL());
                    updateSellerUI(currentProduct);

                } else {

                    backEnd.logit("No item by that pid found!");

                    Fragment inventory = new ShowInventory();
                    Bundle args = new Bundle();
                    args.putString("currentUserType", "seller");
                    args.putString("request", getString(R.string.seller_get_all_products_request));

                    ((BaseActivity) getActivity()).redirectToFragment(inventory, args);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateSellerUI(final Product currentProduct) {

        productImage = getView().findViewById(R.id.productImage);
        price = getView().findViewById(R.id.sellerPrice);
        productName = getView().findViewById(R.id.productName);
        soldBy = getView().findViewById(R.id.seller);
        modify = getView().findViewById(R.id.modifyInventory);
        delete = getView().findViewById(R.id.deleteInventory);
        stocks = getView().findViewById(R.id.stock);

        author = getView().findViewById(R.id.author);
        genre = getView().findViewById(R.id.genre);
        publisher = getView().findViewById(R.id.publisher);
        bookStocks = getView().findViewById(R.id.bookStocks);
        title = getView().findViewById(R.id.title);
        pages = getView().findViewById(R.id.pages);
        datePublished = getView().findViewById(R.id.datePublished);
        itemDescription = getView().findViewById(R.id.itemDescription);
        modify = getView().findViewById(R.id.modifyInventoryItem);
        delete = getView().findViewById(R.id.deleteInventoryItem);

        getItemDetails((Book) currentProduct, author, genre, publisher, bookStocks, title, pages, datePublished, itemDescription);

        // product specific details in textbox

        Picasso.get().load(currentProduct.getImageURL()).into(productImage);
        productName.setText(currentProduct.getName());
        soldBy.setText(currentProduct.getSellerName());
        price.setText(String.format("$%.2f", currentProduct.getPrice()));


        String stocks_str = "Stocks: " + currentProduct.getAvailableStock() + " items";
        stocks.setText(stocks_str);

        // TODO display product specific details in the table below

        final Seller currentSeller = (Seller) (backEnd.getFromPersistentStorage("currentUser"));


        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backEnd.notifyByToast("Update your inventory file and reupload to notice the changes");

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backEnd.removeItemFromInventory(currentProduct.getPID(), currentProduct.getType());
                backEnd.notifyByToast("Item removed from inventory!");
                Fragment showInventory = new ShowInventory();
                Bundle args = new Bundle();
                args.putString("request", getResources().getString(R.string.seller_get_all_products_request));
                args.putString("productType", "book");
                args.putString("currentUserType", "seller");

                ((BaseActivity) getActivity()).redirectToFragment(showInventory, args);

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
