package com.example.thebookworm;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bookworm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BackEnd singleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        singleton = new BackEnd(this, "FragmentManager");

        Intent prevActivity = getIntent();
        String currentUserType = prevActivity.getStringExtra("userType");

        if (currentUserType.equals("Buyer")) {
            handleBuyer();
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
        setSupportActionBar(toolbar);
        toolbar.setTitle("Buyer DashBoard");
        toolbar.inflateMenu(R.menu.buyer_toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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
        setSupportActionBar(toolbar);
        toolbar.setTitle("Seller Dashboard");

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                String message = "This will add a book";
                singleton.notifyByToast(message);

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Fragment dashBoard = new SellerDashboard();
        replaceFragment(dashBoard);
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

            case R.id.viewOrders:
                singleton.notifyByToast("View all orders!");
                break;

            case R.id.logoutButton:
                singleton.logout();
                break;

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
