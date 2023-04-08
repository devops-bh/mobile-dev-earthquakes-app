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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearSnapHelper;

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
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
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
    public double magnittude; // maybe have a "getAsDouble" & a "getAsString" method?
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
            this.magnittude = magAsDbl;
        } catch(NumberFormatException e) { // use a more specialised catchable error type
            System.out.println("ERROR CAUGHT: ");
            System.out.println(e);
        }
        // do something similar to get the depth etc
        System.out.println("Mag " + this.magnittude);
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
        /*
        Issue:
        https://stackoverflow.com/questions/21600344/java-hashmap-containskey-returns-false-for-existing-object
        I believe using the location string (a mutable data type) is causing the containsKey method to return false even if the values are identical
        if (monitoringStations.containsKey(monitoringStations)) {
            this.monitoringStations.get(monitoringStation).add(earthquake);
            // doesn't crash app
            if (earthquake.title != null) {
                Log.d("E T", earthquake.title);
            }
        } else {
            monitoringStations.put(monitoringStation, new ArrayList<Earthquake>());
            this.monitoringStations.get(monitoringStation).add(earthquake);
        }
        System.out.println("INPUTTED MONITORING STATION: " + monitoringStation + "Contains keyL " + monitoringStations.containsKey(monitoringStations));
         */

        /*
        try {
            if (this.monitoringStations.size() < 1) {
                this.monitoringStations.put(monitoringStation, new ArrayList<Earthquake>());
                //this.monitoringStations.get(monitoringStation).add(earthquake);
                ArrayList<Earthquake> current = this.monitoringStations.get(monitoringStation);
                current.add(earthquake);
                this.monitoringStations.put(monitoringStation, current);
                return;
            }
            for (String key : monitoringStations.keySet()) {
                System.out.println(monitoringStations.get(key));
                if (key == monitoringStation) {
                    /*
                    Are these lines doing what I think they're doing?
                    Essentially I think the issue is the previous array is being overwritten when adding to the location multiple times
                     *
                    ArrayList<Earthquake> current = this.monitoringStations.get(monitoringStation);
                    current.add(earthquake);
                    this.monitoringStations.put(monitoringStation, current);
               //     this.monitoringStations.get(monitoringStation).add(earthquake);
                    System.out.println("TRUE: " + monitoringStation + " == " + key);
                    return;
                } else {
                 //   monitoringStations.put(monitoringStation, new ArrayList<Earthquake>());
//                    this.monitoringStations.get(monitoringStation).add(earthquake);
                    ArrayList<Earthquake> current = new ArrayList(); // this.monitoringStations.get(monitoringStation);
                    current.add(earthquake);
                    this.monitoringStations.put(monitoringStation, current);
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println(e);
        }
         */
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
}

// todo: convert the list portion of the app to a fragment
public class MainActivity extends ListActivity implements OnClickListener
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
    ArrayList<String> listItems = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        monitoringStationsManager = new MonitoringStationsManager();
        /*
        //String monitoringStation = "TAJIKISTAN"; // hypothetically this would be changed dynamiically?
        ArrayList tajikistanEarthquakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation(monitoringStation);
         */
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);
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
                //              in.readLine().replaceAll("<channel>", "");
