package com.example.bryce_harper_s2221473;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EarthquakesRecyclerViewAdapter extends RecyclerView.Adapter<EarthquakesRecyclerViewAdapter.EarthquakesRecyclerViewHolder> {
    // Name                 Bryce Harper
// Student ID           S2221473
    Context context;
    ArrayList<Earthquake> earthquakes;
    int position;
    public EarthquakesRecyclerViewAdapter(Context _context, ArrayList<Earthquake> _earthquakes) {
        context = _context;
        earthquakes = _earthquakes;
    }

    @Override
    public EarthquakesRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.earthquake_row, parent, false);
        return new EarthquakesRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EarthquakesRecyclerViewHolder holder, int position) {
        this.position = position;
        holder.titleTextView.setText(earthquakes.get(position).title);
        Double magnitude = earthquakes.get(position).magnitude;
        holder.magnitudeTextView.setText(String.valueOf(magnitude));
        if (Math.floor(magnitude) >= 6) {
            // maybe its a better user-experience to have the entire item's background highlighted in red, but... I'm lazy
            holder.magnitudeTextView.setTextColor(Color.WHITE);
            holder.magnitudeTextView.setBackgroundColor(Color.RED);
        }
        holder.earthquakeIndex = position;
    }
    @Override
    public int getItemCount() {
        return earthquakes.size();
    }
    public class EarthquakesRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView magnitudeTextView;
        int earthquakeIndex;
        public EarthquakesRecyclerViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            magnitudeTextView = itemView.findViewById(R.id.magnitude);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View _view = (View) view;
                    Intent i = new Intent(context, MapsActivity.class);
                    i.putExtra("lat", earthquakes.get(earthquakeIndex).lat);
                    i.putExtra("long", earthquakes.get(earthquakeIndex).lng);
                    i.putExtra("monitoringStation", earthquakes.get(earthquakeIndex).location);
                    i.putExtra("magnitude", earthquakes.get(earthquakeIndex).magnitude);
                    context.startActivity(i);
                }
            });
        }
    }
}
