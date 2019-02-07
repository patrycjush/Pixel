package com.ezajpat.pixel;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrdersActivity extends AppCompatActivity {

    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRefRead;
    private String userID;

    //list
    ListView listView;

    private MyCustomAdapter mAdapter;

    private ArrayList<String> mProductsOrders = new ArrayList<String>();
    private Map<Integer, Integer> mCheckout = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        toolbar.setTitle("Zamówienia");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


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

        myRefRead.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (listView.getAdapter() == null) {
                    String name;
                    String additives;
                    String price;

                    for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                        if (ds1.getKey().equals("Orders")) {
                            for (DataSnapshot ds2 : ds1.getChildren()) {
                                if (ds2.getKey().equals(userID)) {
                                    for (DataSnapshot ds3 : ds2.getChildren()) {
                                        name = "#" + ds3.getKey();
                                        mAdapter.addSectionHeaderItem(name);
                                        name = ds3.child("name").getValue().toString();
                                        additives = ds3.child("additives").getValue().toString();
                                        price = ds3.child("price").getValue().toString() + ",00 zł";
                                        mAdapter.addItem(name, additives, price);
                                        mProductsOrders.add("{\"name\": \"" + name + "\",\"additives\": \"" + additives + "\", \"price\": \"" + ds3.child("price").getValue().toString() + "\"}");
                                    }
                                }
                            }
                        }
                    }

                    listView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
