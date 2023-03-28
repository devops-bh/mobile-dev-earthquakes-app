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
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
        System.out.println("EARTHQUAKE CREATED");
    }
    public void moreParsing() { // [refator] ...
        if (this.description == null || this.description == "") {
            return;
        }

        // assumes details are in a rigid predictable order
        String[] details = description.split(";");
        for (int i = 0; i < details.length; i++) {
            System.out.println("Detail " + details[i] + " I " + i);
        }
        System.out.println(" D @ len - 1" + details[details.length - 1]); // magbutyde
        // crashes the app
        // this.magnittude = Double.parseDouble(details[details.length - 1].split(":")[1].replace(" ", ""));
        String[] magAsStr = details[details.length - 1].split(": "); //[1].replace(" ", "");
        System.out.println("MAG AS STR|" + magAsStr.getClass().getName());
        for (int i = 0; i < magAsStr.length; i++) {
            System.out.println(" M A S " + i + " " + magAsStr[i]);
        }
        System.out.println(" M A S .L " + magAsStr[magAsStr.length-1] + " Length: " + (magAsStr.length-1));
        // not confirmed to work, I'm curioous if the "null" in the XML string is somehow affecting this?
        try { // the above parsing logic appears to be wrong so this is just a quick fix
            double magAsDbl = Double.valueOf(magAsStr[magAsStr.length-1]);
            this.magnittude = magAsDbl;
            System.out.println(("magAsDbl: " + magAsDbl));
        } catch(NumberFormatException e) { // use a more specialised catchable error type
            System.out.println("ERROR CAUGHT: ");
            System.out.println(e);
            System.out.println("Contiuing ");
        }
        // do something similar to get the depth etc
        System.out.println("Mag " + this.magnittude);
    }

}


class MonitoringStationsManager { // do I need to inherit from iterable to enable iteration?
    private HashMap<String, ArrayList<Earthquake>> monitoringStations;
    public MonitoringStationsManager() { // [refactor] should probably use .ToLowerCase() or ignoreCase to prevent case sensitivity bugs
        this.monitoringStations = new HashMap<String, ArrayList<Earthquake>>();
    }

    public void add(String monitoringStation, Earthquake earthquake) {
        if (monitoringStations.containsKey(monitoringStations)) {
            this.monitoringStations.get(monitoringStation).add(earthquake);
            // doesn't crash app
            if (earthquake.title != null) {
                Log.d("E T", earthquake.title);
            }
        } else {
            monitoringStations.put(monitoringStation, new ArrayList<Earthquake>());
        }
    }