//                in.readLine().replaceAll("</channel>", "");
                //in.readLine().replaceAll("geo:",""); // isn't working as expected

                // I guess for the meantime I'll just ignore the rss tag/s in during the while loop

                // note: don't think its related to here, but I think the app is freezing due to the high volume of text being rendered (when scrolling)

                boolean canParse = false;
                while ((inputLine = in.readLine()) != null) {
                    if (/*!inputLine.contains("geo") &&*/ !inputLine.contains("<rss version") && !inputLine.contains("</rss>") && !inputLine.contains("<?xml version") && !inputLine.contains("</xml>")) {
                        /* I do not need to replace "nulls" with empty strings here because they are not being added here */
                        //Log.d("MyTag", " INPUT LINE IS "+inputLine);
                        // Log.d("MyTag",inputLine);
                        //Log.e("MyTag",inputLine);
//                        if (inputLine.contains("geo")) {
                        //System.out.println(inputLine.substring(5, inputLine.length()));
                        //                          System.out.println(inputLine);
                        //        result += inputLine.replaceAll("geo:","");
                        //        System.out.println(" BECOMES: " + inputLine.replaceAll("geo:",""));
                        /*
                            inputLine.replace("geo:", "");
                            System.out.println(inputLine);
                        */
//                        } else {
                        //                          result = result + inputLine; //.replace("null", ""); //
                        //                    }
                        /*
                        if (!inputLine.equalsIgnoreCase("<channel>") && !inputLine.equalsIgnoreCase("</channel>")) {
                            result = result + inputLine.replaceAll("geo:", ""); //.replace("null", ""); //
                        }
                         */
                        // todo: double check that I only need the item data
                        // I'm doing this because it means I don't need to check for multiple uses of a tag with the same name
                        // e.g. there's a description tag outside the channel tag and inside the item tags
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
                    // I think it'd be better to use the START_TAG + END_TAGS when parsing but I'm not sure if thats allowed
                    // I think I should use an or statement rather than an && but should probably double check this later
                    //if (!inputLine.contains("<rss version=") && !inputLine.contains("</rss>")) { // kinda wanna add a better check to ensure its a tag
                    // if (result == null) result = inputLine;
                    // tags are still slipping through

                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    //LinkedList<Earthquake> earthquakes = parseData(result);
                    parseData(result);
                    /*
                    ArrayList<Earthquake> ST_earthquakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation("SOUTHERNTURKEY");
                    ArrayList<Earthquake> R_earthquakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation("ROMANIA");
                    System.out.println(R_earthquakes.addAll(ST_earthquakes)); // returns true
                    */
                        /*
                        Bug; rather than adding a new earthquake with the same location, this code, or the MonitoringStationsManager.add method
                        appears to simply be duplicated either the earthquake item, retrieving the same item twice, or duplicating the list view element
                         */
                    ArrayList<Earthquake> earthquakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation("KERMADECISLANDS"); // maybe this method just don't work :| (though the method maybe inconsistent - though I'm not convinced thats because the data is volatile but rather the code itself needs improvement )
                    for (int i = 0; i < earthquakes.size(); i++) {
                        String title = earthquakes.get(i).title;
                        // the following lin eoutputs "null  null"
                        listItems.add(earthquakes.get(i).title + " - Location: " + earthquakes.get(i).location);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }

        /*
        private ArrayList<Earthquake> parseData(String dataToParse) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(dataToParse));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        System.out.println(xpp.getName() + " " + xpp.nextText() + " " + xpp.nextText());
                    } else if (eventType == XmlPullParser.END_TAG) {

                    }
                }
            } catch (XmlPullParserException e) {
                System.out.println((e));
            } catch (IOException e) {
                System.out.println((e));
            }

            return new ArrayList<Earthquake>();
        }
    }

         */


//    private ArrayList<Earthquake> parseData(String dataToParse) throws XmlPullParserException, IOException {
            /*
            ArrayList<Earthquake> alist = new ArrayList<Earthquake>();
            Earthquake earthquake = null;
             */
            /*
            try {

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(dataToParse));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                        boolean isItem = false;
                        if (eventType == XmlPullParser.START_DOCUMENT) {
                            System.out.println("Start document");
                        } else if (eventType == XmlPullParser.START_TAG) {
                            //if (!xpp.getName().equalsIgnoreCase("geo:lat") || xpp.getName().equalsIgnoreCase("geo:long")) {

                            //}
                            //System.out.println(xpp.getText());
                           if (xpp.getName().equalsIgnoreCase("item")) {
                                isItem = true;
                            }
                            System.out.println("Start tag " + xpp.getName());
                        } else if (eventType == XmlPullParser.END_TAG) {
                            //if (!xpp.getName().equalsIgnoreCase("geo:lat") || xpp.getName().equalsIgnoreCase("geo:long")) {

                            //}
                            switch (xpp.getName()) {
                                case "item":
  //                                  monitoringStationsManager.add(earthquake.location, earthquake);
    //                               System.out.println("End tag " + xpp.getName() + "CUR TEXT " + xpp.getText());
                                    isItem = false;
                                    break;
                            }
                        } else if (eventType == XmlPullParser.TEXT) {
//                            String innerText = xpp.getText();
                            //if (!xpp.getName().equalsIgnoreCase("geo:lat") || xpp.getName().equalsIgnoreCase("geo:long")) {

                            //}
                            /*
                            switch (xpp.getName().toLowerCase()) {
                                case "item":
                                    isItem = true;
                                    earthquake = new Earthquake();
                                    break;
                                case "title":
                                    if (!isItem) break;
                                    earthquake.title = innerText;
                                case "description":
                                    // assumes details are in a rigid predictable order
                                    String[] details = innerText.split(";");
                                    String location = details[1].replace("location: ", "");
                                    earthquake.location = location;
                                    System.out.println(earthquake.location);
                                    String[] magAsStr = details[details.length - 1].split(": ");
                                    //try {
                                    double magAsDbl = Double.valueOf(magAsStr[magAsStr.length - 1]);
                                    double magnittude = magAsDbl;
                                    earthquake.magnittude = magnittude;
                                    //} catch(NumberFormatException e) { // use a more specialised catchable error type
                                    /*
                                        System.out.println("ERROR CAUGHT: ");
                                        System.out.println(e);
                                        System.out.println("Contiuing ");
                                        */
        //}
        //break;
        //}
            /*
                            System.out.println("Text " + xpp.getText());
                        }
                        eventType = xpp.next();
                }
                System.out.println("End document");
            } catch (XmlPullParserException e) {
                Log.e("XmlPullParserException", e.getLocalizedMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getLocalizedMessage());
            }

            return alist;
             */

        // todo: implement earthquake using a mix of the PullParser3 example code and the above code
            /*

            Earthquake widget = null;
            //LinkedList <Earthquake> alist = null; // will unlikely use this list since I'm using the monitoringStations data structure/s
            ArrayList <Earthquake> alist = new ArrayList<>(); // ^
            try
            {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( new StringReader( dataToParse ) );
                int eventType = xpp.getEventType();
                boolean isItem = false;
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    // Found a start tag
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        // Check which Tag we have
                        /*
                        if (xpp.getName().equalsIgnoreCase("channel"))
                        {
                            alist  = new LinkedList<alist>();
                        }
                        else *
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            Log.e("MyTag","Item Start Tag found");
                            widget = new Earthquake();
                            isItem = true;
                        }
                        else
                        if (xpp.getName().equalsIgnoreCase("title"))
                        {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            // Do something with text
                            Log.e("MyTag","Bolt is " + temp);
                            if (isItem) {
                                widget.title = temp;
                            }
                        }
                        else
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("description"))
                            {
                                if (isItem) {
                                // todo - extract location + magnitude (see above)
                                    // assumes details are in a rigid predictable order
                                    /*
                                    String[] details = xpp.getText().split(";");
                                    String location = details[1].replace("location: ", "");
                                    widget.location = location;
                                    System.out.println(widget.location);
                                    String[] magAsStr = details[details.length - 1].split(": ");
                                    //try {
                                    double magAsDbl = Double.valueOf(magAsStr[magAsStr.length - 1]);
                                    double magnittude = magAsDbl;
                                    widget.magnittude = magnittude;

                                     *
                                    System.out.println(xpp.getText());
                                }
                            }
                    }
                    else
                    if(eventType == XmlPullParser.END_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            Log.e("MyTag","widget is " + widget.toString() + "TITEL: " + widget.title);
                            alist.add(widget);
                            // use my monitoringStationsManager
                            isItem = false;
                        }
                        /*
                        else
                        if (xpp.getName().equalsIgnoreCase("widgetcollection"))
                        {
                            int size;
                            size = alist.size();
                            Log.e("MyTag","widgetcollection size is " + size);
                        }
                         *
                    }


                    // Get the next event
                    eventType = xpp.next();

                } // End of while

                //return alist;

            }
            catch (XmlPullParserException ae1)
            {
                Log.e("MyTag","Parsing error" + ae1.toString());
            }
            catch (IOException ae1)
            {
                Log.e("MyTag","IO error during parsing");
            }

            Log.e("MyTag","End document");

            return alist;
             */
        /*

        ArrayList<Earthquake> alist = new ArrayList<Earthquake>();
        Earthquake earthquake = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(dataToParse));
            int eventType = xpp.getEventType();
            boolean isItem = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                /*if (eventType == XmlPullParser.START_DOCUMENT) {
                    System.out.println("Start document");
                } else *
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().toLowerCase()) {
                        case "item":
                            earthquake = new Earthquake();
                            isItem = true;
                            break;
                        case "description":
                            if (!isItem) break;
                            // assumes details are in a rigid predictable order
//                            String[] details = xpp.nextText().split(";");
                            /*
                            String location = details[1].replace("location: ", "");
                            earthquake.location = location;
                            System.out.println(earthquake.location);
                            String[] magAsStr = details[details.length - 1].split(": ");
                            double magAsDbl = Double.valueOf(magAsStr[magAsStr.length - 1]);
                            double magnittude = magAsDbl;
                            earthquake.magnittude = magnittude;
                             */
        //                         break;
        //              }
       /*         }
                else if (eventType == XmlPullParser.TEXT) {
                    if (isItem)
                    {
                        System.out.println("TEXT: " + xpp.getText() + " NEXT: " + xpp.nextText());
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    switch (xpp.getName()) {
                        case "item":
                            monitoringStationsManager.add(earthquake.location, earthquake);
                            isItem = false;
                            break;
                    }
                }
                eventType = xpp.next();
            }
            System.out.println("End document");
        } catch (XmlPullParserException e) {
            Log.e("XmlPullParserException", e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getLocalizedMessage());
        }

        return alist;

    }
        */
/*
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader("<foo>Hello World!</foo>"));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    System.out.println("Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    System.out.println("Start tag " + xpp.getName() + " TEXT: " + xpp.getText() + " NEXT TEXT: " + xpp.nextText());
                } else if (eventType == XmlPullParser.END_TAG) {
                    System.out.println("End tag " + xpp.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    System.out.println("Text " + xpp.getText());
                }
                eventType = xpp.next();
            }
            System.out.println("End document");

        } catch (XmlPullParserException e) {

        } catch (IOException e) {

        }
        return new ArrayList<Earthquake>(); // return alist;
    }
    }

 */
    /*
    private ArrayList<Earthquake> parseData(String dataToParse)
    {
        Earthquake earthquake = null;
        ArrayList<Earthquake> alist  = new ArrayList<Earthquake>();

        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( dataToParse ) );
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                // Found a start tag
                if(eventType == XmlPullParser.START_TAG)
                {
                    System.out.println("Name: " + xpp.getName());
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        Log.e("MyTag","Item Start Tag found");
                        earthquake = new Earthquake();
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "Bolt is " + temp);
                        if (earthquake != null) {
                            earthquake.title = temp;
                            System.out.println("EARTHQUAKE TITLE: " + earthquake.title);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("description")) {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text
                        Log.e("MyTag", "Nut is " + temp);
                        if (earthquake != null) {
                            earthquake.description = temp;
                        }
                    } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text
                        Log.d("MyTag", "Washer is " + temp);
                    }
                }
                else if(eventType == XmlPullParser.END_TAG) {
                    System.out.println("END_TAG" + xpp.getName());
                    Log.d("END_TAG", "END TAG" + xpp.getName());
                    if (xpp.getName().equalsIgnoreCase("item")) { // there doesn't seem to be an closing for "item
                        //alist.add(earthquake);
                        if (earthquake != null) {
                            System.out.println("but there is hope");
                            alist.add(earthquake); // [bug] earthquake is repeatedly adding argentina (atm)
                        }
                    }
                    if (xpp.getName().equalsIgnoreCase("channel")) {
                        System.out.println("channel tag"); // I suspect this will never run
                    }
                }
                // Get the next event
                eventType = xpp.next();
            } // End of while
        }
        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }
        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        }

        Log.e("MyTag","End document");
        return alist;

    }
     */

        //private LinkedList<Earthquake> parseData(String dataToParse) {
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
                                widget.magnittude = magnittude;
                                break;
                            case "lat":
                                widget.lat = Float.valueOf(xpp.nextText());
                                break;
                            case "long":
                                //widget.long = Float.valueOf(xpp.nextText());
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
