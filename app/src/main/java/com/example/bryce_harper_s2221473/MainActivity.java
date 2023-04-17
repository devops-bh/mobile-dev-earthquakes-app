//
// Name                 Bryce Harper
// Student ID           S2221473
// Programme of Study   Computing
//

// Update the package name to include your Student Identifier
package com.example.bryce_harper_s2221473;

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

/*
Quick notes:
I tried implementing model view view-model without Jetpack Compose's ViewModel class; though I'd definitely improve this in the future
The code for retaining the state between life cycle destructions is there and worked with
the original implementation of MVVM but I couldn't get it working with this variant
Monitoring station is synomynous with location
There could be a concrete monitoring station model but I didn't see the need for an explicit model
The monitoring stations manager class is really just a wrapper around a concurrent hashmap
I anticipated displaying earthquakes based on location but ran out of time
There's methods like "getEarthquakes" which really return monitoring stations (which contain earthquakes) - these could prob be named better
Sorry I put little effort into the UI

*/

class Earthquake implements Parcelable {
    // Name                 Bryce Harper
    // Student ID           S2221473
    public String title;
    public String description;
    public String link ;
    public String pubDate;
    public String category;
    public float lat;
    public float lng;
    public double magnitude;
    public String location;
    public Earthquake() {

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
    // Name                 Bryce Harper
// Student ID           S2221473
    public void update();
}

class Observable {
    ArrayList<Observer> observers;
    // Name                 Bryce Harper
// Student ID           S2221473
    public Observable() {
        observers = new ArrayList<Observer>();
    }
    public void register(Observer observer) {
        observers.add(observer);
    }
    public void unregister(Observer observer) {
        observers.remove(observer);
    }
    public void notifyAllObservers() {
        System.out.println(this);
        for (Observer observer : observers) {
            observer.update();
        }
    }
}

class AllEarthquakesViewModel extends Observable implements Observer {
    // Name                 Bryce Harper
// Student ID           S2221473
    EarthquakeRepository earthquakeRepository;
    MonitoringStationsManager monitoringStationsManager;
    ArrayList<Earthquake> UI_earthquakes;
    Bundle savedInstanceState;
    public AllEarthquakesViewModel() {
        earthquakeRepository = new EarthquakeRepository();
        earthquakeRepository.init();
        earthquakeRepository.register(this);
    }
    public void getEarthquakesOnDate(String selectedDate) {
        ArrayList<Earthquake> earthquakes = this.UI_earthquakes;
        for (int i = 0; i < earthquakes.size(); i++) {
            String quakeDate = earthquakes.get(i).pubDate.substring(4, 16);
            if (quakeDate != selectedDate) {
                earthquakes.remove(i);
            }
            System.out.println("QD: " + quakeDate);
        }
        this.notifyAllObservers();
    }
    public Set getMonitoringStationNames() {
        return this.monitoringStationsManager.getMonitoringStationNames();
    }
    public ArrayList<String> getMonitoringStationNamesAsArrayList() {
        return this.monitoringStationsManager.getMonitoringStationNamesAsArrayList();
    }
    public void getStrongestEarthquakes() {
        // todo: loop through each monitoring station's earthquakes & append the ones with the strong value to an array list
    }
    public void getWeakestEarthquakes() {
        // todo: loop through each monitoring station's earthquakes & append the ones with the strong value to an array list
    }

