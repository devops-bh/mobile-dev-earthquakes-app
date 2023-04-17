package com.example.bryce_harper_s2221473;

import android.content.Context;
import android.graphics.Color;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

public class EarthquakeInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    // Name                 Bryce Harper
// Student ID           S2221473
    private final View mWindow;
    private Context mContext;

    public EarthquakeInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.earthquake_window_info, null);
    }

    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        String snippet = marker.getSnippet();
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        TextView magnitudeTextView = (TextView) view.findViewById(R.id.magnitude);
        // todo: magnitude + pubDate etc
        if (!title.equals("")) {
            titleTextView.setText(title);
        }
        if (!snippet.equals("")) {
            // storing the additional data maybe isn't the smartest way of transferring the data but oh well
            //Double magnitude = Double.valueOf(snippet.split(";")[1].replaceAll(" ", ""));
           // System.out.println("Mag: " + snippet);
            //magnitudeTextView.setText(magnitude.toString());
        }
        String markerEarthquakeTag = (String) marker.getTag();
        Double magnitude = Double.valueOf(markerEarthquakeTag.split(";")[0]);
        System.out.println(markerEarthquakeTag);
        System.out.println(magnitude.toString());
        if (Math.floor(magnitude) >= 6) {
            magnitudeTextView.setTextColor(Color.WHITE);
            magnitudeTextView.setBackgroundColor(Color.RED);
            magnitudeTextView.setText(magnitude.toString());
        } else {
            magnitudeTextView.setTextColor(Color.BLACK);
            // orange
            magnitudeTextView.setBackgroundColor(Color.rgb(255, 165, 0));
        }
        magnitudeTextView.setText(magnitude.toString());
    }

    @Override
    public View getInfoContents(@NonNull Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
