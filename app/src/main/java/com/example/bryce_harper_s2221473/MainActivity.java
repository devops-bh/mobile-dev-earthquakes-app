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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;

// import gcu.mpd.bgsdatastarter.R;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private TextView rawDataDisplay;
    private Button startButton;
    //private String result; // defaults to null
    private String result = "";
    private String url1="";
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

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
                //in.readLine().replaceAll("<?xml version=\"1.0\"?>", "").trim(); // not sure if I need trim
                // the following lines may be cause the app to crash
                /*
                in.readLine().replaceAll("<rss version=\"2.0\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">", "").trim();
                in.readLine().replaceAll("</rss>", "").trim();
                 */

                // I guess for the meantime I'll just ignore the rss tag/s in during the while loop

                // note: don't think its related to here, but I think the app is freezing due to the high volume of text being rendered (when scrolling)

                while ((inputLine = in.readLine()) != null)
                {
                    // I think it'd be better to use the START_TAG + END_TAGS when parsing but I'm not sure if thats allowed
                    // I think I should use an or statement rather than an && but should probably double check this later
                    //if (!inputLine.contains("<rss version=") && !inputLine.contains("</rss>")) { // kinda wanna add a better check to ensure its a tag
                    // if (result == null) result = inputLine;
                        result = result + inputLine; //.replace("null", "");
                        Log.d("MyTag", " INPUT LINE IS "+inputLine);
                        if (inputLine == null) {
                            Log.d("MyTag", " INPUT LINE IS [exp null] "+inputLine);
                        }
                        // Log.d("MyTag",inputLine);
                    //}
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
                    rawDataDisplay.setText(result);
                    // parseData(result.replace("null","")); // the replace null is [hopefully] a quick fix
                    parseData((result));
                }
            });
        }
    }
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
                    Log.d("MyTag", "START TAG");
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        String content = xpp.getText();
                        // Do something with text
                        Log.d("MyTitle","TItle " + temp + " is "+ content);
                    }
                    else
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("Description"))
                        {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            String content = xpp.getText();
                            Log.d("MyTag","Desc " + temp + " is "+ content);
                            // Do something with text
                            //Log.e("MyTag","Description is " + temp);
                        }
                        else
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("link"))
                            {
                                // Now just get the associated text
                                String temp = xpp.nextText();
                                String content = xpp.getText();
                                // Do something with text
                                Log.d("MyTag","Link " + temp + " is "+ content);
                            }
                            // more fields need parsing
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
}