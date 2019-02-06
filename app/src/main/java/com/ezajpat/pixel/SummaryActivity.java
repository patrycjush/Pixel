package com.ezajpat.pixel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SummaryActivity extends AppCompatActivity {

    TextView tvDeliveryPrice, tvAmount;
    RadioGroup rGroup;
    int amount = 0;

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

        tvDeliveryPrice = (TextView) findViewById(R.id.tv_delivery_price);
        tvAmount = (TextView) findViewById(R.id.tv_amount);
        rGroup = (RadioGroup)findViewById(R.id.radio_group_delivery);

        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //checkedId refers to the selected RadioButton
                //Perform an action based on the option chosen
                if (checkedId == R.id.radio_item_delivery) {
                    amount = Integer.parseInt(tvAmount.getText().toString().split(",")[0]) + 11;
                    tvAmount.setText(amount + ",00 zł");
                } else {
                    if(amount > 0) {
                        amount = Integer.parseInt(tvAmount.getText().toString().split(",")[0]) - 11;
                        tvAmount.setText(amount + ",00 zł");
                    }
                }
            }
        });
    }
}
