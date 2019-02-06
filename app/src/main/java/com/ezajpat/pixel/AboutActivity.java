package com.ezajpat.pixel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView tvAboutText, tvAboutUsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        toolbar.setTitle("O nas");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvAboutText = (TextView) findViewById(R.id.tvAboutText);
        tvAboutUsText = (TextView) findViewById(R.id.tvAboutUs);

        String textAbout = "Burgery to nasza specjalność. Jeśli szukasz 100% wołowiny, świeżych i chrupiących warzyw, autorskich sosów\ni niecodziennych dodatków – dobrze trafiłeś!\n\n";

        String textAboutUs = "Pozdrawiamy, zespół Pixel";

        tvAboutText.setMovementMethod(new ScrollingMovementMethod());
        tvAboutText.setText(textAbout);
        tvAboutUsText.setText(textAboutUs);
    }
}
