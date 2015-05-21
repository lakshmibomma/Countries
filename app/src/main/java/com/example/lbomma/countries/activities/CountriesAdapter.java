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
        TextView countryTextView = (TextView) view.findViewById(R.id.list_item_country_textView);
        String name = cursor.getString(CountriesFragment.COL_COUNTRY_NAME);
        name = "Country : " + name;
        countryTextView.setText(name);

        TextView regionTextView = (TextView) view.findViewById(R.id.list_item_region_textView);
        String region = cursor.getString(CountriesFragment.COL_COUNTRY_REGION);
        if (region == null || region.isEmpty()) {
            region = "Region : " + "Not Applicable";

        } else {
            region = "Region : " + region;

        }
        regionTextView.setText(region);

    }
}
