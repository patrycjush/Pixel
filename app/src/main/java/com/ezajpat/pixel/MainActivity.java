package com.ezajpat.pixel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRefRead, myRefWrite;
    private String userID;
    private String orderID ;
    private Boolean first;
    private int restaurant = 2;

    //list
    ListView listView;

    private MyCustomAdapter mAdapter;

    private ArrayList<String> mProducts = new ArrayList<String>();
    private ArrayList<String> mSpecialProducts = new ArrayList<String>();
    private ArrayList<String> mRestaurants = new ArrayList<String>();
    private ArrayList<String> mNames = new ArrayList<String>();
    private Map<Integer, Integer> mCheckout = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRefRead = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        listView = (ListView)findViewById(R.id.list);
        mAdapter = new MyCustomAdapter(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        Intent intent = getIntent();
        orderID = intent.getStringExtra("ORDER_ID");
        if(orderID != null) {
            myRefWrite = mFirebaseDatabase.getReference().child("Checkout").child(orderID);
            myRefWrite.removeValue();
            mCheckout.clear();
        }

        myRefRead.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name;
                String additives;
                String price;
                int productID;

                // MENU
                if (listView.getAdapter() == null) {

                    // STORE CHECKOUT COUNT
                    for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                        if (ds1.getKey().equals("Checkout")) {
                            for (DataSnapshot ds2 : ds1.getChildren()) {
                                if(ds2.getKey().equals(userID)) {
                                    for (DataSnapshot ds3 : ds2.getChildren()) {
                                        productID = Integer.parseInt(ds3.getKey());
                                        mCheckout.put(productID, Integer.parseInt(ds3.child("count").getValue().toString()));
                                    }
                                }
                            }
                        }
                    }

                    for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                        if (ds1.getKey().equals("Menu")) {
                            for (DataSnapshot ds2 : ds1.getChildren()) {
                                first = true;
                                for (DataSnapshot ds3 : ds2.child("items").getChildren()) {
                                    name = "#" + ds3.child("name").getValue().toString();

                                    if (ds2.child("name").getValue().toString().equals("Łódź")) {
                                        mAdapter.addSectionHeaderItem(name);
                                        mProducts.add("nonclickable");
                                    }
                                    for (DataSnapshot ds4 : ds3.child("items").getChildren()) {
                                        name = ds4.child("name").getValue().toString();
                                        additives = ds4.child("additives").getValue().toString();
                                        price = ds4.child("price").getValue().toString() + ",00 zł | +";

                                        if (ds2.child("name").getValue().toString().equals("Łódź")) {
                                            mAdapter.addItem(name, additives, price);
                                            mProducts.add("{\"name\": \"" + name + "\",\"additives\": \"" + additives + "\", \"price\": \"" + ds4.child("price").getValue().toString() + "\"}");
                                        }

                                        if(first) {
                                            mSpecialProducts.add("{\"name\": \"" + name + "\",\"additives\": \"" + additives + "\", \"price\": \"" + ds4.child("price").getValue().toString() + "\"}");
                                            mRestaurants.add(ds2.child("name").getValue().toString());
                                            mNames.add(name);
                                            first = false;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    System.out.println(mRestaurants);

                    listView.setAdapter(mAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (!mProducts.get(position).equals("nonclickable")) {
                                JSONObject reader = null;
                                String name = "";
                                String additives = "";
                                String price = "";

                                try {
                                    reader = new JSONObject(mProducts.get(position));

                                    name = reader.getString("name");
                                    additives = reader.getString("additives");
                                    price = reader.getString("price");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                myRefWrite = mFirebaseDatabase.getReference().child("Checkout").child(userID).child(position + "");
                                myRefWrite.child("name").setValue(name);
                                myRefWrite.child("additives").setValue(additives);
                                myRefWrite.child("price").setValue(price);

                                if(mCheckout.containsKey(position)) {
                                    mCheckout.put(position, mCheckout.get(position) + 1);
                                    myRefWrite.child("count").setValue(mCheckout.get(position));
                                } else {
                                    mCheckout.put(position, 1);
                                    myRefWrite.child("count").setValue(1);
                                }

                                Toast.makeText(MainActivity.this, "Dodano do koszyka: " + name, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "W trakcie implementacji. Przycisk będzie przenosić użytkownika do koszyka :)", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, CheckoutActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_location) {
            String[] items = new String[mRestaurants.size()];
            items = mRestaurants.toArray(items);

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Wybierz lokal");
            b.setSingleChoiceItems(items, restaurant, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCheckout.clear();
                    restaurant = which;
                    mProducts.set(1, mSpecialProducts.get(which));
                    mAdapter.mItems.set(1, mNames.get(which));
                    listView.setAdapter(mAdapter);

                    myRefWrite = mFirebaseDatabase.getReference().child("Checkout").child(userID);
                    myRefWrite.removeValue();
                    dialog.dismiss();
                }

            });

            b.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            startActivity(new Intent(MainActivity.this, OrdersActivity.class));
        } else if (id == R.id.nav_map) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        } else if (id == R.id.nav_info) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
