package com.example.lbomma.countries.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lbomma.countries.CountriesIntentService;
import com.example.lbomma.countries.R;
import com.example.lbomma.countries.data.CountryContract;



/**
 * Created by LBomma on 4/9/15.
 */
public class CountriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COUNTRY_LOADER = 0;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private ListView mListView;


    private static final String[] COUNTRY_COLUMNS = {
            //Column names for the country table
            CountryContract.CountryEntry.TABLE_NAME + "." + CountryContract.CountryEntry._ID,
            CountryContract.CountryEntry.COLUMN_NAME,
            CountryContract.CountryEntry.COLUMN_REGION,
            CountryContract.CountryEntry.COLUMN_CAPITAL,
            CountryContract.CountryEntry.COLUMN_CURRENCY,
            CountryContract.CountryEntry.COLUMN_NATIONALITY,
            CountryContract.CountryEntry.COLUMN_POPULATION,
            CountryContract.CountryEntry.COLUMN_LATITUDE,
            CountryContract.CountryEntry.COLUMN_LONGITUDE
    };

    // These indices are tied to COUNTRIES_COLUMNS.
    // If COUNTRIES_COLUMNS changes, these
    // must change.
    static final int COL_COUNTRY_ID = 0;
    static final int COL_COUNTRY_NAME = 1;
    static final int COL_COUNTRY_REGION = 2;
    static final int COL_COUNTRY_CAPITAL = 3;
    static final int COL_COUNTRY_CURRENCY = 4;
    static final int COL_COUNTRY_NATIONALITY = 5;
    static final int COL_COUNTRY_POPULATION = 6;
    static final int COL_COUNTRY_LATITUDE = 7;
    static final int COL_COUNTRY_LONGITUDE = 8;


    private CountriesAdapter mCountriesAdapter;
    private static final String PREF_USER_REFRESHED = "user_refreshed";
    private boolean isDataRefreshed;


    public CountriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.countriesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_refresh)
//        {
//            updateCountriesList();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mCountriesAdapter = new CountriesAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView  = (ListView) rootView.findViewById(R.id.listview_countries);
        mListView.setAdapter(mCountriesAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null)
                {
                    System.out.println( "country name is " + cursor.getString(COL_COUNTRY_NAME)) ;
                    System.out.println( "country latitude is " + cursor.getDouble(COL_COUNTRY_LATITUDE)) ;

                    ((CountriesListner) getActivity())
                            .onItemSelected(CountryContract.CountryEntry.buildSelectedCountry(
                                    cursor.getString(COL_COUNTRY_NAME)));

//                    Intent intent = new Intent(getActivity(), DetailActivity.class)
//                            .setData(CountryContract.CountryEntry.buildSelectedCountry(
//                                    cursor.getString(COL_COUNTRY_NAME)
//                            ));
//                    startActivity(intent);
                }
                mPosition = position;

            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(COUNTRY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateCountriesList()
    {

        Intent intent = new Intent(getActivity(), CountriesIntentService.class);
        getActivity().startService(intent);


//        FetchCountriesTask countryTask = new FetchCountriesTask(getActivity());
//        countryTask.execute();
        //getLoaderManager().restartLoader(COUNTRY_LOADER, null, this);

    }

    @Override
    public void onStart()
    {
        super.onStart();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        isDataRefreshed = sp.getBoolean(PREF_USER_REFRESHED, false);
        if (!isDataRefreshed)
        {
            updateCountriesList();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(PREF_USER_REFRESHED,true).commit();
        }
    }
       /*********Cursor Loader methods******/
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {

        // Sort order:  Ascending, by date.
        String sortOrder = CountryContract.CountryEntry.COLUMN_NAME;
        Uri countryUri = CountryContract.CountryEntry.buildCountriesList();

        return new CursorLoader(getActivity(),
                countryUri,
                COUNTRY_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        mCountriesAdapter.swapCursor(cursor);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mCountriesAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
}

