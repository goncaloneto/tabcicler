package com.pentaho.tabcicler;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gmneto on 04/08/2017.
 */

public class UrlList extends ListActivity {
    //List of array strings which will serve as list items
    ArrayList<String> listItems = new ArrayList<String>();

    ArrayList<String> durations = new ArrayList<String>();


    //Defining a string adapter which will handle the data of the listview
    ArrayAdapter<String> adapter;

    //File to save/load URLs List
    private String filename = "UrlFile";

    private String secondFilename = "UrlDurationFile";

    private final int DEFAULT_DURATION = 10;

    //Duration in seconds (before load next URL)
    private int duration = DEFAULT_DURATION;

    @Override
    public void onCreate(Bundle icicle) {
        Map<String,ArrayList<String>> map;

        //Read File to load the list
        map = loadListUrls(this);

        listItems = map.get("UrlList");
        durations = map.get("DurationList");

        Log.i(this.toString(), "######## ListItems: #########");
        for(String s : listItems){
            Log.i(this.toString(), s);
        }

        Log.i(this.toString(), "######## Duration List: #########");
        for(String s : durations){
            Log.i(this.toString(), s);
        }

        super.onCreate(icicle);
        setContentView(R.layout.activity_url_list);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);

        //When clicking on start button
        final Button start = (Button) findViewById(R.id.startBtn);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //If the list is not empty
                if (listItems.size() > 0) {
                    //Send list to the WebView Activity
                    Intent myIntent = new Intent(UrlList.this, ImmersiveWebView.class);
                    myIntent.putExtra("list", listItems);
                    myIntent.putExtra("durationList", durations);

                    //Set the duration from the edit element
                    try {
                        duration = Integer.parseInt(((EditText) findViewById(R.id.duration)).getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        duration = DEFAULT_DURATION;
                    }

                    //Send the duration to the WebView Activity
                    myIntent.putExtra("duration", duration);

                    //Start WebView Activity
                    UrlList.this.startActivity(myIntent);
                } else {
                    //If the List is empty print a Toast message
                    Toast.makeText(view.getContext(), "List is empty. Try to add an URL to the list.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Delete item of the list when clicking it
        ListView mListView = (ListView) findViewById(android.R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                listItems.remove(pos);
                durations.remove(pos);
                saveList(v.getContext(), listItems);
                adapter.notifyDataSetChanged();
            }
        });

    }

    //Add URLs to the List
    public void addItems(View v) {
        EditText editUrl = (EditText) findViewById(R.id.edit);
        EditText editDuration = (EditText) findViewById(R.id.duration);
        String url = editUrl.getText().toString();
        String duration = editDuration.getText().toString();

        //Add only if url is not empty
        if (!url.isEmpty()) {

            //If URL doesn't contain 'http://' send a Toast error message
            if (!url.contains("http://")) {
                Toast.makeText(v.getContext(), "Malformed URL. URL must start with 'http://'.", Toast.LENGTH_SHORT).show();
            } else {

                if( duration.isEmpty() ){
                    duration = Integer.toString(DEFAULT_DURATION);
                }

                //Add Url to the list and save it in the file
                listItems.add(url);

                durations.add(duration);

                saveList(v.getContext(), listItems, durations);

                editDuration.setText( Integer.toString( DEFAULT_DURATION ) );
                editDuration.setSelection(editDuration.getText().length());

                //Delete the edit box to be ready to write another URL
                editUrl.setText("http://");
                //Focus to the end of the text
                editUrl.setSelection(editUrl.getText().length());
                editUrl.requestFocus();
            }
        }
        //List changed, notify the adapter to map it on the ListView
        adapter.notifyDataSetChanged();

    }

    public ArrayList<String> loadList(Context context) {
        Log.i(this.toString(), "Reading file...");
        ArrayList<String> list = new ArrayList<String>();
        String filePath = context.getFilesDir().getPath().toString() + "/" + filename;
        FileInputStream inputStream;
        String url;

        try {
            inputStream = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((url = reader.readLine()) != null) {
                list.add(url);
            }

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.toString(), e.getMessage());
        }
        return list;
    }

    public void saveList(Context context, ArrayList<String> list) {
        Log.i(this.toString(), "Writing to file...");
        FileOutputStream outputStream;
        String filePath = context.getFilesDir().getPath().toString() + "/" + filename;

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(filePath));
            for (String url : list)
                pw.println(url);
            pw.close();
        } catch (FileNotFoundException f) {

            Log.e(this.toString(), f.getMessage());
            File file = new File(filePath);

            try {
                file.createNewFile();
                PrintWriter pw = new PrintWriter(new FileOutputStream(filePath));
                for (String url : list)
                    pw.println(url);
                pw.close();
                Log.e(this.toString(), "File created at " + filePath + " - " + file.exists());
            } catch (Exception e) {
                Log.e(this.toString(), e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.e(this.toString(), e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<String,ArrayList<String>> loadListUrls(Context context) {
        Log.i(this.toString(), "Reading file...");
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> durationList = new ArrayList<String>();
        String filePath = context.getFilesDir().getPath().toString() + "/" + secondFilename;
        FileInputStream inputStream;
        String line;
        Map<String,ArrayList<String>> map = new HashMap<>();

        try {
            inputStream = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            int count = 0;

            while ((line = reader.readLine()) != null) {
                if( count % 2 == 0 ){
                    list.add(line);
                } else {
                    durationList.add(line);
                }
                count++;
            }

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.toString(), e.getMessage());
        }

        map.put("UrlList", list);
        map.put("DurationList", durationList);

        return map;
    }

    public void saveList(Context context, ArrayList<String> list, ArrayList<String> durationList) {
        Log.i(this.toString(), "Writing to file...");
        FileOutputStream outputStream;
        String filePath = context.getFilesDir().getPath().toString() + "/" + secondFilename;

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(filePath));
            for ( int i = 0 ; i < list.size(); i++ ){
                pw.println( list.get(i) );
                pw.println( durationList.get(i) );
            }
            pw.close();
        } catch (FileNotFoundException f) {

            Log.e(this.toString(), f.getMessage());
            File file = new File(filePath);

            try {
                file.createNewFile();
                PrintWriter pw = new PrintWriter(new FileOutputStream(filePath));
                for ( int i = 0 ; i < list.size(); i++ ){
                    pw.println( list.get(i) );
                    pw.println( durationList.get(i) );
                }
                pw.close();
                Log.e(this.toString(), "File created at " + filePath + " - " + file.exists());
            } catch (Exception e) {
                Log.e(this.toString(), e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.e(this.toString(), e.getMessage());
            e.printStackTrace();
        }
    }
}
