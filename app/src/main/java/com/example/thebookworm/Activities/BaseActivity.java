package com.example.thebookworm.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.thebookworm.Fragments.BuyerDashBoard;
import com.example.thebookworm.Fragments.SellerDashboard;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Seller;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BackEnd singleton;
    boolean isBuyer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        singleton = new BackEnd(this, "FragmentManager");

        Intent prevActivity = getIntent();
        String currentUserType = prevActivity.getStringExtra("userType");

        if (currentUserType.equals("Buyer")) {
            handleBuyer();
            isBuyer = true;
        } else if (currentUserType.equals("Seller")) {
            handleSeller();
        } else {
            throw new IllegalArgumentException("Invalid user. Please check intent args");
        }
    }

    protected void replaceFragment(Fragment currentFragment) {
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, currentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void handleBuyer() {
        setContentView(R.layout.buyer_navbar);
        Toolbar toolbar = findViewById(R.id.toolbar); // this will be sellers dashboard
        toolbar.setTitle("Buyer DashBoard");
        toolbar.inflateMenu(R.menu.buyer_toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View navbar = navigationView.getHeaderView(0);
        updateBuyerDashUI(navbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Fragment dashBoard = new BuyerDashBoard();
        replaceFragment(dashBoard);
    }

    private void handleSeller() {

        setContentView(R.layout.seller_navbar);
        Toolbar toolbar = findViewById(R.id.toolbar); // this will be sellers dashboard
        toolbar.setTitle("Seller Dashboard");
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

        Fragment dashBoard = new SellerDashboard();
        replaceFragment(dashBoard);
    }

    private void updateSellerDashUI(View navbar) {
        Seller currentSeller = (Seller) singleton.getFromPersistentStorage("currentUser");

        CircleImageView profilePic = navbar.findViewById(R.id.profilePic);
        TextView sellerName = navbar.findViewById(R.id.userName);
        TextView sellerEmail = navbar.findViewById(R.id.userEmail);

        sellerName.setText(currentSeller.getName());
        Picasso.get().load(currentSeller.getProfilePic()).into(profilePic);
        sellerEmail.setText(currentSeller.getEmail());
    }


    private void updateBuyerDashUI(View navbar) {
        Buyer currentBuyer = (Buyer) singleton.getFromPersistentStorage("currentUser");

        CircleImageView profilePic = navbar.findViewById(R.id.profilePic);
        TextView sellerName = navbar.findViewById(R.id.userName);
        TextView sellerEmail = navbar.findViewById(R.id.userEmail);

        sellerName.setText(currentBuyer.getNickname());
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

//        return super.onCreateOptionsMenu(R.menu.buyer_toolbar);
        // TODO fix this
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id) {
            case R.id.cart:
                singleton.notifyByToast("Shopping Cart!");
                break;

            case R.id.catalog:
                singleton.notifyByToast("Show Catalog");
                break;

            case R.id.viewCustomers:
                singleton.notifyByToast("View Customers!");
                break;

            case R.id.orders:
                singleton.notifyByToast("View all orders!");
                break;

            case R.id.logoutButton:
                singleton.notifyByToast("Logging out!");
                singleton.logout();
                break;

            ///////

            case R.id.search:
                singleton.notifyByToast("Search!");
                break;

            case R.id.filter:
                singleton.notifyByToast("Filter!");
                break;

            case R.id.cartButton:
                singleton.notifyByToast("Cart!");
                break;

        }

        switch (item.getItemId()) {


        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
