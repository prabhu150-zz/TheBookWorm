package com.example.thebookworm.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.thebookworm.Models.Buyer;
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

import java.util.ArrayList;
import java.util.List;

public class ShowCatalog extends Fragment {

    private BackEnd backEnd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        backEnd = new BackEnd(getContext(), "BuyerDashBoard");
        return inflater.inflate(R.layout.product_catalog, container, false);
    }

    private void notifyByToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        backEnd = new BackEnd(getActivity(), "BuyerDashBoard");
        handleBuyersCatalog();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void handleBuyersCatalog() {
        Buyer currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
        getAllProducts("book"); // this can be extended to include all types of products in future by passing in an enum of all items currently supported
    }

    private void getAllProducts(final String productType) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("/market/products/" + productType + "/");

        Query getAllProducts = productRef.orderByChild("/pid/");

        final GroupAdapter<ViewHolder> adapter = new GroupAdapter<>();

        final RecyclerView recyclerView = getView().findViewById(R.id.inventoryList);
        final List<ProductList> productsList = new ArrayList<>();
        final List<Product> catalogProducts = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getAllProducts.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    backEnd.logit("Found " + dataSnapshot.getChildrenCount() + " products!");
                    for (DataSnapshot currentChild : dataSnapshot.getChildren()) {
                        Product currentProduct = backEnd.getSpecificProduct(currentChild, productType);
                        catalogProducts.add(currentProduct);
                        productsList.add(new ProductList(currentProduct, backEnd, adapter));

                    }

//                    Collections.reverse(productsList);

                    for (ProductList currProd : productsList)
                        adapter.add(currProd);


                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(@NonNull Item item, @NonNull View view) {

                            String pid = ((TextView) view.findViewById(R.id.productID)).getText().toString();
                            Product selectedProduct = findProduct(catalogProducts, pid);
                            Fragment productDescription = new CatalogItemDescription();

                            Bundle fragArguments = new Bundle();
                            fragArguments.putString("pid", selectedProduct.getPID());
                            fragArguments.putString("productType", selectedProduct.getType());
                            fragArguments.putString("request", getString(R.string.buyer_get_product_by_id_request));

                            ((BaseActivity) getActivity()).redirectToFragment(productDescription, fragArguments);

                            Log.d("Args", "FragArgs: " + pid);

                        }
                    });

                    recyclerView.setAdapter(adapter);

                } else {
                    backEnd.logit("No products found!");
                    getView().findViewById(R.id.emptyInventoryAlert).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.placeOrder).setVisibility(View.GONE);
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

}

class ProductList extends Item<ViewHolder> {

    Product currentProduct;
    BackEnd backEnd;
    GroupAdapter<ViewHolder> adapter;


    public ProductList(Product currentProduct, BackEnd backEnd, GroupAdapter<ViewHolder> adapter) {
        this.currentProduct = currentProduct;
        this.backEnd = backEnd;
        this.adapter = adapter;
    }

    @Override
    public int getLayout() {
        return R.layout.product_catalog_row;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        TextView productName = viewHolder.itemView.findViewById(R.id.productName);
        TextView productPrice = viewHolder.itemView.findViewById(R.id.sellerPrice);
        ImageView productImage = viewHolder.itemView.findViewById(R.id.productImage);
        TextView productStock = viewHolder.itemView.findViewById(R.id.stock);
        TextView productSeller = viewHolder.itemView.findViewById(R.id.seller);
        TextView productId = viewHolder.itemView.findViewById(R.id.productID);

        Picasso.get().load(currentProduct.getImageURL()).into(productImage);
        productName.setText(currentProduct.getName());
        productPrice.setText(String.format("$%.2f", currentProduct.getPrice()));
        productStock.setText("Stock: " + currentProduct.getAvailableStock());
        productSeller.setText("Sold By: " + currentProduct.getSoldBy());
        productId.setText(currentProduct.getPID());
    }

}


