package com.example.lbomma.countries.activities;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lbomma.countries.R;


/**
 * Created by LBomma on 4/25/15.
 */
public class CountriesAdapter extends CursorAdapter {
    public CountriesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_country, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Bind the data with the User interface elements
        TextView tv = (TextView)view;
        String name = cursor.getString(CountriesFragment.COL_COUNTRY_NAME);
        String region = cursor.getString(CountriesFragment.COL_COUNTRY_REGION);

        String message = "Country : " + name+ " , " +"Region : " + region;
        tv.setText(message);
    }
}
