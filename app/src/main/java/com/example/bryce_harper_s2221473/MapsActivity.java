package com.example.bryce_harper_s2221473;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.bryce_harper_s2221473.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
// Name                 Bryce Harper
// Student ID           S2221473

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(12.0f);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Float lat = extras.getFloat("lat");
            Float _long = extras.getFloat("long");
            Double magnitude = extras.getDouble("magnitude");
            String monitoringStation = extras.getString("monitoringStation");
            LatLng coordinate = new LatLng(lat, _long);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
            MarkerOptions markerOptions = new MarkerOptions().position(coordinate).title("Marker in " + monitoringStation)
                    .snippet(monitoringStation + ";" + magnitude + ";"+lat + ";"+_long);
            if (Math.floor(magnitude) >= 6) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            }
            Earthquake markerEarthquake = new Earthquake();
            markerEarthquake.lat = lat;
            markerEarthquake.lng = _long;
            markerEarthquake.magnitude = magnitude;
            mMap.addMarker(markerOptions).setTag(magnitude+";"+lat+";"+_long+";"+monitoringStation+";");
            CameraUpdate location = CameraUpdateFactory.newLatLng(coordinate);
            mMap.animateCamera(location, 5000, null);
            mMap.setInfoWindowAdapter(new EarthquakeInfoWindowAdapter(MapsActivity.this));
        }
    }
}