    public ArrayList getAllEarthquakesFromMonitoringStation(String monitoringStation) {
        if (this.monitoringStations.containsKey(monitoringStation)) {
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

        // More Code goes here
        monitoringStationsManager = new MonitoringStationsManager();
        //ArrayList<String> listItems=new ArrayList<String>();
        String monitoringStation = "TAJIKISTAN"; // hypothetically this would be changed dynamiically?
        ArrayList tajikistanEarthquakes = monitoringStationsManager.getAllEarthquakesFromMonitoringStation(monitoringStation);
        //adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tajikistanEarthquakes);
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
    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                //
                // Throw away the first 2 header lines before parsing
                // ^ Does this mean we remove the XML & rss version tags, but keep everything inside the channel element including the title, link, description, language, lastbuilddate and image?
                // do I do this before the while loop e.g. replace the unwanted tags with empty strings? or do I do it in the while loop by simply not appending the unwanted strings to the result variable
                // are we expected to get rid of the 2 headers lines a particular way e.g. use a particular method?
                // alternatively; am I allowed to just "ignore" the 2 header lines in the while loop?
                //Log.d("IN RL", in.readLine());// <?xml version="1.0"?>
                // the following approach does not respect immutability (not sure if this matters)
                // these appear to be causing the nulls
                in.readLine().replaceAll("<?xml version=\"1.0\"?>", "");//.trim(); // not sure if I need trim
                // the following lines may be cause the app to crash
                in.readLine().replaceAll("<rss version=\"2.0\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">", "");//.trim();
                in.readLine().replaceAll("</rss>", ""); //.trim();

                // I guess for the meantime I'll just ignore the rss tag/s in during the while loop

                // note: don't think its related to here, but I think the app is freezing due to the high volume of text being rendered (when scrolling)

                while ((inputLine = in.readLine()) != null)
                {
                    // I think it'd be better to use the START_TAG + END_TAGS when parsing but I'm not sure if thats allowed
                    // I think I should use an or statement rather than an && but should probably double check this later
                    //if (!inputLine.contains("<rss version=") && !inputLine.contains("</rss>")) { // kinda wanna add a better check to ensure its a tag
                    // if (result == null) result = inputLine;
                    // tags are still slipping through
                    if (!inputLine.contains("<rss version") || !inputLine.contains("</rss>") || !inputLine.contains("<?xml version") || !inputLine.contains("</xml>")) {
                        /* I do not need to replace "nulls" with empty strings here because they are not being added here */
                        result = result + inputLine; //.replace("null", ""); //
                        //Log.d("MyTag", " INPUT LINE IS "+inputLine);
                        // Log.d("MyTag",inputLine);
                    }
                    //Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    //rawDataDisplay.setText(result);
                    // parseData(result.replace("null","")); // the replace null is [hopefully] a quick fix
                    // investigate if nulls appear here (maybe replaceAll might return nulls?)
//                    ArrayList<Earthquake> parsedData = parseData(result.replaceAll("null", ""));
   //                 if (parsedData == null) { System.out.println(("NUll")); }
                    /*
                    ArrayList pd = parseData(result );

                    if (pd == null) { System.out.println("Dammit"); }
                    if (pd != null) {
                        System.out.println("Size: " + pd.size());
                    }
                     */
                    ArrayList<Earthquake> earthquakes = parseData(result);
                    System.out.println("Earthquake size: "+ earthquakes.size()); // 5
                    for (int i = 0; i < earthquakes.size(); i++) {
                        String title = earthquakes.get(i).title;
                        // the following lin eoutputs "null null"
                        listItems.add(earthquakes.get(i).title + " " + earthquakes.get(i).description);
                    }
                    adapter.notifyDataSetChanged();
                    /*
                    for (int i = 0; i < parsedData.size(); i++) {
                        String title = parsedData.get(i).title;
                        listItems.add(parsedData.get(i).title + " " + parsedData.get(i).description);
                    }
                    adapter.notifyDataSetChanged();

                     */
                }
            });
        }
    }
    /*
    private void parseData(String dataToParse)
    {
        // [refactor] call this method in its own thread or run this code in its own thread
        // [note] I can't remember if methods should be responsible for their own threading
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( dataToParse ) );
            int eventType = xpp.getEventType();
            Log.d("MyTag", "PARSING XML");
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                Log.d("MyTag", "NOT YET END OF XML DOC" + eventType + " :/ ");
                // [refactor] these nested ifs are ugly; improve the code before carrying on with the parsing
                // Found a start tag
                if(eventType == XmlPullParser.START_TAG) // never true
                {
                    System.out.println("XPP text: " + xpp.nextText());
                    Earthquake quake = new Earthquake();
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        switch (xpp.getName().toLowerCase()) {
                            case "title":
                                // [refactor] should probably use setters here to account for null values
                                // may even use the builder pattern
                                //quake.title = xpp.nextText(); // or getText
                                // quake.title = xpp.getText(); // or getText
                                //                          System.out.println("Get tile...");
//                            Log.d("Title", quake.title);
                                System.out.println("XPP text: " + xpp.nextText());

                                break;
                            case "description":
                                quake.description = xpp.nextText();
                                break;
                            case "link":
                                quake.link = xpp.nextText();
                                break;
                            case "pubdate":
                                quake.pubDate = xpp.nextText();
                                break;
                            case "category":
                                quake.category = xpp.nextText();
                                break;
                            case "lat":
                                quake.lat = Float.parseFloat(xpp.nextText());
                                break;
                            case "lng":
                                quake.lng = Float.parseFloat(xpp.nextText());
                                break;
                            default:
                                break;
                        }
                    }
                    // [todo] identify continent using geolocation - ignore
                    //continentsManager.add(continentName, quake);
                    quake.moreParsing();
                    continentsManager.add("asia", quake);
                    ArrayList<Earthquake> earthquakesInAsia = continentsManager.getAllEarthquakesInContinent("asia"); //.get(0).title;
                    for (int counter = 0; counter < earthquakesInAsia.size(); counter++) {
                        if (earthquakesInAsia.get(counter).title == null) {
                            //Log.d("MyTag", earthquakesInAsia.get(counter).title);
                            // null occurs here
                            System.out.println("Title: " + earthquakesInAsia.get(counter).title); // null
                        }
                        //System.out.println(earthquakesInAsia.get(counter).title); // null
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

    } // End of parseData
     */
