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
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
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

interface Observer {
    public void update();
    /* I was really hoping to have this method be optional , but I don't think thats possible
    for now it doesn't matter, though I'd look into refactoring this for a long lived app */
    public void update(Object state);
    // this really should had been an abstract class?
}

class Observable {
    ArrayList<Observer> observers;
    public Observable() {
        observers = new ArrayList<Observer>();
    }
    public void register(Observer observer) {
        observers.add(observer);
    }
    public void unregister(Observer observer) {
        observers.remove(observer);
    }
    /* Originally this wasn't supposed to have a parameter */
    public void notifyAllObservers(Object stateIfAny) { // I don't think the earthquake repository as actually running this as I expected?
        System.out.println(this);
        for (Observer observer : observers) {
            /*
            if (stateIfAny == null) {  // quick hack; see https://www.youtube.com/watch?v=vKVzRbsMnTQ
                observer.update();
            } else {
            }*/
            observer.update(stateIfAny);
        }
    }
    /* alternatively could use optional parameters, but I'm not sure how I feel about passing the data this way
    see: https://youtu.be/_BpmfnqjgzQ?t=2610
    And technically I could introduce a strategy which decides whether data needs to be "pushed" (injected/passed) as an argument
     */
    /*
    public void notifyAllObservers(Object state ) { // I don't think the earthquake repository as actually running this as I expected?
        System.out.println(this);
        for (Observer observer : observers) {
            observer.update(state);
        }
    }*/
}

// AllEarthquakesViewModel probably wasn't the correct name for this
class AllEarthquakesViewModel extends Observable implements Observer {
    EarthquakeRepository earthquakeRepository;
    MonitoringStationsManager monitoringStationsManager;
    ArrayList<Earthquake> UI_earthquakes; // this is what the activity views are observing (there's probably a mutability debate to be had here somewhere but right now I don't care)
    /* could implement filtering strategies */
    Bundle savedInstanceState;
    public AllEarthquakesViewModel() {
        // maybe the EarthquakeReposiory or Earthquakes should be injected?
        // NO! Because EarthquakeReposiory is also an observable :|
        earthquakeRepository = new EarthquakeRepository();
        earthquakeRepository.init();
        earthquakeRepository.register(this);
    }
    /*
    What happens when there are no earthquakes on the selected date?
    I think the method deserves a unit test
    */
    public void getEarthquakesOnDate(String selectedDate) {
        ArrayList<Earthquake> earthquakes = this.UI_earthquakes;
        for (int i = 0; i < earthquakes.size(); i++) {
            String quakeDate = earthquakes.get(i).pubDate.substring(4, 16);
            if (quakeDate != selectedDate) {
                earthquakes.remove(i);
            }
            System.out.println("QD: " + quakeDate);
        }
        this.UI_earthquakes = earthquakes; // why not just rename earthquakes to UI_earthquakes i.e. remove directly from UI_earthquakes? I don't know, but I suppose if error handling were to be used this imitates a "transaction" (Database term)
        //this.notifyAllObservers(); /* I guess here, observer differs from pub sub, as they subscribe to topics, meaning some subs don't need to worry about their state being changed by others, with this current implementation, I'm slightly anticipating weird UI bugs when I implement the other Weakest/Strongest quake activity, we'll see*/
        /* I suppose I could instead maybe do something such as observer.earthquakes = this.UI_earthquakes
        but that wouldn't work for observers which don't have the earthquake property
        So I guess I'd abstract it to something like observer.stateData = ?
        */
        this.notifyAllObservers(earthquakes);
    }
    public void getStrongestEarthquakes() {
        // todo: loop through each monitoring station's earthquakes & append the ones with the strong value to an array list
    }
    public void getWeakestEarthquakes() {
        // todo: loop through each monitoring station's earthquakes & append the ones with the strong value to an array list
    }

    /*
    Originally I tried to avoid (or just didn't think about) a "pull [not poll]" based system
    But it may had been the simplest route afterall , so now I guess I'm using a hybrid approach
    of push [push] & pull in that the Observable tells the observer there is data
    but for the case of the onSavedInstanceState functionality, the observer [the Main Activity]
    will instead just "pull" i.e. ask for the earthquakes (& assume it gets what it expects)
    Also I sort of want to call this something like getEarthquakesForUI
    */
    public ArrayList<Earthquake> getEarthquakes() {
        return this.UI_earthquakes;
    }

    public void ascending() {

    }

    public void descending() {

    }

    @Override
    public void update() {
        /*
         this is a hint of a poor design (tbh I did briefly try a UML diagram
         Essentially, I didn't anticipate that I'd be passing state around like the other update method
        */
        Log.d("AllEarthquakesViewModel", "This should never be ran");
    }

