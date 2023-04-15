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
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// import gcu.mpd.bgsdatastarter.R;

/// Earthquake Model
class Earthquake implements Parcelable { // this may actually become an interface
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
    public Earthquake(Parcel in) {
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.title = data[0];
        this.description = data[1];
        this.link = data[2];
        this.pubDate = data[3];
        this.category = data[4];
        this.lat = Float.valueOf(data[5]);
        this.lng = Float.valueOf(data[6]);
        this.magnitude = Double.valueOf(data[7]);
        this.location = data[8];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.title ,
                this.description ,
                this.link ,
                this.pubDate ,
                this.category ,
                Float.toString(this.lat) ,
                Float.toString(this.lng) ,
                Double.toString(this.magnitude),
                this.location
        });
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Earthquake createFromParcel(Parcel in) {
            return new Earthquake(in);
        }

        public Earthquake[] newArray(int size) {
            return new Earthquake[size];
        }
    };
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

// I guess this is could be a singleton; I guess technically the MonitoringStationManager is a repository too?
class EarthquakeRepository {
    private HandlerThread handlerThread;
    MonitoringStationsManager monitoringStationsManager; // this is really just a wrapper around ConcurrentHashMap because I didn't understand hash maps well enough
    public EarthquakeRepository() {
        monitoringStationsManager = new MonitoringStationsManager();
    }
    /*
        this will initiate the asynchronous request to the earthquake data source
        I'm not a fan of this code, but its ok for now; I'd definitely try to refactor this
        in a long lived app
        I decided to use the init method rather than doing this in the constructor
        to be explicit that there's hidden complexity (which the dev needs to be somewhat concerned about)
    */
    public void init() {
        this._setMonitoringStations();
    }
    // there's a comment further down which really should be here
    public MonitoringStationsManager getEarthquakes() throws Exception {
        if (this.monitoringStationsManager != null) {
            return this.monitoringStationsManager;
        }
        // I really hate that I'm forcing the use of try-catch
        // https://stackoverflow.com/a/2737554
        throw new Exception("EarthquakeRepositories.monitoringStationsManager is null");
    }
    //public ArrayList<Earthquake> getEarthquakes() {
    public MonitoringStationsManager requestEarthquakes() {
        return null;
    }

    // this could probably be renamed to something better
    private void _setMonitoringStations() {
        /*
        there might be some confusion about the use of monitoringStations here (the naming) here...
        Essentially I wrote the app with the use of the MonitoringStations (which we now know is a
        wrapper around concurrent map)
        e.g. the developer asked for earthquakes but got monitoring stations back, wtf?
        But rather than type MonitoringStations, I'd rather be more (or less?) vague about how
        the quakes are structured
        if this was a long lived app then I'd likely refactor this to make sense to others
         */
        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        Handler asyncHandler = new Handler(handlerThread.getLooper()) {
            // we don't need to send the message
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Object response = msg.obj;

            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run()
            {
                // your async code goes here.
                Log.e("MyTg","In asynctaskrun");

                // create message and pass any object here doesn't matter
                // for a simple example I have used a simple string
                Message message = new Message();
                String urlSource = "http://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
                String earthquakesXMLString = getEarthquakesAsStringAsync(urlSource);
                parseEarthquakesString(earthquakesXMLString);
                message.obj = monitoringStationsManager; // monitoringStationsManager.monitoringStations;
               // asyncHandler.sendMessage(message);
            }
        };
        asyncHandler.post(runnable);
    }

    private String getEarthquakesAsStringAsync(String url) {
        URL aurl;
        URLConnection yc;
        BufferedReader in = null;
        String inputLine = "";
        String result = "";
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
        return result;
    }
    private void parseEarthquakesString(String earthquakesString) {
        Earthquake widget = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(earthquakesString));
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
                        case "pubdate":
                            widget.pubDate = xpp.nextText();
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
    }
}

// todo: convert the list portion of the app to a fragment
public class MainActivity extends AppCompatActivity /* extends ListActivity */ implements OnClickListener, DatePickerDialog.OnDateSetListener
{
    private TextView rawDataDisplay;
    private Button startButton;
    //private String result; // defaults to null
    private String result = "";
    private String url1="";
    //private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private String urlSource = "http://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
    EarthquakeRepository earthquakeRepository;
    MonitoringStationsManager monitoringStationsManager;
    ArrayAdapter<String> adapter;
    RecyclerView recyclerView;
    ArrayList<String> listItems = new ArrayList<String>();
    EarthquakesRecyclerViewAdapter earthquakesRecyclerViewAdapter;
    ArrayList<Earthquake> earthquakes;
    protected void onCreate(Bundle savedInstanceState)
    {
        earthquakeRepository = new EarthquakeRepository();
        earthquakeRepository.init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        monitoringStationsManager = new MonitoringStationsManager();
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        //setListAdapter(adapter);

        earthquakes = new ArrayList<Earthquake>();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (savedInstanceState != null) {
            //earthquakes = savedInstanceState.getParcelableArrayList("earthquakes");
            System.out.println("NUM OF QUAKES: " + savedInstanceState.getString("earthquakes_count"));
            int earthquakes_count = Integer.valueOf(savedInstanceState.getString("earthquakes_count"));
            for (int i = 0; i < earthquakes_count; i++) {
                //earthquakes.add(savedInstanceState.getParcelable("earthquake"+Integer.toString(i)));
                Earthquake earthquake = savedInstanceState.getParcelable("earthquake"+Integer.toString(i));
                System.out.println("EARTHQUAKE IN: "+ savedInstanceState.getParcelable("earthquake"+Integer.toString(i)));
                earthquakes.add(earthquake);
                // I mean, technically I could just store the state earthquakes as a giant XML string similar to what we originally done?
                // maybe rebuild the monitoringStations too? which honestly should just be passed to the adapter ?
            }
            Log.d("Orientation", "loaded earthquakes from state");
        }
        earthquakesRecyclerViewAdapter = new EarthquakesRecyclerViewAdapter(MainActivity.this, earthquakes);
        recyclerView.setAdapter(earthquakesRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        // should look better when the UI is finished; otherwise resort to the linear vertical layout
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.magnitude_sort_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (earthquakesRecyclerViewAdapter != null) {
                        Collections.sort(earthquakes, new Comparator<Earthquake>() {
                            @Override
                            public int compare(Earthquake quakeA, Earthquake quakeB) {
                                if (position >= 1) {
                                    return Double.valueOf(quakeA.magnitude).compareTo(quakeB.magnitude);
                                } else {
                                    return Double.valueOf(quakeB.magnitude).compareTo(quakeA.magnitude);
                                }
                            }
                        });
                    earthquakesRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // doesn't work as expected but oh well
                Collections.sort(earthquakes, new Comparator<Earthquake>() {
                    @Override
                    public int compare(Earthquake quakeA, Earthquake quakeB) {
                        return Double.valueOf(quakeA.magnitude).compareTo(quakeB.magnitude);
                    }
                });
                if (earthquakesRecyclerViewAdapter != null) {
                    earthquakesRecyclerViewAdapter.notifyDataSetChanged();
                }
        }});

