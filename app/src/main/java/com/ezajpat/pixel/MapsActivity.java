package com.ezajpat.pixel;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        toolbar.setTitle("Nasze restauracje");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng manufaktura = new LatLng(51.779768, 19.448147);
        mMap.addMarker(new MarkerOptions().position(manufaktura).title("PIXEL ŁÓDŹ MANUFAKTURA"));

        LatLng forum = new LatLng(54.349330, 18.643495);
        mMap.addMarker(new MarkerOptions().position(forum).title("PIXEL FORUM GDAŃSK"));

        LatLng tarasy = new LatLng(52.230082, 21.002869);
        mMap.addMarker(new MarkerOptions().position(tarasy).title("PIXEL ZŁOTE TARASY"));

        LatLng posnania = new LatLng(52.396409, 16.955593);
        mMap.addMarker(new MarkerOptions().position(posnania).title("PIXEL POSNANIA"));

        LatLng dworzec = new LatLng(50.257214, 19.023712);
        mMap.addMarker(new MarkerOptions().position(dworzec).title("PIXEL STARY DWORZEC"));



        mMap.moveCamera(CameraUpdateFactory.newLatLng(manufaktura));
        mMap.setMinZoomPreference(5);
        mMap.setMaxZoomPreference(20);
    }
}