    @Override
    public void update(Object state) {
        try {
            this.monitoringStationsManager = earthquakeRepository.getEarthquakes();
            this.UI_earthquakes = new ArrayList<Earthquake>(); /* TODO: explain why I'm doing this*/
            for ( String key : monitoringStationsManager.getMonitoringStations().keySet() ) {
                System.out.println( key );
                ArrayList<Earthquake> quakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation(key);
                // why'd we not want to use the commented out line? Because then we might end up with duplicate data
                this.UI_earthquakes.addAll(quakes);
            }
            this.notifyAllObservers(UI_earthquakes); // not sure how I feel about injecting the [state] data for the UI
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setSavedInstanceState(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
    }
    public Bundle getSavedInstanceState() {
        return this.savedInstanceState;
    }
}
// this is somewhat of a builder class
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

    // the parameters for this could probably be better, this could also be more complex & thus flexible
    // but there's not yet a need to remove earthquakes, asides from filtering on the UI layer
    public void removeQuake(String location, int index) {
        // todo
    }
    public void removeMonitoringStation(String location) {
        // todo
    }

    // may rename to getMonitoringStationQuakes
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
    public Set getMonitoringStationNames() {
        return this.monitoringStations.keySet();
    }
    public Map<String, ArrayList<Earthquake>> getMonitoringStations() {
        return this.monitoringStations;
    }
}

// I guess this is could be a singleton; I guess technically the MonitoringStationManager is a repository too?
class EarthquakeRepository extends Observable {
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


    private void notifyObserversOnMainThread(Object response)
    {
        Handler uiThread = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> {
            /*
            // there was an error when trying to cast this to response, but I didn't bother looking into it
            because at the moment I don't actually need the message, I'm essentially just
            assuming there's data
            String message = (String) response;
            Log.d("EARTHQUAKE_REPOSITORY",message);
             */
            /*
            It really doesn't make sense for this to have an argument
            */
            this.notifyAllObservers(null);
        };
        uiThread.post(runnable);
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
        /*
        is this a memory leak since we aren't calling handlerThread.quit() ? e.g. there's a period of time the thread
         is alive after we've finished using it but the garbage collector hasn't cleaned up the memory?
         */
        handlerThread.start();
        Handler asyncHandler = new Handler(handlerThread.getLooper()) {
            // we don't need to send the message
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Object response = msg.obj;
                notifyObserversOnMainThread(response);
            }
        };
        Runnable runnable = () -> {
            // your async code goes here.
            Log.e("MyTg","In asynctaskrun");

            // create message and pass any object here doesn't matter
            // for a simple example I have used a simple string
            Message message = new Message();
            String urlSource = "http://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
            String earthquakesXMLString = getEarthquakesAsStringAsync(urlSource);
            parseEarthquakesString(earthquakesXMLString);
            message.obj = monitoringStationsManager; // monitoringStationsManager.monitoringStations;
            /*
            [refactor]
            I could create an enum which maps topics to integers e.g. 1 = "earthquake_data_recieved" (inspired by pub sub)
            */
            asyncHandler.sendMessage(message);
            /*
                Because this is triggering observer code which triggers UI code, Android complains
                that the UI code is being run in this thread
            */
            //this.notifyAllObservers(); // so, similar to JS's arrow functions, the lecturer's method where they created a new Runnable object had a different scope/context than the outer class, hence
            //         this.notifyAllObservers(); failed, whereas I assume lambas are bounded [don't know if thats the right word] to the correct [so the outer class's] scope
            // asyncHandler.sendMessage(message);
            // [refactor] should probably use message passing & have a somewhat pub sub topic mechanism in a more focused method ?
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
                        // monitoringStationsManager.notify();
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
public class MainActivity extends AppCompatActivity /* extends ListActivity */ implements OnClickListener, DatePickerDialog.OnDateSetListener, Observer
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
    // no longer being used
    ArrayList<Earthquake> earthquakes;
    AllEarthquakesViewModel earthquakesViewModel;
    protected void onCreate(Bundle savedInstanceState)
    {
        /*
        earthquakesViewModel = new AllEarthquakesViewModel();
        earthquakesViewModel.register(this);
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        earthquakesViewModel = new AllEarthquakesViewModel();
        // maybe explain why savedInstanceState isn't being treated as an observable
        earthquakesViewModel.setSavedInstanceState(savedInstanceState);
        earthquakesViewModel.register(this);

        //monitoringStationsManager = new MonitoringStationsManager();
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        //setListAdapter(adapter);
        /*
        [Implementing the interaction between the View-Model & the View regarding Model View View-Model]

        // This code is commented out, because
        // technically I could instantiate the ViewModel
        // assign the view model, but then I couldn't register the view model
        // until this code had run, otherwise the observable triggers this main activity class's update method
        // which results in earthquakesRecyclerViewAdapter being null which makes sense
        // a simple workaround is to call the earthquakesViewModel.register(this);
        // on a line after this code (& similar UI setup code has ran )
        // but, I think when scrolling, its easy to not notice the earthquakesViewModel.register(this); line
        // which I suspect will lead to confusion, therefor, (partially inspired by Jetpack Compose's recomposition)
        // I am simply going to recreate the UI elements on each [observer] update which may not be the most
        // performant solution (because Jetpack Compose has tricks where it only renders if there's a change etc)
        // but hopefully its the most intuitive option , so essentially this class's update method becomes the onCreate method
        // actually nevermind this wall of text, as I just realised that doing this will likely interfere (negatively)
        // with the saving of tempoary state regarding the lifecycle behavior/s
        // so ultimately, the most intuitive solution is just to initiate & register at the top
        // but I'm unsure if this has anything to do with the EarthquakeRepository, as my original "hope" (premature optimization)
        // was this would give time for the EarthquakeRepository's init method to run etc & thus the user would less likely
        // notice the loading of the data

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

         */
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState == null) return;
        //outState.putStringArrayList();
        // outState.putBundle("earthquakes", earthquakes);
        // outState.putParcelableArrayList("earthquakes", (ArrayList<? extends Parcelable>) earthquakes);
        /*
        this was failing because I believe earthquakes was in the classes' top level scope
        but offcourse its no longer being used
        perhaps what I should had done a while back was just do something like
         public void update(Object _earthquakes) {
            earthquakes = earthquakesViewModel.UI_earthquakes
         }
        */
        ArrayList<Earthquake> earthquakes = earthquakesViewModel.getEarthquakes();
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
        // I don't like this naming but hopefully it becomes more obvious when this makes use of the view model
        earthquakesViewModel.getEarthquakesOnDate(selectedDate);
        //earthquakesRecyclerViewAdapter.notifyDataSetChanged(); // maybe this should've originally been outside & after the loop? Yeah, it doesn't matter but outside the loop would be more performant
    }