       Button openDatePickerButton = (Button) findViewById(R.id.date_picker_button);
       openDatePickerButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               DialogFragment datePicker = new DatePickerFragment();
               datePicker.show(getSupportFragmentManager(), "date picker");
           }
       });

       /*

        EditText searchView = findViewById(R.id.searchView);
        searchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*
                genuinely not sure how performant this is likely to be
                but I'm also hoping that its able to find the recycler view items
                which aren't technically in memroy
                 */
                /*
                note: the emulator will not display characters typed using the real keyboard
                [And might even crash the app :|] (possibly because I was stupidly typing when there was no earthquake data)
                but will display the characters typed using the emulated keyboard
                (hopefully this isn't a major issue)
                 */
                /*
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //do something here
                    System.out.println("typing");
                    System.out.println("Search View: "+ searchView.getText().toString());
                    System.out.println("Search View 2: "+ searchView.getText());
                    for (int i = 0; i < earthquakes.size(); i++) {
                        //char unicodeChar = (char)event.getUnicodeChar();
                        System.out.println("Search View: "+ searchView.getText());
                        if (earthquakes.get(i).location.toLowerCase().contains(searchView.getText())) {
                            earthquakes.remove(i);
                            earthquakesRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                    return true;
                }
                return false;

            }
                }
            });
    }
                 */
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putStringArrayList();
        // outState.putBundle("earthquakes", earthquakes);
        // outState.putParcelableArrayList("earthquakes", (ArrayList<? extends Parcelable>) earthquakes);
        for (int i = 0; i < earthquakes.size(); i++) {
            outState.putParcelable("earthquake" + i, earthquakes.get(i));
        }
        outState.putString("earthquakes_count", Integer.toString(earthquakes.size()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        // todo: update recycler adapter with earthquake with corrosponding date
        // remember to convert the dates to UNIX timestamps
        // refactor this using a lambda for a more functional-esque approach
        String selectedDate = DateFormat.getDateInstance().format(calendar.getTime());

        for (int i = 0; i < earthquakes.size(); i++) {
            /* if I wanted to get a date range, I believe i could convert the
            currentDate & the pubDate to a unix timestamp which makes
            comparing them easier
            */
            /*
            System.out.println("Dates: " + earthquakes.get(i).pubDate + " == " + selectedDate);

            try {
                DateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                // you can change format of date
                Date earthquakeDate = formatter.parse(earthquakes.get(i).pubDate);
                Date userSelectedDate = formatter.parse(selectedDate);
                Timestamp timeStampDateA = new Timestamp(earthquakeDate.getTime());
                Timestamp timeStampDateB = new Timestamp(userSelectedDate.getTime());
                System.out.println("TIMESTAMP: " + timeStampDateA + " vs " + timeStampDateB);
            } catch (ParseException e) {
                System.out.println("Exception :" + e);
            }
             */
            String quakeDate = earthquakes.get(i).pubDate.substring(4, 16);
            if (quakeDate != selectedDate) {
                earthquakes.remove(i);
                earthquakesRecyclerViewAdapter.notifyDataSetChanged();
            }
            System.out.println("QD: " + quakeDate);
        }
    }

    public void onClick(View aview)
    {
        // startProgress();
        try {
            // this was inspired by futures, or more specifically const data = await getData(); in JS, but... um...
            this.monitoringStationsManager = earthquakeRepository.getEarthquakes();
            // alt
            for ( String key : monitoringStationsManager.getMonitoringStations().keySet() ) {
                System.out.println( key );
                ArrayList<Earthquake> quakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation(key);
                earthquakes.addAll(quakes);
            }
            earthquakesRecyclerViewAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("EarthquakeRepository", e.getLocalizedMessage());
            // display toast saying "data unavailible"
            throw new RuntimeException(e);
        }
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
                    earthquakes = new ArrayList<Earthquake>();
                    for ( String key : monitoringStationsManager.getMonitoringStations().keySet() ) {
                        System.out.println( key );
                        ArrayList<Earthquake> quakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation(key);
                        earthquakes.addAll(quakes);
                    }
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    earthquakesRecyclerViewAdapter = new EarthquakesRecyclerViewAdapter(MainActivity.this, earthquakes);
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
                            case "pubdate":
                                widget.pubDate = xpp.nextText();
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
