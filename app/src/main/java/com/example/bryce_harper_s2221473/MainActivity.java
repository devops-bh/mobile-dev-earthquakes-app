/*  Starter project for Mobile Platform Development in Semester B Session 2018/2019
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 _________________
// Student ID           _________________
// Programme of Study   _________________
//

// Update the package name to include your Student Identifier
package com.example.bryce_harper_s2221473;
// package gcu.mpd.bgsdatastarter;

// import android.support.v7.app.AppCompatActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// import gcu.mpd.bgsdatastarter.R;

class Earthquake { // this may actually become an interface
    // title & description contain more data & thus I'll need to parse them using the ";" as the separator
    public String title;
    public String description;
    public String link ; // maybe make this a URL datatype
    public String pubDate; // there a date datatype?
    public String category;
    public float lat;
    public float lng;
    public double magnitude; // maybe have a "getAsDouble" & a "getAsString" method?
    public String location;
    public Earthquake() {

    }
    public void moreParsing() { // [refator] ...
        if (this.description == null || this.description == "") {
            return;
        }

        // assumes details are in a rigid predictable order
        String[] details = description.split(";");
        for (int i = 0; i < details.length; i++) {
        }
        // crashes the app
        // this.magnittude = Double.parseDouble(details[details.length - 1].split(":")[1].replace(" ", ""));
        String[] magAsStr = details[details.length - 1].split(": "); //[1].replace(" ", "");
        for (int i = 0; i < magAsStr.length; i++) {

        }
        // not confirmed to work, I'm curioous if the "null" in the XML string is somehow affecting this?
        try { // the above parsing logic appears to be wrong so this is just a quick fix
            double magAsDbl = Double.valueOf(magAsStr[magAsStr.length-1]);
            this.magnitude = magAsDbl;
        } catch(NumberFormatException e) { // use a more specialised catchable error type
            System.out.println("ERROR CAUGHT: ");
            System.out.println(e);
        }
        // do something similar to get the depth etc
        System.out.println("Mag " + this.magnitude);
    }

}


class MonitoringStationsManager { // do I need to inherit from iterable to enable iteration?
    private Map<String, ArrayList<Earthquake>> monitoringStations;
    public MonitoringStationsManager() { // [refactor] should probably use .ToLowerCase() or ignoreCase to prevent case sensitivity bugs
        this.monitoringStations = new ConcurrentHashMap<String, ArrayList<Earthquake>>();
    }


    public void add(String monitoringStation, Earthquake earthquake) {
        ArrayList<Earthquake> current;
        if (this.monitoringStations.get(monitoringStation) == null) {
            current = new ArrayList<Earthquake>();
            current.add(earthquake);
        } else {
            current = this.monitoringStations.get(monitoringStation);
            current.add(earthquake);
        }
        monitoringStations.put(monitoringStation, current);
        //this.monitoringStations.putIfAbsent(monitoringStation, current);
        System.out.println();
    }

    public ArrayList getAllEarthquakesFromMonitoringStation(String monitoringStation) {
        if (this.monitoringStations.containsKey(monitoringStation)) {
            System.out.println("LEN: "+this.monitoringStations.get(monitoringStation));
            return this.monitoringStations.get(monitoringStation);
        } else {
            return new ArrayList();
        }
    }

    public ArrayList<Earthquake> getAllEarthquakesByIndex(int index) {
        return this.monitoringStations.get(index);
    }
    public Map<String, ArrayList<Earthquake>> getMonitoringStations() {
        return this.monitoringStations;
    }
}

// todo: convert the list portion of the app to a fragment
public class MainActivity extends AppCompatActivity /* extends ListActivity */ implements OnClickListener
{
    private TextView rawDataDisplay;
    private Button startButton;
    //private String result; // defaults to null
    private String result = "";
    private String url1="";
    //private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private String urlSource = "http://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
    MonitoringStationsManager monitoringStationsManager;
    ArrayAdapter<String> adapter;
    RecyclerView recyclerView;
    ArrayList<String> listItems = new ArrayList<String>();
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        monitoringStationsManager = new MonitoringStationsManager();
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        //setListAdapter(adapter);
    }

    public void onClick(View aview)
    {
        startProgress();
    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    } //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable {
        private String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                in.readLine().replaceAll("<?xml version=\"1.0\"?>", "");//.trim(); // not sure if I need trim
                // the following lines may be cause the app to crash
                in.readLine().replaceAll("<rss version=\"2.0\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">", "");//.trim();
                in.readLine().replaceAll("</rss>", ""); //.trim();

                boolean canParse = false;
                while ((inputLine = in.readLine()) != null) {
                    if (/*!inputLine.contains("geo") &&*/ !inputLine.contains("<rss version") && !inputLine.contains("</rss>") && !inputLine.contains("<?xml version") && !inputLine.contains("</xml>")) {
                            if (inputLine.contains("<item>")) {
                                canParse = true;
                            }
                            if (canParse) {
                                if (inputLine.contains("geo:")) {
                                    result += inputLine.replaceAll("geo:","");
                                }else{
                                    result += inputLine;
                                }
                            }
                            if (inputLine.contains("</item>")) {
                                canParse = false;
                            }
                    }
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    parseData(result);
                    /*
                    ArrayList<Earthquake> earthquakesKI = monitoringStationsManager.getAllEarthquakesFromMonitoringStation("KERMADECISLANDS"); // maybe this method just don't work :| (though the method maybe inconsistent - though I'm not convinced thats because the data is volatile but rather the code itself needs improvement )
                    ArrayList<Earthquake> earthquakesST = monitoringStationsManager.getAllEarthquakesFromMonitoringStation("SOUTHERNTURKEY"); // maybe this method just don't work :| (though the method maybe inconsistent - though I'm not convinced thats because the data is volatile but rather the code itself needs improvement )
                    ArrayList<Earthquake> earthquakes = new ArrayList();
                    earthquakes.addAll(earthquakesST);
                    earthquakes.addAll(earthquakesKI);
                    */
                    /*
                    ArrayList<Earthquake> earthquakes = new ArrayList<Earthquake>();
                    for ( String key : monitoringStationsManager.getMonitoringStations().keySet() ) {
                        System.out.println( key );
                        ArrayList<Earthquake> quakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation(key);
                        earthquakes.addAll(quakes);
                    }
                    for (int i = 0; i < earthquakes.size(); i++) {
                        String title = earthquakes.get(i).title;
                        listItems.add(earthquakes.get(i).title + " - Location: " + earthquakes.get(i).location);
                    }
                    adapter.notifyDataSetChanged();
                    ListView lv = findViewById(android.R.id.list);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            System.out.println("LOC: " + earthquakes.get(position).location);
                            Intent i = new Intent(MainActivity.this, MapsActivity.class);
                            i.putExtra("lat", earthquakes.get(position).lat);
                            i.putExtra("long", earthquakes.get(position).lng);
                            i.putExtra("monitoringStation", earthquakes.get(position).location);
                            i.putExtra("magnitude", earthquakes.get(position).magnitude);
                            startActivity(i);

                        }
                    });
                     */

                    // maybe replace linear layout manager with StaggeredGridLayoutManager
                    ArrayList<Earthquake> earthquakes = new ArrayList<Earthquake>();
                    for ( String key : monitoringStationsManager.getMonitoringStations().keySet() ) {
                        System.out.println( key );
                        ArrayList<Earthquake> quakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation(key);
                        earthquakes.addAll(quakes);
                    }
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    EarthquakesRecyclerViewAdapter earthquakesRecyclerViewAdapter = new EarthquakesRecyclerViewAdapter(MainActivity.this, earthquakes);
                    recyclerView.setAdapter(earthquakesRecyclerViewAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                }
            });
        }

        private void parseData(String dataToParse) {
            Earthquake widget = null;
            LinkedList<Earthquake> alist = new LinkedList<>();
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(dataToParse));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    // Found a start tag
                    if (eventType == XmlPullParser.START_TAG) {
                        /*
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            widget = new Earthquake();
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            // Do something with text
                            Log.e("MyTag", "Bolt is " + temp + xpp.getName());
                            widget.title = temp;
                        }
                         */
                        switch (xpp.getName().toLowerCase()) {
                            case "item":
                                widget = new Earthquake();
                                break;
                            case "title":
                                widget.title = xpp.nextText();
                                break;
                            case "description":
                                String[] details = xpp.nextText().split(";");
                                String location = details[1].replace(" Location: ", "");
                                widget.location = location.replaceAll(" ", ""); // should really replace whitespace first
                                String[] magAsStr = details[details.length - 1].split(": ");
                                //try {
                                double magAsDbl = Double.valueOf(magAsStr[magAsStr.length - 1]);
                                double magnittude = magAsDbl;
                                widget.magnitude = magnittude;
                                break;
                            case "lat":
                                widget.lat = Float.valueOf(xpp.nextText());
                                break;
                            case "long":
                                widget.lng = Float.valueOf(xpp.nextText());
                                break;
                            case "magnitude":
                                widget.magnitude = Double.valueOf(xpp.nextText());
                                break;
                            // etc
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            //alist.add(widget);
                            monitoringStationsManager.add(widget.location, widget);
                            System.out.println("Adding earthquake: " + widget.location);
                        }
                    }


                    // Get the next event
                    eventType = xpp.next();

                } // End of while

                //return alist;
            } catch (XmlPullParserException ae1) {
                Log.e("MyTag", "Parsing error" + ae1.toString());
            } catch (IOException ae1) {
                Log.e("MyTag", "IO error during parsing");
            }

            Log.d("MyTag", "End document");

            //return alist;
        }
    }
}