    public ArrayList<Earthquake> getEarthquakes() {
        try {
            this.monitoringStationsManager = earthquakeRepository.getEarthquakes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.UI_earthquakes = new ArrayList<Earthquake>(); /* TODO: explain why I'm doing this*/
        for ( String key : monitoringStationsManager.getMonitoringStations().keySet() ) {
            System.out.println( key );
            ArrayList<Earthquake> quakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation(key);
            this.UI_earthquakes.addAll(quakes);
        }
        return this.UI_earthquakes;
    }

    public void ascending() {

    }

    public void descending() {

    }
    @Override
    public void update() {
        try {
            this.notifyAllObservers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setSavedInstanceState(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        ArrayList<Earthquake> tempQuakes = new ArrayList<>();
        if (savedInstanceState != null) {
            int earthquakes_count = Integer.valueOf(savedInstanceState.getString("earthquakes_count"));
            for (int i = 0; i < earthquakes_count; i++) {
                Earthquake earthquake = savedInstanceState.getParcelable("earthquake" + Integer.toString(i));
                tempQuakes.add(earthquake);
            }
            this.UI_earthquakes = tempQuakes;
            this.notifyAllObservers();
            Log.d("Orientation", "loaded earthquakes from state");
        }
    }
    public Bundle getSavedInstanceState() {

        return this.savedInstanceState;
    }
}
// this is somewhat of a builder class
class MonitoringStationsManager {
    // Name                 Bryce Harper
// Student ID           S2221473
    private Map<String, ArrayList<Earthquake>> monitoringStations;
    public MonitoringStationsManager() {
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
        System.out.println();
    }

    public void removeQuake(String location, int index) {
        // todo
    }
    public void removeMonitoringStation(String location) {
        // todo
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
    public Set getMonitoringStationNames() {
        return this.monitoringStations.keySet();
    }
    public ArrayList<String> getMonitoringStationNamesAsArrayList() {
        ArrayList<String> temp = new ArrayList<String>();
        for (String key : monitoringStations.keySet()) {
            temp.add(key);
        }
        return temp;
    }
    public Map<String, ArrayList<Earthquake>> getMonitoringStations() {
        return this.monitoringStations;
    }
}

class EarthquakeRepository extends Observable {
    // Name                 Bryce Harper
// Student ID           S2221473
    private HandlerThread handlerThread;
    MonitoringStationsManager monitoringStationsManager;
    public EarthquakeRepository() {
        monitoringStationsManager = new MonitoringStationsManager();
    }
    public void init() {
        this._setMonitoringStations();
    }
    public MonitoringStationsManager getEarthquakes() throws Exception {
        if (this.monitoringStationsManager != null) {
            return this.monitoringStationsManager;
        }
        throw new Exception("EarthquakeRepositories.monitoringStationsManager is null");
    }

    private void notifyObserversOnMainThread(Object response)
    {
        Handler uiThread = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> {
            this.notifyAllObservers();
        };
        uiThread.post(runnable);
    }
    private void _setMonitoringStations() {
        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        Handler asyncHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Object response = msg.obj;
                notifyObserversOnMainThread(response);
            }
        };
        Runnable runnable = () -> {
            Message message = new Message();
            String urlSource = "http://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
            String earthquakesXMLString = getEarthquakesAsStringAsync(urlSource);
            parseEarthquakesString(earthquakesXMLString);
            message.obj = monitoringStationsManager;
            asyncHandler.sendMessage(message);
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
            aurl = new URL(url);
            yc = aurl.openConnection();
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            in.readLine().replaceAll("<?xml version=\"1.0\"?>", "");
            in.readLine().replaceAll("<rss version=\"2.0\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">", "");//.trim();
            in.readLine().replaceAll("</rss>", "");

            boolean canParse = false;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.contains("<rss version") && !inputLine.contains("</rss>") && !inputLine.contains("<?xml version") && !inputLine.contains("</xml>")) {
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
        Earthquake earthquake = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(earthquakesString));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // Found a start tag
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().toLowerCase()) {
                        case "item":
                            earthquake = new Earthquake();
                            break;
                        case "title":
                            earthquake.title = xpp.nextText();
                            break;
                        case "description":
                            String[] details = xpp.nextText().split(";");
                            String location = details[1].replace(" Location: ", "");
                            earthquake.location = location.replaceAll(" ", "");
                            String[] magAsStr = details[details.length - 1].split(": ");
                            double magAsDbl = Double.valueOf(magAsStr[magAsStr.length - 1]);
                            double magnittude = magAsDbl;
                            earthquake.magnitude = magnittude;
                            break;
                        case "lat":
                            earthquake.lat = Float.valueOf(xpp.nextText());
                            break;
                        case "long":
                            earthquake.lng = Float.valueOf(xpp.nextText());
                            break;
                        case "magnitude":
                            earthquake.magnitude = Double.valueOf(xpp.nextText());
                            break;
                        case "pubdate":
                            earthquake.pubDate = xpp.nextText();
                            break;
                        // etc
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        //alist.add(earthquake);
                        monitoringStationsManager.add(earthquake.location, earthquake);
                        // monitoringStationsManager.notify();
                        System.out.println("Adding earthquake: " + earthquake.location);
                    }
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException ae1) {
            Log.e("MyTag", "Parsing error" + ae1.toString());
        } catch (IOException ae1) {
            Log.e("MyTag", "IO error during parsing");
        }

        Log.d("MyTag", "End document");
    }
}
public class MainActivity extends AppCompatActivity  implements OnClickListener, DatePickerDialog.OnDateSetListener, Observer
{
    // Name                 Bryce Harper
// Student ID           S2221473
    private TextView rawDataDisplay;
    private Button startButton;
    private String result = "";
    private String url1="";
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
    ArrayList<String> stationsSpinnerList;
    ArrayAdapter stationsSpinnerAdapter;
    ArrayList<Character> typedChars;
    Bundle savedInstanceState;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        earthquakesViewModel = new AllEarthquakesViewModel();
        ArrayList<Earthquake> tempQuakes = new ArrayList<Earthquake>();
        earthquakesViewModel.register(this);
        if (savedInstanceState != null) {
            //earthquakesViewModel.setSavedInstanceState(savedInstanceState);
            this.savedInstanceState = savedInstanceState;
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState == null) return;
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
        String selectedDate = DateFormat.getDateInstance().format(calendar.getTime());
        earthquakesViewModel.getEarthquakesOnDate(selectedDate);
    }

    public void onClick(View aview) {
        this.update();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        earthquakesRecyclerViewAdapter = new EarthquakesRecyclerViewAdapter(MainActivity.this, earthquakes);
        recyclerView.setAdapter(earthquakesRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        earthquakesRecyclerViewAdapter.notifyDataSetChanged();


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.magnitude_sort_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (earthquakesRecyclerViewAdapter != null) {
                    Log.d("MainActivity_onItemSelected", String.valueOf(earthquakes.size()));
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
                Collections.sort(earthquakes, new Comparator<Earthquake>() {
                    @Override
                    public int compare(Earthquake quakeA, Earthquake quakeB) {
                        return Double.valueOf(quakeA.magnitude).compareTo(quakeB.magnitude);
                    }
                });
                if (earthquakesRecyclerViewAdapter != null) {
                    earthquakesRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });

        Button openDatePickerButton = (Button) findViewById(R.id.date_picker_button);
        openDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

        public void update () {
            if (this.savedInstanceState == null) {
                this.earthquakes = earthquakesViewModel.getEarthquakes();
            } else {
                this.earthquakes = new ArrayList<>();
                int earthquakes_count = Integer.valueOf(savedInstanceState.getString("earthquakes_count"));
                for (int i = 0; i < earthquakes_count; i++) {
                    Earthquake earthquake = savedInstanceState.getParcelable("earthquake" + Integer.toString(i));
                    this.earthquakes.add(earthquake);
                }
            }
            if (earthquakesRecyclerViewAdapter != null) {
                earthquakesRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
}
