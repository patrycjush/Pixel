package com.ezajpat.pixel;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRefRead, myRefWrite;
    private String userID;

    //list
    ListView listView;

    private MyCustomAdapter mAdapter;

    private ArrayList<String> mProducts = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        toolbar.setTitle("Koszyk");
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
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // System.out.println(" Value = " + dataSnapshot.toString());
                if (listView.getAdapter() == null) {
                    String name;
                    String additives;
                    String price;

                    for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                        if (ds1.getKey().equals("Checkout")) {
                            for (DataSnapshot ds2 : ds1.getChildren()) {
                                name = "#" + ds2.getKey();
                                mAdapter.addSectionHeaderItem(name);
                                mProducts.add("nonclickable");
                                if (ds2.getKey().equals(userID)) {
                                    for (DataSnapshot ds3 : ds2.getChildren()) {
                                        // System.out.println(" Name = " + ds3.child("name").getValue().toString());
                                        name = ds3.child("name").getValue().toString();
                                        additives = ds3.child("additives").getValue().toString();
                                        price = ds3.child("price").getValue().toString() + ",00 zł | " + ds3.child("count").getValue().toString();
                                        mAdapter.addItem(name, additives, price);
                                        mProducts.add("{\"name\": \"" + name + "\",\"additives\": \"" + additives + "\", \"price\": \"" + ds3.child("price").getValue().toString() + "\"}");
                                    }
                                }
                            }
                        }
                    }

                    listView.setAdapter(mAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(CheckoutActivity.this, "Usunięto z koszyka: ", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "W trakcie implementacji. Przycisk będzie przenosić użytkownika do podsumowania zamówienia :)", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                startActivity(new Intent(CheckoutActivity.this, SummaryActivity.class));
            }
        });
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
