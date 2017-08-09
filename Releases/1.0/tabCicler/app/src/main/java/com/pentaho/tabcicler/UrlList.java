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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.concurrent.Exchanger;

/**
 * Created by gmneto on 04/08/2017.
 */

public class UrlList extends ListActivity {
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();


    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //File to save/load URLs List
    private String filename = "UrlFile";

    //Duration in seconds (before load next URL)
    private int duration = 10;

    @Override
    public void onCreate(Bundle icicle) {
        listItems = read(this);

        super.onCreate(icicle);
        setContentView(R.layout.activity_url_list);
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);


        final Button start = (Button) findViewById(R.id.startBtn);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if( listItems.size() > 0 ) {
                    Intent myIntent = new Intent(UrlList.this, ImmersiveWebView.class);
                    myIntent.putExtra("list", listItems);
                    try {
                        duration = Integer.parseInt(((EditText) findViewById(R.id.duration)).getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        duration = 10;
                    }
                    myIntent.putExtra("duration", duration);
                    UrlList.this.startActivity(myIntent);
                } else {
                    Toast.makeText(view.getContext(), "List is empty. Try to add an URL to the list.", Toast.LENGTH_SHORT ).show();
                }
            }
        });

        ListView mListView = (ListView) findViewById(android.R.id.list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                listItems.remove(pos);
                write(v.getContext(), listItems);
                adapter.notifyDataSetChanged();
            }
        });

    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItems(View v) {
        EditText edit = (EditText) findViewById(R.id.edit);
        String url = edit.getText().toString();

        if ( !url.isEmpty() ){
            if( !url.contains( "http://" ) ){
                Toast.makeText(v.getContext(), "Malformed URL. URL must start with 'http://'.", Toast.LENGTH_SHORT ).show();
            } else {
                listItems.add( url );
                write(v.getContext(), listItems);
                edit.setText( "http://" );
            }
        }
        adapter.notifyDataSetChanged();

    }

    public ArrayList<String> read(Context context) {
        Log.i(this.toString(),"Reading file...");
        ArrayList<String> list = new ArrayList<String>();
        String filePath = context.getFilesDir().getPath().toString() + "/" + filename;
        FileInputStream inputStream;
        String url;

        try {
            inputStream = new FileInputStream( filePath );
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((url=reader.readLine()) != null) {
                list.add(url);
            }

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.toString(),e.getMessage());
        }
        return list;
    }

    public void write(Context context, ArrayList<String> list) {
        Log.i(this.toString(),"Writing to file...");
        FileOutputStream outputStream;
        String filePath = context.getFilesDir().getPath().toString() + "/" + filename;

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(filePath));
            for (String url : list)
                pw.println(url);
            pw.close();
        } catch (FileNotFoundException f) {

            Log.e(this.toString(),f.getMessage());
            File file = new File(filePath);

            try{
                file.createNewFile();
                PrintWriter pw = new PrintWriter(new FileOutputStream(filePath));
                for (String url : list)
                    pw.println(url);
                pw.close();
                Log.e(this.toString(), "File created at " + filePath + " - " + file.exists() );
            }catch (Exception e) {
                Log.e(this.toString(),e.getMessage());
                e.printStackTrace();
            }

        }catch (Exception e) {
            Log.e(this.toString(),e.getMessage());
            e.printStackTrace();
        }
    }
}
