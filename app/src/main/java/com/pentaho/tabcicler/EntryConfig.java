package com.pentaho.tabcicler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by gmneto on 09/08/2017.
 */

public class EntryConfig extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final int position = intent.getIntExtra("position",0);
        final String url = intent.getStringExtra("url");

        setContentView(R.layout.activity_entry_config);

        TextView title = (TextView) findViewById(R.id.urlConfigTitle);
        Button delete = (Button) findViewById(R.id.delete);
        Button save = (Button) findViewById(R.id.save);
        FloatingActionButton xButton = (FloatingActionButton) findViewById(R.id.xButton);
        final EditText duration = (EditText) findViewById(R.id.editDuration);

        title.setText(url);

        Drawable d = delete.getBackground();
        PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.rgb(220,80,70), PorterDuff.Mode.SRC_ATOP);
        d.setColorFilter(filter);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("position", position);
                setResult( -1, resultIntent);
                finish();
            }
        });

        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("position", position);

                String d = duration.getText().toString();

                if( d.isEmpty() ){
                    Toast.makeText( EntryConfig.this, "Duration cannot be empty!" , Toast.LENGTH_SHORT).show();
                    finish();
                }

                resultIntent.putExtra( "duration", d );
                setResult( 1, resultIntent );
                finish();
            }
        });
    }
}
