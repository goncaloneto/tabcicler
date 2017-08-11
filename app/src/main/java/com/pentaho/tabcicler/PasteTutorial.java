package com.pentaho.tabcicler;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

/**
 * Created by gmneto on 11/08/2017.
 */

public class PasteTutorial extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paste_tutorial);

        FloatingActionButton close = (FloatingActionButton) findViewById(R.id.xButton);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
