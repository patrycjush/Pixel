package com.ezajpat.pixel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class SummaryActivity extends AppCompatActivity {

    private static final String TAG = "ViewDatabase";

    EditText etName, etPhone, etEmail;
    TextView tvDeliveryPrice, tvAmount;
    RadioGroup rGroup;
    RadioButton rBtnTakeAway, rBtnDelivery;
    Button btnConfirmOrder;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRefRead, myRefWrite;
    private String userID;

    private int amount = 0;
    private String orderID;
    private boolean delivery = false;
    private boolean filled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        toolbar.setTitle("Podsumowanie");
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
        amount = Integer.parseInt(intent.getStringExtra("AMOUNT"));

        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etEmail = (EditText) findViewById(R.id.et_email);
        tvDeliveryPrice = (TextView) findViewById(R.id.tv_delivery_price);
        tvAmount = (TextView) findViewById(R.id.tv_amount);
        rGroup = (RadioGroup)findViewById(R.id.radio_group_delivery);
        rBtnTakeAway = (RadioButton)findViewById(R.id.radio_item_take_away);
        rBtnDelivery = (RadioButton)findViewById(R.id.radio_item_delivery);
        btnConfirmOrder = (Button) findViewById(R.id.confirm_order_button);

        tvAmount.setText(amount + ",00 zł");

        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rBtnTakeAway.setError(null);
                rBtnDelivery.setError(null);

                if (checkedId == R.id.radio_item_delivery) {
                    delivery = true;
                    amount = Integer.parseInt(tvAmount.getText().toString().split(",")[0]) + 11;
                    tvDeliveryPrice.setText("11,00 zł");
                    tvAmount.setText(amount + ",00 zł");
                } else {
                    if(delivery && amount != 0) {
                        delivery = false;
                        amount = Integer.parseInt(tvAmount.getText().toString().split(",")[0]) - 11;
                        tvDeliveryPrice.setText("0,00 zł");
                        tvAmount.setText(amount + ",00 zł");
                    }
                }
            }
        });

        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                filled = true;

                if (etName.getText().toString().trim().length() == 0) {
                    etName.setError("Imię i nazwisko nie może być puste");
                    filled = false;
                }

                if (etPhone.getText().toString().trim().length() == 0) {
                    etPhone.setError("Nr telefonu nie może być puste");
                    filled = false;
                }

                if (etEmail.getText().toString().trim().length() == 0) {
                    etEmail.setError("Email nie może być puste");
                    filled = false;
                }

                if (!rBtnTakeAway.isChecked() && !rBtnDelivery.isChecked()) {
                    rBtnTakeAway.setError("");
                    rBtnDelivery.setError("");
                    filled = false;
                }

                if (filled) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SummaryActivity.this);
                    alertDialogBuilder.setMessage("Czy jesteś pewien, że chcesz złożyć zamówienie?");
                    alertDialogBuilder.setPositiveButton("Tak",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    // Toast.makeText(SummaryActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                    // create instance of Random class
                                    Random rand = new Random();
                                    orderID = "";
                                    for(int i = 0; i < 10; i++) {
                                        orderID += String.valueOf(rand.nextInt(10));
                                    }

                                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                                    Date date = new Date();

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SummaryActivity.this);
                                    alertDialogBuilder.setTitle("Podsumowanie zamówienia");
                                    alertDialogBuilder.setMessage("Dziękujemy za zaufanie w jakość i smak naszych potraw. Nasi kucharze zaczynają\nprzygotowywać twoje zamówienie, aby spełnić Twoje kulinarne kaprysy. Smacznego!\n\nCzas przygotowania:30 min\nNumer zamówienia: " + orderID);

                                    alertDialogBuilder.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            Intent intent = new Intent(SummaryActivity.this, MainActivity.class);
                                            intent.putExtra("ORDER_ID", String.valueOf(userID));
                                            startActivity(intent);
                                        }
                                    });

                                    myRefWrite = mFirebaseDatabase.getReference().child("Orders").child(userID).child(orderID);
                                    myRefWrite.child("name").setValue(dateFormat.format(date));
                                    myRefWrite.child("additives").setValue("-");
                                    myRefWrite.child("price").setValue(String.valueOf(amount));

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("Nie",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
    }
}