    public void onClick(View aview)
    {
        // startProgress();
        /*
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
            // locationsSpinnerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("EarthquakeRepository", e.getLocalizedMessage());
            // display toast saying "data unavailible"
            throw new RuntimeException(e);
        }
         */
    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    } //

    /*
        // yeah, this code seems really awkward; I think I want to avoid having variant methods e.g. updateOnDateSelection etc
        // and without an understanding of the ViewModel class, you likely wouldn't guess where update was called
        [refactor] I should update the methods to the correct types rather than casting from Object to ArrayList<Earthquakes>
        I briefly looked at method overloading etc, and I suppose I probably should had just used optional parameters,
        but I'd rather just redesign & rewrite this after its been submitted when I have more time
     */
    @Override
    public void update(Object _earthquakes) { // not sure about injecting the data but oh well
        // https://www.youtube.com/watch?v=SWBN0y0lFNY
        // [Optimization] Actually check if the UI state has changed before allocating UI elements on the heap (not sure if this would be done here or elsewhere)
        /*

        */
     //   earthquakes = new ArrayList<Earthquake>();
        ArrayList<Earthquake> earthquakes = (ArrayList<Earthquake>) _earthquakes;
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        // not a fan of the way I'm passing savedInstanceState but oh well
        Bundle savedInstanceState = earthquakesViewModel.getSavedInstanceState();
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
        earthquakesRecyclerViewAdapter.notifyDataSetChanged(); // maybe this should've originally been outside & after the loop? Yeah, it doesn't matter but outside the loop would be more performant


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
                    Log.d("MainActivity_onItemSelected",String.valueOf(earthquakes.size()));
                    Collections.sort(earthquakes, new Comparator<Earthquake>() {
                        @Override
                        public int compare(Earthquake quakeA, Earthquake quakeB) {
                            if (position >= 1) {
                                    /*
                                     [MVVM] will return to this later , I feel like this is such little logic that
                                     it doesn't matter whether this lies in the view or the view model
                                     but if I decide to move it to the view model then I think I'm going to need a
                                     MonitoringStationsManager object to manage its own earthquakes array list
                                     which [at the moment] is difficult to grasp
                                    */
                                //earthquakes = monitoringStationsManager.ascending();
                                return Double.valueOf(quakeA.magnitude).compareTo(quakeB.magnitude);
                            } else {
                                return Double.valueOf(quakeB.magnitude).compareTo(quakeA.magnitude);
                            }
                        }
                    });
                    earthquakesRecyclerViewAdapter.notifyDataSetChanged();
                    System.out.println("WHY IS THIS RUNNING WHEN earthquakesRecyclerViewAdapter is null?");
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
    public void update() {
        // there's a comment elsewhere explaining why this is empty
        Log.d("MainActivity", "this should never be ran");
    }

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
                    /*
                    earthquakes = new ArrayList<Earthquake>();
                    for ( String key : monitoringStationsManager.getMonitoringStations().keySet() ) {
                        System.out.println( key );
                        ArrayList<Earthquake> quakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation(key);
                        earthquakes.addAll(quakes);
                    }
                     */
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
