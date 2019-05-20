package com.example.thebookworm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Order;
import com.example.thebookworm.Models.Seller;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class OrdersList extends Fragment {

    private BackEnd backEnd;
    private RecyclerView ordersRecyclerView;
    boolean isBuyer = false;
    private TextView emptyOrdersAlert, customerName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_description, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backEnd = new BackEnd(getActivity(), "OrdersList#logger");

    }

    private void findIDs() {
        ordersRecyclerView = getView().findViewById(R.id.ordersList);
        emptyOrdersAlert = getView().findViewById(R.id.emptyOrdersAlert);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        findIDs();

        String currentUserType = getArguments().getString("currentUserType");

        if (currentUserType.equals("buyer")) {
            getBuyerOrders();
            isBuyer = true;
        } else if (currentUserType.equals("seller")) {
            getSellerOrders();
        } else {
            backEnd.logit("Request not supported!");
        }


    }

    private void getSellerOrders() {
        final Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");

        final GroupAdapter<ViewHolder> adapter = new GroupAdapter<>();

        final List<OrdersItems> ordersLists = new ArrayList<>();

        final List<Order> orders = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("/users/sellers/" + currentSeller.getUserID()).child("/orders/").orderByChild("/grandTotal/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                backEnd.logit("Retrieving " + dataSnapshot.getChildrenCount() + " orders!");
                if (dataSnapshot.exists()) {
                    int index = 1;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Order currentOrder = backEnd.getSpecificOrder(child);

                        orders.add(currentOrder);
                        ordersLists.add(new OrdersItems(currentOrder, index++, child.child("customer/name").getValue().toString(), isBuyer));
                    }

                    for (OrdersItems curr : ordersLists)
                        adapter.add(curr);


                    ordersRecyclerView.setAdapter(adapter);

                } else {
                    backEnd.logit("No orders found!");

                    emptyOrdersAlert.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                backEnd.notifyByToast("FireBase error: " + databaseError.getMessage());

            }
        });


    }

    private void getBuyerOrders() {

        final Buyer currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");


        final GroupAdapter<ViewHolder> adapter = new GroupAdapter<>();

        final List<OrdersItems> ordersLists = new ArrayList<>();

        final List<Order> orders = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("/users/buyers/" + currentBuyer.getUserID()).child("/orders/").orderByChild("/grandTotal/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                backEnd.logit("Retrieving " + dataSnapshot.getChildrenCount() + " orders!");
                if (dataSnapshot.exists()) {
                    int index = 1;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Order currentOrder = backEnd.getSpecificOrder(child);

                        backEnd.logit("Retrieved Order# " + index + " Bill: " + currentOrder.getBill());

                        orders.add(currentOrder);
                        ordersLists.add(new OrdersItems(currentOrder, index++, currentBuyer.getName(), isBuyer));
                    }

                    for (OrdersItems curr : ordersLists)
                        adapter.add(curr);


                    ordersRecyclerView.setAdapter(adapter);

                } else {
                    backEnd.logit("No orders found!");

                    emptyOrdersAlert.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                backEnd.notifyByToast("FireBase error: " + databaseError.getMessage());

            }
        });

    }

}


class OrdersItems extends Item<ViewHolder> {

    Order currentOrder;
    Integer index;
    String currentBuyer;
    boolean isBuyer;

    public OrdersItems(Order currentOrder, int index, String currentBuyer, boolean isBuyer) {
        this.currentOrder = currentOrder;
        this.index = index;
        this.currentBuyer = currentBuyer;
        this.isBuyer = isBuyer;
    }

    @Override
    public int getLayout() {
        return R.layout.order_row;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {

        TextView orderNumber = viewHolder.itemView.findViewById(R.id.orderNumber);

        TextView numItems = viewHolder.itemView.findViewById(R.id.numItemsOrder);
        TextView customerName = viewHolder.itemView.findViewById(R.id.customerName);
        TextView theBill = viewHolder.itemView.findViewById(R.id.theBill);
        TextView grandTotal = viewHolder.itemView.findViewById(R.id.grandTotalOrder);
        TextView shippingCost = viewHolder.itemView.findViewById(R.id.shippingCostsOrder);
        TextView estimatedTax = viewHolder.itemView.findViewById(R.id.estimatedTax);


        orderNumber.setText("Order #" + index);

        if (!isBuyer)
            customerName.setText("Bought by: " + currentBuyer);
        else
            customerName.setVisibility(View.INVISIBLE);

        numItems.setText(currentOrder.getNumItems() + " items");
        theBill.setText(String.format("Bill : %.2f", currentOrder.getBill()) + "$");
        grandTotal.setText(String.format("Total : %.2f", currentOrder.getGrandTotal()) + "$");
        shippingCost.setText(String.format("Shipping : %.2f", currentOrder.getShippingCosts()) + "$");
        estimatedTax.setText(String.format("Tax : %.2f", currentOrder.getEstimatedTax()) + "$");


    }

}