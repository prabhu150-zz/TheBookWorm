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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.thebookworm.Activities.BaseActivity;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Product;
import com.example.thebookworm.Models.Seller;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowInventory extends Fragment {


    private BackEnd backEnd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.show_seller_inventory, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        backEnd = new BackEnd(getActivity(), "ShowInventory");
        super.onCreate(savedInstanceState);

        Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");
        currentSeller.loadInventory(getActivity());


    }

    @Override
    public void onStart() {
        super.onStart();
        handleSellerInventory();
    }

    public void handleSellerInventory() {
        Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");
        getAllProductsFromSeller(currentSeller, "book"); // again include an enum later to show all supported products

    }

    private void getAllProductsFromSeller(final Seller currentSeller, final String productType) {

        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("/users/sellers/" + currentSeller.getUserID()).child("/inventory/").child(productType);

        backEnd.logit("Getting all products from seller: " + currentSeller.getUserID());

        final GroupAdapter<ViewHolder> adapter = new GroupAdapter<>();

        final RecyclerView recyclerView = getView().findViewById(R.id.inventoryList);
        final List<InventoryList> inventoryList = new ArrayList<>();


        final List<Product> inventoryItems = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    backEnd.logit("Found " + dataSnapshot.getChildrenCount() + " products!");
                    for (DataSnapshot currentChild : dataSnapshot.getChildren()) {
                        Product currentProduct = backEnd.getSpecificProduct(currentChild, productType);
                        inventoryItems.add(currentProduct);
                        inventoryList.add(new InventoryList(currentProduct, backEnd, adapter));

                    }

                    Collections.reverse(inventoryList);

                    for (InventoryList currProd : inventoryList)
                        adapter.add(currProd);


                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(@NonNull Item item, @NonNull View view) {

                            String pid = ((TextView) view.findViewById(R.id.productID)).getText().toString();
                            Product selectedProduct = findProduct(inventoryItems, pid);
                            Fragment productDescription = new CatalogItemDescription();

                            Bundle fragArguments = new Bundle();
                            fragArguments.putString("pid", selectedProduct.getPID());
                            fragArguments.putString("productType", selectedProduct.getType());
                            fragArguments.putString("currentUserType", "seller");

                            fragArguments.putString("request", getString(R.string.seller_get_product_by_id_request));

                            ((BaseActivity) getActivity()).redirectToFragment(productDescription, fragArguments);

                            Log.d("Args", "FragArgs: " + pid);

                        }
                    });

                    recyclerView.setAdapter(adapter);

                } else {
                    backEnd.logit("No products found!");
                    getView().findViewById(R.id.emptyInventoryAlert).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.placeOrder).setVisibility(View.GONE);
                    backEnd.notifyByToast("No Products Found!");
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
                backEnd.notifyByToast("Couldnt get products. Error: " + databaseError.getMessage());
            }
        });
    }
}

class InventoryList extends Item<ViewHolder> {

    Product currentProduct;
    BackEnd backEnd;
    GroupAdapter<ViewHolder> adapter;


    public InventoryList(Product currentProduct, BackEnd backEnd, GroupAdapter<ViewHolder> adapter) {
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
        Button removeFromInventory = viewHolder.itemView.findViewById(R.id.removeFromCart);

        Picasso.get().load(currentProduct.getImageURL()).into(productImage);
        productName.setText(currentProduct.getName());
        productPrice.setText(String.format("$%.2f", currentProduct.getPrice()));
        productStock.setText("Stock: " + currentProduct.getAvailableStock());
        productSeller.setText("Sold By: " + currentProduct.getSoldBy());
        productId.setText(currentProduct.getPID());

        final InventoryList currentRow = this;

        removeFromInventory.setVisibility(View.VISIBLE);
        removeFromInventory.setText("Remove from Inventory");
        removeFromInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backEnd.removeItemFromInventory(currentProduct.getPID(), currentProduct.getType());
//                    backEnd.updateSellerOnBackEnd();
                adapter.remove(currentRow);
                notifyChanged();
            }
        });


    }

}


