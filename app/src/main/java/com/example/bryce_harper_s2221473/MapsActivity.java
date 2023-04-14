package com.example.bryce_harper_s2221473;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.Handler;

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

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
            //The key argument here must match that used in the other activity
            System.out.println("FROM MAIN: " + lat + " Long: " + _long);
//        LatLng sydney2 = new LatLng(lat, _long);
            LatLng coordinate = new LatLng(lat, _long); //Store these lat lng values somewhere. These should be constant.
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
            // note: this appears to be instant, I originally assumed the Google Maps SDK had an equivalent of Mapbox's FlyTo method
            mMap.animateCamera(location, 5000, null);
            mMap.setInfoWindowAdapter(new EarthquakeInfoWindowAdapter(MapsActivity.this));
        }
        /*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-32, 151);
//        LatLng sydney2 = new LatLng(-36, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng coordinate = new LatLng(-2, 151); //Store these lat lng values somewhere. These should be constant.
        mMap.addMarker(new MarkerOptions().position(coordinate).title("Marker in Sydney"));

        Handler handler1 = new Handler();
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("FIRST");
                mMap.addMarker(new MarkerOptions().position(coordinate).title("Marker in Sydney"));
            }
        };

        Handler handler = new Handler();
        Runnable r=new Runnable() {
            public void run() {
                //what ever you do here will be done after 3 seconds delay.
            System.out.println("here we go");
            CameraUpdate location = CameraUpdateFactory.newLatLng(coordinate);
            mMap.animateCamera(location, 5000, null);
            }
        };
        handler.postDelayed(r, 12000);
        handler.postDelayed(r, 10000);
         */

    }
}