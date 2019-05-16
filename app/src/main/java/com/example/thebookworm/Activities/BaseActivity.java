package com.example.thebookworm.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bookworm.R;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Fragments.CustomerList;
import com.example.thebookworm.Fragments.OrderList;
import com.example.thebookworm.Fragments.ShowCatalog;
import com.example.thebookworm.Fragments.ShowInventory;
import com.example.thebookworm.Fragments.ViewCart;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Seller;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean isBuyer;
    private BackEnd backEnd;
    private String currentFragment = "#currentFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backEnd = new BackEnd(this, "FragmentManager");

        Intent prevActivity = getIntent();
        String currentUserType = prevActivity.getStringExtra("userType");

        if (currentUserType.equals("buyer")) {
            isBuyer = true;
            handleBuyer();
        } else if (currentUserType.equals("seller")) {
            isBuyer = false;
            handleSeller();
        } else {
            throw new IllegalArgumentException("Invalid user. Please check intent args");
        }
        backEnd.logit("Reached baseactivity. IsBuyer: " + isBuyer);

    }


    protected void replaceFragment(Fragment currentFragment) {
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.fragment_container, currentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void redirectToFragment(Fragment fragment, Bundle fragmentArguments) {

        final String request = fragmentArguments.getString("request");
        final String userType = fragmentArguments.getString("userType");

        final String showSpecificProductBuyer = getString(R.string.buyer_get_product_by_id_request);
        final String showBuyersCatalog = getString(R.string.buyer_get_all_products_request);
        final String userWantsToBuyRequest = getString(R.string.buyer_proceed_to_order);
        final String showUserHisCart = getString(R.string.buyer_proceed_to_cart);

        final String showSellerHisInventory = getString(R.string.seller_get_all_products_request);
        final String showSellerHisInventoryItem = getString(R.string.seller_get_product_by_id_request);
        final String showSellerHisOrders = getString(R.string.seller_see_orders_request);
        final String showSellerHisCustomers = getString(R.string.seller_see_customers_request);


        backEnd.logit("Reached redirection. IsBuyer: " + isBuyer);


        if (request.equals(showSpecificProductBuyer)) {

            backEnd.logit("About to display specific item");

            fragment.setArguments(fragmentArguments);
            replaceFragment(fragment);

        } else if (request.equals(showSellerHisInventoryItem)) {

            backEnd.logit("About to display inventory");
            fragment.setArguments(fragmentArguments);
            replaceFragment(fragment);

        } else if (request.equals(showBuyersCatalog)) {
            backEnd.logit("About to display all products");
            fragment.setArguments(fragmentArguments);
            replaceFragment(fragment);

        } else if (request.equals(showSellerHisOrders)) {
            //TODO to be implemented

        } else if (request.equals(showSellerHisCustomers)) {
            //TODO to be implemented
        } else if (request.equals(showUserHisCart)) {
            backEnd.logit("Showing Cart!");
            fragment.setArguments(fragmentArguments);
            replaceFragment(fragment);
        } else if (request.equals(userWantsToBuyRequest)) {
            // TODO to be implemented
        } else if (request.equals(showSellerHisInventory)) {
            backEnd.logit("Showing inventory:");
            fragment.setArguments(fragmentArguments);
            replaceFragment(fragment);
        } else {
            throw new IllegalArgumentException("This user not currently supported!" + request);
        }

    }


    private void handleBuyer() {
        setContentView(R.layout.buyer_navbar);

        Toolbar toolbar = findViewById(R.id.toolbar); // this will be sellers dashboard
        toolbar.setTitle("Catalog"); // handle at buyer fragment

        toolbar.inflateMenu(R.menu.buyer_toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View navbar = navigationView.getHeaderView(0);

        updateBuyerDashUI(navbar); // // handle at buyer fragment

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Fragment dashBoard = new ShowCatalog();
        Bundle arguments = new Bundle();
        arguments.putString("userType", "buyer");
        arguments.putString("request", getString(R.string.buyer_get_all_products_request));
        redirectToFragment(dashBoard, arguments);
    }

    private void handleSeller() {

        setContentView(R.layout.seller_navbar);
        Toolbar toolbar = findViewById(R.id.toolbar); // this will be sellers dashboard
        toolbar.setTitle("Inventory");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View navbar = navigationView.getHeaderView(0);
        updateSellerDashUI(navbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        Fragment dashBoard = new ShowInventory();
        Bundle arguments = new Bundle();
        arguments.putString("userType", "buyer");
        arguments.putString("request", getString(R.string.seller_get_all_products_request));
        redirectToFragment(dashBoard, arguments);

    }

    private void updateSellerDashUI(View navbar) {
        Seller currentSeller = (Seller) backEnd.getFromPersistentStorage("currentUser");

        CircleImageView profilePic = navbar.findViewById(R.id.profilePic);
        TextView sellerName = navbar.findViewById(R.id.userName);
        TextView sellerEmail = navbar.findViewById(R.id.userEmail);

        sellerName.setText(currentSeller.getName());
        Picasso.get().load(currentSeller.getProfilePic()).into(profilePic);
        sellerEmail.setText(currentSeller.getEmail());
    }

    private void updateBuyerDashUI(View navbar) {
        Buyer currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");

        CircleImageView profilePic = navbar.findViewById(R.id.profilePic);
        TextView sellerName = navbar.findViewById(R.id.userName);
        TextView sellerEmail = navbar.findViewById(R.id.userEmail);

        sellerName.setText(currentBuyer.getName());
        Picasso.get().load(currentBuyer.getProfilePic()).into(profilePic);
        sellerEmail.setText(currentBuyer.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.buyer_toolbar, menu);
            MenuItem search = menu.findItem(R.id.search);
            SearchView searchView = (SearchView)
                    search.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.search:
                backEnd.notifyByToast("Search clicked!");
                break;

            case R.id.filter:
                backEnd.notifyByToast("Filter clicked!");
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here. Nav drawer items here
        int id = item.getItemId();

        switch (id) {
            case R.id.cart: {
                backEnd.notifyByToast("Shopping Cart!");

                Bundle args = new Bundle();
                args.putString("request", getString(R.string.buyer_proceed_to_cart));
                redirectToFragment(new ViewCart(), args);


//                replaceFragment(); // BUYER ONLY

            }
                break;

            case R.id.catalog: {


                Bundle args = new Bundle();
                if (isBuyer) {

                    Fragment showCatalog = new ShowCatalog();
                    backEnd.notifyByToast("Show Catalog");
                    args.putString("userType", "buyer");
                    args.putString("request", getString(R.string.buyer_get_all_products_request));
                    redirectToFragment(showCatalog, args);

                } else {


                    Fragment showInventory = new ShowInventory();
                    backEnd.notifyByToast("Show Inventory");
                    args.putString("userType", "seller");
                    args.putString("request", getString(R.string.seller_get_all_products_request));
                    redirectToFragment(showInventory, args);
                }

            }
                break;

            case R.id.viewCustomers: {
                backEnd.notifyByToast("View Customers!"); // SELLER ONLY

                Fragment seeCustomers = new CustomerList(); //TODO to be implemented

                Bundle args = new Bundle();

                args.putString("request", getString(R.string.seller_see_customers_request));
                // get seller ID from that class

                redirectToFragment(seeCustomers, args);

            }
                break;

            case R.id.orders: {

                Fragment seeOrders = new OrderList();
                Bundle args = new Bundle();

                if (isBuyer) {
                    backEnd.notifyByToast("Show Buyers Orders");
                    args.putString("userType", "buyer");
                    args.putString("request", getString(R.string.buyer_see_orders_request));

                } else {
                    backEnd.notifyByToast("Show Sellers Orders");
                    args.putString("userType", "seller");
                    args.putString("request", getString(R.string.seller_see_orders_request));
                }
                redirectToFragment(seeOrders, args);

            }
            backEnd.notifyByToast("View all orders!"); // BOTH
                break;

            case R.id.logoutButton:
                backEnd.notifyByToast("Logging out!"); // BOTH
                backEnd.logout();
                break;

            //

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    public Fragment getVisibleFragment TODO get visible fragment to make changes to it



}
