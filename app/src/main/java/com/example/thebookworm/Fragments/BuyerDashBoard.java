package com.example.thebookworm.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.thebookworm.Activities.BaseActivity;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Book;
import com.example.thebookworm.Models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuyerDashBoard extends Fragment {

    private BackEnd singleton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        singleton = new BackEnd(getContext(), "BuyerDashBoard");
        return inflater.inflate(R.layout.buyer_dashboard, container, false);
    }

    private void notifyByToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        singleton = new BackEnd(getContext(), "BuyerDashBoard");
        handleBuyerDashBoard();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void handleBuyerDashBoard() {
        getAllProducts("book"); // this can be extended to include all types of products in future
    }

    private void getAllProducts(final String productType) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("/market/products/" + productType + "/");

        Query getAllProducts = productRef.orderByChild("/pid/");

        final GroupAdapter<ViewHolder> adapter = new GroupAdapter<>();
        final RecyclerView recyclerView = getView().findViewById(R.id.productsList);
        final List<ProductRow> productsList = new ArrayList<>();
        final List<Product> catalogProducts = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getAllProducts.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    singleton.logit("Found " + dataSnapshot.getChildrenCount() + " products!");
                    for (DataSnapshot currentChild : dataSnapshot.getChildren()) {

                        Product currentProduct = getSpecificProduct(currentChild, productType);

                        catalogProducts.add(currentProduct);
                        productsList.add(new ProductRow(currentProduct));

                    }

                    Collections.reverse(productsList);

                    for (ProductRow currProd : productsList)
                        adapter.add(currProd);



                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(@NonNull Item item, @NonNull View view) {

                            String pid = ((TextView) view.findViewById(R.id.productID)).getText().toString();


                            Product selectedProduct = findProduct(catalogProducts, pid);


                            Bundle fragArguments = new Bundle();


                            fragArguments.putString("pid", selectedProduct.getPID());
                            fragArguments.putString("productType", "book"); // TODO fix this after reloading inventory should be selectprod.getType()


                            ((BaseActivity) getActivity()).redirectToFragment(getString(R.string.buyer_get_product_by_id_request), fragArguments);


                            Log.d("Args", "FragArgs: " + pid);


                        }
                    });

                    recyclerView.setAdapter(adapter);

                } else {
                    singleton.logit("No products found!");
                    getView().findViewById(R.id.emptyInventoryAlert).setVisibility(View.VISIBLE);
                    notifyByToast("No Products Found!");
                }

            }

            private Product findProduct(List<Product> catalogProducts, String pid) {
                for (Product curr : catalogProducts)
                    if (curr.getPID().equals(pid))
                        return curr;

                throw new IllegalStateException("Product not found!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                notifyByToast("Couldnt get products. Error: " + databaseError.getMessage());
            }
        });


    }

    @NotNull
    private Product getSpecificProduct(DataSnapshot currentChild, String productType) {

        Product currentProduct;

        productType = productType.trim().toLowerCase().replaceAll("[^\\w\\s]", "");

        switch (productType) {
            case "book":

                currentProduct = new Book(singleton.getChildStringVal(currentChild, "/name/"), singleton.getChildStringVal(currentChild, "/description/"), singleton.getChildStringVal(currentChild, "/imageURL/"), Double.parseDouble(singleton.getChildStringVal(currentChild, "/price/")), singleton.getChildStringVal(currentChild, "/pid/"), Integer.parseInt(singleton.getChildStringVal(currentChild, "/availableStock/")), singleton.getChildStringVal(currentChild, "/soldBy"), singleton.getChildStringVal(currentChild, "/type"));
//                String author, String genre, String publisher, int pages, String datePublished

                ((Book) currentProduct).setDetails(singleton.getChildStringVal(currentChild, "/author/"), singleton.getChildStringVal(currentChild, "/genre"), singleton.getChildStringVal(currentChild, "/publisher/"), Integer.parseInt(singleton.getChildStringVal(currentChild, "/pages/")), singleton.getChildStringVal(currentChild, "/datePublished"));
                return currentProduct;

            default:
                throw new IllegalArgumentException("This product " + productType + " is not yet supported!");
        }
    }


    static class ProductRow extends Item<ViewHolder> implements Filterable {

    Product currentProduct;

    ProductRow(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

        private Filter productFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };

        @Override
        public int getLayout() {
            return R.layout.product_catalog_row;
        }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        TextView productName = viewHolder.itemView.findViewById(R.id.productName);
        TextView productPrice = viewHolder.itemView.findViewById(R.id.soldBy);
        ImageView productImage = viewHolder.itemView.findViewById(R.id.productImage);
        TextView productStock = viewHolder.itemView.findViewById(R.id.stock);
        TextView productSeller = viewHolder.itemView.findViewById(R.id.seller);
        TextView productId = viewHolder.itemView.findViewById(R.id.productID);


        Picasso.get().load(currentProduct.getImageURL()).into(productImage);
        productName.setText(currentProduct.getName());
        productPrice.setText(String.format("$%.2f", currentProduct.getPrice()));
        productStock.setText(String.valueOf(currentProduct.getAvailableStock()));
        productSeller.setText(currentProduct.getSoldBy());
        productId.setText(currentProduct.getPID());

    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    }
}



