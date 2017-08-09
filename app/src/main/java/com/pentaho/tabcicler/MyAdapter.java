package com.pentaho.tabcicler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.ArrayList;

/**
 * Created by gmneto on 09/08/2017.
 */

public class MyAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> listOne;
    private ArrayList<String> listTwo;

    public MyAdapter(Context context, ArrayList<String> listOne, ArrayList<String> listTwo) {
        this.context = context;
        this.listOne = listOne;
        this.listTwo = listTwo;
    }

    @Override
    public int getCount() {
        return listOne.size();
    }

    @Override
    public Object getItem(int position) {
        return listOne.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TwoLineListItem twoLineListItem;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(
                    android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) convertView;
        }

        TextView text1 = twoLineListItem.getText1();
        TextView text2 = twoLineListItem.getText2();

        text1.setText( listOne.get(position) );
        text2.setText( listTwo.get(position) );

        return twoLineListItem;
    }
}