/*
    private void parseData(String dataToParse) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(new StringReader( dataToParse ) );
            boolean check =false;
            String text = "";

            //factory instantiates an object
            boolean parsingItem = false;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                Earthquake earthquake = null;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String tagname = parser.getName();
                        Log.i("Tag names are ", tagname);
                        if (parser.getName().equalsIgnoreCase("item")) {
                            parsingItem = true;
                            earthquake = new Earthquake();
                            System.out.println("PARSING ITEM");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (parsingItem) {
                            if (parser.getName().equalsIgnoreCase("title")) {
                                Log.i("Title is", text + " TAG IS " + parser.getName());
                                // further parse the title
                                //earthquake.title = text;
                            }
                            if (parser.getName().equalsIgnoreCase("description")) {
                                Log.i("Description is", text + " TAG IS " + parser.getName());
                                // further parse the title
                                String[] splitDesc = text.split(";");
                                // [refactor] I guess I could use direct inedexing or iteration with the tradeoff being flexibility or speed
                                String location = splitDesc[1].replace("Location: ", "").replaceAll(" ", "");
                                Log.i("Location", location);
                                /*
                                if (!monitoringStations.contains(location) {
                                    monitoringStations.add(location);
                                }
                                *
                                // [refactor] convert the replacement to lowercase
                                Double magnitude = Double.parseDouble(splitDesc[splitDesc.length-1].replace("Magnitude: ", "").replaceAll(" ", ""));
                                Log.i("magnitude", ""+magnitude);
                                //earthquake.location = location;
                             //   earthquake.magnittude = magnitude;
                            }
                        }
                        if (parser.getName().equalsIgnoreCase("item")) {
                           // this.monitoringStationsManager.add(earthquake.location, earthquake);
                            listItems.add("1");
                            adapter.notifyDataSetChanged();
                            parsingItem = false;
                            System.out.println("NO LONGER PARSING ITEM");
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 */

    private ArrayList<Earthquake> parseData(String dataToParse)
    {
        Earthquake widget = null;
//        ArrayList <Earthquake> alist = null;
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
                    Log.d("Name: ", xpp.getName());
                    boolean isItem = false; // not convinced this is needed; is to ensure the earlier title tag deson't interfere
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        Log.d("ALIST", "Instantiating alist"); // never logged
                        //alist  = new ArrayList<Earthquake>();
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        /* adding the previous earthquake to the list because
                        * a closing tag for "item" never seems to be encountered
                        * I don't think these lines are ran as expected */
                        if (widget != null && alist != null) {
                             //   alist.add(widget);
                              //  System.out.println("LIST" + "added");
                           //     Log.d("CURRENT", "added");
                        } else {
                          //  System.out.println("LIST" + "WIDGET AND/OR LIST IS NULL");
                         //   Log.d("CURRENT", "WIDGET AND/OR LIST IS NULL");
                        }

                        Log.e("MyTag","Item Start Tag found");
                        widget = new Earthquake();
                        isItem = true;
                    } // else if (xpp.getName().equalsIgnoreCase("title"))
                    if (isItem) {
                        if (xpp.getName().equalsIgnoreCase("title")) {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            // Do something with text
                            Log.e("MyTag", "Bolt is " + temp);
                            // widget.title = temp;
                            //  System.out.println("Earthquake title:" + widget.title);
                            if (widget != null) {
                                widget.title = temp;
                                System.out.println("EARTHQUAKE TITLE: " + widget.title);
                            }
                        } else
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("description")) {
                                // Now just get the associated text
                                String temp = xpp.nextText();
                                // Do something with text
                                Log.e("MyTag", "Nut is " + temp);
                                if (widget != null) {
                                    widget.description = temp;
                                }
                                //      widget.description = temp;
                            } else
                                // Check which Tag we have
                                if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                    // Now just get the associated text
                                    String temp = xpp.nextText();
                                    // Do something with text
                                    Log.d("MyTag", "Washer is " + temp);
                                    if (widget != null) {
                                        widget.pubDate = temp;
                                        // NOTE: ONLY DO THIS ON THE LAST PARSED TAG
                                        // ASSUMES THE LAST TAG IS EXPECTED
                                        alist.add(widget);
                                        System.out.println("pubDate is " + temp);
                                        Log.d("pubDate is " , temp);
                                    }
                                    //        widget.pubDate = temp;
                                }
                    }
                }
                else
                if(eventType == XmlPullParser.END_TAG) {
                    System.out.println("END_TAG" + xpp.getName());
                    Log.d("END_TAG", "END TAG" + xpp.getName());
                    boolean isItem = false;
                    if (xpp.getName().equalsIgnoreCase("item")) { // there doesn't seem to be an closing for "item
                        Log.i("LIST", "widget is being added to list " + widget.toString());
                        if (widget == null) {
                //            System.out.println("LIST" + " widget is null");
                //            Log.d("LIST" ," widget is null");
                        } else {
                  //          alist.add(widget);
                   //         System.out.println("LIST" + " added");
                     //       Log.d("LIST" ," added");
                        }
                        isItem = true;
                        System.out.println("iS ITEM IS TRUE");
                        //alist.add(widget);
                        //                      listItems.add(widget.title);
//                        adapter.notifyDataSetChanged();
                        // listItems.add(widget.title + " " + widget.description);
                    }
                    //else if (xpp.getName().equalsIgnoreCase("channel")) {
                        if (isItem) {
                            if (xpp.getName().equalsIgnoreCase("title")) {
                            /*
                        int size;
                        size = alist.size();
                        Log.e("MyTag", "widgetcollection size is " + size);
                        */
                        //   adapter.notifyDataSetChanged();
                            System.out.println("Closing tag - Title: ");
                            Log.d("Closing tag", "Title");
                        }
                    }
                    if (widget != null && alist != null) {
                        // alist.add(widget); // earthquake object is not null but properties are
                   //     System.out.println("LIST" + "added " + widget.title); // widget.title is null
                       // Log.d("CURRENT", "added " + widget.title); // widget.title is null
                        // this is logged
                        // but this code is ran multiple times
                        // & thus may cause duplicate earthquales
  //                      listItems.add(widget.title);
//                        adapter.notifyDataSetChanged();
                    } else {
                    //    System.out.println("LIST" + "WIDGET AND/OR LIST IS NULL");
                    //    Log.d("CURRENT", "WIDGET AND/OR LIST IS NULL");
                    }

                    isItem = false;
                }


                // Get the next event
                eventType = xpp.next();

            } // End of while
            /*
            // does nothing
                        listItems.add(widget.title);
                        adapter.notifyDataSetChanged();
                */
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
        /*
        for (int i = 0; i < alist.size(); i++) {
            String title = alist.get(i).title;
            listItems.add(alist.get(i).title + " " + alist.get(i).description);
        }
        adapter.notifyDataSetChanged();

         */
        if (alist != null) {
            System.out.println("NOT Null " + alist.size());
            /*
            // errors
            listItems.add(widget.title);
            adapter.notifyDataSetChanged();
            */
        } else {
            System.out.println("ALIST IS NULL");
        }
        return alist;

    }
}