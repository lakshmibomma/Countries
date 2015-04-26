package com.example.lbomma.countries.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.content.CursorLoader;


import com.example.lbomma.countries.R;
import com.example.lbomma.countries.data.CountryContract;



/**
 * Created by LBomma on 4/9/15.
 */
public class CountiresFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int COUNTRY_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] COUNTRY_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            CountryContract.CountryEntry.TABLE_NAME + "." + CountryContract.CountryEntry._ID,
            CountryContract.CountryEntry.COLUMN_NAME,
            CountryContract.CountryEntry.COLUMN_REGION,
            CountryContract.CountryEntry.COLUMN_CAPITAL,
            CountryContract.CountryEntry.COLUMN_CURRENCY,
            CountryContract.CountryEntry.COLUMN_NATIONALITY,
            CountryContract.CountryEntry.COLUMN_POPULATION
    };

    // These indices are tied to COUNTRIES_COLUMNS.  If COUNTRIES_COLUMNS changes, these
    // must change.
    static final int COL_COUNTRY_ID = 0;
    static final int COL_COUNTRY_NAME = 1;
    static final int COL_COUNTRY_REGION = 2;
    static final int COL_COUNTRY_CAPITAL = 3;
    static final int COL_COUNTRY_CURRENCY = 4;
    static final int COL_COUNTRY_NATIONALITY = 5;
    static final int COL_COUNTRY_POPULATION = 6;



    private CountriesAdapter mCountriestAdapter;

    public CountiresFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.countriesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateCountriesList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mCountriestAdapter = new CountriesAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_countries);
        listView.setAdapter(mCountriestAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


//                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//                if (cursor != null) {
//                   // String locationSetting = Utility.getPreferredLocation(getActivity());
//                    Intent intent = new Intent(getActivity(), DetailActivity.class)
//                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
//                            ));
//                    startActivity(intent);
//                }
                //To do how to pass object through extras
//                String countryDetails = mCountriestAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                .putExtra(Intent.EXTRA_TEXT, countryDetails);
//                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(COUNTRY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateCountriesList() {
        FetchCountriesTask countryTask = new FetchCountriesTask(getActivity());
        countryTask.execute();
        getLoaderManager().restartLoader(COUNTRY_LOADER, null, this);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateCountriesList();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Sort order:  Ascending, by date.
        String sortOrder = CountryContract.CountryEntry.COLUMN_NAME;
        Uri weatherForLocationUri = CountryContract.CountryEntry.buildCountriesList();

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                COUNTRY_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCountriestAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCountriestAdapter.swapCursor(null);
    }
}

